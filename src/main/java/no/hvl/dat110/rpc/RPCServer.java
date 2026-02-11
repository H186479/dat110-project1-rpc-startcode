package no.hvl.dat110.rpc;

import java.util.HashMap;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessagingServer;

public class RPCServer {

	private MessagingServer msgserver;
	private MessageConnection connection;
	
	// hashmap to register RPC methods which are required to extend RPCRemoteImpl
	// the key in the hashmap is the RPC identifier of the method
	private HashMap<Byte,RPCRemoteImpl> services;
	
	public RPCServer(int port) {
		
		this.msgserver = new MessagingServer(port);
		this.services = new HashMap<Byte,RPCRemoteImpl>();
		
	}

	public void run() {

		RPCRemoteImpl rpcstop = new RPCServerStopImpl(RPCCommon.RPIDSTOP, this);
		register(RPCCommon.RPIDSTOP, rpcstop);

		System.out.println("RPC SERVER RUN - Services: " + services.size());

		boolean stop = false;


		while (!stop) {

			connection = msgserver.accept();
			System.out.println("RPC SERVER ACCEPTED");

			if (connection != null) {
				Message requestmsg, replymsg;

				while ((requestmsg = connection.receive()) != null) {
					byte[] payload = requestmsg.getData();
					byte rpcid = payload[0];

					// 1. Utfør metoden hvis den finnes
					if (services.containsKey(rpcid)) {
						byte[] param = RPCUtils.decapsulate(payload);
						byte[] result = services.get(rpcid).invoke(param);

						// 2. Send SVAR tilbake til klienten (Viktig!)
						byte[] reply = RPCUtils.encapsulate(rpcid, result);
						connection.send(new Message(reply));
					}

					// 3. Sjekk stopp ETTER at svaret er sendt
					if (rpcid == RPCCommon.RPIDSTOP) {
						stop = true;
						break;
					}
				}
// 4. Lukk koblingen HER slik at neste test kan kalle accept()
				connection.close();
			}
		}
	}
	
	// used by server side method implementations to register themselves in the RPC server
	public void register(byte rpcid, RPCRemoteImpl impl) {
		services.put(rpcid, impl);
	}
	
	public void stop() {

		if (connection != null) {
			connection.close();
		} else {
			System.out.println("RPCServer.stop - connection was null");
		}
		
		if (msgserver != null) {
			msgserver.stop();
		} else {
			System.out.println("RPCServer.stop - msgserver was null");
		}
		
	}
}
