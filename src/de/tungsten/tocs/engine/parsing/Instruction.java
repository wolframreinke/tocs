package de.tungsten.tocs.engine.parsing;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <code>Instruction</code> represents a type of instruction which consists of a verb, a
 * prefix and arguments. This kind of instruction can be used very often to represent a 
 * player's instruction.
 * <p>
 * To parse the player's input into an <code>Instruction</code> object, it needs to have the
 * following form:
 * <p>
 * <code>VERB PREFIX ARGUMENTS</code>
 * <p>
 * <code>VERB</code> is the first word of the instruction. <code>PREFIX</code> is the set
 * of identifiers found after the <code>VERB</code> but before the occurance of the first
 * {@link Preposition}. <code>ARGUMENTS</code> is a set of additional arguments, consisting
 * of a <code>Preposition</code> and a set of identifiers.
 * 
 * @author Wolfram
 * @see #InstructionParser
 */
public class Instruction {

	/**
	 * The verb of this <code>Instruction</code>, and therefore the first word in the player's
	 * input. In the string
	 * <p>
	 * <code>"open the door"</code>
	 * <p>
	 * <code>"open"</code> is the verb, as it is the first word of this instruction.
	 */
	private String verb;
	
	/**
	 * The prefix of this <code>Instruction</code>, and therefore the set of words found
	 * directly behind the verb, but before the first preposition. In the string
	 * <p>
	 * <code>"open the box with a knife"</code>
	 * <p>
	 * <code>"box"</code> is the prefix, as <code>"with"</code> is a preposition and
	 * <code>"open"</code> is a verb. 
	 */
	private Set<NodeLocation> prefix;
	
	/**
	 * The arguments of this <code>Instruction</code>, and therefore the additional
	 * instruction arguments, each consisting of a preposition and a set of identifiers.
	 * To easily access the desired arguments, they are organized in a <code>EnumMap</code>.
	 * In the string
	 * <p>
	 * <code>"open the box with a knife"</code>
	 * <p>
	 * <code>"with a knife"</code> is an argument and is stored such that 
	 * <code>arguments.get( Preposition.WITH )</code> returns i.a. <code>"knife"</code>.
	 */
	private EnumMap<Preposition, Set<NodeLocation>> arguments;
	
	public Instruction( 
			String verb, 
			Set<NodeLocation> prefix, 
			EnumMap<Preposition, Set<NodeLocation>> arguments ) {
		
		this.verb = verb;
		this.prefix = prefix;
		this.arguments = arguments;
	}

	/**
	 * Returns the {@link #verb} of this <code>Instruction</code>.
	 * @return the <code>verb</code> of this <code>Instruction</code>.
	 */
	public String getVerb() {
		return verb;
	}

	/**
	 * Returns the {@link #prefix} of this <code>Instruction</code>.
	 * @return the <code>prefix</code> of this <code>Instruction</code>.
	 */
	public Set<NodeLocation> getPrefix() {
		return prefix;
	}

	/**
	 * Returns the {@link #arguments} of this <code>Instruction</code>.
	 * @return the <code>arguments</code> of this <code>Instruction</code>.
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
