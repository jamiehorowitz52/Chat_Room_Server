package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.lang.Thread;

/**
 * Jamie Horowitz
 */
public class ChatServer extends ChatWindow {

	private ClientHandler handler;
	private ArrayList<ClientHandler> clientList = new ArrayList<ClientHandler>();

	public ChatServer(){
		super();
		this.setTitle("Chat Server");
		this.setLocation(80,80);

		try {
			// Create a listening service for connections
			// at the designated port number.
			ServerSocket srv = new ServerSocket(2113);

			while (true) {
				// The method accept() blocks until a client connects.
				printMsg("Waiting for a connection");
				Socket socket = srv.accept();

				handler = new ClientHandler(socket);
				Thread thread = new Thread(handler);
				thread.start();
				clientList.add(handler);
				//start new thread and pass in handler	
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/** This innter class handles communication to/from one client. */
	class ClientHandler implements Runnable{
		private PrintWriter writer;
		private BufferedReader reader;
		public String clientName = "";

		public ClientHandler(Socket socket) {
			try {
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e){
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
				}
		}

		public void handleConnection() {
			try {
				while(true) {
					// read a message from the client
					String s = readMsg();
					sendMsg(s);
				}
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		/** Receive and display a message */
		public String readMsg() throws IOException {
			String s = reader.readLine();

			printMsg(s);
			return s;

		}
		/** Send a string */
		public void sendMsg(String s){
			for(int i = 0; i<clientList.size(); i++) {
				clientList.get(i).writer.println(s); //"hiiiii +"
			}
		}
		
		//runnable methods
		public void run()
		{
			this.handleConnection();
		}

	}

	public static void main(String args[]){
		new ChatServer();
	}
}
