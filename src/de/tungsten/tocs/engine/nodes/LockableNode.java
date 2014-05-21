package de.tungsten.tocs.engine.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein {@link OpenableNode schließbarer Knoten}, der sich zu und aufschlie0en
 * lässt. Für diese Operationen stehen die Methoden {@link #lock(String)} und
 * {@link #unlock(String)} bereit, die jeweils als Übergabeparameter den
 * Identifier des Schlüssels erwarten. Um abzufragen, ob der Knoten 
 * verschlossen ist, steht {@link #isLocked()} bereit.
 * <p>
 * Für einen <code>LocableNode</code> stehen mehrere Schlüssel zur Verfügung,
 * mit denen man ihn aufschließen kann. Schlüssel können mit 
 * {@link #addKey(String)} hinzugefügt werden.
 * 
 * TODO Neu designen?
 * 
 * @author tungsten
 *
 */
public class LockableNode extends OpenableNode {

	/**
	 * Speichert, ob der Knoten abgeschlossen (<code>true</code>) oder
	 * aufgeschlossen (<code>false</code>) ist.
	 */
	private boolean locked;
	
	/**
	 * Die Schlüssel die verwendet werden können, um diesen Knoten auf-
	 * bzw. zuzuschließen. Als Schlüssel sollte hier am besten die
	 * Identifier der entsprechenden Knoten benutzt werden.
	 * TODO Warum dann nicht gleich die Nodes?
	 */
	private List<String> keys = new ArrayList<String>();
	
	/**
	 * Erstellt einen neuen <code>LocableNode</code> ohne Parent-Knoten.
	 * Damit wird ein Root-Knoten erstellt. Die Namen und die Beschreibung
	 * (siehe {@link Node}), sowie <code>closed</code> 
	 * (siehe {@linkOpenableNode}) werden festgelegt. Zusätzlich muss 
	 * festelegt werden, ob dieser Knoten verschlossen oder nicht verschlossen
	 * ist.
	 * 
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						können. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Dieser Wert darf
	 * 						nicht <code>null</code> sein, sonst wird eine
	 * 						<code>NodeException</code> geworfen.
	 * @param closed		Ob der Knoten initial geöffent (<code>false</code>)
	 * 						oder geschlossen (<code>true</code>) ist.
	 * @param locked		Ob der Knoten initial aufgeschlossen
	 * 						(<code>false</code>) oder abgeschlossen
	 * 						(<code>true</code>) ist.
	 */
	public LockableNode( String[] names, String description, boolean closed,
			boolean locked ) {
		this( null, names, description, closed, locked );
	}
	
	/**
	 * Erstellt einen neuen <code>LocableNode</code>. 
	 * Die Namen und die Beschreibung (siehe {@link Node}), sowie 
	 * <code>closed</code> (siehe {@linkOpenableNode}) werden festgelegt. 
	 * Zusätzlich muss festelegt werden, ob dieser Knoten verschlossen oder 
	 * nicht verschlossen ist.
	 * 
	 * @param parent		Der diesem Knoten übergeordnete Knoten.
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						können. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Dieser Wert darf
	 * 						nicht <code>null</code> sein, sonst wird eine
	 * 						<code>NodeException</code> geworfen.
	 * @param closed		Ob der Knoten initial geöffent (<code>false</code>)
	 * 						oder geschlossen (<code>true</code>) ist.
	 * @param locked		Ob der Knoten initial aufgeschlossen
	 * 						(<code>false</code>) oder abgeschlossen
	 * 						(<code>true</code>) ist.
	 */
	public LockableNode(Node parent, String[] names, String description,
			boolean closed, boolean locked) {
		super(parent, names, description, closed);

		this.locked = locked;
	}
	
	/**
	 * Öffnet diesen Knoten, unter der Voraussetzung, dass er aufgeschlossen
	 * ist.
	 * 
	 * @return	<code>true</code>, wenn der Knoten aufgeschlossen war, und
	 * 			daher erfolgreich geöffnet wurde, <code>false</code> wenn
	 * 			er nicht geöffnet werden konnte, weil er verschlossen war.
	 */
	@Override
	public boolean open() {
		
		if ( !locked ) closed = false;
		return !locked;
	}

	/**
	 * Schließt diesen Knoten unter der Verwendung des gegebenen Schlüssels
	 * ab. Wenn der Schlüssel nicht passt, wird der Knoten auch nicht 
	 * abgeschlossen.
	 * 
	 * @param key	Der Schlüssel, der zum abschließen verwendet werden soll.
	 * @return		<code>true</code>, wenn der Schlüssel gepasst hat, und
	 * 				der Knoten erfolgreich abgeschlossen werden konnte,
	 * 				andernfalls <code>false</code>
	 */
	public boolean lock( String key ) {
		if ( keys.contains( key ) ) {
			locked = true;
			return false;
		}
		else return false;
	}
	
	/**
	 * Schließt diesen Knoten unter der Verwendung des gegebenen Schlüssels
	 * auf. Wenn der Schlüssel nicht passt, wird der Knoten auch nicht 
	 * aufgeschlossen.
	 * 
	 * @param key	Der Schlüssel, der zum aufschließen verwendet werden soll.
	 * @return		<code>true</code>, wenn der Schlüssel gepasst hat, und
	 * 				der Knoten erfolgreich aufgeschlossen werden konnte,
	 * 				andernfalls <code>false</code>
	 */
	public boolean unlock( String key ) {
		if ( keys.contains( key ) ) {
			locked = false;
			return true;
		}
		else return false;
	}
	
	/**
	 * Gibt zurück, ob dieser Knoten abgeschlossen ist.
	 * 
	 * @return	<code>true</code>, wenn dieser Knoten abgeschlossen ist,
	 * 			anderfalls <code>false</code>.
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Fügt diesem Knoten einen Schlüssel hinzu, sodass der Aufruf von
	 * {@link #lock(String)} oder {@link #unlock(String)} mit demselben
	 * Schlüssel <code>true</code> zurückliefert.
	 * 
	 * @param identifier	Der hinzuzufügende Schlüssel.
	 * @return				Dieser Knoten, zwecks chaining.
	 */
	public LockableNode addKey( String identifier ) {
		
		if ( !keys.contains( identifier ) ) keys.add( identifier );
		return this;
	}
}
