package se.su.it.smack.pubsub.elements;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Used with <code>CreateElement</code> to configure a created node. 
 * 
 * @author Mikko Pohja
 *
 */
public class ConfigureElement extends PubSubElement
{

    private Hashtable fields;
    
    public String getName()
    {
        return "configure";
    }

    /**
     * Adds a field element inside the configure element.
     * 
     * @param name Name of the field.
     * @param value Value of the field.
     */
    public void addField(String name, String value) {
        if (fields == null)
            fields = new Hashtable();
        
        fields.put(name, value);
    }

    public String toXML()
    {
        if (fields == null || fields.isEmpty())
            return "  <configure/>\n";
        else {
            String markup = "", name;
            Enumeration names = fields.keys();
            while (names.hasMoreElements()) {
                name = (String)names.nextElement();
                markup += "      <field var='" + name + "'><value>" + fields.get(name) + "</value></field>\n";
            }
            return "  <configure>\n    <x xmlns='jabber:x:data' type='submit'>\n"
                    + "      <field var='FORM_TYPE' type='hidden'>"
                    + "<value>http://jabber.org/protocol/pubsub#node_config</value>"
                    + "</field>\n"
                    + markup
                    + "    </x>\n  </configure>\n";
        }
    }

}
