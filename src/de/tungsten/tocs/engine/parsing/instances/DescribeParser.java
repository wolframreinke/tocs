package de.tungsten.tocs.engine.parsing.instances;

import java.util.Set;

import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.parsing.IParser;
import de.tungsten.tocs.engine.parsing.Instruction;
import de.tungsten.tocs.engine.parsing.InstructionParser;
import de.tungsten.tocs.engine.parsing.NodeLocation;
import de.tungsten.tocs.engine.parsing.NodeLocator;
import de.tungsten.tocs.engine.parsing.Preposition;
import de.tungsten.tocs.net.IPlayerConnection;

public class DescribeParser extends InstructionParser implements IParser {

	@Override
	public String[] getKeywords() {
		// TODO add more...
		return new String[] { 
				"describe", 
				"examine", 
				"inspect", 
				"look",
				"read" 
		};
	}

	@Override
	public void parse( String input, IPlayerConnection connection ) {
		
		if ( input.equals("look around" )
				|| input.equals( "look" )
				|| input.equals( "describe" ) ) {
			
			connection.write( connection.getPlayer().getParent().getDescription() );
			
		} else {
		
			Player player = connection.getPlayer();
			Instruction instruction = super.createInstruction( input );
			
			// in "describe the bear", the prefix contains the target objects,
			// in "look at the bear", the TO-argument contains the targets.
			// Therefore both are used.
			Set<NodeLocation> locations = instruction.getPrefix();
			if ( instruction.getArguments().get( Preposition.TO ) != null )
				locations.addAll( instruction.getArguments().get( Preposition.TO ) );
			
			for (NodeLocation location : locations) {
				
				Node target = NodeLocator.findNodeUsingLocators( location, player );
				if ( target != null ) {
					
					String description = target.getDescription();
					connection.write( description );
				} else {
					
					if ( !location.getSubject().equals( "" ) )
						connection.write( "What do you mean by \"" + location.getSubject() + "\"?" );
				}
			}
		}
	}

}
