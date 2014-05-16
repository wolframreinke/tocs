package de.tungsten.tocs.engine.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein {@link OpenableNode schlie�barer Knoten}, der sich zu und aufschlie0en
 * l�sst. F�r diese Operationen stehen die Methoden {@link #lock(String)} und
 * {@link #unlock(String)} bereit, die jeweils als �bergabeparameter den
 * Identifier des Schl�ssels erwarten. Um abzufragen, ob der Knoten 
 * verschlossen ist, steht {@link #isLocked()} bereit.
 * <p>
 * F�r einen <code>LocableNode</code> stehen mehrere Schl�ssel zur Verf�gung,
 * mit denen man ihn aufschlie�en kann. Schl�ssel k�nnen mit 
 * {@link #addKey(String)} hinzugef�gt werden.
 * 
 * TODO Neu designen?
 * 
 * @author Wolfram
 *
 */
public class LockableNode extends OpenableNode {

	/**
	 * Speichert, ob der Knoten abgeschlossen (<code>true</code>) oder
	 * aufgeschlossen (<code>false</code>) ist.
	 */
	private boolean locked;
	
	/**
	 * Die Schl�ssel die verwendet werden k�nnen, um diesen Knoten auf-
	 * bzw. zuzuschlie�en. Als Schl�ssel sollte hier am besten die
	 * Identifier der entsprechenden Knoten benutzt werden.
	 * TODO Warum dann nicht gleich die Nodes?
	 */
	private List<String> keys = new ArrayList<String>();
	
	/**
	 * Erstellt einen neuen <code>LocableNode</code> ohne Parent-Knoten.
	 * Damit wird ein Root-Knoten erstellt. Die Namen und die Beschreibung
	 * (siehe {@link Node}), sowie <code>closed</code> 
	 * (siehe {@linkOpenableNode}) werden festgelegt. Zus�tzlich muss 
	 * festelegt werden, ob dieser Knoten verschlossen oder nicht verschlossen
	 * ist.
	 * 
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Dieser Wert darf
	 * 						nicht <code>null</code> sein, sonst wird eine
	 * 						<code>NodeException</code> geworfen.
	 * @param closed		Ob der Knoten initial ge�ffent (<code>false</code>)
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
	 * Zus�tzlich muss festelegt werden, ob dieser Knoten verschlossen oder 
	 * nicht verschlossen ist.
	 * 
	 * @param parent		Der diesem Knoten �bergeordnete Knoten.
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Dieser Wert darf
	 * 						nicht <code>null</code> sein, sonst wird eine
	 * 						<code>NodeException</code> geworfen.
	 * @param closed		Ob der Knoten initial ge�ffent (<code>false</code>)
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
	 * �ffnet diesen Knoten, unter der Voraussetzung, dass er aufgeschlossen
	 * ist.
	 * 
	 * @return	<code>true</code>, wenn der Knoten aufgeschlossen war, und
	 * 			daher erfolgreich ge�ffnet wurde, <code>false</code> wenn
	 * 			er nicht ge�ffnet werden konnte, weil er verschlossen war.
	 */
	@Override
	public boolean open() {
		
		if ( !locked ) closed = false;
		return !locked;
	}

	/**
	 * Schlie�t diesen Knoten unter der Verwendung des gegebenen Schl�ssels
	 * ab. Wenn der Schl�ssel nicht passt, wird der Knoten auch nicht 
	 * abgeschlossen.
	 * 
	 * @param key	Der Schl�ssel, der zum abschlie�en verwendet werden soll.
	 * @return		<code>true</code>, wenn der Schl�ssel gepasst hat, und
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
	 * Schlie�t diesen Knoten unter der Verwendung des gegebenen Schl�ssels
	 * auf. Wenn der Schl�ssel nicht passt, wird der Knoten auch nicht 
	 * aufgeschlossen.
	 * 
	 * @param key	Der Schl�ssel, der zum aufschlie�en verwendet werden soll.
	 * @return		<code>true</code>, wenn der Schl�ssel gepasst hat, und
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
	 * Gibt zur�ck, ob dieser Knoten abgeschlossen ist.
	 * 
	 * @return	<code>true</code>, wenn dieser Knoten abgeschlossen ist,
	 * 			anderfalls <code>false</code>.
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * F�gt diesem Knoten einen Schl�ssel hinzu, sodass der Aufruf von
	 * {@link #lock(String)} oder {@link #unlock(String)} mit demselben
	 * Schl�ssel <code>true</code> zur�ckliefert.
	 * 
	 * @param identifier	Der hinzuzuf�gende Schl�ssel.
	 * @return				Dieser Knoten, zwecks chaining.
	 */
	public LockableNode addKey( String identifier ) {
		
		if ( !keys.contains( identifier ) ) keys.add( identifier );
		return this;
	}
}
