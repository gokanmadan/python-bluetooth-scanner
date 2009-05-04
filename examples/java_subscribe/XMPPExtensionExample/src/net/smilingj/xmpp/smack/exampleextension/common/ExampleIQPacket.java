package net.smilingj.xmpp.smack.exampleextension.common;

import org.jivesoftware.smack.packet.IQ;

public class ExampleIQPacket extends IQ {

    private String examplecontent = null;
    
    public ExampleIQPacket() {
    	// enpty constructor for introspection parser
    }

    public ExampleIQPacket(String examplecontent) {
    	this.examplecontent = examplecontent;
    }

    public String getExamplecontent() {
        return this.examplecontent;
    }

    public void setExamplecontent(String examplecontent) {
    	this.examplecontent = examplecontent;
    }
    
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<query xmlns=\"jabber:iq:exampleiqpacket\">");
        if(examplecontent != null) buf.append("<examplecontent>").append(examplecontent).append("</examplecontent>");
        buf.append("</query>");
        return buf.toString();
    }
}