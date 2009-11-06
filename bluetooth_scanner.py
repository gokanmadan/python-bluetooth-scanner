import logging
import sys
import os

import commands
import simplejson
import urllib

from optparse import OptionParser

from btscan.proximitydetection  import FilterAddExpiration, FilterSetProximity
from btscan.btscanner           import FileBTMonitor, BTMonitor, FilterAddLocation, SCANNER_PATH
from btscan.namelookup          import FilterNameLookup, FileBTNameDriver

from btscan.constants           import *



# use stream for output
logging.getLogger('').addHandler(logging.StreamHandler())
logger = logging.getLogger('NearFarScanner')





parser = OptionParser()
parser.set_description("""
This program will call a URL with information about bluetooth devices that have been detected nearby
it is designed to help you quickly prototype applications that require proximity detection.  Only state changes
(i.e. arrivals and departures ) will be sent to the URL.

See the examples/ directory to learn more about what you can do with bluetooth_scanner.py
""")
parser.add_option("--devices", dest="devices", help="Number of Bluetooth devices to use for name lookups", type='int' , default = None)

parser.add_option("-v", dest="verbose",help="Verbose"           , default = False, action="store_true")
parser.add_option("--vv", dest="very_verbose",help="Very Verbose"           , default = False, action="store_true")

# for daemon
parser.add_option("--pid", dest = "pid_file", help="Pid filename", default= PID_PATH + "/bt_scanner.pid")
parser.add_option("-d", dest="daemon",help="Damonize"           , default = False, action="store_true")

parser.add_option("-l", dest="location",help="Location x,y,z"           , default = "-1,-1,-1" )

parser.add_option("--url", dest="callback_url",help="URL to be called on events"           , default = None )
parser.add_option("--output", dest="output_file",help="File to write output to"           , default = None )
parser.add_option("--input", dest="input_file",help="Read bluetooth from file instead of device"           , default = None )
parser.add_option("--names_file", dest="names_file",help="Read bluetooth from file instead of device"           , default = None )
parser.add_option("--loop", dest="loop",help="Number of times to loop the input file (use -1 to loop forever)", default = 1 )
parser.add_option("--raw", dest="raw",help="Send all Bluetooth packets without determining proximity", action="store_true", default = None )

#
# Jabber support using presence packets (hopefully they are not throttles)
#
parser.add_option("--jabber_user"     , dest="jabber_user",help="Specify a jabber user to log in as")
parser.add_option("--jabber_port"     , dest="jabber_port",help="Specify a jabber server port", default=5222)
parser.add_option("--jabber_password" , dest="jabber_password",help="Specify a jabber password to user for login")
parser.add_option("--jabber_pubsub"   , dest="jabber_pubsub",action="store_true", help="Publish updates using pubsub", default=None)

# Nokoscope output
#
parser.add_option("--nokoscope_token", dest="nokoscope_token",help="Nokoscope Auth Token", default = None )
parser.add_option("--nokoscope_user", dest="nokoscope_user",help="Nokoscope API User", default = None )



(options, args) = parser.parse_args()

# get devices if we can
(status,hcitool_output) = commands.getstatusoutput("hcitool dev")
  

if(not options.input_file):
  # if we aren't inputing from a file, make sure we have bluetooth devices
  
  if(not os.path.exists(SCANNER_PATH)):
    logger.error("%s does not exist, please make sure you have run 'make' " % (SCANNER_PATH))
    exit(2)

  if(status !=0):
    logger.error("bluez-utils are not installed or bluetooth is not configured corectly")
    exit(2)    

  if(hcitool_output.count("\n") ==0):
    logger.error("No bluetooth devices detected")
    exit(2)    


if(hcitool_output.count("\n") ==1 and not options.names_file):
  logger.error("Name lookups will be disabled, only one bluetooth device detected")


if(options.very_verbose):
  logging.getLogger('').setLevel(logging.DEBUG)
else:
  logging.getLogger('').setLevel(logging.INFO)

# Daemon code:
if(options.daemon):
  import resource
  maxfd = resource.getrlimit(resource.RLIMIT_NOFILE)[1]
  if maxfd == resource.RLIM_INFINITY:
    maxfd = 1024
  for fd in range(0, maxfd):
    try:
      os.close(fd)
    except OSError:  
      pass
  REDIRECT_TO = os.devnull
  os.open(REDIRECT_TO, os.O_RDWR) 
  os.dup2(0, 1) # stdout
  os.dup2(0, 2) # stderr

  pid = os.fork()
  if pid == 0:
    pass
  else:
    os._exit(0)
  
  # write pidfile:
  file(options.pid_file,"w").write("%d" % os.getpid())


options.location = [int(i) for i in options.location.split(",")]

if(not options.devices):
  options.devices = hcitool_output.count("\n")

if(options.devices > hcitool_output.count("\n")):
  options.devices = hcitool_output.count("\n")

_devices = [i for i in range(1,options.devices)]

#
# --------------------
# A simple jabber extension
jabber_callback = None
if(options.jabber_user and options.jabber_password):
  """
    So I had this incredible realization, that it might make sense to just publish updates onto
    an XMPP service, as another way of sending this data around.
    
    I am not going to put it in the core of Bluetooth Scanner -- but it's a reasonable extension.
  
    I will include the desired stuff here, and produce a Jabber Callback.
  """
  from btscan.extras import jabber_client
  jabber_callback = jabber_client.get_callback(options.jabber_user, options.jabber_password, port=options.jabber_port, pubsub=options.jabber_pubsub)


# It would be pretty trivial to add a Nokoscope argument here
#
#
nokoscope_callback = None
if(options.nokoscope_user and options.nokoscope_token):
  #
  # You need to have a nokoscope Account from Nokia to use this feature.
  #
  from btscan.extras import eb2lib
  records = []
  def nokoscope_call(data):
    
    rec = eb2lib.create_rec(type='bt_scan', data=data )
    c = eb2lib.Nokoscope(options.nokoscope_user, token=options.nokoscope_token)
    c.put([rec])
    logger.debug("uploaded packet to nokoscope")
  
  nokoscope_callback = nokoscope_call
#
# Everything about this line is mostly argument processing.
# --------------------------------------------------
# Everything below this line is doing actual work on bluetooth results.
#
#
 
  


#
# Setup BT input
#
btm = None
if(options.input_file):
  btm = FileBTMonitor(options.input_file, options.loop)
else:
  btm = BTMonitor()

#
# Setup name lookup
#
nl = None
if(options.names_file):
  nl = FilterNameLookup([1], FileBTNameDriver(options.names_file) )
else:
  nl = FilterNameLookup(_devices)

#
# Setup output, etc.
#
_file = None
if(options.output_file):
  _file = open(options.output_file, "a")

if(options.callback_url and options.callback_url.find("?") < 0):
  options.callback_url += "?"


  
location_filter  = FilterAddLocation(options.location[0], options.location[1], options.location[2])
expire_filter    = FilterAddExpiration( NORMAL_EXPIRE, HIDDEN_EXPIRE)
proximity_filter = None


def process_line(data):
  """
    Basically, the BT scanners produce input that looks like: 
    {raw: "MAC ADDRESS|Signal Strength"}
    
    This simple callback processes this
  """
  " Process a line of data from the BT scanner"
  data = location_filter.filter(data)
  
  if(options.devices > 1 or options.names_file):
    nl.async_lookup( data, process_callback)
  
  process_callback(data)

def process_callback(data):
  """
    After we've read the data, call the process callback to
    handle expiration of information and keeping track of near
    and far.
  """
  if data == {}:
    return
  
  
  if(not options.raw):
    data = expire_filter.filter(data)
    data = proximity_filter.filter(data)
  
  process_output_callback(data)
  

def process_output_callback(data):
  """
    This callback takes the filtered data, and sends it elseware
    specifically to a callback URL
  """

  # if it's blank, just return
  if data == {}:
    return

  if(options.verbose):
    logger.info(simplejson.dumps(data))

  if(options.callback_url):
    try:
      res = urllib.urlopen(options.callback_url + urllib.urlencode(data)).read()
      logger.debug("Callback URL: %s - Response: %s" %(options.callback_url, res))
    except Exception,e:
      logger.error("Cannot make request to %s" %(options.callback_url + urllib.urlencode(data)))
      logger.error(e)
      
    if(options.verbose and not options.very_verbose):
      logger.info("\nCalled URL: %s -Response: %s" %(options.callback_url + urllib.urlencode(data), res))
  
  #
  # this could be cleaned up into individual callbacks and such with a callback list, but eh.
  #
  if(options.output_file):
    _file.write(simplejson.dumps(data) + "\n")

  if(jabber_callback):
    jabber_callback(data)

  if(nokoscope_callback):
    nokoscope_callback(data)
#
# Set the Filter Proximity
#
proximity_filter= FilterSetProximity( process_output_callback, {})


#
# Start the scanner, we are good to go.
# 
try: 
  btm.start( process_line )
except KeyboardInterrupt:
  logger.error("Got Keyobard interrupt")

if(jabber_callback):
  jabber_client._stop()

logger.info("Done.")
