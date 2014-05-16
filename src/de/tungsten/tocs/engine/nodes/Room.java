package de.tungsten.tocs.engine.nodes;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class Room extends LockableNode {

	private EnumMap<DoorDirection, Room> adjacentRooms = new EnumMap<DoorDirection, Room>( DoorDirection.class );
	
	public Room(Node parent, String[] names, String description, 
			boolean closed, boolean locked) {
		
		super(parent, names, description, closed, locked);
		
	}
	
	public boolean canAccess( DoorDirection direction ) {
		
		if ( isLocked() ) return false;
		
		Room target = adjacentRooms.get( direction );
		if ( target == null || target.isLocked() ) return false;
		else return true;
	
	}
	
	public Iterable<Room> getAdjacentRooms() {

		Set<Room> result = new HashSet<Room>();
		for (Entry<DoorDirection, Room> door : adjacentRooms.entrySet()) {
			if ( this.canAccess( door.getKey() ) )
				result.add( door.getValue() );
		}
		
		return result;
	}
	
	public Room getAdjacentRoom( DoorDirection direction ) {

		return adjacentRooms.get( direction );
	}

	public void setAdjacentRoom( DoorDirection direction, Room room ) {
		adjacentRooms.put( direction, room );
	}
}
