package socket;

import gui.Messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

/**
 * A connected main.java.socket that is used to communicate back and forth over a network.
 */
public class Socket {

	protected final int BASE_PORT = 55550;
	
	protected boolean isCleanlyClosed;
	
	protected java.net.Socket socket;
	protected BufferedReader reader;
	protected PrintWriter writer;
		
	/**
	 * Creates a new empty main.java.socket.
	 */
	protected Socket() {
		socket = null;
		reader = null;
		writer = null;
		isCleanlyClosed = true;
	}
	
	/**
	 * Returns an main.java.action message received from the main.java.socket.
	 * @return an main.java.action message received from the main.java.socket
	 */
	public String getActionMessage() {
		try {
			return reader.readLine();
		}
		catch (SocketException ex) {}
		catch (IOException ex) {
			Messenger.error(ex, ex.getMessage(), "Socket Read Error");
		}
		return null;		
	}
	
	/**
	 * Sends an main.java.action message across the main.java.socket.
	 * @param actionMessage main.java.action message to send
	 */
	public void sendActionMessage(String actionMessage) {
		writer.println(actionMessage);
		writer.flush();
	}	
	
	/**
	 * Returns whether or not this main.java.socket has been closed.
	 * @return true if the main.java.socket has not been initialized or connected or if it has been closed; false, otherwise
	 */
	public boolean isCleanlyClosed() {
		return (isCleanlyClosed);
	}
	
	/**
	 * Closes and cleans up the Socket and the tools it uses.
	 */
	public void close() {
		isCleanlyClosed = true;
		try { if (socket != null) socket.close(); } catch (IOException ex) {}
		try { if (reader != null) reader.close(); } catch (IOException ex) {}
		if (writer != null) writer.close();
	}
}
