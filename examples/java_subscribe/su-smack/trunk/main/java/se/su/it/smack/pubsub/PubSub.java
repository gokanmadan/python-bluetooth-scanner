/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.pubsub;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.xmlpull.v1.XmlPullParser;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.pubsub.elements.CreateElement;

public class PubSub extends IQ implements XMPPElement {

	private static final String NS = "http://jabber.org/protocol/pubsub";
	
	private List<XMPPElement> children = new ArrayList<XMPPElement>();
	
	/**
	 * @return
	 */
	public XMPPElement getChild() {
		return (XMPPElement)children.get(0);
	}
	
	public void addChild(XMPPElement o) throws IllegalArgumentException {
		 appendChild(o);
	}
	
	/**
	 * Adds a child element for pubsub element
	 * @param o child element
	 */
	public void appendChild(XMPPElement o)
	{
		children.add(o);
	}
	
	public PubSub() {
		super();
	}

	/**
	 * 
	 * @return
	 */
	public String getChildElementXML() {
	    String markup = "";
	    for (XMPPElement c : children)
	    {
	    	markup += c.toXML();
	    }
	    return "\n<pubsub xmlns=\""+NS+"\">\n"+ markup +"</pubsub>";
	}

	public List<XMPPElement> getChildren() {
	    return children;
	}

	/**
	 * @param elt
	 */
	public void setChild(XMPPElement elt) {
		children.set(0,elt);
	}

	public String getNamespace() {
		return NS;
	}
	
	
	public static void main(String[] args) throws Exception {
		PubSub pubSub = new PubSub();
		pubSub.setFrom("leifj@su.se/test");
		pubSub.setTo("pubsub.cdr.su.se");
		pubSub.setType(IQ.Type.SET);
		pubSub.appendChild(new CreateElement("test/a/node"));
		System.err.println(pubSub.toXML());
	}

	public void parse(XmlPullParser pp) throws Exception {
		throw new IllegalArgumentException("should not be called");
	}
}
