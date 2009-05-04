/*
 * Created on Nov 8, 2005
 *
 */
package se.su.it.smack.pubsub;

import java.util.Collection;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.xmlpull.v1.XmlPullParser;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.packet.XMPPElementSupport;
import se.su.it.smack.provider.PubSubEventProvider;
import se.su.it.smack.pubsub.elements.ItemsElement;

public class PubSubEvent extends XMPPElementSupport implements PacketExtension {
	
	public PubSubEvent() 
	{
		super();
	}

	public String getElementName() 
	{
		return "event";
	}
	
	public String getName()
	{
		return getElementName();
	}

	public String getNamespace() 
	{
		return PubSubEventProvider.NS;
	}

	@Override
	public String getBeginXMLAttributes() {
		return "xmlns=\""+PubSubEventProvider.NS+"\"";
	}
	
	public void parse(XmlPullParser pp) throws Exception {
		throw new IllegalStateException("Should not be called - incorrectly positioned parser");
		
	}
	
	public Collection<XMPPElement> getItems()
	{	
		XMPPElement child = getChild();
		if (child == null)
			throw new IllegalArgumentException("Uninitialized pubsub event");
		
		if (child instanceof ItemsElement) {
			ItemsElement items = (ItemsElement) child;
			return items.getChildren();
		}
		
		throw new IllegalArgumentException("Not an item notification event");
	}
	
}
