#!/usr/bin/python
"""
  Makes the magic happen (assuming we are still in examples/location_server)

  to run:
  python start.py
  
  
  Now open another terminal window, and try querying the server using curl.  It will return JSON
  
  curl "http://localhost:9090/?query=address&address=MAC_ADDRESS_HERE"
  
  curl "http://localhost:9090/?query=location&x=-1&y=-1&z=-1"
  
"""
import os
import signal
_pid = os.fork()

if(_pid == 0):
  os.system("cd ../../ && python bluetooth_scanner.py --pid=/tmp/bluetooth.pid --url=http://localhost:9090/ ")
  exit()
else:
  
  try:
    os.system("python location_server.py")
  except (KeyboardInterrupt, SystemExit):
    print "Shutting Down"
  finally:
    pid = int( open("/tmp/bluetooth.pid").read())
    if(pid > 10):
      os.kill(pid  , 9 )
    
    os.kill(_pid, 9)
