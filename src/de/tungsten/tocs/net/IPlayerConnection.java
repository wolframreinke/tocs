package de.tungsten.tocs.net;

import de.tungsten.tocs.engine.nodes.Player;

/**
 * Eine Verbindung zu einem Spieler von TOCS. Zus�tzlich zum Lesen und Schreiben
 * von Nachrichten, bietet dieses Interface Funktionalit�t zum Zugriff auf
 * den dazugeh�rigen Spielerknoten.
 * <p>
 * Implementierende Klassen stellen die Br�cke zwischen der Netzwerk-
 * (oder sonstigen physikalischen)-Verbindung zu einem Spieler und dem
 * Spieler-Knoten im Spiel dar.
 * 
 * @author Wolfram
 *
 */
public interface IPlayerConnection extends IConnection {
	
	/**
	 * Gibt den zu Verbindung geh�renden {@link Player} zur�ck.
	 * 
	 * @return
	 */
	public Player getPlayer();
}
