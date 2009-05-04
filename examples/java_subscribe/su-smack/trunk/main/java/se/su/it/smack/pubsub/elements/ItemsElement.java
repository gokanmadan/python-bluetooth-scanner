/*
 * Created on Nov 8, 2005
 *
 */
package se.su.it.smack.pubsub.elements;

public class ItemsElement extends PubSubElement {

	public ItemsElement() {
		super();
	}

	public ItemsElement(String node) {
		super(node);
	}

	public String getName() {
		return "items";
	}

	public String toXML() {
	    return "  <items node='"+this.getNode()+"'/>\n";
	}
}
