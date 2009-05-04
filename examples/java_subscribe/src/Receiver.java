

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.SASLAuthentication;

public class Receiver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		try {
			XMPPConnection.DEBUG_ENABLED = true;
			
			XMPPConnection receiver = new XMPPConnection("test.hab.la");
			
  		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
  		receiver.connect();
  		
			
			receiver.addPacketListener(new PacketListener() {

				public void processPacket(Packet p) {
					System.out.println("running in here");
					
					ExamplePacketExtension e = (ExamplePacketExtension) p.getExtension(ExamplePacketExtension.namespace);
					System.out.println("Extension content: ("+e.getExampleAttribut()+")");
					
				}}, new PacketExtensionFilter(ExamplePacketExtension.namespace));
			
			receiver.login("test7", "test7", "rockon");		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
