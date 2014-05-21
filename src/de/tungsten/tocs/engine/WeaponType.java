package de.tungsten.tocs.engine;

import de.tungsten.tocs.engine.nodes.WeaponNode;

/**
 * Beinhaltet die Daten f�r ein Waffenmodell (z.B. Ak-47). Das umfasst den Namen
 * des Modells, die Magazin-Kapazit�t, die Feuerrate, den Schaden und den
 * Preis im {@link EquipmentStore}.
 * <p>
 * Objekte dieser Klasse sind immutable, sie werden im Konstruktor vollst�ndig
 * initialisiert und �ndern sich nicht mehr.
 * <p>
 * Wichtig ist, dass es von einem Waffentyp (den diese Klasse repr�sentiert)
 * mehrere Instnazen im Spiel geben kann (Es k�nnen mehrere Spieler eine
 * AK-47 haben). Einzelne Waffen werden durch die Klasse 
 * {@link WeaponNode} abgebildet.
 * 
 * @author tungsten
 *
 */
public class WeaponType {

	/**
	 * Der Name des Waffenmodells (z.B. "AK-47").
	 */
	private String name;
	
	/**
	 * Die Beschreibung dieses Waffenmodells.
	 */
	private String description;
	
	/**
	 * Die Kapazit�t eines Magazins f�r dieses Modell. Dabei wird ignoriert,
	 * dass eventuell mehrere Magazin-Typen in ein Waffenmodell passen w�rden,
	 * irgendwo muss man mal einen Strich ziehen.
	 */
	private int	capacity;
	
	/**
	 * Die Feuerrate der Waffe. Da andere Implementierungen f�r ein text-only 
	 * Spiel merkw�rdig w�ren, bezeichnet dieses Attribut die Anzahl der Sch�sse
	 * die immer zugleich abgegeben werden. So k�nnen Halb- und Vollautomatische
	 * Waffen modelliert werden.
	 */
	private int rate;
	
	/**
	 * Der Schaden den die Waffe anrichtet. Ein Spieler hat 100 Lebenspunkte.
	 */
	private int damage;
	
	/**
	 * Der Preis der Waffe im {@link EquipmentStore}.
	 */
	private int price;
	
	/**
	 * Erstellt eine neue Instanz von <code>WeaponType</code>.
	 * 
	 * @param name			{@link #name}
	 * @param description	{@link #description}
	 * @param capacity		{@link #capacity}
	 * @param rate			{@link #rate}
	 * @param damage		{@link #damage}
	 * @param price			{@link #price}
	 */
	public WeaponType( String name, String description, int capacity, int rate, int damage, int price ) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.rate = rate;
		this.damage = damage;
		this.price = price;
	}
	
	/**
	 * Gibt den {@link #name} des Waffenmodells zur�ck.
	 * 
	 * @return den Namen des Waffemodells.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gibt die {@link #description Beschreibung} des Waffenmodells
	 * zur�ck.
	 * 
	 * @return	Die Beschreibung des Waffenmodells.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gibt die {@link #capacity} des Waffenmodells zur�ck.
	 * 
	 * @return Die Magazin-Kapazit�t des Waffenmodells.
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Gibt die {@link #rate} des Waffenmodells zur�ck.
	 * 
	 * @return Die Feuerrate des Waffenmodells.
	 */
	public int getRate() {
		return rate;
	}
	
	/**
	 * Gibt den {@link #damage} des Waffenmodells zur�ck.
	 * 
	 * @return Den Schaden, den die Waffe beim Spieler anrichtet.
	 */
	public int getDamage() {
		return damage;
	}
	
	/**
	 * Gibt den {@link #price} des Waffenmodells zur�ck.
	 * 
	 * @return 	Den Preis eine Waffe dieses Modells im 
	 * 			<code>EquipmentStore</code>
	 */
	public int getPrice() {
		return price;
	}
	
	public WeaponNode newInstance() {
		return new WeaponNode( new String[] { name }, description, this );
	}
}
