package de.tungsten.tocs.menus;

import de.tungsten.tocs.net.IConnection;

public class Prompt implements IMenu {

	private String message;
	
	public Prompt( String message ) {
		this.message = message;
	}
	
	@Override
	public void setParent(IMenu parent) {
		/* ignore - there is no way back... */
	}

	@Override
	public Object display( IConnection connection ) {
		
		connection.write( "\n" + message );
		
		boolean found = false;
		String result = null;
		
		while ( !found ) {
			
			connection.write( "\n> " );
			
			result = connection.read();
			
			if ( result == null || result.isEmpty() )
				continue;
			
			result = result.trim();
			if ( result.isEmpty() ) {
				connection.write( "\nEnter a value!");
			} else {
				found = true;
			}
		}
		
		return result;
		
	}

}
