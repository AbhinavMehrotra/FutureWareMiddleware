package org.ubhave.anticipatorymiddleware.server.communication;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * TCPServer class is responsible to establish a TCP listener.
 * This listener listens to the client connections.
 * It extends Thread class.
 */
public class TCPServer extends Thread{

	private final int server_port;
	
	private boolean running;
	
	ServerSocket serverSocket =null;
	
	public TCPServer(int server_port) {
		this.server_port = server_port;
	}
	

	public void stopServer(){
		this.running = false;
	}
	
	/**
	 * Runs with the thread.
	 */
	@Override
    public void run() {
        super.run(); 
        running =true;
 
            try {
				InetAddress serverAddr = InetAddress.getLocalHost();
                System.out.println("IP..."+serverAddr );
				serverSocket = new ServerSocket(this.server_port);
                System.out.println("Port..."+this.server_port );
			} catch (IOException e1) {
                System.out.println(e1.toString());
                System.exit(-1);
			}
            
            while(running)
				try {
					new HandleClient(serverSocket.accept()).start();
				} catch (IOException e) {
	                System.out.println(e.toString());
				}
            	try {
					serverSocket.close();
				} catch (IOException e) {
	                System.out.println(e.toString());
				}
 
         
    }

}
