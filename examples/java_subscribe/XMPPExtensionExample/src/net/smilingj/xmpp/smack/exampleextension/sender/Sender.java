package net.smilingj.xmpp.smack.exampleextension.sender;

import net.smilingj.xmpp.smack.exampleextension.common.ExampleIQPacket;
import net.smilingj.xmpp.smack.exampleextension.common.ExamplePacketExtension;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;


public class Sender {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// set debugging enabled
			XMPPConnection.DEBUG_ENABLED = true;
			
			// setup connection
			XMPPConnection connection = new XMPPConnection("jgrimm19");
			connection.connect();
			
			// add packet listener for ExampleIQPacket
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet p) {
					ExampleIQPacket ep = (ExampleIQPacket)p;
					System.out.println("Recieve ExampleIQPacket ["+ep.toXML()+"]");
				}}, new PacketTypeFilter(ExampleIQPacket.class));
			
			// perform login
			connection.login("sender", "SENDER");
			
			// create and send an ExampleIQPacket 
			ExampleIQPacket p = new ExampleIQPacket();
			connection.sendPacket(p);
			
			// generate a new message and add a custom extension
			Message m = new Message();
			ExamplePacketExtension e = new ExamplePacketExtension();
			
			e.setExampleAttribut("Hello World!");
			m.addExtension(e);
			m.setTo("reciever@jgrimm19");
			
			connection.sendPacket(m);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
