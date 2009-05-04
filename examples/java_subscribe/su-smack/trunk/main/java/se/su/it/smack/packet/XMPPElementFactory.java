/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.packet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.xmlpull.v1.XmlPullParser;

public class XMPPElementFactory {

	public XMPPElementFactory() {
		super();
	}

	public static XMPPElement create(String name) throws Exception
	{
		// remove the '-' from the name
		name = WordUtils.capitalizeFully(name, new char[]{'-'});
		name = StringUtils.remove(name, '-');
//		String className = "se.su.it.smack.pubsub.elements."+name.substring(0,1).toUpperCase()+name.substring(1)+"Element";
		String className = "se.su.it.smack.pubsub.elements."+name+"Element";
		Class cls = Class.forName(className);
		XMPPElement elt = (XMPPElement)cls.newInstance();
		return elt;
	}

	public static XMPPElement create(XmlPullParser pp) throws Exception
	{
		pp.require(XmlPullParser.START_TAG,null,null);
		XMPPElement elt = create(pp.getName());
		elt.parse(pp);
		return elt;
	}

}
