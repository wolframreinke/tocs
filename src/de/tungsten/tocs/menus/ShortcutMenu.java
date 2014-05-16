package de.tungsten.tocs.menus;

import java.util.ArrayList;
import java.util.List;

import de.tungsten.tocs.net.IConnection;

public class ShortcutMenu implements IMenu {

	private class Option {
		public char shortcut;
		public String text;
		public IMenu target;
		
		public Option( char shortcut, String text, IMenu target ) {
			this.shortcut = shortcut;
			this.text = text;
			this.target = target;
		}
	}
	
	private String message;
	private List<Option> options = new ArrayList<Option>();
	
	public ShortcutMenu( String message ) {
		this.message = message;
	}
	
	public ShortcutMenu addOption( char shortcut, String text, IMenu target ) {

		options.add( new Option( shortcut, text, target ) );
		
		return this;
	}
	
	@Override
	public void setParent(IMenu parent) {
		/* ignore - this menu provides no possibility to go back. */
	}

	@Override
	public Object display( IConnection connection ) {

		connection.write( "\n" + message + " (" );
		String s = "";
		for (Option current : options) {
			s += current.shortcut + ": " + current.text + ", ";
		}
		
		connection.write( s.substring( 0, s.length() - 2 ) + ")" );
		
		boolean found = false;
		Object result = null;
		
		while ( !found ) {
			
			connection.write( "\n> " );
			
			String input = connection.read();
			
			if ( input == null || input.isEmpty() )
				continue;
			
			Option selection = null;
			for (Option current : options) {
				if ( current.shortcut == input.charAt( 0 ) ) {
					selection = current;
					break;
				}
			}
			
			if ( selection == null ) {
				connection.write( "\nEnter one of the shortcuts!" );
			} else {
				found = true;
				if ( selection.target != null )
					result = selection.target.display( connection );
			}
		}
		
		return result;
	}

}
