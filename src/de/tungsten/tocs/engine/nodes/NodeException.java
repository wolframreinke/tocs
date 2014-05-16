package de.tungsten.tocs.engine.nodes;

/**
 * Diese <code>RuntimeException</code> wird von {@link Node} und seinen
 * Subklassen verwendet, um Fehler anzuzeigen. Der den Fehler verursachende 
 * Knoten kann über die {@link #nodeID} ermittelt werden.
 * 
 * @author Wolfram
 *
 */
@SuppressWarnings("serial")
public class NodeException extends RuntimeException {

	/**
	 * Die ID des Knotens, der den Fehler hervorgerufen hat.
	 */
	private final int nodeID;
	
	/**
	 * Erstellt eine neue Instanz von <code>NodeException</code>. Dabei werden
	 * der Verursacher des Fehlers und die Fehlernachricht festgelegt.
	 * 
	 * @param nodeID	Die ID des Knotens, der den Fehler verursacht hat.
	 * @param message	Die Fehlermeldung.
	 */
	public NodeException( int nodeID, String message ) {
		super( message );
		
		this.nodeID = nodeID;
	}
	
	/**
	 * Gibt die ID des Knotens zurück, der den Fehler hervorgerufen hat.
	 * 
	 * @return die {@link #nodeID}.
	 */
	public int getNodeID() {
		return nodeID;
	}
}
