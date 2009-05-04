/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.tests;

import org.jivesoftware.smack.packet.IQ;

import se.su.it.smack.pubsub.PubSub;
import se.su.it.smack.pubsub.elements.CreateElement;
import se.su.it.smack.pubsub.elements.ItemElement;
import se.su.it.smack.pubsub.elements.PublishElement;
import junit.framework.TestCase;

public class PubSubCreateTest extends TestCase {

	public void testCreatePubSub()
	{
		PubSub pubSub = new PubSub();
		pubSub.setFrom("leifj@su.se/test");
		pubSub.setTo("pubsub.cdr.su.se");
		pubSub.setType(IQ.Type.SET);
		pubSub.addChild(new CreateElement("test/a/node"));
		System.err.println(pubSub.toXML());
		System.err.println(pubSub.getChild().toXML());
		assertTrue(pubSub.getChild().toXML().contains("<create"));
	}
	
	public void testCreatePublish()
	{
		PubSub pubSub = new PubSub();
		pubSub.setFrom("leifj@su.se/test");
		pubSub.setTo("pubsub.cdr.su.se");
		pubSub.setType(IQ.Type.SET);
		PublishElement publish = new PublishElement("test/a/node");
		pubSub.addChild(publish);
		publish.addChild(new ItemElement(null,"<a><kaka foo=\"plupp\"/></a>"));
		ItemElement item = (ItemElement)publish.getChild();
		assertNotNull(item);
		System.err.println("ITEM: "+item.toXML());
		assertEquals(item.toXML().startsWith("<item"),true);
		
		assertEquals(pubSub.getChild().toXML().startsWith("<publish"),true);
		System.err.println(pubSub.toXML());
	}
	
}
