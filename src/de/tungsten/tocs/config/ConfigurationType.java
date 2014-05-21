package de.tungsten.tocs.config;

/**
 * Der Typ eines Konfigurations-Eintrages. Infrage kommen:
 * <ul>
 * <li> {@link #INTEGER}
 * <li> {@link #NUMERICAL}
 * <li> {@link #STRING}
 * </ul>
 * <p>
 * Dieser <code>enum</code> wird eventuell noch erweitert.
 * 
 * @author tungsten
 *
 */
public enum ConfigurationType {

	/**
	 * Eine Ganzzahl. Hier werden alle werte akzeptiert, die sich mittels
	 * <code>Integer.parseInt( ... )</code> in einen integer umwandeln lassen.
	 */
	INTEGER,
	
	/**
	 * Eine Komma-Zahl. Hier werden alle Werte akzeptiert, die sich mittels
	 * <code>Double.parseDouble( ... )</code> in einen double umwandeln lassen.
	 */
	NUMERICAL,
	
	/**
	 * Eine beliebige Zeichenkette. Hier werden alle Werte, die kein '=' und
	 * kein '\n' enthalten akzeptiert.
	 */
	STRING;
}
