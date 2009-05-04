/*
 * Created on Jan 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package se.su.it.smack.utils;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.DefaultPacketExtension;

import org.jivesoftware.smack.packet.XMPPError;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.provider.PubSubEventProvider;
import se.su.it.smack.pubsub.PubSubEvent;
import se.su.it.smack.pubsub.PubSubEventListener;
import se.su.it.smack.pubsub.elements.DeleteElement;
import se.su.it.smack.pubsub.elements.ItemElement;
import se.su.it.smack.pubsub.elements.ItemsElement;
import se.su.it.smack.pubsub.elements.PurgeElement;
import se.su.it.smack.pubsub.elements.RetractElement;



/**
 * @author leifj
 *
 */
public class XMPPUtils {

	private static final long TIMEOUT = 3000;
	private static final Log log = LogFactory.getLog(XMPPUtils.class);
	
	/**
	 * 
	 */
	protected XMPPUtils() { }

	public static String jidForConnection(XMPPConnection c, String resource)
	{
		return c.getUser()+"@"+c.getHost()+(resource == null ? "" : "/"+resource);
	}
	
	public static String jidForConnection(XMPPConnection c)
	{
		return jidForConnection(c,null);
	}
	
	public static Packet sendAndWait(XMPPConnection c, Packet p)
		throws XMPPException
	{
		return sendAndWait(c,p,TIMEOUT);
	}
	
	public static Packet sendAndWait(XMPPConnection c, Packet p, long timeout)
		throws XMPPException
	{
		PacketCollector pc = c.createPacketCollector(new PacketIDFilter(p.getPacketID()));
		c.sendPacket(p);
		Packet result = pc.nextResult(timeout);
		if (result == null)
			throw new XMPPException("Timeout occured waiting for response to id "+p.getPacketID());
		XMPPError error = result.getError();
		if (error != null)
			throw new XMPPException(error);
		return result;
	}
	
	public static DefaultPacketExtension getPubSubEvent(Packet packet)
	{
		if (log.isTraceEnabled())
			log.trace("Getting pubsub from "+packet.toXML());
		
		System.out.println(packet.toXML());
		
		PacketExtension ext = packet.getExtension("event",PubSubEventProvider.NS);
		if (ext == null) {
			ext = packet.getExtension("x",PubSubEventProvider.NS);
		}
		
		if (log.isTraceEnabled())
			log.trace("Got "+(ext == null ? "nothing" : ext.toXML()));
		
		System.out.println("Got "+(ext == null ? "nothing" : ext.toXML()));
		System.out.println(ext);
		System.out.println(ext.getClass());
		System.out.println(((DefaultPacketExtension)ext).getNames());
		System.out.println("--------------------------------------\n------------------");
		System.out.println("getting value of whatever is in packet");		
		//System.out.println(((DefaultPacketExtension) ext).getValue("items"));
		
		try {
       System.out.println((PubSubEvent)ext);
    }catch (Exception e) {
      System.out.println("fucking found you -- whore");
      System.out.println(e);
      e.printStackTrace();
    }
    
		
		return (DefaultPacketExtension)ext;
	}
	
	public static PacketListener createEventPacketListener(final PubSubEventListener eventListener) throws Exception
	{	
		return new PacketListener()
		{
			private final Log log = LogFactory.getLog(eventListener.getClass());
			
			public void processPacket(Packet packet) {
				try {
					DefaultPacketExtension event = XMPPUtils.getPubSubEvent(packet);
					if (log.isDebugEnabled())
						log.debug("Processing event "+event.toXML());
					/*
					XMPPElement child = ((DefaultPacketExtension)event).getChildren().get(0);
					if (child instanceof ItemsElement) {
						ItemsElement items = (ItemsElement) child;
						for (Iterator it = items.getChildren().iterator(); it.hasNext(); )
						{
							ItemElement item = (ItemElement)it.next();
							eventListener.onPublish(item);
						}
					} else if (child instanceof DeleteElement) {
						eventListener.onDelete((DeleteElement)child);
					} else if (child instanceof RetractElement) {
						eventListener.onRetract((RetractElement)child);
					} else if (child instanceof PurgeElement) {
						eventListener.onPurge((PurgeElement)child);
					} else {
						log.error("Got unknown event: "+packet.toXML());
					}
				*/
				} catch (Exception ex) {
					ex.printStackTrace();
					log.error(ex);
				}
				
			}
		};
	}
	
	public static PacketFilter createPubSubFilter() {
		return new PacketFilter() {
			public boolean accept(Packet packet) {				
				return XMPPUtils.getPubSubEvent(packet) != null;
			}
		};
	}
	
}
