package de.tungsten.tocs.menus;

import java.util.ArrayList;
import java.util.List;

import de.tungsten.tocs.net.IConnection;

public class ShortcutSelector implements IMenu {

	private class Option {
		public char shortcut;
		public String text;
		public Object value;
		
		public Option(char shortcut, String text, Object value) {
			this.shortcut = shortcut;
			this.text = text;
			this.value = value;
		}

	}
	
	private String message;
	private List<Option> options = new ArrayList<Option>();
	
	public ShortcutSelector( String message ) {
		this.message = message;
	}
	
	public ShortcutSelector addOption( char shortcut, String text, Object returnValue ) {
		options.add( new Option( shortcut, text, returnValue ) );
		
		return this;
	}
	
	@Override
	public void setParent(IMenu parent) {
		/* ignore - the value selector will not provide the possibility to jump back. */
	}

	@Override
	public Object display( IConnection connection ) {

		connection.write( "\n" + message + " (" );
		String s = "";
		for (Option current : options) {
			s += current.shortcut + ": " + current.text + ", ";
		}
		
		connection.write( s.substring( 0, s.length() - 2) + ")" );
		
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
				result = selection.value;
			}
			
		}
		
		return result;
	}


}
