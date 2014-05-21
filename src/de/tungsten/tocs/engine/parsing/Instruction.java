package de.tungsten.tocs.engine.parsing;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <code>Instruction</code> repr�sentiert einen Typ von Befehl, der aus einem Verb,
 * einem Pr�fix und mehreren Argumenten zusammengesetzt ist. Dieser Typ von Anweisung
 * kann sehr oft als Ziel-Struktur beim Parsen der Eingaben eines Spielers verwendet werden.
 * <p>
 * Um in diese Struktur geparst werden zu k�nnen, muss die Spieler-Eingabe folgende
 * grobe Form haben:
 * <br><code>VERB PREFIX ARGUMENTS</code>.<br>
 * <code>VERB</code> ist das erste Wort in der Eingabe. <code>PREFIX</code> enth�lt die
 * Identifier direkt nach dem Verb, aber noch vor der ersten {@link Preposition}.
 * <code>ARGUMENTS</code> stellt eine Menge von zus�tzlichen Argumenten dar, die
 * jeweils aus einer <code>Preposition</code> und mehreren Identifiers besteht.
 * 
 * @author tungsten
 * @see #InstructionParser
 */
public class Instruction {

	/**
	 * Das Verb diese Instruktion, und damit das erste Wort in der Eingabe des Spielers.
	 * <br><b>Beispiel:</b>
	 * <br><code>"open the door"</code> 
	 * <br>Hier ist "open" das Verb, da es das erste Wort in der Eingabe ist (Worter werden
	 * nach Leerzeichen getrennt).
	 */
	private String verb;

	/**
	 * Der Pr�fix dieser Instruktion.
	 * <br><b>Beispiel:</b>
	 * <br><code>"open the box with the knife"</code>
	 * <br>Hier ist "box" der Pr�fix, da es nach dem Verb steht und vor der ersten
	 * Pr�osition ("with"). Der Artikel wird entfernt. Prinzipiell w�ren auch mehrere 
	 * Identifier erlaubt, die mit "and" getrennt werden.
	 */
	private Set<NodeLocation> prefix;
	
	/**
	 * Die zus�tzlichen Argument dieses Knotens als Mapping von Pr�positionen zu
	 * den dazugeh�rigen Identifiers.
	 * <br><b>Beispiel:</b>
	 * <br><code>"put the ball from the table into the box"</code>
	 * <br>Hier sind die Argumente zum ersten "from table" wobei "from" zu der
	 * Pr�position OF geh�rt, und zum zweiten "into box" wobei "into" zu der Pr�position
	 * TO geh�rt.
	 * 
	 * @see {@link Preposition}.
	 */
	private EnumMap<Preposition, Set<NodeLocation>> arguments;
	
	/**
	 * Erstellt eine neue Instanz von <code>Instruction</code>.
	 * 
	 * @param verb		{@link #verb}
	 * @param prefix	{@link #prefix}
	 * @param arguments	{@link #arguments}
	 */
	public Instruction( 
			String verb, 
			Set<NodeLocation> prefix, 
			EnumMap<Preposition, Set<NodeLocation>> arguments ) {
		
		this.verb = verb;
		this.prefix = prefix;
		this.arguments = arguments;
	}

	/**
	 * Gibt das {@link #verb Verb} dieser <code>Instruction</code> zur�ck.
	 * @return Das Verb dieser Instruktion.
	 */
	public String getVerb() {
		return verb;
	}

	/**
	 * Gibt den {@link #prefix Pr�fix} dieser <code>Instruction</code> zur�ck.
	 * @return Den Pr�fix dieser Instruktion.
	 */
	public Set<NodeLocation> getPrefix() {
		return prefix;
	}

	/**
	 * Gibt die {@link #arguments Argumente} dieser <code>Instruction</code> zur�ck.
	 * @return Die Argumente dieser Instruktion.
	 */
	public EnumMap<Preposition, Set<NodeLocation>> getArguments() {
		return arguments;
	}
	
	@Override
	public String toString() {
		String result = "VERB =\n\t" + verb + "\nPREFIX =\n";
		for (NodeLocation location : prefix) {	
			result += "\t" + location + "\n";
		}
		
		result += "ARGUMENTS =\n";
		for (Entry<Preposition, Set<NodeLocation>> argument : arguments.entrySet()) {
			result += "\t" + argument.getKey().toString() + "\n";
			for (NodeLocation location : argument.getValue()) {
				result += "\t\t" + location + "\n";
			}
		}
		
		return result;
	}
}
