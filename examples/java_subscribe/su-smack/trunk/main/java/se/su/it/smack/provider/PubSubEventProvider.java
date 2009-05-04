/*
 * Created on Nov 8, 2005
 *
 */
package se.su.it.smack.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.pubsub.PubSubEvent;

public class PubSubEventProvider extends ProviderSupport implements PacketExtensionProvider {

	public static final String NS = "http://jabber.org/protocol/pubsub#event";
	
	public PubSubEventProvider() {
		super();
	}
	
	protected XMPPElement createRoot()
	{
		return new PubSubEvent();
	}

	public PacketExtension parseExtension(XmlPullParser pp) throws Exception {
		return (PacketExtension)parseElement(pp);
	}

}
