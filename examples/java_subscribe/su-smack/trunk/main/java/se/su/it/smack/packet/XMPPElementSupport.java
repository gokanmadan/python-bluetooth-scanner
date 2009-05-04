/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.packet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.xmlpull.v1.XmlPullParser;

public abstract class XMPPElementSupport implements XMPPElement {

	private List<XMPPElement> children;
	
	public XMPPElementSupport() {
		super();
		this.children = new ArrayList<XMPPElement>();
	}

	public List<XMPPElement> getChildren() {
		return children;
	}
	
	public boolean isSingleChild()
	{
		return false;
	}

	public void addChild(XMPPElement elt) {
		synchronized(children) {
			if (isSingleChild())
				setChild(elt);
			else
				synchronized(children) { children.add(elt); }
		}
	}
	
	protected void setChild(XMPPElement elt) {
		if (children.size() == 0)
			children.add(elt);
		else
			children.set(0,elt);
	}
	
	public abstract String getName();
	public String getBeginXMLAttributes() 
	{
		return "";
	}
	
	protected String getBeginXML()
	{
		return "<"+getName()+" "+getBeginXMLAttributes()+">\n";
	}
	
	protected String getEndXML() 
	{
		return "</"+getName()+">";
	}
	
	public XMPPElement getChild()
	{
		return (XMPPElement)children.get(0);
	}
	
	public String getChildXML()
	{
		StringBuffer buf = new StringBuffer();
		for (XMPPElement c : getChildren())
        {
        	buf.append(c.toXML()).append("\n");
        }
		return buf.toString();
	}
	
	public String toXML() {
		return getBeginXML()+getChildXML()+getEndXML();
    }

	public void parse(XmlPullParser pp) throws Exception
	{
		pp.require(XmlPullParser.START_TAG,null,null);
		for (int i = 0; i < pp.getAttributeCount(); i++)
		{
			BeanUtils.setProperty(this,pp.getAttributeName(i),pp.getAttributeValue(i));
		}
	}
	
}
