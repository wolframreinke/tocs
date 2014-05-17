package de.tungsten.tocs.engine.parsing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Erstellt ein {@link Instruction}-Objekt aus der Eingabe eines Spielers. Für Details,
 * siehe {@link #createInstruction(String)}.
 * 
 * @author Wolfram
 *
 */
public abstract class InstructionParser {

	/**
	 * Die "kleinen Wörter", die beim parsen ignoriert werden, da sie zur Identifikation
	 * von Knoten irrelevant sind.
	 */
	private static final String[] ARTICLES = new String[] 
			{ "the", "this", "my", "these", "those", "a", "an", "some", "one" };
	
	/**
	 * Erstellt eine {@link Instruction}-Objekt aus der gegebenen Eingabe eines
	 * Spielers. Dabei werden "kleine Wörter" (siehe {@link #ARTICLES}) ignoriert. So
	 * wird aus "open the box" eine Instruktion mit dem Präfix "box" (ohne "the"). 
	 * <p>
	 * Identifier werden nach "and"s getrennt, sodass aus "the big box and the small box"
	 * nicht "big", "box", "small", "box" wird, sondern "big box" und "small box".
	 * <p>
	 * Generell ist der Parser relativ robust implementiert, in der englischen Sprache gern
	 * verwendete Zeichen und Füllwörter werden entweder ersetzt oder ignoriert.
	 * 
	 * @param input	Die zu interpretierende Eingabe.
	 * @return		Die aus der Eingabe geparste Instruction.
	 */
	protected Instruction createInstruction( String input ) {

		// Dieses Wort trennt Identifier. Aus "A B C and D E" werden die Identifier
		// "A B C" und "D E".
		final String DELIMITER = "and";

		// Mit diesen Variablen wird letztlich die Instruction erstellt.
		String verb = null;
		Set<NodeLocation> prefix = new HashSet<NodeLocation>();
		EnumMap<Preposition, Set<NodeLocation>> arguments = new EnumMap<Preposition, Set<NodeLocation>>(Preposition.class);
		arguments.put( Preposition.TO, new HashSet<NodeLocation>() );
		arguments.put( Preposition.WITH, new HashSet<NodeLocation>() );

		// Einige Standard-Ersetzungen, um den Parser robuster zu machen
		input = input.replace( ",", " " + DELIMITER + " " );
		input = input.replace( "as well as", DELIMITER );
		input = input.replace( ";", "" );
		input = input.replace( ".", "" );
		input = input.replace( "!", "" );
		input = input.trim();

		// In Wörter aufsplitten
		String[] words = input.split("\\s+");
		
		// Gibt an, ob das momentan betrachtete Wort teil eines Arguments der Instruction
		// ist. Wenn nicht gehört es nämlich zum Präfix-Part
		boolean prepositionMode = false;

		// Gibt an, ob das Verb bereits gespeichert wurde
		boolean verbRecognized = true;

		// Die zuletzt gefundenen Identifier in einer Menge, da ihre Reihenfolge irrelevant
		// ist
		Set<String> lastObjects = new HashSet<String>();
		// Die zuletzt gefundenen Locations. Mehrere Identifier bilden zusammen eine
		// Location, die dann mit "of" o.ä. verknpft sind. Beispiel:
		// the ball and the knife on the table
		// ball und knife sind zusammen eine Location, die nächste ist dann table
		List<Set<String>> lastLocation = new ArrayList<Set<String>>();
		
		// Der zuletzt gefundenen Identifier
		String lastIdentifier = "";
		
		// Die zuletzt gefundene Präposition
		Preposition lastPreposition = null;
		
		for (String word : words) {

			// Das Verb ist das erste Wort der Eingabe
			if ( verbRecognized ) {
				verb = word;
				verbRecognized = false;
				continue;
			}

			// "kleine Worter" werden ignoriert.
			boolean isSmallWord = false;
			for (String article : ARTICLES) {
				if ( article.equalsIgnoreCase( word ) ) {
					isSmallWord = true;
					break;
				}
			}
			
			if ( !isSmallWord ) {

				// Wenn das aktuelle Wort eine Präposition ist wird es gespeichert.
				// Wenn nicht gibt fromString null zurück
				Preposition currentPreposition = Preposition.fromString( word );
			
				if ( word.equalsIgnoreCase( DELIMITER ) ) {

					// Wenn das aktuelle Wort der DELIMITER ist, dann waren die
					// bisherigen Wörter Teil eines neue Identifier.
					// Dieser wird geaddet.
					lastObjects.add( lastIdentifier );
					lastIdentifier = "";
					
				} else if ( currentPreposition == null ) {

					// Wenn das aktuelle Wort keine Präposition ist, dann wird es 
					// zum aktuellen Identifier hinzugefügt.
					lastIdentifier += " " + word;
					
				} else if ( currentPreposition == Preposition.OF ) {

					// Das aktuelle Wort ist eine Präposition des Types OF. Daher wird
					// der aktuelle Identifier zur aktuellen Location hinzugefügt, und
					// diese Location zur Liste der Locations geaddet.
					lastObjects.add( lastIdentifier );
					lastLocation.add( lastObjects );
					lastObjects = new HashSet<String>();
					lastIdentifier = "";
					
				} else {

					// Das aktuelle Wort ist eine Präosition, die nicht von Typ OF ist.
					// Erstmal den letzten Identifier und die letzte Location speichern.
					lastObjects.add( lastIdentifier );
					lastLocation.add( lastObjects );
					
					if ( prepositionMode ) {

						// Wenn das nicht die erste Präposition ist, dann müssen die
						// aktuellen Locations zu einem InstructionArgument geaddet
						// werden.
						Set<NodeLocation> nodeLocations = createNodeLocations( lastLocation );
						if ( arguments.get( currentPreposition) != null )
							arguments.get( currentPreposition ).addAll( nodeLocations );
						else
							arguments.put( currentPreposition, nodeLocations );
						
						lastPreposition = currentPreposition;

						
					} else {
						
						// Wenn es die erste Präposition ist, dann müssen die aktuelle
						// Locataions zum Präfix geaddet werden.
						prepositionMode = true;
						prefix = createNodeLocations( lastLocation );
						lastPreposition = currentPreposition;

					}

					// Alle Variablen clearen
					lastIdentifier = "";
					lastObjects = new HashSet<String>();
					lastLocation = new ArrayList<Set<String>>();
					
				}
			} 
		}

		// Wenn die Schleife verlassen wird, wurden die letzten Wörter noch nicht zur
		// aktuellen Location geaddet.
		lastObjects.add( lastIdentifier );
		lastLocation.add( lastObjects );
		
		// Aus den einzelnen Identifiers muss nun noch eine NodeLocation gemacht werden.
		Set<NodeLocation> nodeLocations = createNodeLocations( lastLocation );
		if ( prepositionMode ) {
			if ( arguments.get( lastPreposition ) != null )
				arguments.get( lastPreposition ).addAll( nodeLocations );
			else 
				arguments.put( lastPreposition, nodeLocations );
		} else
			prefix = nodeLocations;

		// Instruction-Objet erstellen und zurückgeben.
		Instruction result = new Instruction(verb, prefix, arguments);
		return result;
	}
	
	/**
	 * Erstellt eine Menge von <code>NodeLocation</code>s aus dem Übergabeparameter.
	 * Dieser ist eine Liste von Mengen von Identifiers. Beispiel:
	 * <br><code>{ {knife,stone} {box,chest} }</code><br>
	 * Die Rückgabe sollen die <code>NodeLocation</code>s von jedem Element der ersten
	 * Menge zu jedem Element der letzten Menge über jeden möglichen Pfad (also
	 * quasi das kartesische Produkt aller gegebenen Mengen). Beispiel:
	 * <br>Aus <code>{ {a1,a2} {b1,b2} {c1,c2} }</code> werden die 
	 * <code>NodeLocation</code>s <code>{ a1->b1->c1, a1->b1->c2, a1->b2->c1, 
	 * a1->b2->c2, ..., a2->b2->c2 }
	 * </code><br>
	 * 
	 * @param locations	siehe oben.
	 * @return			siehe oben.
	 */
	private Set<NodeLocation> createNodeLocations( List<Set<String>> locations ) {

		// Die Reihenfolge der Sets muss erst getauscht werden. Dafür wird ein Stack
		// verwendet.
		Stack<Set<String>> reverseStack = new Stack<Set<String>>();
		for (Set<String> set : locations) 
			reverseStack.push( set );
		
		locations.clear();
		while ( !reverseStack.isEmpty() )
			locations.add( reverseStack.pop() );

		// Der Rückgabewert
		Set<NodeLocation> nodeLocations = new HashSet<NodeLocation>();
		
		// Für diesen Algorithmus wird ein Stack benötigt, der Sets von Listen enthält,
		// die Eingabe ist aber eine Liste von Sets, daher muss erst transformiert werden.
		Stack<Set<List<String>>> stack = new Stack<Set<List<String>>>();
		for (Set<String> set : locations) {

			// Erst ein Set von Listen von Strings aus dem Set von Strings erstellen.
			// Dazu wird für jeden String ein eigenes Set erstellt, dass nur diesen
			// String enthält
			Set<List<String>> element = new HashSet<List<String>>();
			for (String current : set) {
				List<String> list = new ArrayList<String>();
				list.add( current.trim() );
				
				element.add( list );
			}

			// Das transformierte Ergebnis kann nun auf den Stack
			stack.push( element );
		}

		while ( stack.size() > 1 ) {

			// Zwei Elemente vom Stack poppen. Aus diesen beiden Mengen wird
			// das kartesische Produkt berechnet werden.
			Set<List<String>> set01 = stack.pop(),
							  set02 = stack.pop();

			// Das Ergebnis des kartesischen Produkts wird in diese Menge geschrieben
			// werden.
			Set<List<String>> result = new HashSet<List<String>>();

			// Kartesisches Produkt (also die Kombination von jedem Element der ersten
			// Menge mit jedem Element der zweiten Menge) berechnen.
			for (List<String> element02 : set02) {
				for (List<String> element01 : set01) {

					List<String> list = new ArrayList<String>();
					list.addAll( element02 );
					list.addAll( element01 );
					
					result.add( list );
				}
			}
			
			// Das Ergebnis wieder auf den Stack pushen
			stack.push( result );	
		} 

		// Das letzte auf dem Stack befindliche Element ist das Ergebnis.
		for (List<String> list : stack.pop()) {
			nodeLocations.add( new NodeLocation( list ) );
		}
		
		return nodeLocations;
	}
}
















