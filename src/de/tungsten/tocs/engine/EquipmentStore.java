package de.tungsten.tocs.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.WeaponNode;
import de.tungsten.tocs.engine.parsing.NodeLocator;
import de.tungsten.tocs.menus.NumericalMenu;
import de.tungsten.tocs.menus.NumericalSelector;
import de.tungsten.tocs.net.IPlayerConnection;

/**
 * <code>EquipmentStore</code> zeigt ein Auswahlmenüs zur Auswahl von Equipment für einen 
 * Spieler an. Der Spieler muss dabei zunächst die Waffenkategorie festlegen, dann die
 * eigentliche Waffe.
 * <p>
 * Die Waffe wird dem Spieler direkt in die Hände gelegt, aber nur, wenn er sie sich
 * auch leisten kann. Kann er das, wird ihm der Preis der Waffe an Guthaben abgezogen.
 * <p>
 * Die Waffendaten werden dynamisch aus einer XML-Datei gelesen. Zur Validierung der
 * XMl-Datei kann die data/weapontypes.xsd XML-Schema-Datei verwendet werden.
 * 
 * @author reinke.wolfram-tit13
 *
 */
public class EquipmentStore implements ISubSystem {

	/**
	 * Eine Waffenkategorie wie z.B. "Sturmgewehr". Diese Klasse besteht aus dem
	 * Namen der Kategorie und allen Waffen, die dazu gehören.
	 * 
	 * @author tungsten
	 *
	 */
	private class Category {
		public String name;
		public List<WeaponType> weapons;
		
		public Category( String name ) {
			this.name = name;
			weapons = new ArrayList<WeaponType>();
		}
	}
	
	/**
	 * Die Waffenkategorien, wie sie aus der XML-Datei gelesen wurden.
	 */
	private static List<Category> weaponTypes;
	
	/**
	 * Erstellt eine neue Instanz von EquipmentStore, und lädt gleich die
	 * Waffendaten aus der gegebenen XML-Datei.
	 * 
	 * @param xmlData		Die XML-Datei mit den Waffendaten
	 * @throws Exception	Wenn beim Laden der Waffendaten ein Fehler aufgetreten
	 * 						ist.
	 */
	public EquipmentStore( File xmlData ) throws Exception {
		
		if ( weaponTypes == null ) {
			
			weaponTypes = new ArrayList<Category>();
			loadItems( xmlData );
			
		}
	}
	
	/**
	 * Zeigt dem Spieler an der gegebenen Verbindung zunächst ein Auswahlmenü
	 * für die Waffenkategorie an, und danach ein Auswahlmenü für die eigentliche
	 * Waffe.
	 * <p>
	 * Wenn er sich für eine Waffe entschieden hat, wird ihm die Waffe in die 
	 * Hand gegeben (siehe {@link Player} und ihm der Preis der Waffe vom
	 * Guthaben abgezogen, sofern er sich die Waffe leisten kann.
	 * 
	 */
	@Override
	public void operate( IPlayerConnection connection ) {
		
		// Menu aufbauen
		NumericalMenu categoryMenu = new NumericalMenu( "Which kind of equipment do you want to buy?" );
		
		for (Category current : weaponTypes) {
			
			NumericalSelector weaponMenu = new NumericalSelector( "Which " + current.name + " do you want to buy?" );
			
			for (WeaponType weapon : current.weapons) {
				weaponMenu.addOption( weapon.getName() + " ($" + weapon.getPrice() + ")", weapon );
			}
			
			categoryMenu.addOption( current.name, weaponMenu );
		}
		
		// Menu anzeigen
		WeaponType selection = (WeaponType) categoryMenu.display( connection );
		
		if ( connection.getPlayer().consumeMoney( selection.getPrice() ) ) {
			// consumeMoney gibt true zurück, wenn sich der Spieler das leisten kann.
			Node hands = NodeLocator.findNodeAtPlayer( Node.NODE_HANDS, connection.getPlayer() );
			hands.addChild( new WeaponNode( connection.getPlayer(), new String[] { selection.getName() }, "", selection) );
		}
	}
	
	/**
	 * Lädt die Waffenkategorien und -typen aus der gegebenen XML-Datei.
	 * 
	 * @param xml			Die Xml-Datei, aus der die Daten geladen werden.
	 * @throws Exception	Wenn beim Laden ein Fehler auftritt.
	 */
	private void loadItems( File xml ) throws Exception {
		
		Document xmlDocument = (new SAXBuilder()).build( xml );
		
		Element root = xmlDocument.getRootElement();
		List<Element> weapons = root.getChildren();
		
		for (Element item : weapons) {
			//if ( item.getNamespace().equals( "tocsdb.html" ) ) {
				
				// Alle Daten, die für den Konstruktor-Aufruf benötigt werden
				// aus der Datei ziehen
				Namespace ns = item.getNamespace();
			
				String 	category 	= item.getChildText( "category", ns);
				String 	name		= item.getChildText( "name", ns );
				String 	description = item.getChildText( "description", ns );
				int		capacity	= Integer.parseInt( item.getChildText( "capacity", ns ) );
				int		rate		= Integer.parseInt( item.getChildText( "rate", ns ) );
				int 	damage		= Integer.parseInt( item.getChildText( "damage", ns ) );
				int		price		= Integer.parseInt( item.getChildText( "price", ns ) );
				
				// Wenn die Kategorie schon gibt, dann adden, sonst neu
				// erstellen und auch adden
				Category currentCategory = null;
				for (Category cat : weaponTypes) {
					if ( cat.name.equals( category ) ) {
						currentCategory = cat;
						break;
					}
				}
				if ( currentCategory == null ) {
					currentCategory = new Category( category );
					weaponTypes.add( currentCategory );
				}
				
				// Waffe saven.
				currentCategory.weapons.add( new WeaponType( name, description, capacity, rate, damage, price ) );
			//}
		}
	}
}
