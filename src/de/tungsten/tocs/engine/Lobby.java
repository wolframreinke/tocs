package de.tungsten.tocs.engine;

import java.util.HashSet;
import java.util.Set;

import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Die Lobby ist der "Ort", an dem alle Spieler darauf warten, dass sie spawnen dürfen. In der Wartezeit können sie
 * Waffen kaufen und ihren Skin anpassen. Da in TOCS jeder Spieler seinen eigenen Thread hat, stemmt diese Klasse nur
 * den Verwaltungsaufwand, die Kommunikation mit dem Spieler wird von einzelnen {@link LobbySubSystem}s erledigt.
 * <p>
 * Wenn ein Spieler im <code>LobbySubSystem</code> die Option <code>Ready</code> gewählt hat, um mitzuteilen, dass er
 * bereit zum spawnen ist, wird diese Klasse benachrichtigt (via {@link #update()}). Wenn nun alle Spieler bereit sind,
 * werden sie gespawnt, ansonsten wird noch gewartet.
 * <p>
 * Mit dem zweiten Spieler, der sich in die Lobby begibt, wird ein 120s Countdown gestartet. Wenn auf 0 heruntergezählt
 * wurde, werden alle Spieler, die bereits ready sind gespawnt. Alle anderen Spieer werden sofot gespawnt, wenn sie selbst
 * bereit sind.
 * <p>
 * Diese Klasse ist als Singleton implementiert, d.h. alle Objekte teilen sich 
 * dieselbe Instanz von <code>Lobby</code>. Auf diese Instanz kann mit
 * {@link #getInstance()} erreicht werden.
 * 
 * @author reinke.wolfram-tit13
 *
 */
public class Lobby  {

	/**
	 * Der Countdown wird in einem eigenen Thread erledigt. Diese Klasse zählt von einem gegebenen Wert herunter auf
	 * 0. Dabei wird die dazugehörige <code>Lobby</code> in folgenden Intervallen über die noch verbleibende Zeit
	 * benachrichtigt:
	 * <ul>
	 * <li> Mindestens alle 60 Sekunden, und
	 * <li> Alle 10 Sekunden, wenn die verbleibende Zeit kleiner als 60 Sekunden ist, und
	 * <li> Jede Sekunde, wenn die verbleibende Zeit kleiner als 10 Sekunden ist.
	 * </ul>
	 * 
	 * @author reinke.wolfram-tit13
	 *
	 */
	private class CountdownTimer extends Thread {
		
		private int seconds;
		private Lobby lobby;
		
		public CountdownTimer( Lobby lobby, int seconds ) {
			super( "CountdownTimer" );
			
			this.lobby = lobby;
			this.seconds = seconds;
		}
		
		@Override
		public void run() {
			
			// bis null runterzählen
			while ( !isInterrupted() && seconds >= 0 ) {
				
				// eine Sekunde schlafen
				try {
					Thread.sleep( 1000 );
				} catch ( InterruptedException e ) {
					break;
				}
				
				// Lobby benachrichten, wenn nötig
				seconds--;
				if ( seconds % 60 == 0 
						|| (seconds % 10 == 0 && seconds < 60) 
						|| seconds < 10 )
					lobby.checkCountdown( seconds );
				
			}
		}
	}
	
	private static Lobby instance = null;
	private Lobby() {}
	
	/**
	 * Gibt die einzige existierende Instanz dieser Klasse zur�ck. Die Instanz
	 * wird erst erstellt, wenn sie zum ersten Mal angefordert wird.
	 * 
	 * @return	Die einzige existierende Instanz von <code>Lobby</code>.
	 */
	public static Lobby getInstance() {
		if ( instance == null )
			instance = new Lobby();
		
		return instance;
	}
	
	/**
	 * Die <code>IPlayerConnection</code>s die sich gerade in der Lobby befinden.
	 */
	private Set<IPlayerConnection> connections = new HashSet<IPlayerConnection>();
	
	/**
	 * Die zu den {@link #connections} passenden <code>LobbySubSystem</code>s.
	 */
	private Set<LobbySubSystem> subSystems = new HashSet<LobbySubSystem>();
	
	/**
	 * Gibt an, ob das Spiel gerade läuft (<code>true</code>), oder ob sich alle Spieler noch in der Lobby befinden
	 * (<code>false</code>).
	 */
	private boolean gameRunning = false;
	
	/**
	 * Fügt die gegebene <code>IPlayerConnection</code> zur <code>Lobby</code> hinzu. Der Spieler wird dabei noch nicht
	 * an das entsprechende <code>LobbySubSystem</code> weitergeleitet. Dieses wird nur passend initialisiert und dann
	 * zurückgegeben.
	 * <p>
	 * Wenn der zweite Spieler die Lobby betritt, wird der Countdown von 120s gestartet, nachdem alle Spieler automatisch
	 * gespawnt werden.
	 * 
	 * @return Das <code>LobbySubSystem</code> für die gegebene <code>IPlayerConnection</code>. Die <code>operate</code>-
	 * Methode des SubSystems wurde noch nicht aufgerufen.
	 */
	public synchronized LobbySubSystem assignLobbySubSystem( IPlayerConnection connection ) {
		
		connections.add( connection );
		
		// LobbySubSystem erstellen
		LobbySubSystem subSystem = new LobbySubSystem( this );
		subSystems.add( subSystem );
		
		// Countdown starten wenn es der zweite Spieler ist
		if ( subSystems.size() == 2 )
			new CountdownTimer( this, 120 ).start();
		
		return subSystem;
		
	}
	
	/**
	 * Teilt dieser <code>Lobby</code> mit, dass ein Spieler die Option <code>Ready</code> gewählt hat.
	 * Wenn alle Spieler diese Option gewählt haben, werden sie gespawnt. Das spawnen geschieht jedoch nicht in
	 * dieser Methode, sondern muss selbst erledigt werden.
	 * 
	 */
	public synchronized void update() {
		
		// Wenn schon alle gespawnt sind, muss nichts gemacht werden.
		if ( gameRunning ) return;
		
		// Rausfinden ob alle Spieler ready sind.
		boolean allReady = true;
		for (LobbySubSystem current : subSystems) {
			if ( !current.isReady() ) allReady = false;
		}
		
		// Alle Spieler releasen
		if ( allReady ) {
			gameRunning = true;
			
			for (LobbySubSystem current : subSystems) {
				current.releasePlayer();
				subSystems.remove( current );
			}

			for (IPlayerConnection current : connections) {
				connections.remove( current );
			}
		}
	}
	
	/**
	 * Teilt der <code>Lobby</code> mit, dass wieder ein Intervall beim <code>CountdownTimer</code> abgelaufen
	 * ist. Es wird eine entsprechende Meldung ausgegeben und wenn der Countdown abgeschlossen ist, können die 
	 * Spieler gespawnt werden.
	 * 
	 * @param seconds
	 */
	private synchronized void checkCountdown( int seconds ) {
		
		// Mitteilung
		for (IPlayerConnection current : connections) {
			current.write( "Game starts in " + seconds + " seconds." );
		}
		
		// Wenn schon alle gespawnt sind muss nichts geschehen, sonst
		// nur wenn der Countdown fertig ist.
		if ( seconds == 0 && !gameRunning ) {
			gameRunning = true;
			
			for (LobbySubSystem current : subSystems) {
				current.releasePlayer();
				subSystems.remove( current );
			}
			
			for (IPlayerConnection current : connections) {
				connections.remove( current );
			}
		}
	}
	
	/**
	 * Gibt zurück, ob das Spiel bereits gestartet wurde (d.h. ob die Spieler bereits die Erlaubnis zu spawnen haben).
	 * 
	 * @return <code>true</code>, wenn das Spiel lauft, und die Spieler nach belieben spawnen dürfen, sonst 
	 * <code>false</code>.
	 */
	public boolean isGameRunning() {
		return gameRunning;
	}
	
	/**
	 * Legt fest, ob die Spieler spawnen dürfen (d.h. ob das Spiel bereits gestartet wurde).
	 * 
	 * @param value <code>true</code>, um das Spiel zu starten, und dem Spielern die Erlaubnis zu geben zu spawnen,
	 * sonst <code>false</code>.
	 */
	public void setGameRunning( boolean value ) {
		gameRunning = value;
	}
}
