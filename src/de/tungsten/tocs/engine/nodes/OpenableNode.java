package de.tungsten.tocs.engine.nodes;

/**
 * Ein {@link Node Knoten} im Spiel, der sich öffnen und schließen lässt. Für
 * diese Operationen stehen die Methode {@link #open()} und {@link #close()}
 * bereit, um den abzufragen, ob der Knoten gerade geschlossen oder offen
 * ist, gibt es {@link #isClosed()}.
 * <p>
 * <code>OpenableNode</code>s können zwar geöffnet und geschlossen werden,
 * aber nicht auf- und abgeschlossen werden. Für diese Funktionalität steht
 * {@link LockableNode} bereit.
 * 
 * @author Wolfram
 *
 */
public class OpenableNode extends Node {

	/**
	 * Speichert, ob dieser <code>OpenableNode</code> geschlossen 
	 * (<code>true</code>) oder offen (<code>false</code>) ist.
	 */
	protected boolean closed;
	
	/**
	 * Erstellt einen neuen <code>OpenableNode</code> ohne Parent-Knoten. Dabei
	 * werden die Namen und die Beschreibung (siehe {@link Node}) initialisiert,
	 * sowie gespeichert, ob der Knoten initial geschlossen oder offen sein
	 * soll.
	 * <p>
	 * Da als Parent-Knoten <code>null</code> angenommen wird, wird mit diesem
	 * Konstruktor ein Root-Knoten erstellt.
	 * 
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						können. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Ist die Beschreibung
	 * 						<code>null</code> wird eine Exception geworfen.
	 * @param closed		Ob der Knoten initial offen (<code>false</code>) oder
	 * 						geschlossen (<code>true</code>) ist.
	 */
	public OpenableNode( String[] names, String description, boolean closed ) {
		this( null, names, description, closed );
	}
	
	/**
	 * Erstellt eine neue Instanz von <code>OpenableNode</code>. Dabei wird
	 * der Parent-Knoten, die Namen und die Beschreibung festgelegt
	 * (siehe {@link Node}).
	 * 
	 * @param parent		Der diesem Knoten übergeordnete Knoten.
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						können. Dieses Array muss mindestens einen Wert 
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 *						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Ist die Beschreibung
	 * 						<code>null</code> wird eine <code>NodeException</code>
	 * 						geworfen
	 * @param closed		Ob der Knoten initial offen (<code>false</code>) oder
	 * 						geschlossen (<code>true</code>) ist.
	 */
	public OpenableNode(Node parent, String[] names, String description, boolean closed) {
		super( parent, names, description );
		
		this.closed = closed;
	}
	
	/**
	 * Öffnet diesen Knoten, d.h. setzt <code>closed</code> auf 
	 * <code>false</code>. Der Rückgabewert ist immer <code>true</code> und
	 * kann von Subklassen überschrieben werden, um das Öffnen zu verhindern.
	 * 
	 * @return	<code>true</code>.
	 */
	public boolean open() {
		
		closed = false;
		return true;	// Rückgabewert nur für Subklassen
	}
	
	/**
	 * Schließt diesen Knoten, d.h. setzt <code>closed</code> auf
	 * <code>true</code>. Der Rückgabewert ist immer <code>true</code> und
	 * kann von Subklassen überschrieben werden, um das Schlie0en zu verhindern.
	 * 
	 * @return <code>true</code>.
	 */
	public boolean close() {
		closed = true;
		return true;	// Rückgabewert nur für Subklassen
	}
	
	/**
	 * Gibt zurück, ob der Knoten geschlossen ist.
	 * 
	 * @return 	<code>true</code>, wenn der Knoten geschlossen ist,
	 * 			andernfalls <code>false</code>.
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Der gegebene Knoten wird nur hinzugefügt, wenn dieser Knoten offen ist.
	 */
	@Override
	public Node addChild( Node child ) {
		
		// Nur in offenen Kisten können Sachen gelegt werden.
		if ( !closed )
			return super.addChild( child );
		else return null;
	}
}
