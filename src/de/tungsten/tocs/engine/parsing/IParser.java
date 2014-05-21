package de.tungsten.tocs.engine.parsing;

import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Ein <code>IParser</code> parst den Befehl eines Spielers und f�hrt ihn aus. Dabei gibt
 * es f�r jeden Befehl eine eigene Implementation von <code>IParser</code>. Um die
 * Unterst�tzung neuer Befehle hinzuzuf�gen, muss so lediglich eine neue Implementation
 * erstellt werden.
 * 
 * @author tungsten
 *
 */
public interface IParser {
	
	/**
	 * Gibt die Schl�sselw�rter dieses Parsers zur�ck. Die Schl�sselw�rter werden verwendet,
	 * um festzustellen, ob dieser Parser die Eingabe des Spielers verarbeiten kann.
	 * <br><b>Beispiel:</b>
	 * Eine Implementation von <code>IParser</code> soll Befehle parsen k�nnen, die etwas
	 * mit dem Werfen von Gegenst�nden zu tun haben. Als Schl�sselw�rter k�nnten hier 
	 * "werfen", "schleudern", "schmei�en", etc verwendet werden. Wenn entschieden werden
	 * soll, ob die Eingabe des Spielers an diesen Parser �bergeben werden soll, wird 
	 * die Eingabe mit diesen Schl�sselw�rtern verglichen, sodass 
	 * "wirf den Ball" an diesen Parser weitergereicht wird,
	 * "iss ein Sandwich" allerdings nicht.
	 * 
	 * @return Die Schl�sselw�rter dieses Parsers.
	 */
	public abstract String[] getKeywords();
	
	/**
	 * Interpretiert die gegebene Eingabe des Spielers als Befehl, und f�hrt ihn dann
	 * unter Benutzung der gegebenen <code>IPlayerConnection</code> aus.
	 * 
	 * @param input			Die zu interpretierende Eingabe.
	 * @param connection	Die <code>IPlayerConnection</code> die genutzt wird, um
	 * 						den Befehl auszuf�hren.
	 */
	public abstract void parse( String input, IPlayerConnection connection );
}
