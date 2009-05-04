/*
 * Created on Aug 21, 2005
 *
 */
package se.su.it.smack.pubsub.elements;

public class PurgeElement extends PubSubElement {

	public PurgeElement() {
		super();
	}

	public PurgeElement(String node) {
		super(node);
	}

	public String getName() {
		return "purge";
	}

    public String toXML() {
        return "  <purge node='"+this.getNode()+"'/>\n";
    }
}
