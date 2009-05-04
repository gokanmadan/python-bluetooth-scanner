/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.pubsub.elements;

public class DeleteElement extends PubSubElement {

	public String getName() {
		return "delete";
	}
	
	public DeleteElement(String node) { super(node); }
	public DeleteElement() { super(); }
    public String toXML() {
        return "  <delete node='"+this.getNode()+"'/>\n";
    }
}
