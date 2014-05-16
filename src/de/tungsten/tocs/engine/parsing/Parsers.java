package de.tungsten.tocs.engine.parsing;

import java.util.HashSet;
import java.util.Set;

import de.tungsten.tocs.net.IPlayerConnection;

public class Parsers {

	private static Parsers instance = null;
	
	private Parsers() {}
	
	public static Parsers getInstance() {
		if ( instance == null )
			instance = new Parsers();
		
		return instance;
	}
	
	private Set<IParser> parsers = new HashSet<IParser>();
	
	public synchronized void addParser( IParser parser ) {
		if ( parser != null )
			parsers.add( parser );
	}
	
	public synchronized void parse( String input, IPlayerConnection connection ) {
		
		try {
			boolean found = false;
			for (IParser parser : parsers) {
			
				boolean suitable = false;
				for ( String keyword : parser.getKeywords() ) {
					
					if ( input.toLowerCase().startsWith( keyword.toLowerCase() ) )
						suitable = true;
				}
				
				if ( suitable ) {
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
