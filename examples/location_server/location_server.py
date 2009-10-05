"""
  really simple bluetooth location server.
  
  example query:
  
  # get information for a particular machine
  curl "http://localhost:9090/?query=address&address=MAC_ADDRESS_HERE"
  
  # get all devices near that location
  curl "http://localhost:9090/?query=location&x=-1&y=-1&z=-1"
  
  # get all devices near a location that are not expired:
  curl "http://localhost:9090/?query=location&x=-1&y=-1&z=-1&current=1"
  
  Ussage:
  
    python location_server.py
  
"""

from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
from SocketServer import ThreadingMixIn
import threading
from cgi import parse_qs, escape
import simplejson
import time

address_locations = {}
device_map        = {}

class Handler(BaseHTTPRequestHandler):
    
    def do_GET(self):
        self.send_response(200)
        self.end_headers()

        if("?" in self.path):
          (cmd,args) = self.path.split("?")
          dict_args = self.args_to_dict( parse_qs(args) )
          res = self.process(cmd, dict_args )
          
          if "callback" in dict_args:
            result = "%s(%s);" %( dict_args["callback"], simplejson.dumps(res) )
          else:
            result = "%s" %( simplejson.dumps(res) )
          
          self.wfile.write( result )
        return

    def process(self, cmd, data):
      """
        Processes a request, using the query param to determine if it's a location or a MAC
        address query.
        
        Returns stored information for either one ADDRESS or all ADDRESSES at a location.
      """
      if('query' in data):
        print address_locations
        
        if(data['query'] == "location"):
          ret = []
          for k,v in address_locations.items():
            if(v['x'] == data['x'] and v['y'] == data['y'] and v['z'] == data['z']):
              # filter expired items
              if("expire" in v and time.time() > v['expire'] ):
                continue
                
              ret.append(v)
          return ret
          
        elif(data['query'] == "address" and "address" in data):
          return address_locations.get( data["address"], False )
          
        
      else:
        if "device_name" in data:
            device_map[ data["device_id"]] = data["device_name"]
        elif data["device_id"] in device_map:
            data[ "device_name"] = device_map[ data["device_id"] ]
                
        # store it
        address_locations[ data['device_id'] ] = data
        return True
        
    def args_to_dict(self, args):
      " Convert a CGI parsed query into a dictionary, for me"
      ret = {}
      for i in args.keys():
        ret[i] = args[i][0]
      
      return ret
      
class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    """Handle requests in a separate thread."""


if __name__ == '__main__':
    server = ThreadedHTTPServer(('0.0.0.0', 9090), Handler)
    print 'Starting server, use <Ctrl-C> to stop'
    server.serve_forever()