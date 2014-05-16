package de.tungsten.tocs.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.config.ConfigurationType;
import de.tungsten.tocs.engine.Lobby;
import de.tungsten.tocs.engine.LobbySubSystem;
import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.parsing.Parsers;
import de.tungsten.tocs.menus.MenuSequence;
import de.tungsten.tocs.menus.Prompt;
import de.tungsten.tocs.menus.ShortcutSelector;

// Noch keine Dokumentation an einigen Stellen, hier wird sich 
// wahrscheinlich noch einiges ändern.

public class Connection extends Thread implements IPlayerConnection, Closeable {

	public	static final String	CONFIG_RETRIES	= "connectionRetries";
	private static final int 	DEFAULT_RETRIES	= 5;
	
	public	static final String	CONFIG_TIMEOUT	= "connectionTimeout";
	private static final int 	DEFAULT_TIMEOUT	= 500;
	
	private Player player;
	private BufferedReader in;
	private BufferedWriter out;
	
	private final int retries;
	private final int timeout;
	
	public Connection( Socket socket ) {
		
		// Configuration Objekt referenzieren
		Configuration config = Configuration.getInstance();
		
		// retries und timeout aus den Konfigurationen auslesen
		retries = (int) config.getValue(
				CONFIG_RETRIES, 
				ConfigurationType.INTEGER, 
				DEFAULT_RETRIES );
		
		timeout = (int) config.getValue( 
				CONFIG_TIMEOUT, 
				ConfigurationType.INTEGER, 
				DEFAULT_TIMEOUT );
		
		try {
			// Zum lesen und Schreiben werden BufferedReader/-Writer verwendet
			in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			out = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );
			
			// Thread gleich starten
			start();
			
		} catch (IOException e) {}
	}
	
	@Override
	public void run() {
		
		// Den Spieler nach Namen und Team fragen
		initialize();
		
		// Den Spieler in der Lobby beschäftigen
		sendToLobby();
		
		// Spieler wurde gespawnt, seine Eingaben müssen geparst werden
		Parsers parsers = Parsers.getInstance();
		
		while ( true ) {
			
			this.write( "\n\n> " );
			
			String input;
			do {
				input = this.read();
				
			} while ( input == null );
			
			// Weiterleiten der Eingabe an das Instruction Parsing System
			parsers.parse( input, this );
		}
	}
	
	private void initialize() {

		// Erst nach Namen, dann nach Team fragen
		MenuSequence sequence = new MenuSequence()
					.addMenu( new Prompt( "Choose your nickname!" ) )
					.addMenu( new ShortcutSelector( "Which team do you want to join?" )
							.addOption( 't', "terrorists", Team.TERRORISTS )
							.addOption( 'c', "counter-terrorists", Team.COUNTER_TERRORISTS )
							.addOption( 'a', "auto-assign", null ) );
					
		List<?> results = (List<?>) sequence.display( this );
		
		// Ergebnisse der MenuSequence auslesen. Die casts sind sicher,
		// TODO eventuell ein paar assertions hinzufügen
		String 	nickname 	= (String) results.get( 0 );
		Team 	team 		= 	(Team) results.get( 1 );
		
		// TODO Das gefällt mir noch nicht.
		if ( team == null )
			team = Team.assignAutomaticly();
		Team.incrementTeam( team );
		
		// Mit diesen Daten kann das Spieler-Objekt erstellt werden.
		this.player = new Player( nickname, team );
	}
	
	private void sendToLobby() {
		
		// Ab in die Lobby
		LobbySubSystem lobby = Lobby.getInstance().assignLobbySubSystem( this );
		lobby.operate( this );
		
		// Wenn die operate methode zurückkehrt, darf der Spieler spawnen
		TOCSServer.getMap().spawn( this );
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public boolean write( String message ) {
		
		// Diese Zeilen existieren wegen den Anforderungen in IConnection
		message.replace( String.valueOf(IConnection.MESSAGE_SEPARATOR), IConnection.MESSAGE_SEPARATOR_ESCAPE );
		message += IConnection.MESSAGE_SEPARATOR;
		
		// retries mal probieren
		for (int i = 0; i < retries; i++) {
			try {
				
				out.write( message );
				out.flush();
				
				return true;
				
			} catch (IOException ioExcp) {

				try {
					// timeout lang schlafen
					Thread.sleep( timeout );
				} catch (InterruptedException interExcp) {}
			}
		}
		
		return false;
	}
	
	@Override
	public String read() {
			
		for (int i = 0; i < retries; i++) {
			try {
				
				String result = "";
				
				// message zeichenweise einlesen
				char c;
				while ( (c = (char) in.read()) != IConnection.MESSAGE_SEPARATOR ) 
					result += c;
				
				// Das ist teil des Spezifikation in IConnection
				return result.replace( IConnection.MESSAGE_SEPARATOR_ESCAPE, String.valueOf(IConnection.MESSAGE_SEPARATOR) );
				
			} catch ( IOException ioExc ) {
				
				try {
					Thread.sleep( timeout );
				} catch (InterruptedException e) {}
			}
		}
		return null;
	}
	
	@Override
	public void close() {
		// ?
	}
}
