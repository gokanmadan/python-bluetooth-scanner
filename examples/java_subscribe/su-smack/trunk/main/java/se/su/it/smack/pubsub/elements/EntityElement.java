package se.su.it.smack.pubsub.elements;

/**
 * @see http://www.jabber.org/jeps/jep-0060.html#entity-subscribe
 * 
 * @author goern
 *
 */
public class EntityElement extends PubSubElement {
	private String jid;
	private String affiliation;
	private String subid;
	private String subscription;
	private int errorCode;
	private String errorType;
	
	public String getName() {
		return "entity";
	}

	/**
	 * @return Returns the affiliation.
	 */
	public String getAffiliation() {
		return affiliation;
	}

	/**
	 * @param affiliation The affiliation to set.
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	/**
	 * @return Returns the errorCode.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode The errorCode to set.
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return Returns the errorType.
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType The errorType to set.
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return Returns the jid.
	 */
	public String getJid() {
		return jid;
	}

	/**
	 * @param jid The jid to set.
	 */
	public void setJid(String jid) {
		this.jid = jid;
	}

	/**
	 * @return Returns the subid.
	 */
	public String getSubid() {
		return subid;
	}

	/**
	 * @param subid The subid to set.
	 */
	public void setSubid(String subid) {
		this.subid = subid;
	}

	/**
	 * @return Returns the subscription.
	 */
	public String getSubscription() {
		return subscription;
	}

	/**
	 * @param subscription The subscription to set.
	 */
	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	/**
	 * 
	 */
	public String toXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(getName());
		
		if (getNode() != null)
			buf.append(" node=\"").append(getNode()).append("\"");
		
		if (getJid() != null)
			buf.append(" jid=\"").append(getJid()).append("\"");
		
		if (getAffiliation() != null)
			buf.append(" affiliation=\"").append(getAffiliation()).append("\"");
		
		if (getSubscription() != null)
			buf.append(" subscription=\"").append(getSubscription()).append("\"");
			
		buf.append("/>");

		return buf.toString();
	}
}
