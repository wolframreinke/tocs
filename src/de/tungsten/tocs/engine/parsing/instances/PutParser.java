package de.tungsten.tocs.engine.parsing.instances;

import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.parsing.IParser;
import de.tungsten.tocs.engine.parsing.Instruction;
import de.tungsten.tocs.engine.parsing.InstructionParser;
import de.tungsten.tocs.engine.parsing.NodeLocation;
import de.tungsten.tocs.engine.parsing.NodeLocator;
import de.tungsten.tocs.engine.parsing.Preposition;
import de.tungsten.tocs.net.IPlayerConnection;

public class PutParser extends InstructionParser implements IParser {

	@Override
	public String[] getKeywords() {
		return new String[] {
				"put",
				"move",
				"place",
				"transport"
		};
	}

	@Override
	public void parse( String input, IPlayerConnection connection ) {

		Player player = connection.getPlayer();
		Instruction instruction = super.createInstruction( input );

		Node to = null;
		for (NodeLocation location : instruction.getArguments().get( Preposition.TO )) {
			
			if ( to == null ) {
				
				to = NodeLocator.findNodeUsingLocators( location, player );
				if ( to == null ) 
					connection.write( "I cannot see any \"" + location.getTarget() + "\" around here." );
				
			}
		}
		
		if ( to != null ) {
		
			for (NodeLocation location : instruction.getPrefix()) {
				
				Node object = NodeLocator.findNodeUsingLocators( location, player );
				if ( object != null ) {
					
					object.move( to );
					connection.write( "Done." );
				} else {
					connection.write( "I cannot find this \"" + location.getTarget() + "\". " );
				}
			}
		}
		
		
	}

}
