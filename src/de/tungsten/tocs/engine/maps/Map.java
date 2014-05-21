package de.tungsten.tocs.engine.maps;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.nodes.Node;
import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Eine <code>Map</code> beinhaltet den Einstiegs-Punkt zum dazugehörigen
 * {@link Node}-Baum, speichert die Spawnpoints zu den einzelnen Teaams und
 * den Punkt an dem die Bombe platziert werden muss.
 * <p>
 * <code>Map</code> beinhaltet Funktionalitäten zum spawnen von 
 * Spielern auf der Karte.
 * 
 * @author tungsten
 *
 */
public class Map {

	/**
	 * Der Name der Karte. Er wird Spielern beim Spawnen angezeigt.
	 */
	private String name;
	
	/**
	 * Der Root-<code>Node</code> des Knoten-Baums dieser <code>Map</code>.
	 * @see {@link Node}
	 */
	private	Node root;
	
	/**
	 * Der Knoten im Knoten-Baum, in dem die Bombe von den Terroristen platziert
	 * werden muss.
	 */
	private Node bombPoint;
	
	/**
	 * Diese <code>EnumMap</code> mappt {@link Team} zum entsprechenden 
	 * Spawnpoint (der Knoten, in dem die Spieler platziert werden).
	 */
	private EnumMap<Team, Node> spawnPoints;
	
	/**
	 * Die Spieler, die auf der <code>Map</code> gespawnt wurden.
	 */
	private Set<IPlayerConnection> connections = new HashSet<IPlayerConnection>();
	
	/**
	 * Instanziiert ein neues <code>Map</code>-Objekt mit Name,
	 * Root-<code>Node</code>, Bomben-Ziel und Spawnpoints.
	 * 
	 * @param name			Der Name der <code>Map</code>.
	 * @param root			Der Root-<code>Node</code> der <code>Map</code>.
	 * @param bombPoint		Der Ort, an dem die Bombe platziert werden muss.
	 * @param spawnPoints	Die Punkte, an denen die Spieler spawnen.
	 */
	public Map( String name, Node root, Node bombPoint, EnumMap<Team, Node> spawnPoints ) {
		this.name = name;
		this.root = root;
		this.bombPoint = bombPoint;
		this.spawnPoints = spawnPoints;
	}
	
	/**
	 * Spawnt einen Spieler auf dieser <code>Map</code>, d.h. der Spieler wird
	 * in dem Knoten platziert, der in {@link #spawnPoints} zum 
	 * <code>Team</code> des Spielers gehört.
	 * <p>
	 * Nach dem spawnen wird dem Spieler der Name der <code>Map</code>,
	 * sowie die <code>description</code> des Knotens, in dem er
	 * gespawnt ist, angezeigt.
	 * 
	 * @param connection	Die Verbindung zu dem zu spawnenden Spieler.
	 */
	public synchronized void spawn( IPlayerConnection connection ) {
		
		// Spieler zum Spawnpoint bewegen
		Node spawnPoint = spawnPoints.get( connection.getPlayer().getTeam() );
		connection.getPlayer().move( spawnPoint );
		
		// Info printen
		connection.write( "---------- " + this.name + " ----------\n\n" );
		connection.write( connection.getPlayer().getParent().getDescription() );
		
		// Spieler merken
		connections.add( connection );
	}
	
	/**
	 * Gibt den Namen dieser <code>Map</code> zurück.
	 * 
	 * @return den Namen dieser <code>Map</code>.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gibt den Root-{@link Node} dieser <code>Map</code> zurück.
	 * 
	 * @return den Root-Knoten dieser <code>Map</code>.
	 */
	public Node getRoot() {
		return root;
	}
	
	/**
	 * Gibt den Ort zurück, an dem die Terroristen die Bombe platzieren
	 * müssen.
	 * 
	 * @return	s.o.
	 */
	public Node getBombPoint() {
		return bombPoint;
	}
	
	
}
