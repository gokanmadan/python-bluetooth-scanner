/*
  javac -classpath smack_3_1_0/smackx.jar SubClient.java
  java -classpath smack_3_1_0/smackx.jar:. SubClient
  
  Alright,  I am not feeling 'it' -- meaning up to hacking this thing to support IQ pub/sub
  
  http://www.igniterealtime.org/builds/smack/docs/latest/javadoc/
  
  is a good place to start.
  
  Basically you'd want to subscribe to the bluetooth 'channel' and then 
  
  process message packets containing Items.
  
SUBSCRIBE:
<iq type="set" from="ben@pubjab.be-n.com"
    to="pubsub.pubjab.be-n.com" id="sub1">
  <pubsub xmlns="http://jabber.org/protocol/pubsub">
    <subscribe node="home/pubjab.be-n.com/ben/btscan"
               jid="ben@pubjab.be-n.com"/>
  </pubsub>
</iq>

*/
import java.io.IOException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.FromContainsFilter; 
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.packet.IQ;



public class SubClient {
    
    // Notice the username is NOT smack.test@gmail.com
    private static String username = "sub2";
    private static String password = "rocket";
    private static String host     = "141.211.184.132";
    private static String hostname = "pubjab.be-n.com";
    private static String node = "/home/pubjab.be-n.com/ben/btscan";
    
    /*
    public static class MessageParrot implements PacketListener {
        private XMPPConnection xmppConnection;
        
        public MessageParrot(XMPPConnection conn) {
            xmppConnection = conn;
        }
        
        public void processPacket(Packet packet) {
            System.out.println(packet.toXML() );
            
              Do what you will.. with the packets here :-)
            
              Smack might be filtering stuff.. it's hard to tell?
            
            Message message = (Message)packet;
//            System.out.println(message.toXML() );
            if(message.getBody() != null) {
               String fromName = StringUtils.parseBareAddress(message.getFrom());
               System.out.println("Message from " + fromName + "\n" + message.getBody() + "\n");
               Message reply = new Message();
               reply.setTo(fromName);
               reply.setBody("I am a Java bot. You said: " + message.getBody());
               xmppConnection.sendPacket(reply);
            }
        }
    };
    */
    
    public static void main( String[] args ) {
        XMPPConnection.DEBUG_ENABLED=true;
        System.out.println("Starting IM client");
        
        // gtalk requires this or your messages bounce back as errors
        ConnectionConfiguration connConfig = new ConnectionConfiguration(SubClient.host, 5222, SubClient.hostname);
        connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        XMPPConnection connection = new XMPPConnection(connConfig);
        
        /* needed because SASL seems broken with ejabberd */
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        
        try {
            connection.connect();
            System.out.println("Connected to " + connection.getHost());
        } catch (XMPPException ex) {
            //ex.printStackTrace();
            System.out.println("Failed to connect to " + connection.getHost());
            System.exit(1);
        }
        try {
            connection.login(username, password);
            System.out.println("Logged in as " + connection.getUser());
            
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            
        } catch (XMPPException ex) {
            //ex.printStackTrace();
            // XMPPConnection only remember the username if login is succesful
            // so we can''t use connection.getUser() unless we log in correctly
            System.out.println("Failed to log in as " + username);
            System.exit(1);
        }
        
        /* Ok here is where I need to add the pubsub stuff */
        IQSubscribe iq = new IQSubscribe(SubClient.username + "@" + SubClient.hostname, SubClient.node );
        iq.setTo("pubsub." + SubClient.hostname);
        iq.setType(IQ.Type.SET);
        System.out.println(iq.toXML() );
        connection.sendPacket(iq);
        
        
        
        PacketFilter filter = new FromContainsFilter("pubsub");
        connection.addPacketListener(new PacketListener() {

  				public void processPacket(Packet p) {
  				  System.out.println(p.toXML());
  				  PacketExtension e = p.getExtension("http://jabber.org/protocol/pubsub#event");
  				  if(e == null) {
  				    e = p.getExtension("x","http://jabber.org/protocol/pubsub");
  				  }
  				  System.out.println(e);
  				  System.out.println("rockit");
  				  System.out.println(e.toXML());		  
				    System.out.println("gere");
  				}}, filter );
  				
        
        
        // Collect these messages
        PacketCollector collector = connection.createPacketCollector(filter);
        while(true){
          Packet packet = collector.nextResult();
          System.out.println(packet.toXML());
        }
        
        /*
        PacketFilter filter = new PacketTypeFilter(Message.class);
        connection.addPacketListener(new MessageParrot(connection), filter);

                
        System.out.println("Press enter to disconnect\n");

        try {
            System.in.read();
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
        */
        //connection.disconnect();
        
    }
}
