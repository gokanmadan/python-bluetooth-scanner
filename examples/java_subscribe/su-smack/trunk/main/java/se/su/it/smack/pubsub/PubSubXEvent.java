/*
 * Created on Nov 8, 2005
 *
 */
package se.su.it.smack.pubsub;


/**
 * This class is a work-around for buggy servers who return event in <x/> tag instead of <event/>.
 * 
 * @author leifj
 *
 */
public class PubSubXEvent extends PubSubEvent {

	public PubSubXEvent() {
		super();
	}

	public String getElementName() {
		return "x";
	}
	
}
