package de.tungsten.tocs.engine.nodes;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Ein <code>Room</code> ist ein Knoten im Spiel, der geschlossen und 
 * abgeschlossen (siehe {@link LockableNode} werden kann, und der T�ren in
 * alle Himmelsrichtungen, sowie nach oben und unten haben kann.
 * <p>
 * Die T�ren sind nichts als Referenzen auf andere <code>Room</code>s. Alle
 * erreichbaren R�ume k�nnen mit {@link #getAdjacentRooms()} erfragt werden,
 * einzelne R�ume mit {@link #getAdjacentRoom(Direction)}. Um herauszufinden,
 * ob ein Raum in einer gegebenen Richtung liegt, kann 
 * {@link #canAccess(Direction)} verwendet werden.
 * <p>
 * Spieler in TOCS k�nnen nur innerhalb von <code>Room</code>-Knoten platziert
 * werden, sie stellen somit die Umgebung dar, in der sich der Spieler bewegen
 * kann. Dabei ist "Raum" nur ein allgemeines Wort, auch offenene Pl�tze k�nnen
 * so modelliert werden.
 * 
 * @author tungsten
 *
 */
public class Room extends LockableNode {

	/**
	 * Speichert die R�ume, die von diesem Raum erreichbar sind, indiziert nach
	 * Richtung.
	 */
	private EnumMap<DoorDirection, Room> adjacentRooms = new EnumMap<DoorDirection, Room>( DoorDirection.class );
	
	/**
	 * Erstellt einen neuen Raum ohne Parent-Knoten. Dabei werden die Namen
	 * und die Beschreibung (siehe {@link Node}) �bergeben, sowie festgelegt,
	 * ob der Knoten offen/geschlossen (siehe {@link OpenableNode}) und
	 * abgeschlossen/aufgeschlossen (siehe {@link LockableNode}) ist.
	 * 
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens einen Wert 
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Ist dieser Wert
	 * 						<code>null</code> wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param closed		Gibt an, ob der Knoten geschlossen (<code>true</code>)
	 * 						oder offen (<code>false</code>) ist.
	 * @param locked		Gibt an, ob der Knoten abgeschlossen 
	 * 						(<code>true</code>) oder aufgeschlossen
	 * 						(<code>false</code>) ist.		
	 */
	public Room( String[] names, String description, 
			boolean closed, boolean locked ) {
		this( null, names, description, closed, locked );
	}
	
	/**
	 * Erstellt eine neue Instanz von <code>Room</code>. Dabei wird der
	 * Parent-Knoten, die Namen und die Beschreibung (siehe {@link Node})
	 * �bergeben, sowie festgelegt, ob der Knonten offen oder geschlossen
	 * (siehe {@link OpenableNode}) und abgeschlossen oder aufgeschlossen
	 * (siehe {@link LockableNode}) ist.
	 * 
	 * @param parent		Der diesem Knoten �bergeordnete Knoten.
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens einen Wert 
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Ist dieser Wert
	 * 						<code>null</code> wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param closed		Gibt an, ob der Knoten geschlossen (<code>true</code>)
	 * 						oder offen (<code>false</code>) ist.
	 * @param locked		Gibt an, ob der Knoten abgeschlossen 
	 * 						(<code>true</code>) oder aufgeschlossen
	 * 						(<code>false</code>) ist.
	 */
	public Room( Node parent, String[] names, String description, 
			boolean closed, boolean locked ) {
		
		super( parent, names, description, closed, locked );
		
	}
	
	/**
	 * Gibt zur�ck, ob in der gegebenen Richtung ein Raum liegt. Es wird
	 * auch <code>false</code> zur�ckgegeben, wenn dieser Raum, oder der
	 * in der gegebenen Richtung liegende Raum abgeschlossen sein sollten.
	 * 
	 * @param direction	Die zu �berpr�fende Richtung.
	 * @return			Ob in der gegebenen Richtung ein Raum erreichbar ist.
	 */
	public boolean canAccess( DoorDirection direction ) {
		
		if ( isLocked() ) return false;
		
		Room target = adjacentRooms.get( direction );
		if ( target == null || target.isLocked() ) return false;
		else return true;
	
	}
	
	/**
	 * Erm�glicht das iterieren �ber alle von diesem Raum aus erreichbaren
	 * (siehe {@link #canAccess(Direction)}) R�ume.
	 * 
	 * @return	Ein <code>Iterable&lt;Room&gt;</code>, welches das iterieren
	 * 			�ber alle erreichbaren R�ume erm�glicht.
	 */
	public Iterable<Room> getAdjacentRooms() {

		Set<Room> result = new HashSet<Room>();
		for (Entry<DoorDirection, Room> door : adjacentRooms.entrySet()) {
			// Wenn der Raum erreichbar ist, dann wird er hinzugef�gt
			if ( this.canAccess( door.getKey() ) )
				result.add( door.getValue() );
		}
		
		return result;
	}
	
	/**
	 * Gibt den Raum zur�ck, der in der gegebenen Richtung liegt,
	 * unabh�ngig davon, ob der {@link #canAccess(Direction) erreichbar}
	 * ist.
	 * 
	 * @param direction		Die Richtung, in der der gew�nschte Raum liegt.
	 * @return				Der Raum der in der gew�nschten Richtung lieft,
	 * 						und <code>null</code> falls kein Raum in dieser
	 * 						Richtung liegt.
	 */
	public Room getAdjacentRoom( DoorDirection direction ) {

		return adjacentRooms.get( direction );
	}

	/**
	 * Erstellt eine Verbindung von diesem Raum zu dem gegebenen Raum in
	 * die gegebene Richtung. Der Aufruf von
	 * {@link #getAdjacentRoom(Direction)} mit derselbe 
	 * <code>Direction</code> gibt von nun an den gegebenen Raum zur�ck.
	 * 
	 * @param direction		Die Richtung, in die der Raum hinzugef�gt werden
	 * 						aoll.
	 * @param room			Der hinzuzuf�gende Raum.
	 * @return				Dieser Raum, zwecks chaining.
	 */
	public Room setAdjacentRoom( DoorDirection direction, Room room ) {
		adjacentRooms.put( direction, room );
		return this;
	}
}
