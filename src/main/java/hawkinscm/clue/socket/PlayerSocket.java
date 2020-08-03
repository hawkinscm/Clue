package hawkinscm.clue.socket;

import hawkinscm.clue.gui.Messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketException;

import hawkinscm.clue.model.Player;

/**
 * A participant-connected socket that is used to communicate back and forth between the host and a participating network player.
 */
public class PlayerSocket extends Socket {

	private ServerSocket serverSocket;
	private Player player;
	
	/**
	 * Creates a new empty, unconnected Player Socket.
	 * @param portIndex index of the player/port which must be between 0 and 9 to create an open, forwarded port
	 */
	public PlayerSocket(int portIndex) {
		player = null;
		try {
			isCleanlyClosed = true;
			if (portIndex < 0 || portIndex > 9)
				throw new SocketException("Internal error: playerIndex was " + portIndex + " and needs to be between 0 and 9 to create a forwarded port.");
			serverSocket = new ServerSocket(BASE_PORT + portIndex);
			isCleanlyClosed = false;
		}
		catch (BindException ex) {
			String message = "<html> Another application is using the desired socket: " + (BASE_PORT + portIndex) + ". <br>";
			message += " This can also be caused by having more than one CLUE game hosting on your machine or network. </html>";
			Messenger.error(message, "Socket Error");
		}
		catch (IOException ex) {
			Messenger.error(ex, ex.getMessage(), "Socket Error");
		}				
	}
	
	/**
	 * Returns the player associated with this Player Socket.
	 * @return the player associated with this Player Socket
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Sets the player associated with this Player Socket.
	 * @param player player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Returns the connection socket port number used by this Player Socket.
	 * @return the connection socket port number used by this Player Socket
	 */
	public int getPortIndex() {
		if (serverSocket == null)
			return -1;
		return serverSocket.getLocalPort() - BASE_PORT;
	}
	
	/**
	 * Waits for a connection from the participating client and returns the client's player name.
	 * @return the client's player name or null on error
	 */
	public String connect(String defaultName) {
		try {
			serverSocket.setSoTimeout(0);
			socket = serverSocket.accept();
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			writer.println(defaultName);
			writer.flush();
			return reader.readLine();
		} 
		catch (IOException ex) {
			if (ex instanceof SocketException && ex.getMessage().equals("socket closed"))
				return null;
			
			Messenger.error(ex, ex.getMessage(), "Socket Error");
		}
		return null;
	}
	
	/**
	 * Closes and reconnects the socket.
	 * @throws IOException on any socket/IO error
	 */
	public void reconnect() throws IOException {
		if (socket != null)
			try { socket.close(); } catch (IOException ex) {}
		if (serverSocket == null || serverSocket.isClosed())
			serverSocket = new ServerSocket(serverSocket.getLocalPort());
		serverSocket.setSoTimeout(30000);
		socket = serverSocket.accept();
		isCleanlyClosed = false;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		reader.readLine();
	}
	
	/**
	 * Returns whether or not the server socket is connected.
	 * @return true if this socket is connected; false otherwise
	 */
	public boolean isConnected() {
		return (socket != null && socket.isConnected());
	}
	
	@Override
	public void close() {
		try { if (serverSocket != null) serverSocket.close(); } catch (IOException ex) {}
		super.close();
	}
}
