package se.su.it.smack.pubsub.elements;

import se.su.it.smack.pubsub.elements.PubSubElement;

/**
 * This Element represents a entity subscribing to a node
 * @author goern
 *
 */
public class SubscribeOptionsElement extends PubSubElement {
	private String jid;

	public String getName() {
		return "subscribe-options";
	}

	public String getJid() {
		return this.jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public SubscribeOptionsElement(String node, String jid) {
		super(node);
		setJid(jid);
	}

	public SubscribeOptionsElement(String node) {
		super(node);
	}

	public SubscribeOptionsElement() {
		super();
	}

	/**
	 * @see http://www.jabber.org/jeps/jep-0060.html#entity-configure
	 */
	public String toXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(getName());

		if (getNode() != null)
			buf.append(" node=\"").append(getNode()).append("\"");

		if (getJid() != null)
			buf.append(" jid=\"").append(getJid()).append("\"");

		buf.append("/>");

		return buf.toString();
	}

}
