package de.tungsten.tocs.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.tungsten.tocs.LogLevel;
import de.tungsten.tocs.Logger;

/*
 * Syntax der Konfigurations-Datei:
 * 
 * <key1> = <value1>\n
 * <key2> = <value2>\n
 * ...
 * 
 * Leerzeichen werden automatisch entfernt. Die einzelnen Eintr�g sind mit
 * einem Zeilenumbruch (\n) zu spearieren.
 * 
 */

/**
 * L�dt Einstellungen aus einer Konfigurationsdatei und stellt sie dann
 * typsicher bereit. Syntaktische und semantische Fehler resultieren in einem
 * <code>WARNING</code> in den Logfiles, beeintr�chtigen aber nicht andere
 * Konfigurationen.
 * <p>
 * Diese Klasse wurde als Singleton implementiert, die einzige Instanz kann
 * �ber {@link #getInstance()} referenziert werden.
 * <p>
 * Zu Testzwecken steht die Methode {@link #useDefaultValues()} bereit. Nach
 * ihrem Aufruf werden die Einstellungen nicht mehr aus der Konfigurationsdatei
 * geladen, sonder direkt die Standardwerte zur�ckgegeben.
 * 
 * @author tungsten
 *
 */
public class Configuration {
	
	public static final String LOG_NAME	= "(CORE) Configuration";
	
	/**
	 * Die Eintrage aus der Konfigurations-Datei. Diese Map wird beim Aufruf
	 * von {@link #loadFromFile(String)} gef�llt und sonst nicht ver�ndert.
	 */
	private Map<String, String> values = null;
	
	/**
	 * Dieses Flag gibt an, ob die Konfigurations-Eintr�ge aus der Datei
	 * verwendet werden, oder ob immer die Default-Werte 
	 * zur�ckgegeben werden sollen.
	 */
	private boolean useDefaultValues = false;
	
	// Singleton implementation
	
	private static Configuration instance = null;
	private Configuration() {}
	
	/**
	 * Gibt die einzige existierende Instanz dieser Klasse zur�ck. Dabei
	 * wird Lazy Initialization verwendet, die Instanz wird als erst erstellt,
	 * wenn sie zum ersten Mal gebraucht wird.
	 * 
	 * @return Die einzige Instanz dieser Klasse.
	 */
	public static Configuration getInstance() {
		if ( instance == null ) 
			instance = new Configuration();
		
		return instance;
	}
	
	/**
	 * Gibt den zum gegebenen <code>key</code> passenden Wert zur�ck. Wenn
	 * der Wert nicht in der Konfigurations-Datei gefunden werden konnte, oder
	 * {@link #useDefaultValues} den Wert <code>true</code> hat, wird der 
	 * gegebene <code>defaultValue</code> verwendet.
	 * <p>
	 * Mit Hilfe des gegebenen {@link IConfigurationType}s wird �berpr�ft,
	 * ob der Konfigurations-Eintrag den richtigen Typ hat. So kann im Falle
	 * eines Typ-Fehlers der <code>defaultValue</code> zur�ckgegeben werden.
	 * Sollen keine Typ�berpr�fungen durchgef�hrt werden, muss f�r 
	 * <code>type</code> <code>null</code> �bergeben werden.
	 * 
	 * @param key			Der Name des Konfigurations-Eintrages.	
	 * @param type			Eine Implementation von <code>IConfigurationType</code>,
	 * 						um semantische Fehler abzufangen. Um die Typpr�fung
	 * 						abzustellen, kann <code>null</code> �bergeben werden.
	 * @param defaultValue	Dieser Wert wird zur�ckgegeben, wenn der gew�nschte
	 * 						und der tats�chliche Typ nicht �bereinstimmen,
	 * 						wenn kein entsprechender Eintrag gefunden wurde
	 * 						oder wenn {@link #useDefaultValues} auf
	 * 						<code>true</code> gesetzt wurde.
	 * @return				Den zum <code>key</code> passenden Eintrag aus 
	 * 						der Konfigurations-Datei, oder den <code>
	 * 						defaultValue</code> (siehe oben).
	 */
	public Object getValue( String key, IConfigurationType type, Object defaultValue ) {
		
		if ( useDefaultValues )
			return defaultValue;
		
		// Wenn die Konfigurationsdatei noch nicht geladen wurde, wird
		// eine Exception geworfen.
		if ( values == null ) 
			throw new ConfigurationNotLoadedException();
		
		String value = values.get( key );
		
		if ( value == null ) {
			// Wert existiert nicht in der HashMap
			return defaultValue;
		}
		else {

			if ( type == null )
				return value;
			
			Object result = type.parse( value );
			
			if ( result == null ) {
				// Semantischer Fehler: Typen stimmen nicht �berein.
				Logger.getInstance().log( LogLevel.WARNING, LOG_NAME, "Semantic error in configuration file: \"" + key + "\" must have the type " + type + ".");
				return defaultValue;
			}
			else {
				
				return result;
			}
		}
	}
	
	/**
	 * Wenn diese Methode aufgerufen wird, werden nicht die Daten aus einer
	 * Konfigurations-Datei als R�ckgabewert von 
	 * {@link #getValue(String, ConfigurationType, Object)} verwendet, sondern
	 * die entsprechenden Default-Werte.
	 * <p>
	 * Nach Aufruf dieser Methode, muss auch {@link #loadFromFile(String)} nicht
	 * mehr aufgerufen werden, damit <code>getValue</code> wie gew�nscht
	 * funktioniert.
	 */
	public void useDefaultValues() {
		useDefaultValues = true;
	}
	
	/**
	 * L�dt alle Konfigurations-Eintr�ge aus der Datei an dem gegebenen
	 * Pfad (<code>configFilePath</code>), damit auf sie per
	 * {@link #getValue(String, ConfigurationType, Object)} zugegriffen werden
	 * kann.
	 * <p>
	 * Wenn der Parameter <code>null</code> ist, werden keine Daten geladen,
	 * allerdings wird die Map zum Speichern der Eintr�ge initialisiert. Daher
	 * wird nun in <code>getValue</code> der entsprechende Default-Wert
	 * zur�ckgegen. Der gleiche Effekt kann durch einen Aufruf an
	 * {@link #useDefaultValues()} erreicht werden.
	 * <p>
	 * Sollte keine passende Datei existieren, wird ein WARNING in die
	 * L�gfiles geschrieben, allerdings werden keine Werte in die Map
	 * geschrieben.
	 * <p>
	 * Wenn die Datei syntaktische Fehler enth�lt (Syntax siehe Anfang dieser
	 * Datei), wird ein WARNING in die L�gfiles geschrieben, und der n�chste
	 * Eintrag interpretiert.
	 * 
	 * @param configFilePath
	 */
	public void loadFromFile( String configFilePath ) {
		
		values = new HashMap<String, String>();
		
		if ( configFilePath == null ) return;
		
		File configFile = new File( configFilePath );
		try {
			
			Logger.getInstance().log( LogLevel.INFO, LOG_NAME, "Loading configuration file \"" + configFilePath + "\"." );	
			Scanner fileScanner;

			fileScanner = new Scanner( configFile );
				

			int line = 1;
			while ( fileScanner.hasNextLine() ) {
				
				String configLine = fileScanner.nextLine();
				Logger.getInstance().log( LogLevel.DEBUG, LOG_NAME, "Read configuration entry: \"" + configLine.replace( "\t", "") + "\"." );
				
				String[] tokens = configLine.split( "=" );
				
				if ( tokens.length == 2 ) {
				
					values.put( tokens[0].trim() , tokens[1].trim() );
					
				} else Logger.getInstance().log( LogLevel.WARNING, LOG_NAME, "Syntax error in configuration file at line " + line + "." );
			
				line++;
			}
			
			fileScanner.close();
		} 
		catch ( FileNotFoundException e ) {
			
			Logger.getInstance().log( LogLevel.WARNING, LOG_NAME, "No configuration file at " + configFilePath + ".");
		}
		
	}
}
