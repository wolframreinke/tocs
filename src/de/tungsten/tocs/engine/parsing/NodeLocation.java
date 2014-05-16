package de.tungsten.tocs.engine.parsing;

import java.util.Iterator;
import java.util.List;

/**
 * <code>NodeLocation</code> contains a collection of Node identifiers.
 * Together, they can be used to locate a Node relative to another node.
 * 
 * @author Wolfram
 *
 */
public class NodeLocation implements Iterable<String> {

	private List<String> identifiers;
	
	public NodeLocation( List<String> identifiers ) {
		this.identifiers = identifiers;
	}
	
	public String getSubject() {
		if ( identifiers.size() > 0 )
			return identifiers.get( identifiers.size() - 1 );
		else
			return "";
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
