package de.tungsten.tocs.engine.nodes;

import de.tungsten.tocs.engine.WeaponType;

public class WeaponNode extends Node {
	
	private WeaponType type;
	private int ammo;
	
	public WeaponNode(Node parent, String[] names, String description, WeaponType type) {
		super(parent, names, description);
		
		this.type = type;
	}

	public WeaponType getType() {
		return type;
	}
	
	public int computeShotDamage() {
		
		int damage = 0;
		
		int i = type.getRate() > ammo ? ammo : type.getRate();
		while ( i > 0 ) {
			
			damage += type.getDamage();
			
			ammo--;
			i--;
		}
		
		return damage;
	}
	
	public void reload() {
		ammo = type.getCapacity();
	}
	
	public int getAmmonition() {
		return ammo;
	}
	
}
