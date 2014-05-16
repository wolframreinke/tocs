package de.tungsten.tocs.engine.parsing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public abstract class InstructionParser {

	private static final String[] ARTICLES = new String[] 
			{ "the", "this", "my", "these", "those", "a", "an", "some", "one" };
	
	protected Instruction createInstruction( String input ) {
	
		// This word delimits the identifiers. In "A B C and D E", only "A B C" and
		// "D E" are identifiers.
		final String DELIMITER = "and";
		
		// These variables are eventually used to create the Instruction object.
		String verb = null;
		Set<NodeLocation> prefix = new HashSet<NodeLocation>();
		EnumMap<Preposition, Set<NodeLocation>> arguments = new EnumMap<Preposition, Set<NodeLocation>>(Preposition.class);
		arguments.put( Preposition.TO, new HashSet<NodeLocation>() );
		arguments.put( Preposition.WITH, new HashSet<NodeLocation>() );
		
		// Perform some default replacements. This makes it far easier afterwards.
		input = input.replace( ",", " " + DELIMITER + " " );
		input = input.replace( "as well as", DELIMITER );
		input = input.replace( ";", "" );
		input = input.replace( ".", "" );
		input = input.replace( "!", "" );
		input = input.trim();
		
		// Split the input into words
		String[] words = input.split("\\s+");
		
		// Indicates whether the currently recognized word is part of an
		// InstructionArgument. If not, it belongs to the prefix part.
		boolean prepositionMode = false;
		
		// Indicates whether the verb was already recognized (false) or not
		// (true).
		boolean verbMode = true;
		
		// Set is chosen as the collection of objects has no specific order
		Set<String> lastObjects = new HashSet<String>();
		// A Location is a collection which has a specific order, but its
		// elements do not needd to have an order.
		List<Set<String>> lastLocation = new ArrayList<Set<String>>();
		String lastIdentifier = "";
		Preposition lastPreposition = null;
		
		for (String word : words) {
			
			// The is the very first word of an instruction
			if ( verbMode ) {
				verb = word;
				verbMode = false;
				continue;
			}
			
			// "small" words, such like articles are skipped
			boolean isSmallWord = false;
			for (String article : ARTICLES) {
				if ( article.equalsIgnoreCase( word ) ) {
					isSmallWord = true;
					break;
				}
			}
			
			if ( !isSmallWord ) {
				
				// If the current word is a preposition, its stored. If not,
				// fromString will return null
				Preposition currentPreposition = Preposition.fromString( word );
			
				if ( word.equalsIgnoreCase( DELIMITER ) ) {
					
					// The current word is DELIMITER, therefore, a new 
					// identifier starts.
					lastObjects.add( lastIdentifier );
					lastIdentifier = "";
					
				} else if ( currentPreposition == null ) {
					
					// The current word is no preposition, but an object. Its added to the current
					// set of objects.
					lastIdentifier += " " + word;
					
				} else if ( currentPreposition == Preposition.OF ) {
					
					// The current word is a preposition of the type OF. Therefore, the current set
					// of objects is added as a new identifier in the current location.
					lastObjects.add( lastIdentifier );
					lastLocation.add( lastObjects );
					lastObjects = new HashSet<String>();
					lastIdentifier = "";
					
				} else {
					
					// The current word is a preposition different from OF. The current set of objects
					// is added to the current location.
					lastObjects.add( lastIdentifier );
					lastLocation.add( lastObjects );
					
					if ( prepositionMode ) {
						
						// Its not the first preposition, so the location is transformed into a set of
						// NodeLocations and saved as an instruction argument.
						Set<NodeLocation> nodeLocations = createNodeLocations( lastLocation );
						if ( arguments.get( currentPreposition) != null )
							arguments.get( currentPreposition ).addAll( nodeLocations );
						else
							arguments.put( currentPreposition, nodeLocations );
						
						lastPreposition = currentPreposition;

						
					} else {
						
						// Its the first preposition, so the location is transformed into a set of
						// NodeLocations and assigned to be the prefix of this instruction.
						prepositionMode = true;
						prefix = createNodeLocations( lastLocation );
						lastPreposition = currentPreposition;

					}
					
					// Clear both
					lastIdentifier = "";
					lastObjects = new HashSet<String>();
					lastLocation = new ArrayList<Set<String>>();
					
				}
			} 
		}
		
		// When quitting the loop, the lastObjects array will still contain elements which must
		// be added to the prefix/arguments
		lastObjects.add( lastIdentifier );
		lastLocation.add( lastObjects );
		Set<NodeLocation> nodeLocations = createNodeLocations( lastLocation );
		if ( prepositionMode ) {
			if ( arguments.get( lastPreposition ) != null )
				arguments.get( lastPreposition ).addAll( nodeLocations );
			else 
				arguments.put( lastPreposition, nodeLocations );
		} else
			prefix = nodeLocations;
		
		// Create the Instruction object and return it
		Instruction result = new Instruction(verb, prefix, arguments);
		return result;
	}
	
	private Set<NodeLocation> createNodeLocations( List<Set<String>> locations ) {
		
		// the order of the sets in the parameter must be reversed first
		Stack<Set<String>> reverseStack = new Stack<Set<String>>();
		for (Set<String> set : locations) 
			reverseStack.push( set );
		
		locations.clear();
		while ( !reverseStack.isEmpty() )
			locations.add( reverseStack.pop() );
		
		// This set will be returned at the end of this method.
		Set<NodeLocation> nodeLocations = new HashSet<NodeLocation>();
		
		// For this algorithm a Stack containing sets of lists is required, but
		// the input is a list of sets, so it needs to be transformed.
		Stack<Set<List<String>>> stack = new Stack<Set<List<String>>>();
		for (Set<String> set : locations) {
			
			// Create a set of a list of Strings from a set of strings. A 
			// list is created for each String element, which contains only
			// this string element.
			Set<List<String>> element = new HashSet<List<String>>();
			for (String current : set) {
				List<String> list = new ArrayList<String>();
				list.add( current.trim() );
				
				element.add( list );
			}
			
			// push this new set.
			stack.push( element );
		}

		while ( stack.size() > 1 ) {
			
			// pop two set of lists. The cartesian product will be computed
			// using these sets.
			Set<List<String>> set01 = stack.pop(),
							  set02 = stack.pop();
			
			// The result of the cartesian product will be written into this
			// set
			Set<List<String>> result = new HashSet<List<String>>();
			
			// compute the cartesian product.
			for (List<String> element02 : set02) {
				for (List<String> element01 : set01) {

					List<String> list = new ArrayList<String>();
					list.addAll( element02 );
					list.addAll( element01 );
					
					result.add( list );
				}
			}
			
			// push the result
			stack.push( result );	
		} 
		
		// The last remaining element on the stack is the result element.
		for (List<String> list : stack.pop()) {
			nodeLocations.add( new NodeLocation( list ) );
		}
		
		return nodeLocations;
	}
}
















