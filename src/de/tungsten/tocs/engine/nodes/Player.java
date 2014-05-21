package de.tungsten.tocs.engine.nodes;

import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.config.ConfigurationType;
import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.parsing.NodeLocator;

/**
 * Ein Spieler ist ein {@link Node Knoten} der einen Nickname, Geld, ein Team, ein
 * Inventar und H�nde hat.
 * 
 * @author tungsten
 *
 */
public class Player extends Node {

	// Zum laden aus der Konfigurationsdatei
	public	static final String CONFIG_PLAYER_CREDIT 	= "playerCredit";
	private static final int 	DEFAULT_PLAYER_CREDIT	= 1000;
	
	/**
	 * Das Team dieses Spielers.
	 */
	private Team team;
	
	/**
	 * Das aktuelle Guthaben dieses Spielers.
	 */
	private int credit;
	
	/**
	 * Erstellt einen neuen Spieler unter Vergabe eines Nicknames und eines Teams. Als
	 * Unterknoten werden automatisch das Inventar und die H�nde des Spielers erstellt.
	 * Der Startguthaben wird aus der Konfigurationsdatei gelesen.
	 * 
	 * @param nickname	Der Nickname des Spielers ist der einzige Name dieses Knotens
	 * 					und damit gleichzetig der Identifier.
	 * @param team		Das Team dieses Spielers.
	 */
	public Player( 
			String nickname, 
			Team team ) {
		
		super(  null, 
				new String[] { nickname }, 
				"This is " + nickname + "." );
		
		this.team = team;
		
		// Startguthaben aus Configuration ziehen
		Configuration config = Configuration.getInstance();
		credit = (int) config.getValue(
				CONFIG_PLAYER_CREDIT, 
				ConfigurationType.INTEGER, 
				DEFAULT_PLAYER_CREDIT );
		
		// Ein Inventar und ein Paar H�nde erstellen.
		this.children.add( new Node( 
				this, 
				new String[] { Node.NODE_INVENTORY, "inventory" }, 
				"A backpack containing equipment." ) );
		this.children.add( new Node( 
				this, 
				new String[] { Node.NODE_HANDS }, 
				"A pair of unkempt hands." ) );
	}
	
	/**
	 * Gibt das <code>Team</code> dieses Spielers zur�ck.
	 * 
	 * @return Das Team, von dem dieser Spieler ein Teil ist.
	 */
	public Team getTeam() {
		return team;
	}
	
	/**
	 * Das aktuelle Guthaben des Spielers in Dollar.
	 * 
	 * @return	Das aktuelle Guthaben des Spielers.
	 */
	public int getMoney() {
		return credit;
	}
	
	/**
	 * Der Nickname dieses Spielers. Diese Methode existiert nur zu Lesbarkeitsgr�nden
	 * und ist gleichbedeutend mit
	 * <pre>
	 * getIdentifier()
	 * </pre>
	 * 
	 * @return	Der Nickname/Identifier dieses Spielers.
	 */
	public String getNickname() {
		return getIdentifier();
	}
	
	/**
	 * Das "Aussehen" (also die Beschreibung) des Spielers wird in dieser Methode
	 * festgelegt.
	 * 
	 * @param skin	Die neue Beschreibung.
	 */
	public void setSkin( String skin ) {
		description = skin;
	}
	
	/**
	 * Pr�ft, ob der gegebene Betrag vom Spieler aufgebracht werden kann. Wenn dies der
	 * Fall ist, wird ihm der Betrag von Guthaben abgezogen und <code>true</code> wird
	 * zur�ckgegeben. Anderfalls wird das Geld nicht verbraucht, und <code>false</code>
	 * wird zur�ckgegeben.
	 * 
	 * @param amount	Der abzuziehende Betrag.
	 * @return			<code>true</code>, wenn der Betrag von Guthaben des Spielers
	 * 					abgezogen werden konnte, sonst <code>false</code>.
	 */
	public boolean consumeMoney( int amount ) {
		if ( amount > credit ) return false;
		else {
			credit -= amount;
			return true;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Spieler k�nnen nur Unterknoten von {@link Room R�umen} sein.
	 */
	@Override
	public void move( Node target ) {
		if ( target instanceof Room )
			super.move( target );
		
		// TODO Irgendeine Exception werfen. Bisher wird aber tats�chlich eine 
		// geworfen, also geht noch irgendwas schief
	}
	
	/**
	 * Gibt den Inventar-Knoten dieses Spielers zur�ck.
	 * 
	 * @return	Das Inventar des Spielers.
	 */
	public Node getInventory() {
		
		return NodeLocator.findSubNode( Node.NODE_INVENTORY, this, 1 );
	}
	
	/**
	 * Gibt die H�nde des Spielers zur�ck. Wenn der Spieler den Befehl "shoot" verwendet,
	 * wird die Waffe abgefeuert, die sich in seiner Hand befindet.
	 * 
	 * @return	Die H�nde des Spielers.
	 */
	public Node getHands() {
		
		return NodeLocator.findSubNode( Node.NODE_HANDS, this, 1 );
	}
}
