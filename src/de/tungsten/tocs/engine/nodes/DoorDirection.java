package de.tungsten.tocs.engine.nodes;

/**
 * <code>DoorDirection</code> contains all directions, a door in a <code>Room</code> might lead to.
 * <p>
 * The elements of this enumeration (together with thier textual representations) are:
 * <ul>
 * 	<li> {@link #NORTH} - "north"
 * 	<li> {@link #SOUTH} - "south"
 * 	<li> {@link #WEST} - "west"
 * 	<li> {@link #EAST} - "east"
 * 	<li> {@link #NORTHWEST} - "northwest"
 * 	<li> {@link #NORTHEAST} - "northeast"
 * 	<li> {@link #SOUTHWEST} - "southwest"
 * 	<li> {@link #SOUTHEAST} - "southeast"
 * 	<li> {@link #UP} - "up"
 * 	<li> {@link #DOWN} - "down"
 * </ul>
 * @author tungsten
 *
 */
public enum DoorDirection {

	/** The passage from/to the room is in the north. The textual representation is "north". */
	NORTH		( "north" ),
	
	/** The passage from/to the room is in the south. The textual representation is "south". */
	SOUTH		( "south" ),
	
	/** The passage from/to the room is in the west. The textual representation is "west". */
	WEST		( "west" ),
	
	/** The passage from/to the room is in the east. The textual representation is "east". */
	EAST		( "east" ),
	
	/** The passage from/to the room is in the northwest. The textual representation is "northwest". */
	NORTHWEST	( "northwest" ),
	
	/** The passage from/to the room is in the northeast. The textual representation is "northeast". */
	NORTHEAST	( "northeast" ),
	
	/** The passage from/to the room is in the southwest. The textual representation is "southwest". */
	SOUTHWEST	( "southwest" ),
	
	/** The passage from/to the room is in the southeast. The textual representation is "southeast". */
	SOUTHEAST	( "southeast" ),
	
	/** The passage from/to the room is upstairs. The textual representation is "up". */
	UP			( "up" ),
	
	/** The passage from/to the room is downstairs. The textual representation is "down". */
	DOWN		( "down" );
	
	/**
	 * The textual representation of this <code>DoorDirection</code>. This string can be used to easily 
	 * convert from strings in several representations of <code>Map</code>s (e.g. XML-maps) to actual
	 * <code>DoorDirection</code> instances, which are then used during the game.
	 */
	String representation;
	
	DoorDirection( String representation ) {
		this.representation = representation;
	}
	
	/**
	 * Returns the <code>DoorDirection</code> instance which's {@link #representation} equals the given
	 * string, or <code>null</code>, if there is no such instance.
	 * @param s		The string which is used to identify the desired <code>DoorDirection</code> instance.
	 * @return		The <code>DoorDirection</code> instance which's textual representation equals 
	 * 				<code>s</code>, or <code>null</code>, if there is no such instance.
	 */
	public static DoorDirection fromString( String s ) {
		for (DoorDirection direction : DoorDirection.values()) {
			if ( direction.representation.equals( s ) )
				return direction;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return representation;
	}
}
