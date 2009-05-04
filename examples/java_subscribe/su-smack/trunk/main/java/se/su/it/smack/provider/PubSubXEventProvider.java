/*
 * Created on Nov 8, 2005
 *
 */
package se.su.it.smack.provider;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.pubsub.PubSubXEvent;

public class PubSubXEventProvider extends PubSubEventProvider {

	public PubSubXEventProvider() {
		super();
	}
	
	protected XMPPElement createRoot() {
		return new PubSubXEvent();
	}

}
