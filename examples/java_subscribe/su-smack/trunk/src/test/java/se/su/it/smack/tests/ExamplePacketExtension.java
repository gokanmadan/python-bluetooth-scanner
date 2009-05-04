
package se.su.it.smack.tests;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;
import org.jivesoftware.smack.packet.DefaultPacketExtension;


public class ExamplePacketExtension extends DefaultPacketExtension implements PacketExtension {
	
	private String exampleAttribut;
	
	public static String namespace = "http://jabber.org/protocol/pubsub#event";
	public static String elementname = "event";
	
	public ExamplePacketExtension() {
	  super(namespace, elementname);
	}
	
	public void setExampleAttribut(String value) {
		exampleAttribut = value;
	}
	
	public String getExampleAttribut() {
		return exampleAttribut;
	}

	public String getElementName() {
		return ExamplePacketExtension.elementname;
	}

	public String getNamespace() {
		return ExamplePacketExtension.namespace;
	}

	public String toXML() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
		if(exampleAttribut != null) buf.append("<content exampleattribut=\"").append(exampleAttribut).append("\"/>");
		buf.append("</").append(getElementName()).append(">");
		
		return buf.toString();
	}
	
	public static class Provider implements PacketExtensionProvider{

		public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		  System.out.println("inside parse extensions");
		  System.out.println("--=================================================");
			ExamplePacketExtension extension = new ExamplePacketExtension();
			boolean done = false;
			while(!done) {
				int eventType = parser.next();
				if(eventType == XmlPullParser.START_TAG) {
					if(parser.getName().equals("item")) {
						extension.setExampleAttribut(parser.getText() );
						done = true;
					}
				}
			}
			return extension;
		}

	}

}
