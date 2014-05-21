package de.tungsten.tocs.engine.nodes;

/**
 * Ein Prädikat das überprüft, ob {@link Node Knoten} einer bestimmten
 * Bedingung genügen.
 * 
 * @author tungsten
 *
 */
public interface INodePredicate {

	/**
	 * Gibt zurück, ob der gegebene Knoten der Bedingung genügt.
	 * 
	 * @param node	Der zu überprüfende Knoten.
	 * @return		<code>true</code>, wenn der Knoten der Bedingung genügt,
	 * 				andernfalls <code>false</code>.
	 * 				
	 */
	public abstract boolean matches( Node node );
}
