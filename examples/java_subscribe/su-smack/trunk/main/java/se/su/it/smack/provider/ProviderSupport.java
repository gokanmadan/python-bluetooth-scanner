/*
 * Created on Nov 10, 2005
 *
 */
package se.su.it.smack.provider;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.PacketExtension;
import org.xmlpull.v1.XmlPullParser;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.packet.XMPPElementFactory;

public abstract class ProviderSupport {

	private static final Log log = LogFactory.getLog(ProviderSupport.class);
	
	public ProviderSupport() { }

	protected abstract XMPPElement createRoot();
	
	public XMPPElement parseElement(XmlPullParser pp) 
		throws Exception 
	{
		XMPPElement root = createRoot();
		ArrayStack stack = new ArrayStack();
		stack.push(root);
		
		boolean done = false;
		while (!done)
		{
			int eventType = pp.next();
			if (log.isTraceEnabled())
				log.trace("IN LOOP: "+pp.getPositionDescription());
			
			if (eventType == XmlPullParser.END_TAG)	
			{
				Object child = stack.pop();
				if (stack.size() > 0)
				{
					XMPPElement parent = (XMPPElement)stack.peek();
					parent.addChild((XMPPElement)child);
				}
				else
				{
					done = true;
				}
			}
			
			if (eventType == XmlPullParser.START_TAG)
			{
                                boolean isSelfClosing = pp.isEmptyElementTag();
				XMPPElement elt = XMPPElementFactory.create(pp);
                                if( isSelfClosing )
                                {
                                    XMPPElement parent = (XMPPElement)stack.peek();
                                    parent.addChild((XMPPElement)elt);
                                }
                                else
                                {
                                    stack.push(elt);
                                }
			}
		}
			
		if (log.isTraceEnabled())
			log.trace("AFTER LOOP: "+pp.getPositionDescription());
		
		return root;
	}
	
}
