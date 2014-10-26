package org.ubhave.anticipatorymiddleware.communication;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Creates a TCS socket to send data to the server.
 * TCP socket opened immediately closes after sending the data.
 */
public class TCPClient{

	private final String TAG = "AnticipatoryManager";
	public static  String SERVERIP; 
	public static  int SERVERPORT;
	private Socket socket;
	private PrintWriter out;
	private static TCPClient instance;

	/**
	 * Returns the instance of TCPClient.
	 * @param serverIp IP address of the server
	 * @param port Port number of the server
	 * @return (Object) Instance of TCPClient
	 */
	protected static TCPClient getInstance(String serverIp, int port){
		if(instance==null){
			instance=new TCPClient(serverIp, port);
		}
		return instance;
	}
	
	private TCPClient(String serverIp, int port) {
		SERVERIP=serverIp;
		SERVERPORT=port;
	}

	/**
	 * Sends the stream to the server
	 * @param message stream of sensor data
	 */
	protected void startSending(String message){
		Log.i(TAG, "Trying to send: "+message);
		new SendData().execute(message);
	}

	/**
	 * Sends data to the server via TCP socket. It extends AsyncTask to perform the work in background.
	 * This class is executed by startSending(String).
	 */
	private class SendData extends AsyncTask<String, Void, Void>{

		protected Void doInBackground(String... data) {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVERIP);
				Log.e("TCP Client", "Connecting to the server..."+serverAddr +" at PORT: "+ SERVERPORT);
				socket = new Socket(serverAddr, SERVERPORT);

				try {
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
					if (out != null && !out.checkError()) {
						System.out.println("send data: "+data[0]);
						out.println(data[0]); //stream 
						out.flush();
					}
					Log.i("TCP Client", "Data Sent.");

				} catch (Exception e) {
					Log.e("TCP", "Error"+e.toString());

				} finally {
					socket.close();  
					Log.e("TCP", "Closed finally");
				}
			} catch (Exception e) {
				Log.e("TCP", "C: Error", e);
			}
			return null;
		}
		
	}

}