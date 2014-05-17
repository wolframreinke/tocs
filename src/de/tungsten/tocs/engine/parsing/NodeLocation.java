package de.tungsten.tocs.engine.parsing;

import java.util.Iterator;
import java.util.List;

/**
 * Eine <code>NodeLocation</code> ist eine <code>Collection</code> von Strings, die
 * zusammen den Suchpfad zu einem Knoten im Spiel bilden. Das erste Element der
 * <code>Collection</code> muss zuerst lokalisiert werden, dann das zweite, und so
 * weiter.
 * 
 * @author Wolfram
 *
 */
public class NodeLocation implements Iterable<String> {

	/**
	 * Die Strings die zusammen den Suchpfad bilden.
	 */
	private List<String> identifiers;
	
	/**
	 * Erstellt eine neue Instanz von <code>NodeLocation</code>. Dabei werden
	 * alle Strings, die den Suchpfad bilden übergeben.
	 * 
	 * @param identifiers	Die Strings die zusammen den Suchpfad bilden.
	 */
	public NodeLocation( List<String> identifiers ) {
		if ( identifiers == null )
			throw new IllegalArgumentException( "NodeLocation: Identifiers must not be null." );
		
		this.identifiers = identifiers;
	}
	
	/**
	 * Gibt den letzten String aus dem Suchpfad zurück. Dieser String ist das Ziel
	 * des Suchpfades.
	 * 
	 * @return	Das letzte Element des Suchpfades.
	 */
	public String getTarget() {
		if ( identifiers.size() > 0 )
			return identifiers.get( identifiers.size() - 1 );
		else
			return ""; // Ein leerer Suchpfad hat kein Target.
	}
	
	@Override
	public Iterator<String> iterator() {
		return identifiers.iterator();
	}
	
	@Override
	public String toString() {
		
		String result = "";
		for (String identifier : identifiers) {
			result += "-> " + identifier;
		}
		
		return result.substring( 3 );
	}
}
