/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.tests;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;


import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.SASLAuthentication;

import org.jivesoftware.smack.packet.PacketExtension;
import se.su.it.smack.pubsub.PubSub;
import se.su.it.smack.pubsub.PubSubEvent;
import se.su.it.smack.pubsub.elements.ItemElement;
import se.su.it.smack.pubsub.elements.PublishElement;
import se.su.it.smack.pubsub.elements.CreateElement;
import se.su.it.smack.pubsub.elements.ConfigureElement;
import se.su.it.smack.pubsub.elements.SubscribeElement;
import se.su.it.smack.pubsub.elements.ItemsElement;
import se.su.it.smack.pubsub.elements.ItemElement;
import org.jivesoftware.smack.filter.PacketExtensionFilter;

import se.su.it.smack.utils.XMPPUtils;

public class PubSubPublishTest extends TestCase {

	public boolean done = false;
	private static final Log log = LogFactory.getLog(PubSubPublishTest.class);
	
	private class TracePacketListener implements PacketListener {
		private String tag;
		public TracePacketListener(String tag) {this.tag = tag;};
		public void processPacket(Packet arg0) {
			System.err.println("RECV: ["+tag+"] "+arg0.toXML());
		}
		
	}
	
	private class TraceWritePacketListener implements PacketListener {
		private String tag;
		public TraceWritePacketListener(String tag) {this.tag = tag;};
		public void processPacket(Packet arg0) {
			System.err.println("SEND: ["+tag+"] "+arg0.toXML());
		}
		
	}
	
	public static void main(String [] args){
	  try {
	    PubSubPublishTest p = new PubSubPublishTest();
	    p.testPublish();
    }catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
    }
	}
	
	public void testPublish()
		throws Exception
	{
		Properties p = new Properties();
		//InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
		InputStream in = new FileInputStream(new File("testing.properties.txt"));
		if (in == null)
		{
			log.warn("test.properties missing - publish tests disabled");
			return;
		}
			
		p.load(in);
		String sjid = p.getProperty("smack.test.sender.jid");
		assertNotNull(sjid);
		String rjid = p.getProperty("smack.test.receiver.jid");
		assertNotNull(rjid);
		String pubsubService = p.getProperty("smack.test.pubsub");
		assertNotNull(pubsubService);
		String node = p.getProperty("smack.test.node");
		String node0 = p.getProperty("smack.test.node0");		
		
		boolean debug = Boolean.parseBoolean(p.getProperty("smack.test.debug"));
		boolean trace = Boolean.parseBoolean(p.getProperty("smack.test.trace"));
		
		
		if (debug)
			XMPPConnection.DEBUG_ENABLED = true;
		
		XMPPConnection sender = new XMPPConnection(StringUtils.parseServer(sjid));
		XMPPConnection receiver = new XMPPConnection(StringUtils.parseServer(rjid));
		
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		
		assertNotNull(sender);
		assertNotNull(receiver);
		System.out.println("here");
		sender.connect();
		receiver.connect();
		System.out.println(StringUtils.parseName(sjid));
		System.out.println(p.getProperty("smack.test.sender.password"));
		System.out.println(StringUtils.parseResource(sjid));
				
				
		/* add a handler */
		receiver.addPacketListener(new PacketListener() {

			public void processPacket(Packet p) {
			  System.out.println("====process packet=============================================");
			  
			  try {
				  PubSubEvent e = (PubSubEvent) p.getExtension(ExamplePacketExtension.namespace);
				  System.out.println("Extension content: ("+e.toXML()+")");
				}catch(Exception ee) {
				  System.out.println("Exception creating channel");
          System.out.println(ee);
          ee.printStackTrace();
				}

			}
			},
			new PacketFilter() { 
  			public boolean accept(Packet packet) { 
  			  System.out.println("=================================================");
  			  //System.out.println(XMPPUtils.getPubSubEvent(packet) != null);
  				return true;
  			}
  		
			}); //, new PacketExtensionFilter(ExamplePacketExtension.namespace));		
				
				
		sender.login(StringUtils.parseName(sjid),p.getProperty("smack.test.sender.password"),StringUtils.parseResource(sjid));
		receiver.login(StringUtils.parseName(rjid),p.getProperty("smack.test.receiver.password"),StringUtils.parseResource(rjid));
		System.out.println("here");
		
		//if(trace )
		//  log.setLevel((Level) Level.DEBUG);
		
		if (trace && debug)
		{
			sender.addPacketListener(new TracePacketListener("sender"),new PacketFilter() {
				public boolean accept(Packet arg0) {
					return true;
				}
			});
			sender.addPacketWriterListener(new TraceWritePacketListener("sender"),new PacketFilter() {
				public boolean accept(Packet arg0) {
					return true;
				}
			});
			
			receiver.addPacketListener(new TracePacketListener("receiver"),new PacketFilter() {
				public boolean accept(Packet arg0) {
					return true;
				}
			});
			receiver.addPacketWriterListener(new TraceWritePacketListener("receiver"),new PacketFilter() {
				public boolean accept(Packet arg0) {
					return true;
				}
			});
		}
		
		receiver.addPacketListener(new PacketListener() {

			public void processPacket(Packet packet) {
				System.out.println("Got an event");

				PubSubEvent   event = XMPPUtils.getPubSubEvent(packet);
				
				System.err.println("EVENT: "+event.toXML());
				System.out.println(  ( (ItemElement) ((ItemsElement) event.getChild()).getChildren().get(0)).getContentXML()  );
				
				done = true;
			}
		},new PacketFilter() { 
			public boolean accept(Packet packet) { 
			  System.out.println("tried to find event");
			  
			  
			  System.out.println(packet.toXML() );

        try {
          System.out.println(XMPPUtils.getPubSubEvent(packet) != null);
  				
  			  
        }catch(Exception ee) {
				  System.out.println("Exception creating channel");
          System.out.println(ee);
          ee.printStackTrace();
				}
				
				return true;
				
			}
		});
		/*
		  Create the service to publish to.
		
		  <iq type="set" to="pubsub.pubjab.be-n.com" id="create3">
        <pubsub xmlns="http://jabber.org/protocol/pubsub">
          <create node="/home/pubjab.be-n.com/ben"/>
          <configure/>
        </pubsub>
      </iq>
      
		*/
		PubSub pubSub1 = new PubSub();
		pubSub1.setTo(pubsubService);
		pubSub1.setType(IQ.Type.SET);
		pubSub1.addChild(new CreateElement(node0));
		pubSub1.addChild(new ConfigureElement());


    try {
       System.out.println(pubSub1.toXML());
      Packet response1 = XMPPUtils.sendAndWait(sender,pubSub1,100000);
    }catch (Exception e) {
      System.out.println("Exception creating channel");
      System.out.println(e);
      e.printStackTrace();
    }
         
     try { 
      pubSub1 = new PubSub();
  		pubSub1.setTo(pubsubService);
  		pubSub1.setType(IQ.Type.SET);
  		pubSub1.addChild(new CreateElement(node));
  		pubSub1.addChild(new ConfigureElement());
  		Packet response1 = XMPPUtils.sendAndWait(sender,pubSub1,100000);
    }catch (Exception e) {
      System.out.println("Exception creating channel");
      System.out.println(e);
      e.printStackTrace();
    }		

    
    /* 
      Now I need to have the receiver subscribe.
    
     */
    PubSub pubSub2 = new PubSub();
 		pubSub2.setTo(pubsubService);
 		pubSub2.setType(IQ.Type.SET);
 		pubSub2.addChild(new SubscribeElement(node, StringUtils.parseBareAddress(rjid) ) );
 		System.out.println("just sent subscription request");
 		Packet response2 = XMPPUtils.sendAndWait(receiver,pubSub2,100000);
    
    
		System.out.println("sending the message at last");
		PubSub pubSub = new PubSub();
		pubSub.setTo(pubsubService);
		pubSub.setType(IQ.Type.SET);
		PublishElement publish = new PublishElement(node);
		pubSub.addChild(publish);
		publish.addChild(new ItemElement(null,"<a><kaka foo=\"plupp\"/></a>"));

		Packet response = XMPPUtils.sendAndWait(sender,pubSub,100000);
		assertNotNull(response);
		assertEquals(response.getFrom(),pubsubService);
		assertNull(response.getError());
		
		while (!done)
		{
			System.err.println("Waiting for notification...");
			Thread.sleep(1000); /* allow notifications */
		}
		
		if (trace)
			Thread.sleep(100000000);
	}
	
}
