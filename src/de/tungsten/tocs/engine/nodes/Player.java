package de.tungsten.tocs.engine.nodes;

import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.config.ConfigurationType;
import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.parsing.NodeLocator;

public class Player extends Node {

	public	static final String CONFIG_PLAYER_CREDIT 	= "playerCredit";
	private static final int 	DEFAULT_PLAYER_CREDIT	= 1000;
	
	private String nickname;
	private Team team;
	private int credit;
	
	public Player( 
			String nickname, 
			Team team ) {
		
		super(  null, 
				new String[] { nickname }, 
				"This is " + nickname + "." );
		
		this.team = team;
		this.nickname = nickname;
		
		// Load player's credit from config file
		Configuration config = Configuration.getInstance();
		credit = (int) config.getValue(
				CONFIG_PLAYER_CREDIT, 
				ConfigurationType.INTEGER, 
				DEFAULT_PLAYER_CREDIT );
		
		// Create an inventory and a pair of hands (used to define to shoot with which weapon)
		this.children.add( new Node( 
				this, 
				new String[] { Node.NODE_INVENTORY, "inventory" }, 
				"A backpack containing equipment." ) );
		this.children.add( new Node( 
				this, 
				new String[] { Node.NODE_HANDS }, 
				"A pair of unkempt hands." ) );
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getMoney() {
		return credit;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setSkin( String skin ) {
		description = skin;
	}
	
	public boolean consumeMoney( int amount ) {
		if ( amount > credit ) return false;
		else {
			credit -= amount;
			return true;
		}
	}
	
	@Override
	public void move( Node target ) {
		if ( target instanceof Room )
			super.move( target );
		
		// TODO throw some kind of exception here
	}
	
	public Node getInventory() {
		
		return NodeLocator.findSubNode( Node.NODE_INVENTORY, this, -1 );
	}
	
	public Node getHands() {
		
		return NodeLocator.findSubNode( Node.NODE_HANDS, this, -1 );
	}
}
