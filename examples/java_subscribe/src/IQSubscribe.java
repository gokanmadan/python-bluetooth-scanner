/**
 * $RCSfile: PEPPubSub.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/11/03 04:46:52 $
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
<iq type="set" from="ben@pubjab.be-n.com"
    to="pubsub.pubjab.be-n.com" id="sub1">
  <pubsub xmlns="http://jabber.org/protocol/pubsub">
    <subscribe node="home/pubjab.be-n.com/ben/btscan"
               jid="ben@pubjab.be-n.com"/>
  </pubsub>
</iq>

javac IQSubscribe.java -classpath smack_3_1_0/smack.jar:smack_3_1_0/smackx.jar

*/
import org.jivesoftware.smack.packet.IQ;

/**
 * Represents XMPP PEP/XEP-163 pubsub packets.<p>
 * 
 * The 'http://jabber.org/protocol/pubsub' namespace  is used to publish personal events items from one client 
 * to subscribed clients (See XEP-163).
 *
 * @author Jeff Williams
 */
public class IQSubscribe extends IQ {
    
    /**
    * Creates a new PubSub.
    *
    */
    String node;
    String jid;
    public IQSubscribe(String _jid, String _node) {
        super();
        this.node = _node;
        this.jid  = _jid;
    }

    /**
    * Returns the XML element name of the extension sub-packet root element.
    * Always returns "x"
    *
    * @return the XML element name of the packet extension.
    */
    public String getElementName() {
        return "pubsub";
    }

    /** 
     * Returns the XML namespace of the extension sub-packet root element.
     * According the specification the namespace is always "jabber:x:roster"
     * (which is not to be confused with the 'jabber:iq:roster' namespace
     *
     * @return the XML namespace of the packet extension.
     */
    public String getNamespace() {
        return "http://jabber.org/protocol/pubsub";
    }

    /**
     /*
     <iq type="set" from="ben@pubjab.be-n.com"
         to="pubsub.pubjab.be-n.com" id="sub1">
       <pubsub xmlns="http://jabber.org/protocol/pubsub">
         <subscribe node="home/pubjab.be-n.com/ben/btscan"
                    jid="ben@pubjab.be-n.com"/>
       </pubsub>
     </iq>
     
     
     */
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<subscribe node=\"").append(this.node).append("\" jid=\"").append(this.jid).append("\"/>");
        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }

}
