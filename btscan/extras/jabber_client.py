"""
  First I am sorry I had to use twisted, but.. twisted is definitely the best 
  for this kind of thing:

  Quick and dirty - python hooks
    

  easy_install twisted
"""

from twisted.words.protocols.jabber import client, jid, xmlstream
from twisted.words.xish import domish
from twisted.internet import reactor
import logging
import simplejson
import random
import threading

logger = logging.getLogger('btscan.extras.jabber_client')


#
# globals -- damn this is ugly -- quick hacky code.
#
global _user, _pass,_xmlstream
_user = None
_pass = None
_xmlstream = None
_me = None
_pubsub = None
_reactor_thread = None

def _stop():
  reactor.callInThread(reactor.stop)

def _process(data):
  " takes data, sends it on the stream"
  reactor.callInThread(_process_reactor, data)

def _process_reactor(data):	
  global _pubsub
	  
  if(not _pubsub):
    presence = domish.Element((None, 'presence'))
    presence.addElement('status').addContent('Online')
    presence.addElement('data').addContent(simplejson.dumps(data))
    _xmlstream.send(presence)
    logger.debug("Sent over Jabber as presence: %s" % (presence.toXml() ) )
  else:
    #pub sub
    (u,host) = _user.split("@")
    # if we are using pubsub, register a chanel.
    iq = domish.Element((None, 'iq'), attribs={
      'from': _me.userhost() ,
      'to': "pubsub." + host,
      'type': 'set'
      })
    pubsub = domish.Element(('http://jabber.org/protocol/pubsub', 'pubsub'))
    publish = domish.Element((None,"publish"), attribs={'node' : "/home/%s/%s/btscan" %(host,u) } )
    publish.addElement("item").addContent(simplejson.dumps(data))
    pubsub.addChild(publish)
    iq.addChild(pubsub)
    _xmlstream.send(iq)
    # send the pubsub message:
    logger.debug("Sent pubsub: %s" % (iq.toXml() ) )
  
  reactor.iterate()
  
  
def get_callback(user, password, port=5222, server=None, pubsub=None, extra = "%i" %(random.randint(0,1000)) ):
  global _user, _pass, _me,_pubsub
  
  _user = user
  _pass = password
  _pubsub = pubsub
  
  (u,host) = _user.split("@")
  
  _me = jid.JID("%s/BluetoothScanner%s" %(_user, extra) )
  factory = client.basicClientFactory(_me, _pass)

  factory.addBootstrap(xmlstream.STREAM_AUTHD_EVENT, authd)
  factory.addBootstrap(xmlstream.STREAM_CONNECTED_EVENT, setup)
  factory.addBootstrap(client.BasicAuthenticator.AUTH_FAILED_EVENT, authfailedEvent)

  reactor.connectTCP(host, 5222, factory)
  # start it all
  _reactor_thread = threading.Thread(target=reactor.run, args=[0])
  _reactor_thread.start() 
  return _process

def setup(xmlstream):
  xmlstream.rawDataInFn = logger.debug
  xmlstream.rawDataOutFn = logger.debug 
def gotPresence(el):
    if(not 'type' in el.attributes):
      return
    t = el.attributes['type']
    if t == 'subscribe':
    # Grant every subscription request
      xmlstream.send(domish.Element((None, 'presence'), attribs={
      'from': _me.full() ,
      'to':el.attributes['from'],
      'type':'subscribed'
    }))

def gotIq(el):
  logger.debug(el)

def authfailedEvent(xmlstream):
  print "auth failed"
  global reactor
  logger.error('Jabber Auth failed!')
  reactor.stop()

def authd(xmlstream):
  global _xmlstream 
  logger.debug("authenticated")
  _xmlstream = xmlstream
  xmlstream.rawDataInFn = logger.debug
  xmlstream.rawDataOutFn = logger.debug 


  presence = domish.Element((None, 'presence'))
  presence.addElement('status').addContent('Online')
  xmlstream.send(presence)
  
  # add a callback for the messages -- need to handle subscribe
  xmlstream.addObserver('/presence', gotPresence)
  xmlstream.addObserver('/iq', gotIq)  
  """
  <iq type="set" to="pubsub.pubjab.be-n.com" id="create3">
    <pubsub xmlns="http://jabber.org/protocol/pubsub">
      <create node="/home/pubjab.be-n.com/ben"/>
      <configure/>
    </pubsub>
  </iq>
  """
  if(_pubsub):
    (u,host) = _user.split("@")
    # if we are using pubsub, register a chanel.
    iq = domish.Element((None, 'iq'), attribs={
      'from': _me.userhost() ,
      'to': "pubsub." + host,
      'type': 'set'
      })
    pubsub = domish.Element(('http://jabber.org/protocol/pubsub', 'pubsub'))
    pubsub.addChild(domish.Element((None,"create"), attribs={'node' : "/home/%s/%s" %(host,u) } ))
    pubsub.addElement("configure")
    iq.addChild(pubsub )
    logger.debug(iq.toXml())
    xmlstream.send(iq)
    iq = domish.Element((None, 'iq'), attribs={
      'from': _me.userhost() ,
      'to': "pubsub." + host,
      'type': 'set'
      })
    pubsub = domish.Element(('http://jabber.org/protocol/pubsub', 'pubsub'))
    pubsub.addChild(domish.Element((None,"create"), attribs={'node' : "/home/%s/%s/btscan" %(host,u) } ))
    pubsub.addElement("configure")
    iq.addChild(pubsub )
    logger.info("Publishing updates to: /home/%s/%s/btscan over XMPP" %(host,u))
    logger.debug(iq.toXml())
    xmlstream.send(iq)
    
  #reactor.iterate(1) 
  #reactor.stop() # stop the reactor.. so we can use iterate instead.
  #reactor.stop() # stop the reactor.. so we can use iterate instead.

if( __name__ =='__main__'):
  import sys
  logging.getLogger('').addHandler(logging.StreamHandler())
  logging.getLogger('').setLevel(logging.DEBUG)
  
  a = get_callback("ben@pubjab.be-n.com", "cool", pubsub=True)
  import time
  time.sleep(4)
  a({'thing' : 'newthing'})
