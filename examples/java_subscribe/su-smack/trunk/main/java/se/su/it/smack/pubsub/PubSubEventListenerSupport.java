/*
 * Created on Nov 9, 2005
 *
 */
package se.su.it.smack.pubsub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.su.it.smack.pubsub.elements.DeleteElement;
import se.su.it.smack.pubsub.elements.ItemElement;
import se.su.it.smack.pubsub.elements.PurgeElement;
import se.su.it.smack.pubsub.elements.RetractElement;

public abstract class PubSubEventListenerSupport implements PubSubEventListener {

	private static final Log log = LogFactory.getLog(PubSubEventListenerSupport.class);
	
	public PubSubEventListenerSupport() {
		super();
	}

	public void onPublish(ItemElement item) throws Exception {
		log.info(item.toXML());
	}

	public void onPurge(PurgeElement purge) throws Exception {
		log.info(purge.toXML());
	}

	public void onDelete(DeleteElement delete) throws Exception {
		log.info(delete.toXML());
	}

	public void onRetract(RetractElement retract) throws Exception {
		log.info(retract.toXML());
	}

}
