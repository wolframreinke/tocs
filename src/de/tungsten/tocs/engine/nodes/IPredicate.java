package de.tungsten.tocs.engine.nodes;

/**
 * Ein Pr�dikat das �berpr�ft, ob {@link Node Knoten} einer bestimmten
 * Bedingung gen�gen.
 *
 * @param T		Der Datentyp des Parameters von <code>matches</code>.	
 * @author tungsten
 *
 */
public interface IPredicate<T> {

	/**
	 * Gibt zur�ck, ob der gegebene Knoten der Bedingung gen�gt.
	 * 
	 * @param tryout	Das zu �berpr�fende Objekt.
	 * @return			<code>true</code>, wenn der Knoten der Bedingung gen�gt,
	 * 					andernfalls <code>false</code>.
	 * 				
	 */
	public abstract boolean matches( T tryout );
}
