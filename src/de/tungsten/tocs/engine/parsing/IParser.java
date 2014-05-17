package de.tungsten.tocs.engine.parsing;

import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Ein <code>IParser</code> parst den Befehl eines Spielers und führt ihn aus. Dabei gibt
 * es für jeden Befehl eine eigene Implementation von <code>IParser</code>. Um die
 * Unterstützung neuer Befehle hinzuzufügen, muss so lediglich eine neue Implementation
 * erstellt werden.
 * 
 * @author Wolfram
 *
 */
public interface IParser {
	
	/**
	 * Gibt die Schlüsselwörter dieses Parsers zurück. Die Schlüsselwörter werden verwendet,
	 * um festzustellen, ob dieser Parser die Eingabe des Spielers verarbeiten kann.
	 * <br><b>Beispiel:</b>
	 * Eine Implementation von <code>IParser</code> soll Befehle parsen können, die etwas
	 * mit dem Werfen von Gegenständen zu tun haben. Als Schlüsselwörter könnten hier 
	 * "werfen", "schleudern", "schmeißen", etc verwendet werden. Wenn entschieden werden
	 * soll, ob die Eingabe des Spielers an diesen Parser übergeben werden soll, wird 
	 * die Eingabe mit diesen Schlüsselwörtern verglichen, sodass 
	 * "wirf den Ball" an diesen Parser weitergereicht wird,
	 * "iss ein Sandwich" allerdings nicht.
	 * 
	 * @return Die Schlüsselwörter dieses Parsers.
	 */
	public abstract String[] getKeywords();
	
	/**
	 * Interpretiert die gegebene Eingabe des Spielers als Befehl, und führt ihn dann
	 * unter Benutzung der gegebenen <code>IPlayerConnection</code> aus.
	 * 
	 * @param input			Die zu interpretierende Eingabe.
	 * @param connection	Die <code>IPlayerConnection</code> die genutzt wird, um
	 * 						den Befehl auszuführen.
	 */
	public abstract void parse( String input, IPlayerConnection connection );
}
