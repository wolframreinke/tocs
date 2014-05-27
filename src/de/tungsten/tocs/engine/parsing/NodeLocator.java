package de.tungsten.tocs.engine.parsing;

import java.util.List;

import de.tungsten.tocs.engine.nodes.IPredicate;
import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.Player;

/**
 * Die abstrakte Klasse <code>NodeLocator</code> bietet statische Methoden zum Suchen
 * nach Knoten im Knoten-Baum. Die Klassen {@link Node} selbst bietet zwar die Methoden
 * <code>find</code> und <code>findByIdentifier</code> aber um das Parsen der 
 * Spielereingaben einfach, und <code>Node</code> m�glichst allgemein zu halten, k�nnen
 * die hier definierten Methode verwendet werden.
 * 
 * @author tungsten
 *
 */
public abstract class NodeLocator {
	
	/**
	 * Sucht nach Knoten, die dieselbe, oder eine von <code>type</code> 
	 * abgeleitete Klasse haben. Der erste gefundene Knoten wird als Ergebnis 
	 * dieser Methode zur�ckgegeben.
	 * <p>
	 * Wenn keine Knoten gefunden werden, die der Suchbedingung entsprechen,
	 * wird <code>null</code> zur�ckgegeben.
	 * <p>
	 * Diese Suche ist als Tiefensuche (Deep-First) implementiert. Daher ist der
	 * R�ckgabewert nicht der passende Knoten, der sich am n�chsten zum
	 * gegebenen Parent-Knoten befindet, sondern der Knoten, der am weitesten
	 * links im Knoten-Baum gefunden wurde.
	 * 
	 * @param type		Der Datentyp des gesuchten Knotens. Dieser Wert darf
	 * 					nicht <code>null</code> sein.
	 * @param parent	Der Knoten in dessen Kind-Knoten sich der gesuchte Knoten
	 * 					befindet. Dieser Wert darf nicht <code>null</code> sein.
	 * @param depth		Die maximale Suchtiefe. Negative Werte stehen f�r eine
	 * 					unbegrenzte Suchtiefe.
	 * @return			Ein <code>Node</code>, der den gegebenen Datentyp hat,
	 * 					und der sich maximal <code>depth</code> Ebenen unter
	 * 					dem Knoten <code>parent</code> befindet.
	 */
	public static Node findSubNode( final Class<? extends Node> type, Node parent, int depth ) {
		
		IPredicate<Node> classComparison = new IPredicate<Node>() {
			@Override
			public boolean matches( Node node ) {
				return node.getClass().isAssignableFrom( type );
			}
		};
		
		List<Node> result = parent.find( classComparison, depth );
		if ( result != null && result.size() > 0 )
			return result.get( 0 );
		else
			return null;
	}
	
	/**
	 * Sucht nach Knoten mit dem gegebenen Namen, die Kind-Knoten des gegebenen
	 * Parent-Knoten sind. Der erste gefundene Knoten wird zur�ckgegeben.
	 * <p>
	 * Knoten deren Identifier dem gegebenen Namen entsprechen werden gegen�ber Knoten,
	 * deren Namen mit dem gegebenen Namen �bereinstimmen bevorzugt. Wenn keine
	 * Knoten der Suchbedingung entsprechen, wird <code>null</code> zur�ckgegeben.
	 * <p>
	 * Da die Suche die Methoden von <code>Node</code> verwendet, ist auch diese
	 * Methode eine Tiefen-Suche (Deep-First). Es wird also nicht der Knoten 
	 * zur�ckgegeben, der am n�chsten zum gegebenen Parent-Knoten ist, sondern der,
	 * der sich in seinen Teilb�umen am weitesten links befindet.
	 * 
	 * @param name			Der Identifier/Name des gesuchten Knotens.
	 * @param parent		Der Parent-Knoten, dessen Kind der gesuchte Knoten ist.
	 * @param depth			Die Suchtiefe. Negative Werte stehen f�r eine unbegrenzte
	 * 						Suchtiefe.
	 * @return				Ein Knoten mit dem gegebenen Namen oder Identifier, der ein
	 * 						Kind-Knoten des gegebenen Parent-Knotens ist.
	 */
	public static Node findSubNode( final String name, Node parent, int depth ) {

		// Anonyme Klasse f�r find(...) in Node
		IPredicate<Node> nameComparison = new IPredicate<Node>() {
			
			@Override
			public boolean matches( Node node ) {
				
				for (String nodeName : node.getNames()) {
					if ( nodeName.equals( name ) )
						return true;
				}
				return false;
			}
			
		};
		
		Node result = null;
		// Zuerst nach passenden Identifiers suchen
		result = parent.findByIdentifier( name, depth );
		
		if ( result == null ) {
			// Wenn keine passenden Identifier, dann nach passenden Namen suchen.
			List<Node> candidates = parent.find( nameComparison, depth );

			// candidates kann eigentlich nicht null sein, aber sicher ist sicher.
			if ( candidates != null && candidates.size() > 0 )
				result = candidates.get( 0 ); // Ersten Treffer zur�ckgeben
			
		}
		
		return result;
	}
	
	/**
	 * Sucht nach <code>Node</code>s in der Umgebung eines Spielers. Um von dieser
	 * Methode gefunden zu werden, muss ein Knoten sich in einem der folgenden Orte
	 * befinden:
	 * <ul>
	 * <li> Im selben Raum wie der gegebenen Spieler (Suchtiefe 1),
	 * <li> Irgendwo (unbegrenzte Suchtiefe) im Inventar des Spielers, oder
	 * <li> in den H�nden des Spielers.
	 * </ul>
	 * Knoten, deren Identifier passen werden gegen�ber Knoten deren Namen passen
	 * bevorzugt. Wenn mehr als ein Knoten passt, wird der erste Treffer zur�ckgegeben.
	 * Wenn kein Knoten passt, wird <code>null</code> zur�ckgegeben.
	 *
	 * @param name		Der Name/Identifier des gesuchten Knotens.
	 * @param player	Der Spieler, in dessen Umgebung der gesuchte Knoten gesucht wird.
	 * @return			Ein Knoten mit dem gegebenen Namen/Identifier, der in der 
	 * 					Umgebung des gegebenen Spielers gefunden wurde, oder
	 * 					<code>null</code> wenn kein solcher Knoten gefunden wurde.
	 */
	public static Node findNodeAtPlayer( String name, Player player ) {

		// In Inventar und H�nden mit unbegrenzter Suchtiefe Suchen.
		Node result = findSubNode( name, player, -1 );

		// Mit Suchtiefe 1 den Raum des Spielers durchsuchen.
		if ( result == null )
			result = findSubNode( name, player.getParent(), 1 );
		
		return result;
	}
	
	/**
	 * Sucht nach <code>Node</code>s in der Umgebung eines Spielers anhand einer
	 * {@link NodeLocation}. 
	 * <p>
	 * Der erste String in der <code>NodeLocation</code> ist die Wurzel, dieser Knoten wird
	 * direkt beim Spieler gesucht. Alle folgenden Knoten werden in der Umgebung des
	 * zuletzt lokalisierten Knotens gesucht.
	 * <br><b>Example:</b><br>
	 * <code>l1 -> l2 -> l3 -> l4</code><br>
	 * "l1" wird direkt beim Spieler gesucht. Nachdem dieser Knoten lokalisiert wurde,
	 * wird "l2" in der Umgebung des mit "l1" lokalisierten Knotens gesucht. "l3" wird
	 * dann in der Umgebung des mit "l2" gefundenen Knotens gesucht.
	 * <p>
	 * Knoten deren Identifier passen werden gegen�ber Knoten, deren Namen passen
	 * bevorzugt. Wenn mehr als ein Knoten gefunden wurde, der auf die
	 * <code>NodeLocation</code> passt, wird der erste Treffer zur�ckgegeben. Wenn kein
	 * passender Knoten gefunden wurde, wird <code>null</code> zur�ckgegeben.
	 * 
	 * @param locators		Die <code>NodeLocation</code>, die zur Suche verwendet werden
	 * 						soll.
	 * @param player		Der Spieler, bei dem der erste Knoten lokalisiert werden soll.
	 * @return				Ein Knoten, der sich an der gegebenen <code>NodeLocation</code>
	 * 						befindet, oder <code>null</code>, wenn kein solcher Knoten
	 * 						gefunden wurde.
	 */
	public static Node findNodeUsingLocators( NodeLocation locators, Player player ) {

		// Dieser boolean wird nach der ersten iteration auf false gesetzt, da nur
		// der erste Knoten beim Spieler gesucht wird.
		boolean lookAtPlayer = true;
		Node currentNode = null;
		
		// NodeLocation implementiert Iteratble
		for (String locator : locators) {

			// Nur bei erster iteration
			if ( lookAtPlayer ) {

				// Wurzel finden
				lookAtPlayer = false;
				currentNode = findNodeAtPlayer( locator, player );
			} else {

				// N�chsten Knoten im Suchpfad finden.
				currentNode = findSubNode( locator, currentNode, 1 );	
			}

			// Wenn ein Element nicht gefunden wurde, wird die Suche gecancelled und
			// null zur�ckgegeben.
			if ( currentNode == null )
					return null;
		}
		
		
		return currentNode;
		
	}
}
