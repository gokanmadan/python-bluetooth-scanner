/*
 * Created on Aug 15, 2005
 *
 */
package se.su.it.smack.pubsub.elements;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.dom2_builder.DOM2XmlPullBuilder2;

import se.su.it.smack.packet.XMPPElement;
import se.su.it.smack.packet.XMPPElementSupport;

public class ItemElement extends XMPPElementSupport {

	private static final Log log = LogFactory.getLog(ItemElement.class);
	
	private String id;
	private Element content;

	public String getName() { return "item"; }
	
	public ItemElement(String id, String xml)
	{
		this.id = id;
		setContent(xml);
	}
	
	public ItemElement(String id)
	{
		this.id = id;
		this.content = null;
	}
	
	public ItemElement() { }
	
	public String getId() {
		if (id == null)
			id = UUID.randomUUID().toString();
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setContent(Element content) {
		this.content = content;
	}
	
	public Element getContent() {
		return content;
	}

	public synchronized void setContent(String xml)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
			setContent(doc.getDocumentElement());
		} 
		catch (Exception ex)
		{
			log.error(ex);
		}
	}
	
	private void enscribe(Node node, StringBuffer buf)
	{
		short type = node.getNodeType();
		switch (type)
		{
		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE:
			buf.append(node.getNodeValue());
			break;
		case Node.ELEMENT_NODE:
			buf.append("<").append(node.getNodeName());
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++)
			{
				Node a = attrs.item(i);
				buf.append(" ").append(a.getNodeName()).append("=\"").append(a.getNodeValue()).append("\"");
			}
			NodeList children = node.getChildNodes();
			if (children != null && children.getLength() > 0)
			{
				buf.append(">");
				for (int i = 0; i < children.getLength(); i++)
				{
					enscribe(children.item(i),buf);
				}
				buf.append("</").append(node.getNodeName()).append(">");
			}
			else 
			{
				buf.append("/>");
			}
		}
	}
	
	public String getContentXML()
	{
		StringBuffer buf = new StringBuffer();
		enscribe(content,buf);
		return buf.toString();
	}
	
	@Override
	public String getBeginXMLAttributes() 
	{
		return getId() != null ? "id=\'"+getId()+"\'" : "";
	}
	
	public String getChildXML() 
	{
		return getContentXML();
	}

	public synchronized void parse(XmlPullParser pp) throws Exception {
		for (int i = 0; i < pp.getAttributeCount(); i++)
		{
			BeanUtils.setProperty(this,pp.getAttributeName(i),pp.getAttributeValue(i));
		}
                if( pp.nextTag() == XmlPullParser.START_TAG )
                {
                    DOM2XmlPullBuilder2 domBuilder = new DOM2XmlPullBuilder2();
                    setContent(domBuilder.parseSubTree(pp));
                }
	}

}
