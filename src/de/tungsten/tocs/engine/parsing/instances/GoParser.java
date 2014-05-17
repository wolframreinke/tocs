package de.tungsten.tocs.engine.parsing.instances;

import de.tungsten.tocs.engine.nodes.DoorDirection;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.nodes.Room;
import de.tungsten.tocs.engine.parsing.IParser;
import de.tungsten.tocs.engine.parsing.Instruction;
import de.tungsten.tocs.engine.parsing.InstructionParser;
import de.tungsten.tocs.engine.parsing.NodeLocation;
import de.tungsten.tocs.engine.parsing.Preposition;
import de.tungsten.tocs.net.IPlayerConnection;

public class GoParser extends InstructionParser implements IParser {

	@Override
	public String[] getKeywords() {

		return new String[] {
				"go",
				"walk",
				"run"
		};
	}

	@Override
	public void parse( String input, IPlayerConnection connection ) {

		Player player = connection.getPlayer();
		Room room = (Room) player.getParent();
		
		Instruction instruction = super.createInstruction( input );
		
		// The player can specify more than one direction. When he says
		// "go north and west", he will go north first and west afterwards.
		for (NodeLocation location : instruction.getPrefix()) {
			
			DoorDirection direction = DoorDirection.fromString( location.getTarget() );
			if ( direction != null ) {
				
				Room nextRoom = room.getAdjacentRoom( direction );
				if ( nextRoom != null ) {
					
					room = nextRoom;
			
				} else {
					connection.write( "There's no way in this direction." );
				}
				
			} else {
				connection.write( "What do you mean by \"" + location.getTarget() + "\"?" );
			}
		
		}	
		
		// Only do this if the prefix is empty. If the player types
		// "go north to the castle", only the "go north" is taken into account.
		if ( instruction.getPrefix().isEmpty() ) {
			
			for (NodeLocation location : instruction.getArguments().get( Preposition.TO )) {
				
				// Look for the node in the current room's adjacent rooms.
				for (Room adjacentRoom : room.getAdjacentRooms()) {

					if ( adjacentRoom.getIdentifier().equals( location.getTarget() ) ) {
						
						room = adjacentRoom;
					} else {
						
						for (String name : adjacentRoom.getNames()) {
							if ( name.equals( location.getTarget() ) )
								room = adjacentRoom;
						}
					}
				}
			}
		}
		
		player.move( room );
		connection.write( room.getDescription() );
	}

}
