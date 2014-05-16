package de.tungsten.tocs;

/**
 * Ein Log-Level das zum Filtern von Log-Nachrichten verwendet wird. Die
 * Members dieses <code>enum</code>s sind (geordnet nach Priorit�t):
 * <ol>
 * <li> {@link #FATAL}
 * <li> {@link #ERROR}
 * <li> {@link #WARNING}
 * <li> {@link #INFO}
 * <li> {@link #DEBUG}
 * </ol>
 * 
 * @author Wolfram
 *
 */
public enum LogLevel {
	
	/**
	 *  Dieses <code>LogLevel</code> hat die Priorit�t 4 und ist damit das
	 *  LogLevel, das als erstes ausgefiltert wird. Nachrichten mit diesem
	 *  Level sind nur zu Debugging-Zwecken in den Programmfluss eingeflochten
	 *  worden.
	 *  <p>
	 *  Die textuelle Entsprechung dieses Levels in Log-Dateien ist
	 *  <code> "[ DEBUG ]" </code>.
	 */
	DEBUG	( 4, "[ DEBUG ]" ),
	
	/**
	 * Dieses <code>LogLevel</code> hat die Priorit�t 3 und wird damit nach
	 * {@link #DEBUG} am ehesten ausgefiltert. Nachrichten dieser Priorit�t 
	 * beinhalten allgemeine Informationen, z.B. "Server wurde gestartet",
	 * die keine weitere Aufmerksamkeit des Benutzers erfordern.
	 * <p>
	 * Die textuelle Entsprechung dieses Levels in Log-Dateien ist
	 * <code> "[ INFO  ]" </code>.
	 */
	INFO	( 3, "[ INFO  ]" ),
	
	/**
	 * Dieses <code>LogLevel</code> hat die Priorit�t 2 und wird damit nach
	 * {@link #INFO} am ehesten ausgefiltert. Nachrichten dieser Priorit�t
	 * beinhalten Warnungen, die wenn sie ignoriert werden zu einem
	 * Fehler f�hren k�nnen.
	 * <p>
	 * Die textuelle Entsprechung dieses Levels in Log-Dateien ist
	 * <code> "[ WARN  ]" </code>.
	 */
	WARNING	( 2, "[ WARN  ]" ),
	
	/**
	 * Dieses <code>LogLevel</code> hat die Priorit�t 1 und wird damit nach
	 * {@link #WARNING} am ehesten ausgefiltert. Nachrichten dieser Priorit�t
	 * beinhalten Fehlermeldungen, die den Programablauf grundlegend �ndern,
	 * die aber grunds�tzlich keinen Neustart des Programms erfordern.
	 * <p>
	 * Die textuelle Entsprechung dieses Levels in Log-Dateien ist
	 * <code> "[ ERROR ]" </code>.
	 */
	ERROR	( 1, "[ ERROR ]" ),
	
	/**
	 * Dieses <code>LogLevel</code> hat die Priorit�t 0 und ist damit das
	 * Level mit der h�chsten Priorit�t. Wenn dieses Level ausgefiltert wird,
	 * werden keine Nachrichten mehr angezeigt.
	 * <br>
	 * Nachrichten dieser Priorit�t enthalten Meldungen �ber schwerwiegende
	 * Fehler, auf die das Programm nicht mehr reagieren kann. Ein Neustart wird
	 * erforderlich.
	 * <p>
	 * Die textuelle Entsprechung dieses Levels in Log-Dateien ist
	 * <code> "[ FATAL ]" </code>.
	 */
	FATAL	( 0, "[ FATAL ]" );
	
	// integer Entsprechung und textuelle Entsprechung
	private int priority;
	private String representation;
	
	LogLevel( int level, String representation ) {
		this.priority = level;
		this.representation = representation;
	}
	
	/**
	 * Wandelt den gegebenen integer in ein <code>LogLevel</code> derselben
	 * Priorit�t um. Wenn kein entsprechendes Level gefunden werden konnte,
	 * wird {@link #DEBUG} zur�ckgegeben.
	 * 
	 * @param priority	Die Priorit�t des gew�nschten Log-Levels.
	 * @return			Das dazugeh�rige Log-Level.
	 */
	public static LogLevel fromLevel( int level ) {
		for (LogLevel l : LogLevel.values()) {
			if ( l.priority == level ) return l;
		}
		return DEBUG;
	}
	
	@Override
	public String toString() {
		return representation;
	}
	
	/**
	 * Gibt die Priorit�t dieses Log-Levels zur�ck.
	 * 
	 * @see #FATAL
	 * @see #ERROR
	 * @see #WARNING
	 * @see #INFO
	 * @see #DEBUG
	 * 
	 * @return	Die Priorit�t des Log-Levels.
	 */
	public int getPriority() {
		return priority;
	}
}
