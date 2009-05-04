/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.packet;

import org.xmlpull.v1.XmlPullParser;

public interface XMPPElement {

	public String toXML();
	public void addChild(XMPPElement o) throws IllegalArgumentException;
	public void parse(XmlPullParser pp) throws Exception;
}
