package de.tungsten.tocs.engine.nodes;

/**
 * Ein Pr�dikat das �berpr�ft, ob {@link Node Knoten} einer bestimmten
 * Bedingung gen�gen.
 * 
 * @author Wolfram
 *
 */
public interface INodePredicate {

	/**
	 * Gibt zur�ck, ob der gegebene Knoten der Bedingung gen�gt.
	 * 
	 * @param node	Der zu �berpr�fende Knoten.
	 * @return		<code>true</code>, wenn der Knoten der Bedingung gen�gt,
	 * 				andernfalls <code>false</code>.
	 * 				
	 */
	public abstract boolean matches( Node node );
}
