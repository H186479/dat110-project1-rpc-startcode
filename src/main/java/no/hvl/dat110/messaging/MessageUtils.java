package no.hvl.dat110.messaging;

import java.util.Arrays;

import no.hvl.dat110.TODO;

public class MessageUtils {

	public static final int SEGMENTSIZE = 128;

	public static int MESSAGINGPORT = 8080;
	public static String MESSAGINGHOST = "localhost";

	public static byte[] encapsulate(Message message) {
		
		byte[] segment = null;
		byte[] data;
		
		// TODO - START
		data = message.getData();
		segment = new byte[SEGMENTSIZE];
		segment[0] = (byte) data.length;
		System.arraycopy(data, 0, segment, 1, data.length);

		// encapulate/encode the payload data of the message and form a segment
		// according to the segment format for the messaging layer

		// TODO - END
		return segment;
		
	}

	public static Message decapsulate(byte[] segment) {

		Message message = null;
		// TODO - START
		int bytes = segment[0];
		byte[] data = Arrays.copyOfRange(segment, 1, 1 + bytes);
		// decapsulate segment and put received payload data into a message
		message = new Message(data);
		// TODO - END
		
		return message;
		
	}
	
}
