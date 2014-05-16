package de.tungsten.tocs.engine.parsing;

import java.util.List;

import de.tungsten.tocs.engine.nodes.INodePredicate;
import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.Player;

/**
 * A <code>NodeLocator</code> provides a simple interface to the <code>Node</code>
 * tree. Instead of using the <code>Node</code>'s methods like 
 * <code>Node.find(...)</code>, the user can use the methods of this class, which
 * provide more specialized functionalities.
 * 
 * @author Wolfram
 *
 */
public abstract class NodeLocator {
	
	/**
	 * Finds a <code>Node</code> with the given name which is a child node of the given
	 * parent <code>Node</code>.
	 * <p>
	 * <code>Node</code>'s whose identifier matches are preferred to <code>Node</code>'s
	 * which names match.
	 * <p>
	 * If more than one <code>Node</code> matches the name, the first one is returned,
	 * if no <code>Node</code> matches, <code>null</code> is returned.
	 * 
	 * @param name			The identifier/name of the desired <code>Node</code>.
	 * @param parent		The parent <code>Node</code> whose child the desired 
	 * 						<code>Node</code> is.
	 * @param depth			The search depth.
	 * @return				A <code>Node</code> with the given identifier or name,
	 * 						which is located at the given parent <code>Node</code>.
	 */
	public static Node findSubNode( final String name, Node parent, int depth ) {
		
		// Anonymous class for name comparison in Node.find(...)
		INodePredicate nameComparison = new INodePredicate() {
			
			@Override
			public boolean matches( Node node ) {
				
				for (String nodeName : node.getNames()) {
					if ( nodeName.equals( name ) )
						return true;
				}
				return false;
			}
			
		};
		
		Node result = null;
		// Look for matching identifier first
		result = parent.findByIdentifier( name, depth );
		
		if ( result == null ) {
			// Now look for names by using the anonymous class
			List<Node> candidates = parent.find( nameComparison, depth );
			
			// candidates shouldn't be null, but better safe than sorry.
			if ( candidates != null && candidates.size() > 0 )
				result = candidates.get( 0 );
			
		}
		
		return result;
	}
	
	/**
	 * Finds a <code>Node</code> in the environment of a player by name. To be found
	 * by this method, the desired <code>Node</code> bust me located...
	 * <ul>
	 * <li>... in the same room as the player, or
	 * <li>... somewhere in the inventory located at the player, or
	 * <li>... in the hands located at the player.
	 * </ul>
	 * <p>
	 * If both, a <code>Node</code>'s identifier and another <code>Node</code>'s
	 * name match, the <code>Node</code>, whose identifier matches is preferred.
	 * <p>
	 * If more than one <code>Node</code> matches, the first match is returned. If no
	 * <code>Node</code> matches, <code>null</code> is returned.
	 * 
	 * @param name		The identifier or name of the desired <code>Node</code>.
	 * @param player	The player at which the desired <code>Node</code> is located.
	 * @return			A <code>Node</code> with the given identifier, or if there is
	 * 					no such <code>Node</code>, the <code>Node</code> with the given
	 * 					name, which is located at the given player. If no
	 * 					<code>Node</code> matches, <code>null</code> is returned.
	 */
	public static Node findNodeAtPlayer( String name, Player player ) {
		
		// Look at the players inventory and hands with infinite search depth
		Node result = findSubNode( name, player, -1 );
		
		// Look at the room, the player is located in
		if ( result == null )
			result = findSubNode( name, player.getParent(), 1 );
		
		return result;
	}
	
	/**
	 * Finds a <code>Node</code> in the environment of a player by a collection of locators.
	 * Generally this methods is able to find the same <code>Nodes</code> as 
	 * {@link #findNodeAtPlayer(String, Player)}. 
	 * <p>
	 * The first given locator is the root. This method will search for it using
	 * <code>findNodeAtPlayer(...)</code>. All following locators are then located in the
	 * context of the previosly located node.
	 * <br><b>Example:</b><br>
	 * <code>{ "n1", "n2", "n3", "n4" }</code>; <code>n1</code> is located first at the
	 * given player. Afterwards <code>n2</code> is located at <code>n1</code>, then
	 * <code>n3</code> is located at <code>n2</code> and so forth. 
	 * <p>
	 * <code>Node</code>'s whose identifiers match the current locator are preferred to
	 * <code>Node</code>'s whose names match.
	 * <p>
	 * If more than one <code>Node</code> match the current locator, the first one is
	 * chosen. If no <code>Node</code> matches the current locator, the search is cancelled
	 * and <code>null</code> is returned.
	 * 
	 * @param locators		An <code>Iterable&lt;String&gt;</code> which is used to
	 * 						locate the desired <code>Node</code>.
	 * @param player		The player at which the first <code>Node</code> in
	 * 						<code>locators</code> is located.
	 * @return				A <code>Node</code> which identifier or name matches the
	 * 						last <code>locator</code>, which is located at the "path"
	 * 						that is specified by the <code>locators</code>.
	 */
	public static Node findNodeUsingLocators( NodeLocation locators, Player player ) {

		// This boolean is set to false after the first iteration. The currentNode
		// is initlially found by findNodeAtPlayer(...).
		boolean lookAtPlayer = true;
		Node currentNode = null;
		
		for (String locator : locators) {
			
			// If first iteration
			if ( lookAtPlayer ) {
				
				// find root node.
				lookAtPlayer = false;
				currentNode = findNodeAtPlayer( locator, player );
			} else {
					
				// find next node in the locator chain
				currentNode = findSubNode( locator, currentNode, 1 );	
			}
			
			// if one of the chain elements was not found, return null
			if ( currentNode == null )
					return null;
		}
		
		
		return currentNode;
		
	}
}
