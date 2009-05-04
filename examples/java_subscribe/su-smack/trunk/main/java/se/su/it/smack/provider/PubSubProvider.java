/*
 * Created on Aug 12, 2005
 *
 */
package se.su.it.smack.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.pubsub.PubSub;

public class PubSubProvider extends ProviderSupport implements IQProvider {

	public static final String NS = "http://jabber.org/protocol/pubsub";
	
	public PubSubProvider() { super(); }
	
	protected XMPPElement createRoot() {
		return new PubSub();
	}
	
	public IQ parseIQ(XmlPullParser pp) throws Exception
	{
		return (IQ)parseElement(pp);
	}
	
	/*
	public IQ parseIQ(XmlPullParser pp) throws Exception {
		PubSub iq = new PubSub();
		ArrayStack stack = new ArrayStack();
		stack.push(iq);
		
		for (int tagType = pp.nextTag(); stack.size() > 0; tagType = pp.nextTag())
		{
			if (tagType == XmlPullParser.END_TAG)	
			{
				Object child = stack.pop();
				if (stack.size() > 0)
				{
					XMPPElement parent = (XMPPElement)stack.peek();
					parent.addChild(child);
				}
			}
			
			if (tagType == XmlPullParser.START_TAG)
			{
				XMPPElement elt = XMPPElementFactory.create(pp);
				stack.push(elt);
			}
		}
		return iq;
	}
	*/
}
