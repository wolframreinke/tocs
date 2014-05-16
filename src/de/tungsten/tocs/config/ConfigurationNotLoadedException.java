package de.tungsten.tocs.config;

/**
 * Diese Exception wird geworfen, wenn die Konfigurations-Datei nicht geladen
 * wurde, und ein Benutzer trotzdem versucht, auf die Konfigurations-Einträge
 * zuzugreifen.
 * <p>
 * Als Superklasse wurde <code>RuntimeException</code> gewählt, da im Spiel
 * das Laden der Konfigurations-Datei zentral geschieht, und deshalb nicht
 * jeder Nutzer von {@link Configuration} diese Exception abfangen müssen
 * soll.
 * 
 * @author Wolfram
 *
 */
@SuppressWarnings("serial")
public class ConfigurationNotLoadedException extends RuntimeException {

	// Diese Klasse existiert nur, weil ConfigurationNotLoadedException
	// eindeutig sagt was los ist, sonst gibts keine Zusatzfunktionen.
	public ConfigurationNotLoadedException() {
		super( "Configuration has not been loaded before first access." );
	}
}
