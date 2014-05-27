package de.tungsten.tocs.engine.parsing;

import java.util.HashSet;
import java.util.Set;

import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Die Klasse <code>Parsers</code> verwaltet Implementationen von <code>IParser</code>.
 * Wird dieser Klasse in {@link #parse(String, IPlayerConnection)} die Eingabe eines
 * Spielers übergeben, wählt sie die geeignete Implementation aus, und leitet die 
 * Anfrage an diese weiter.
 * <p>
 * Diese Klasse ist als Singleton implementiert, das heißt alle Objekte teilen sich
 * dieselben Parser. Die einzige existente Instanz kann mit {@link #getInstance()}
 * erreicht werden.
 * 
 * @author tungsten
 *
 */
public class Parsers {

	private static Parsers instance = null;
	private Parsers() {}
	
	/**
	 * Gibt die einzige existierende Instanz der Klasse <code>Parsers</code>
	 * zurück. Die Instanz wird erst erstellt, wenn sie zum ersten Mal angefordert
	 * wird.
	 * 
	 * @return	Die einzige existierende Instanz dieser Klasse.
	 */
	public static Parsers getInstance() {
		if ( instance == null )
			instance = new Parsers();
		
		return instance;
	}
	
	/**
	 * Die Implementationen von <code>IParser</code>, die zur Verfügung stehen, um die
	 * Eingaben der Spieler zu parsen.
	 */
	private Set<IParser> parsers = new HashSet<IParser>();
	
	/**
	 * Fügt eine Implementation von <code>IParser</code> hinzu, sodass sie zur Verfügung
	 * steht um die Eingaben der Spieler zu parsen.
	 * 
	 * @param parser
	 */
	public synchronized void addParser( IParser parser ) {
		if ( parser != null )
			parsers.add( parser );
	}
	
	/**
	 * Wählt einen geeigneten Parser zum bearbeiten der Anfrage aus, und leitet die Anfrage
	 * dann weiter.
	 * 
	 * @param input			Die zu interpretierende Eingabe.
	 * @param connection	Die <code>IPlayerConnection</code>, die verwendet wird, um die
	 * 						geparste Instruktion auszuführen.
	 */
	public synchronized void parse( String input, IPlayerConnection connection ) {
		
		try {
			// Passenden Parser auswählen
			boolean found = false;
			for (IParser parser : parsers) {
			
				// Passt der aktuelle Parser?
				boolean suitable = false;
				for ( String keyword : parser.getKeywords() ) {
					
					if ( input.toLowerCase().startsWith( keyword.toLowerCase() ) )
						suitable = true;
				}
				
				if ( suitable ) {
					// Wenn der Parser passt, dann Anfrage weiterleiten.
					parser.parse( input, connection );
					found = true;
					break;
				}
			}
			
			if ( !found )
				connection.write( "This was not understandable." );
		} 
		catch ( Exception e ) {
			connection.write( "Sorry, TOCS recognized an internal error." );
		}
	}
}
