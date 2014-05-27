package de.tungsten.tocs.engine.nodes;

/**
 * Ein Prädikat das überprüft, ob {@link Node Knoten} einer bestimmten
 * Bedingung genügen.
 *
 * @param T		Der Datentyp des Parameters von <code>matches</code>.	
 * @author tungsten
 *
 */
public interface IPredicate<T> {

	/**
	 * Gibt zurück, ob der gegebene Knoten der Bedingung genügt.
	 * 
	 * @param tryout	Das zu überprüfende Objekt.
	 * @return			<code>true</code>, wenn der Knoten der Bedingung genügt,
	 * 					andernfalls <code>false</code>.
	 * 				
	 */
	public abstract boolean matches( T tryout );
}
