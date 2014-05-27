package de.tungsten.tocs.engine.maps;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import de.tungsten.tocs.LogLevel;
import de.tungsten.tocs.Logger;
import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.config.StringType;
import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.nodes.DoorDirection;
import de.tungsten.tocs.engine.nodes.LockableNode;
import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.engine.nodes.OpenableNode;
import de.tungsten.tocs.engine.nodes.Room;

/**
 * L�dt <code>Map</code> aus XML-Dateien. Die XML-Dateien sollten durch
 * die XML-Schema-Datei data/xmlmap/xml.xsd validiert werden.
 * <p>
 * Der Pfad zu der XML-Datei, die die erforderlichen Daten enth�lt wird
 * aus der Konfigurations-Datei (Key: "map") ausgelesen. Um einen
 * <code>XMLMapProvider</code> als MapLoader zu verwenden, muss in der
 * Konfigurations-Datei der Schl�ssel "mapLoader" auf "XMLMapProvider" gesetzt
 * werden.
 * 
 * @author tungsten
 *
 */
public class XMLMapProvider implements IMapProvider {
	
	public static final String LOG_NAME			= "(CORE) XMLMapProvider";
	
	/**
	 * Die Zeichenkette, die in der Konfigurations-Datei als Wert f�r den
	 * Schl�ssel "mapLoader" verwendet werden muss, damit ein 
	 * <code>XMLMapProvider</code> als MapLoader f�r TOCS erstellt wird.
	 */
	public static final String CONFIGURATION_NAME = "XMLMapProvider";
	
	/**
	 * Die Klasse <code>Reference</code> repr�sentiert eine noch nicht
	 * aufgel�ste Referenz in der XML-Datei, aus der die <code>Map</code>
	 * geladen wird.
	 * <p>
	 * In der XML-Datei k�nnte zum Beispiel ein "LockableNode" mit der
	 * Zeichenkette "map.room1.key" einen anderen Knoten referenzieren. Diese
	 * Zeichenkette wird zusammen mit dem Referenztyp und dem 
	 * <code>LockableNode</code> als <code>Reference</code> gespeichert.
	 * 
	 * @author tungsten
	 *
	 */
	private class Reference {
		
		/**
		 * Der Reference Identifier des referenzierenden Knotens,
		 * z.B. "map.room1.chest"
		 */
		private final String ownerID;
		
		/**
		 * Der Reference Identifier des referenzierten Knotens,
		 * z.B. "map.room1.key".
		 */
		private final String targetID;
		
		/**
		 * Der Name der XML-Elements, das die Referenz enthielt, und damit der
		 * Typ der Referenz. Beispiele sind "key" oder "north".
		 */
		private final String elementName;
		
		/**
		 * Erstellt eine neue Instanz von <code>Reference</code>,
		 * 
		 * @param ownerID		{@link #ownerID}
		 * @param targetID		{@link #targetID}
		 * @param elementName	{@link #elementName}
		 */
		public Reference( String ownerID, String targetID, String elementName ) {
			this.ownerID = ownerID;
			this.targetID = targetID;
			this.elementName = elementName;
		}

		/**
		 * @return {@link #ownerID}
		 */
		public String getOwnerID() {
			return ownerID;
		}

		/**
		 * @return {@link #targetID}.
		 */
		public String getTargetID() {
			return targetID;
		}

		/**
		 * @return {@link #elementName}.
		 */
		public String getElementName() {
			return elementName;
		}
		
	}

	/*
	 *  Die Namen der XML-Elemente, die in den Dateien verwendet werden.
	 */
	private static final String REFERENCE_ID			= "refID";
	private static final String ID						= "id";
	private static final String NAME					= "name";
	private static final String DESCRIPTION				= "desc";
	private static final String CLOSED					= "closed";
	private static final String LOCKED					= "locked";
	private static final String KEY						= "key";
	private static final String NODE					= "node";
	private static final String ROOT					= "root";
	private static final String BOMB_POINT				= "bombPoint";
	private static final String SPAWN_TERRORISTS		= "spawnTerrorists";
	private static final String SPAWN_COUNTERTERRORISTS	= "spawnCounterterrorists";
	
	
	/*
	 * Eintr�ge in der Konfigurations-Datei
	 */
	private static final String CONFIG_MAP_PATH			= "map";
	private static final String DEFAULT_MAP_PATH		= "data/xmlmap/testmap.xml";
	
	/**
	 * Die XML-Datei, aus der die <code>Map</code> geladen wird.
	 */
	private File xmlFile;
	
	/**
	 * Eine Referenz auf den {@link Logger} zum loggen.
	 */
	private Logger logger;
	
	/**
	 * Dieses Attribut mappt Reference Identifier (wie z.B. "map.cafeteria"
	 * zu im Knoten-Baum existierenden Knoten, also im Beispiel der tats�chlich
	 * existierende Cafeteria-Knoten.
	 */
	private HashMap<String, Node> referenceIDs;
	
	/**
	 * Dieses Attribut speichert alle noch nicht aufgel�sten Referenzen. 
	 * Mithilfe der Map {@link #referenceIDs} werden diese Referenzen 
	 * schlie�lich in echt JVM-interne Referenzen umgewandelt
	 * (in {@link #linkNodes()}).
	 */
	private Set<Reference> references;
	
	/**
	 * Der Root-Knoten der neuen <code>Map</code>.
	 */
	private Node root;
	
	/**
	 * Die <code>Map</code>, die schlie�lich in {@link #provideMap()}
	 * zur�ckgegeben wird.
	 */
	private Map map;
	
	/**
	 * Erstellt eine neue Instanz von <code>XMLMapProvider</code>.
	 * Der Pfad zur XML-Datei, die die <code>Map</code>-Daten enth�lt
	 * wird aus den Konfigurations-Daten ausgelesen.
	 * 
	 */
	public XMLMapProvider() {
		String path = (String) Configuration.getInstance().getValue(
				CONFIG_MAP_PATH,
				StringType.getInstance(), 
				DEFAULT_MAP_PATH );
		
		this.xmlFile = new File( path );
	}
	
	/**
	 * @return {@link #CONFIGURATION_NAME}.
	 */
	@Override
	public String getConfigurationName() {
		return XMLMapProvider.CONFIGURATION_NAME;
	}
	
	/**
	 * L�dt die <code>Map</code> aus der im Konstruktor �bergebenen 
	 * XML-Datei und gibt sie dann zur�ck.
	 * 
	 * @return				Die aus der Datei geladene <code>Map</code>.
	 * @throws Exception	Wenn die Map nicht geladen werden konnte.
	 * 
	 * @see #loadMap(File)
	 */
	@Override
	public Map provideMap() throws Exception {

		loadMap( xmlFile );
		return map;
	}

	/**
	 * Diese private Methode l�dt zun�chst alle Knoten aus der im Konstruktor
	 * �bergebenen XML-Datei und l�st dann die eventuell auftauchenden 
	 * Referenzierungen im Dokument in tats�chliche Referenzen auf.
	 * Inkonsistenzen werden den fehlerfreien Ablauf der Methode nicht 
	 * gef�hrden, trotzdem werden Warnungen im Logger ausgegeben.
	 * 
	 * @param xml			Die XML-Datei, aus der die <code>Map</code> 
	 * 						geladen wird.
	 * @throws Exception	Wenn ein so schwerer Fehler entsteht, dass
	 * 						der Rest der Karte nicht mehr geladen werden
	 * 						kann.
	 */
	private void loadMap( File xml ) throws Exception {
		
		logger = Logger.getInstance();
		logger.log( LogLevel.INFO, LOG_NAME, "Loading Map \"" + xml.getName() + "\"." );
		
		// Speichert die Reference Identifier der Nodes.
		referenceIDs = new HashMap<String, Node>();
		references = new HashSet<Reference>();
		
		Document xmlDocument = (new SAXBuilder()).build( xml );
		Element map = xmlDocument.getRootElement();
		
		// Root Element und Map Name auslesen.
		Namespace ns = map.getNamespace();
		String name = map.getAttributeValue( NAME, ns );
		if ( name == null ) {
			logger.log( LogLevel.WARNING, LOG_NAME, "Specified map has no name-attribute." );
			name = "NO_NAME_SPECIFIED";
		} else name = name.trim();
		
		Element rootElement = map.getChild( ROOT, ns );
		
		// Knoten erstellen und linken
		root = loadNode( null, rootElement );
		linkNodes();
		 
		// Terroristenspawnpoint, Counterterroristenspawnpoint und Bombpoint aus Dokument auslesen.
		// Wenn einer dieser Knoten nicht existieren sollte, kann die Map nicht mehr erstellt werden.
		String terroristsSpawnpointID 			= map.getChildText( SPAWN_TERRORISTS, ns );
		String counterterroristsSpawnpointID	= map.getChildText( SPAWN_COUNTERTERRORISTS, ns );
		String bombPointID						= map.getChildText( BOMB_POINT, ns );
		
		if ( 	   terroristsSpawnpointID 			== null 
				|| counterterroristsSpawnpointID 	== null 
				|| bombPointID 						== null ) {
			
			logger.log( LogLevel.ERROR, LOG_NAME, "<spawnTerrorists>, <spawnCounterterrorists> and <bombPoint> must be defined." );
			throw new Exception( "Map could not be created." );
		} else {

			// Nun m�ssen Spawnpoint f�r Terroristen und Counterterroristen sowie der Bombpoint 
			// aus den Referez-Strings ermittelt werden. Wenn das nicht geht, kann die Map
			// nicht erstellt werden.
			Node terroristsSpawnpoint = referenceIDs.get( terroristsSpawnpointID.trim() );
			Node counterterroristsSpawnpoint = referenceIDs.get( counterterroristsSpawnpointID.trim() );
			Node bombPoint = referenceIDs.get( bombPointID.trim() );
			
			if ( terroristsSpawnpoint == null ) {
				logger.log( LogLevel.ERROR, LOG_NAME, "<spawnTerrorists>'s reference ID cannot be resolved to be a Node." );
				throw new Exception( "Map could not be created" );
			
			} else if ( counterterroristsSpawnpoint == null ) {
				logger.log( LogLevel.ERROR, LOG_NAME, "<spawnCounterterrorists>'s reference ID cannot be resolved to be a Node." );
				throw new Exception( "Map could not be created" );
				
			} else if ( bombPoint == null ) {
				logger.log( LogLevel.ERROR, LOG_NAME, "<bombPoint>'s reference ID cannot be resolved to be a Node." );
				throw new Exception( "Map could not be created" );
				
			} else {
				// Alle Referenzen wurden erfolgreich aufgel�st.
				EnumMap<Team, Node> spawnPoints = new EnumMap<Team, Node>( Team.class );
				spawnPoints.put( Team.TERRORISTS,  terroristsSpawnpoint );
				spawnPoints.put( Team.COUNTER_TERRORISTS, counterterroristsSpawnpoint );
				
				this.map = new Map( name, root, bombPoint, spawnPoints );
			}
		}
	}
	
	/**
	 * Diese private Methode l�dt einen einzelnen Knoten aus einem XML-Element in der Map-Datei. Wenn
	 * der Knoten erstellt wurde, wird die Methode f�r alle Sub-Elemente dieses Elements aufgerufen,
	 * sodass der R�ckgabewert ein kompletter {@link Node}-Baum ist.
	 * <p>
	 * Inknosistenzen in der XML-Datei f�hren nur zu einer Warnung in den Log-Dateien, allerdings 
	 * k�nnen je nach Fehler ganze Teilb�ume des <code>node</code>-Baumes verloren gehen.
	 * <p>
	 * Referenzen auf andere Knoten in der XML-Datei werden hier noch nicht aufgel�st, obwohl es in
	 * manchen F�llen vielleicht m�glich w�re. Stattdessen werden die Referenz-Strings in
	 * {@link #references} abgespeichert, um sie dann sp�ter in {@link #linkNodes()} aufl�sen
	 * zu k�nnen.
	 * 
	 * @param parent		Der Knoten, zu dem der neu erstellte Knoten hinzugef�gt werden soll. 
	 * 						F�r das Root-Element sollte her <code>null</code> �bergeben werden.
	 * @param xmlElement	Das XML-Element (JDOM), von dem der Knoten geladen wird
	 * 
	 * @return				Ein kompletter <code>Node</code>-Baum. Da allerdings noch keine
	 * 						Referenzen aufgel�st wurden, haben <code>Room</code>s keine
	 * 						T�ren, und <code>LockableNode</code>s noch keine Schl�ssel.				
	 */
	private Node loadNode( Node parent, Element xmlElement ) {
		
		// Daten laden, die ben�tigt werden, um den Konstruktor von
		// Node aufzurufen.
		Namespace ns = xmlElement.getNamespace();
		
		// ReferenceID wird nicht f�r den Konstruktor ben�tigt, sondern
		// um die Referenzen sp�ter aufzul�sen.
		String refID 	= xmlElement.getChildText( REFERENCE_ID, ns );
		if ( refID != null ) refID = refID.trim();
		String id		= xmlElement.getChildText( ID, ns );
		if ( id != null ) id = id.trim();
		
		List<Element> nameElements = xmlElement.getChildren( NAME, ns );
		List<String> names = new ArrayList<String>();
		for (Element nameElement : nameElements) {
			
			String name = nameElement.getValue();
			if ( name != null ) name = name.trim();
			
			names.add( name );
		}
		
		String description = xmlElement.getChildText( DESCRIPTION, ns );
		if ( description != null ) description = description.trim();
		
		String[] nameArray = new String[ names.size() + 1 ];
		nameArray[0] = id;
		for (int i = 1; i < nameArray.length; i++) {
			nameArray[i] = names.get( i - 1 );
		}
		
		logger.log( LogLevel.DEBUG, LOG_NAME, "Loading Node " + refID + "." );
		
		// Jetzt muss der geeignete Konstruktor ausgew�hlt werden.
		Node result;
		Attribute typeAttribute = xmlElement.getAttribute( "type" );
		if ( typeAttribute != null ) {
			
			// TODO Dieses Switch-Case ist zu massiv
			switch ( typeAttribute.getValue() ) {
			case "Room": {
				// Wenn der Raum keine ReferenceID hat, kann er sp�ter nicht gelinkt werden
				if ( refID == null ) {
					logger.log( LogLevel.WARNING, LOG_NAME, "Room \"" + id + "\" has no reference ID and is therefore not accessible." );
				} else {
					// F�r alle existierenden Himmelsrichtungen pr�fen, ob der Raum eine
					// T�r in diese Richtung hat.
					for ( DoorDirection direction : DoorDirection.values() ) {
						
						String directionID = xmlElement.getChildText( direction.toString(), ns );
						// Wenn das der Fall ist, wird die Referenz in die List der 
						// unaufgel�sten Referenzen geaddet.
						if ( directionID != null ) {
							Reference directionReference = new Reference( refID, directionID.trim(), direction.toString() );
							references.add( directionReference );
						}
					}
				}
				
				// R�ume sind in XML-Maps weder verschlossen noch verriegelt
				// (also wegen closed und locked).
				result = new Room( parent, nameArray, description, false, false );
				break;
			}
			
			case "OpenableNode": {
				// Wenn das XML-Element ein <closed>-Element hat, dann wird der
				// dor gespeicherte Wert verwendet, sonst false
				String closedString = xmlElement.getChildText( CLOSED, ns );
				boolean closed;
				if ( closedString == null ) closed = false;
				else {
					closed = Boolean.parseBoolean( closedString.trim() );
				}
				
				result = new OpenableNode( parent, nameArray, description, closed );
				break;
			}
				
			case "LockableNode": {
				// Wie bei OpenableNode
				String closedString = xmlElement.getChildText( CLOSED, ns );
				boolean closed;
				if ( closedString == null ) closed = false;
				else {
					closed = Boolean.parseBoolean( closedString.trim() );
				}
				
				// Wenn es ein <locked>-Element gibt, dann wird der dort gespeicherte
				// Wert verwendet, sonst false
				String lockedString = xmlElement.getChildText( LOCKED, ns );
				boolean locked;
				if ( lockedString == null ) locked = false;
				else {
					locked = Boolean.parseBoolean( lockedString.trim() );
				}
				
				// Wenn es ein <key>-Element gibt, wird zum ab-/aufschlie�en des Raums ein
				// Schl�ssel ben�tigt, dessen Referenz-String im <key>-Element steht.
				// Andernfalls kann der Knoten auch ohne Schl�ssel verriegelt werden
				String keyID = xmlElement.getChildText( KEY, ns );
				if ( keyID != null ) {
					// Wenn der Knoten aber keine ReferenceID hat, kann sp�ter auch nichts gelinkt werden.
					if ( refID == null ) 
						logger.log( LogLevel.WARNING, LOG_NAME, "Owner of \"" + keyID.trim() + "\" has no reference ID." );
					else {
						Reference keyReference = new Reference( refID, keyID.trim(), KEY );
						references.add( keyReference );
					}
				}
				
				result = new LockableNode( parent, nameArray, description, closed, locked );
				break;
			}
				
			default: {
				// Andere Werte f�r type sind zwar im XSD-File nicht zul�ssig,
				// aber man wei� ja nie.
				result = new Node( parent, nameArray, description );
				break;
			}
			}
			
		} else {
			// Kein type-Attribut hei�t normaler Knoten.
			result = new Node( parent, nameArray, description );
		}
		
		// Wenn dieser Knoten eine ReferenceID hat, wird sie f�rs sp�tere
		// linken mit dem neuen Knoten verkn�pft.
		if ( refID != null )
			referenceIDs.put( refID, result );
		
		// Jetzt Unterelemente laden.
		List<Element> childNodeElements = xmlElement.getChildren( NODE, ns );
		for (Element element : childNodeElements) {
			logger.log( LogLevel.DEBUG, LOG_NAME, "<" + element.getName() + ">" + element.getChildText( "refID" , element.getNamespace() ) + "</" + element.getName() + ">" );
		}
		
		for (Element element : childNodeElements) {
			loadNode( result, element );
			
		}
		
		return result;
	}

	/**
	 * Diese private Methde linkt den {@link Node}-Baum, sodass alle nicht aufgel�sten Referenzen aufgel�st
	 * werden. Wenn die Methode auf eine semantisch falsche Referenz trifft, oder eine Referenz nicht 
	 * in einen Knoten umgewandelt werden kann, wird sie �bersprungen und eine Warnung ausgegeben.
	 * <p>
	 * Momentan gibt es zwei Typen von Referenz, die <code>key</code>-Referenz und die 
	 * <code>direction</code>-Referenz. Erstere ist eine Referenz von einem <code>Room</code> oder einem
	 * allgemeinen <code>LockableNode</code> auf den zu ihm geh�renden Schl�ssel. Wenn der Ersteller einer
	 * <code>Map</code> m�chte, dass ein Knoten nur mit einem Schl�ssel ge�ffnet werden kann, muss
	 * er einen Schl�ssel-Knoten platzieren, und eine entsprechende Referenz im verschlie�baren Knoten
	 * hinzuf�gen.
	 * <br>
	 * Der zweite Typ ist die Referenz von einem Raum auf einen anderen, um anzuzeigen, dass in diese
	 * Richtung eine T�r zu dem anderen Raum f�hrt. So kann der Ersteller einer <code>Map</code>
	 * z.B. ein "north"-Element hinzuf�gen und in diesem eine Referenz auf den Raum hinterlegen, der
	 * in n�rdliche Richtung liegt.
	 * <br>Diese Methode wird solche Referenzen aufl�sen.
	 */
	private void linkNodes() {
		
		// �ber unaufgel�ste Referenzen iterieren
		for (Reference reference : references) {

			Node owner 	= referenceIDs.get( reference.getOwnerID() );
			Node target	= referenceIDs.get( reference.getTargetID() );
			String type = reference.getElementName();
			
			// Checken ob alle Referenz-Ids in Knoten umgewandelt werden konnten.
			if ( owner == null ) {
				logger.log( LogLevel.WARNING, LOG_NAME, "Could not resolve \"" + reference.getOwnerID() + "\" to a Node." );
			} else {
				if ( target == null ) {
					logger.log( LogLevel.WARNING, LOG_NAME, "Could not resolve \"" + reference.getTargetID() + "\" to a Node." );
				} else {
					
					// Es gibt zwei Referenz-Typen, KEY und DIRECTION
					if ( type.equals( KEY ) ) {
						
						// Nur LockableNodes haben einen Schl�ssel (Room ist eine Subklasse
						// von LockableNode)
						if ( owner instanceof LockableNode ) {
							
							LockableNode lNode = (LockableNode) owner;
							lNode.addKey( target.getIdentifier() );
						} else
							logger.log( LogLevel.WARNING, LOG_NAME, "\"" + owner.getIdentifier() + "\"'s <key>-element is ignored." );
					} else {
						
						// Nur R�ume haben T�ren
						if ( owner instanceof Room ) {
							
							Room room = (Room) owner;
							DoorDirection direction = DoorDirection.fromString( type );
							// Das kann eigentlich nicht passieren, wenn das XML-Dokument mit dem
							// XSD-File validiert wurde
							if ( direction != null ) {
								
								// T�ren k�nnen nur zu anderen R�umen f�hren
								if ( target instanceof Room ) {
									
									Room targetRoom = (Room) target;
									room.setAdjacentRoom( direction, targetRoom );
								} else
									logger.log( LogLevel.WARNING, LOG_NAME, "\"" + reference.getTargetID() + "\" is not a room and can therefore not be accessed by \"" + reference.getOwnerID() + "\"." );
							} else
								logger.log( LogLevel.WARNING, LOG_NAME, "Could not resolve \"" + type + "\" to a direction." );
						} else
							logger.log( LogLevel.WARNING, LOG_NAME, "\"" + owner.getIdentifier() + "\"'s <" + type + ">-element is ignored." );
					}
				}
			}
		}
	}
}


















