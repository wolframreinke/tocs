package de.tungsten.tocs.engine.parsing;

/**
 * A <code>Preposition</code> is a word in the player's input, which is not the description/name/identifier of a
 * node in the game. Instead, it groups the "arguments" of the player's instructions. For instance, in the input
 * <p><code>
 * put the ball in the box on the desk into the chest on the cupboard.
 * </code><p>
 * the prepositions (namely <code>in</code>, <code>on</code>, <code>into</code>) form a tree structure that
 * specifies i.a. that the ball is located in the box, which is located on the desk.
 * <p>
 * One or more prepositions in the english language make one preposition type. For example the prepositions
 * <code>in</code> and <code>on</code> are both instances of the same preposition type, namely <code>OF</code>.
 * These types are the members of <code>Preposition</code>:
 * <ul>
 * <li> {@link #WITH}
 * <li> {@link #TO}
 * <li> {@link #OF}
 * </ul>
 * 
 * @author Wolfram
 *
 */
public enum Preposition {
	
	/**
	 * <code>WITH</code> contains the following prepositions: "with" and "using". When the player uses one of these,
	 * it indicates, that he (formally) wants to pass an argument to a method, so
	 * <p><code>
	 * lock the door with the key
	 * </code><p>
	 * is internally transformed to
	 * <p><code>
	 * door.lock( key );
	 * </code><p>
	 * <code>OF</code> is not a recursive preposition (see {@link #isRecursive()}).
	 */
	WITH 	( new String[] {"with", "using"} ),
	
	/**
	 * <code>TO</code> contains the following prepositions: "to", "into", "onto", "over", "under" and "at". This
	 * is the second non-recursive preposition (see {@link #isRecursive()}) and is used to specify the target of 
	 * the players instruction, e.g.
	 * <p><code>
	 * put the ball into the box
	 * </code><p>
	 * The target is the box (due to "into").
	 */
	TO		( new String[] {"to", "into", "onto", "over", "under", "at"} ),
	
	/**
	 * <code>OF</code> contains the following prepositions: "of", "in", "on" and "from". It is the only recursive
	 * preposition (see {@link #isRecursive()}) and is therefore used to construct a "search path" for objects/subjects
	 * in the player's instructions, so
	 * <p><code>
	 * take the ball on the desk
	 * </code><p>
	 * makes clear, that the ball's "search path" is <code>room -> desk -> ball</code>.
	 */
	OF		( new String[] {"of", "in", "on", "from"} );
	
	String[] content;
	
	Preposition( String[] s ) {
		this.content = s;
	}
	
	/**
	 * Returns the <code>Preposition</code> which contains <code>s</code>, or <code>null</code> if there is
	 * no such <code>Preposition</code>. This method ignores the case of <code>s</code>.
	 * @param s		The String which is compared to the single prepositions.
	 * @return		The preposition corresponding to <code>s</code> or <code>null</code> if there is no
	 * 				such preposition.
	 */
	public static Preposition fromString( String s ) {
		
		for (Preposition current : Preposition.values()) {
			for (String content : current.content) {
				if ( content.equalsIgnoreCase( s ) ) return current;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return content[0];
	}
}
