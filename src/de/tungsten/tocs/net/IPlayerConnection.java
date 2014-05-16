package de.tungsten.tocs.net;

import de.tungsten.tocs.engine.nodes.Player;

/**
 * Eine Verbindung zu einem Spieler von TOCS. Zusätzlich zum Lesen und Schreiben
 * von Nachrichten, bietet dieses Interface Funktionalität zum Zugriff auf
 * den dazugehörigen Spielerknoten.
 * <p>
 * Implementierende Klassen stellen die Brücke zwischen der Netzwerk-
 * (oder sonstigen physikalischen)-Verbindung zu einem Spieler und dem
 * Spieler-Knoten im Spiel dar.
 * 
 * @author Wolfram
 *
 */
public interface IPlayerConnection extends IConnection {
	
	/**
	 * Gibt den zu Verbindung gehörenden {@link Player} zurück.
	 * 
	 * @return
	 */
	public Player getPlayer();
}
