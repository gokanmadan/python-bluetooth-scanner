/*
 * Created on Nov 12, 2005
 *
 */
package se.su.it.smack.pubsub;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;
import org.w3c.dom.Element;
import se.su.it.smack.pubsub.elements.ItemElement;
import se.su.it.smack.pubsub.elements.PublishElement;

public abstract class PubSubProducer implements Iterator {
	
	private static final Log log = LogFactory.getLog(PubSubProducer.class);
	
	private String service;
	private String node;
	private LinkedList elements;
	
	public PubSubProducer()
	{
		this.elements = new LinkedList();
	}
	
	public void addElement(Element elt)
	{
		elements.addLast(elt);
	}
	
	public String getService() 
	{
		return service;
	}
	
	public void setService(String service) 
	{
		this.service = service;
	}
	
	public String getNode() 
	{
		return node;
	}
	
	public void setNode(String node) 
	{
		this.node = node;
	}
	
	public boolean hasNext() 
	{
		return hasMoreItems();
	}
	
	public Object next() 
	{
		if (!hasMoreItems())
			throw new IllegalStateException("No more packets at this time");
		
		PubSub pubSub = new PubSub();
		pubSub.setTo(getService());
		pubSub.setType(IQ.Type.SET);
		PublishElement pub = new PublishElement(getNode());
		pubSub.addChild(pub);
		ItemElement item = new ItemElement();
		item.setContent(nextItem());
		pub.addChild(item);

		return pubSub;
	}

	public void remove() 
	{
		throw new IllegalArgumentException("Application must not try to remove packets");
	}
	
	protected abstract void readElements() throws Exception;
	
	protected boolean hasMoreItems() 
	{
		try {
			readElements();
		} catch (Exception ex) {
			log.error(ex);
		}
		
		return !elements.isEmpty();
	}

	protected Element nextItem() 
	{
		synchronized (elements)
		{
			return (Element)elements.removeFirst();
		}
	}
	
}
