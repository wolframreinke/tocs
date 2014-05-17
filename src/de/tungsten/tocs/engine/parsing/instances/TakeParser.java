package de.tungsten.tocs.engine.parsing.instances;

import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.parsing.IParser;
import de.tungsten.tocs.engine.parsing.Instruction;
import de.tungsten.tocs.engine.parsing.InstructionParser;
import de.tungsten.tocs.engine.parsing.NodeLocation;
import de.tungsten.tocs.engine.parsing.NodeLocator;
import de.tungsten.tocs.net.IPlayerConnection;

public class TakeParser extends InstructionParser implements IParser {

	@Override
	public String[] getKeywords() {
		return new String[] {
			"take",
			"pick"
		};
	}

	@Override
	public void parse(String input, IPlayerConnection connection) {

		Player player  = connection.getPlayer();
		Instruction instruction = super.createInstruction( input );
		
		for (NodeLocation location : instruction.getPrefix()) {
			
			Node object = NodeLocator.findNodeUsingLocators( location, player );
			
			if ( object != null ) {
				
				object.move( player.getInventory() );
				connection.write( "Taken." );
				
			} else {
				
				connection.write( "There is no \"" + location.getTarget() + "\" around here." );
			}
		}
	}

}
