package de.tungsten.tocs.engine.nodes;

import de.tungsten.tocs.engine.WeaponType;

/**
 * Ein <code>WeaponNode</code> ist ein Knoten, der eine Waffe im Spiel
 * repr�sentiert. Ein Waffenknoten kann abgefeuert und nachgeladen werden.
 * Der Typ der Waffe wird mit {@link #getType()} abgefragt.
 * 
 * @author tungsten
 *
 */
public class WeaponNode extends Node {
	
	/**
	 * Der <code>WeaponType</code> der Waffe, z.B. "AK-47".
	 */
	private WeaponType type;
	
	/**
	 * Die Munition, die sich momentan im Magazin befindet.
	 */
	private int ammo;
	
	/**
	 * Erstellt einen neuen Waffenknotne ohne Parent-Knoten. Dabei
	 * werden die Namen und die Beschreibung dieses {@link Node Knotens} 
	 * festgelegt. Zus�tzlich muss der {@link WeaponType Type} der Waffe
	 * �bergeben werden.
	 * 
	 * @param names			Die Namen dieses Knotens, also die Zeichenkette,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen. Als Namen f�r einen Waffenknoten werden
	 * 						der Name des entsprechenden <code>WeaponType</code>s
	 * 						empfohlen.
	 * @param description	Die Beschreibung dieses Knotens. Ist dieser Wert
	 * 						<code>null</code> wird eine <code>NodeException</code>
	 * 						geworfen. Als Beschreibung eines Waffenknotens wird
	 * 						die Beschreibung des entsprechenden 
	 * 						<code>WeaponType</code>s empfohlen.
	 * @param type			Der Typ dieser Waffen.
	 */
	public WeaponNode( String[] names, String description,
			WeaponType type ) {
		this( null, names, description, type );
	}
	
	/**
	 * Erstellt einen neuen Waffenknotne. Dabei
	 * werden die Namen und die Beschreibung dieses {@link Node Knotens} 
	 * festgelegt. Zus�tzlich muss der {@link WeaponType Type} der Waffe
	 * �bergeben werden.
	 * 
	 * @param parent		Der diesem Knoten �bergeordnete Knoten.
	 * @param names			Die Namen dieses Knotens, also die Zeichenkette,
	 * 						mit denen sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens einen Wert
	 * 						enthalten, sonst wird eine <code>NodeException</code>
	 * 						geworfen. Als Namen f�r einen Waffenknoten werden
	 * 						der Name des entsprechenden <code>WeaponType</code>s
	 * 						empfohlen.
	 * @param description	Die Beschreibung dieses Knotens. Ist dieser Wert
	 * 						<code>null</code> wird eine <code>NodeException</code>
	 * 						geworfen. Als Beschreibung eines Waffenknotens wird
	 * 						die Beschreibung des entsprechenden 
	 * 						<code>WeaponType</code>s empfohlen.
	 * @param type			Der Typ dieser Waffen.
	 */
	public WeaponNode(Node parent, String[] names, String description, WeaponType type) {
		super(parent, names, description);
		
		// Typ darf nicht null sein.
		if ( type == null )
			throw new NodeException( getID(), "WeaponNode initialization failed." );
		
		this.type = type;
	}

	/**
	 * Gibt den {@link WeaponType Typ} dieser Waffe zur�ck.
	 * 
	 * @return Den Typ dieser Waffe.
	 */
	public WeaponType getType() {
		return type;
	}
	
	/**
	 * Berechnet den Schaden, den ein Schuss bei einem Spieler anrichtet
	 * und verringert die im Magazin befindliche Munition.
	 * 
	 * @return Der Schaden, den der Schuss beim Spieler anrichtet.
	 */
	public int computeShotDamage() {
		
		int damage = 0;
		
		// Wenn die Feuerrate gr��er ist als die vorhandene Munition wird
		// der Rest der Muntion verschossen.
		int i = type.getRate() > ammo ? ammo : type.getRate();
		while ( i > 0 ) {
			
			damage += type.getDamage();
			
			ammo--;
			i--;
		}
		
		return damage;
	}
	
	/**
	 * F�llt das Magazin dieser Waffe soweit wie m�glich auf.
	 * Die Magazingr��e ist in {@link WeaponType} definiert.
	 */
	public void reload() {
		ammo = type.getCapacity();
	}
	
	/**
	 * Gibt zur�ck, wieviel Munition sich aktuell im Magazin befindet.
	 * 
	 * @return Die Anzahl der im Magazin befindlichen Geschosse.
	 */
	public int getAmmonition() {
		return ammo;
	}
	
}
