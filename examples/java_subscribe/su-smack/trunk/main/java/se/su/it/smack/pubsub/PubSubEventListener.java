/*
 * Created on Nov 9, 2005
 *
 */
package se.su.it.smack.pubsub;

import se.su.it.smack.pubsub.elements.DeleteElement;
import se.su.it.smack.pubsub.elements.ItemElement;
import se.su.it.smack.pubsub.elements.PurgeElement;
import se.su.it.smack.pubsub.elements.RetractElement;

public interface PubSubEventListener {

	public abstract void onPublish(ItemElement item) throws Exception;
	public abstract void onPurge(PurgeElement purge) throws Exception; 
	public abstract void onDelete(DeleteElement delete) throws Exception;
	public abstract void onRetract(RetractElement retract) throws Exception;
	
}
