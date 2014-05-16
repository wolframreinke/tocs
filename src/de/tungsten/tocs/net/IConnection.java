package de.tungsten.tocs.net;


/**
 * Das gemeinsame Interface aller Verbindungen, in die geschrieben und von
 * denen gelesen werden kann. Für das Schreiben und Lesen sind implementierenden
 * Klassen einige Vorgaben auferlegt, diese sind im Javadoc der ensprechenden
 * Methoden dokumentiert.
 * <p>
 * Dieses Interface wurde hauptsächlich zu Testzwecken eingeführt. Vorher 
 * gab es nur die direkte Implementierung einer Netzwerkverbindung zu einem
 * Spieler, zum Testen von einzelnen Modulen/Klassen kann mithilfe dieses
 * Interfaces beispielsweise eine Verbindung zur lokalen Konsole hergestellt
 * werden.
 * 
 * @author Wolfram
 *
 */
public interface IConnection  {

	/**
	 * Wert: 0x17
	 */
	public static final char MESSAGE_SEPARATOR = 0x17;
	public static final String MESSAGE_SEPARATOR_ESCAPE = "%0x17;";
	
	/**
	 * Sendet die gegebene <code>message</code> an das Ziel dieser Verbindung.
	 * <p>
	 * <b>Für implementierende Klassen:</b>
	 * Am Ende der Nachricht muss ein {@link #MESSAGE_SEPARATOR}- Byte 
	 * angehängt werden. Alle Bytes mit diesem Wert in der Nachricht müssen mit 
	 * {@value #MESSAGE_SEPARATOR_ESCAPE} ersetzt werden.
	 * 
	 * @param message	Die zu sendende Nachricht.
	 * @return			<code>true</code>, wenn die Nachricht erfolgreich 
	 * 					zugestellt wurde, sonst <code>false</code>.
	 */
	public boolean write( String message );
	
	/**
	 * Liest eine Nachricht vom Ziel dieser Verbindung. Je nach Implementierung
	 * kann diese Methode entweder blockieren, oder sofot <code>null</code>
	 * zurückgeben.
	 * <p>
	 * <b>Für implementierende Klassen:</b> Die Nachricht wird
	 * von einem {@link #MESSAGE_SEPARATOR}-Byte abgeschlossen. Nach
	 * erhalt der Nachricht müssen alle Zeichenketten 
	 * {@value #MESSAGE_SEPARATOR_ESCAPE} durch ein {@link #MESSAGE_SEPARATOR}
	 * -Byte ersetzt werden.
	 * 
	 * @return	Den eingelesenen String, oder <code>null</code> falls ein
	 * 			Fehler aufgetreten ist.
	 */
	public String read();
}
