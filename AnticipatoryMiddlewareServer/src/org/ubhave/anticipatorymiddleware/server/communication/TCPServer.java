/*******************************************************************************
 *
 * FutureWare Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, a.mehrotra@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
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
