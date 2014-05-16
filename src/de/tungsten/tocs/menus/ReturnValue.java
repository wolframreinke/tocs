package de.tungsten.tocs.menus;

import de.tungsten.tocs.net.IConnection;

public class ReturnValue implements IMenu {

	Object value;
	
	public ReturnValue( Object value ) {
		this.value = value;
	}
	
	@Override
	public void setParent(IMenu parent) {
	}

	@Override
	public Object display( IConnection connection ) {
		return value;
	}

}
