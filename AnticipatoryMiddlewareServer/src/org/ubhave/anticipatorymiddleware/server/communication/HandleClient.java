package org.ubhave.anticipatorymiddleware.server.communication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * HandleClient is responsible for handling clients'connection via TCP socket.
 * It extends Thread class.
 */
class HandleClient extends Thread {
	Socket socket = null;

	/**
	 * Constructor
	 * @param (Socket) TCP socket for accepted client
	 */
	protected HandleClient(Socket socket) {
		super("HandleClientThread"); 
		this.socket = socket;
	}

	/**
	 * Runs the thread to receive data via TCP socket
	 */
	public void run() {
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				//fire the stream to the listener or filter it
				if(inputLine==null || inputLine.isEmpty()){
					System.out.println("Stream is blank");
					continue;            		
				}
				System.out.println("Inside HandleClient input line: "+inputLine);
			}
			System.out.println("Inside HandleClient complete input line: "+inputLine);
			MessageParser.run(inputLine);
			br.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}