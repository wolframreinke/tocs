package de.tungsten.tocs.engine.parsing;

import de.tungsten.tocs.net.IPlayerConnection;

/**
 * <code>IParser</code> is the interface that all classes which are able to process
 * a player's input in form of a string have in common. The Instruction Parsing
 * System first a suitable parser to process the input by using the
 * {@link #getKeywords()} method. Afterwards, the chosen parser's 
 * {@link #parse(String, IPlayerConnection)} method is called to interprete the input.
 * 
 * @author Wolfram
 *
 */
public interface IParser {
	
	/**
	 * Returns a array of strings which identify this parser. The Instruction Parsing
	 * System chooses which parser is used to process the player's input using these
	 * keywords. more than one implementation provides the same keyword, the one which
	 * matched first will be chosen.
	 * <p>
	 * <b>Example:</b><br>
	 * A parser for modifying <code>OpenableNodes</code> may return the keywords
	 * "open" and "close". When the player enters an instruction starting with one of
	 * these keywords, this parser is chosen for interpreting the input.
	 * 
	 * @return	The keywords which identify this parser.
	 */
	public abstract String[] getKeywords();
	
	
	/**
	 * Interpretes a player's input and performs the corresponding actions. The
	 * parameter <code>connection</code> contains a <code>Player</code> object. This
	 * parser will asusme that this player issued the command.
	 * 
	 * @param input			The textual input which has to be interpreted.
	 * @param connection	The connection (and therefore player) on which the parsed
	 * 						action is performed.
	 */
	public abstract void parse( String input, IPlayerConnection connection );
}
