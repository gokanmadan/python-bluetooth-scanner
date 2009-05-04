/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.pubsub.elements;

public class CreateElement extends PubSubElement {

	public String getName() {
		return "create";
	}
	
	public CreateElement(String node) { super(node); }
	public CreateElement() { super(); }
	
	public String toXML() {
	  return "  <create node='"+this.getNode()+"'/>\n";
	}
}
