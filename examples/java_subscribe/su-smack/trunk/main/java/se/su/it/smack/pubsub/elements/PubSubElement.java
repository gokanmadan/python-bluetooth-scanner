/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.pubsub.elements;

import se.su.it.smack.packet.XMPPElementSupport;

public abstract class PubSubElement extends XMPPElementSupport {

	private String node;
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	public PubSubElement() {
		super();
	}
	
	public PubSubElement(String node)
	{
		super();
		setNode(node);
	}

	public abstract String getName();
	
}
