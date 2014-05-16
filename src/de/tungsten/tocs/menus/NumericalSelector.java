package de.tungsten.tocs.menus;

import java.util.ArrayList;
import java.util.List;

import de.tungsten.tocs.net.IConnection;

public class NumericalSelector implements IMenu {

	private class Option {
		public String text;
		public Object value;
		
		public Option( String text, Object value ) {
			this.text = text;
			this.value = value;
		}
	}

	private String message;
	private IMenu parent;
	private List<Option> options = new ArrayList<Option>();
	
	public NumericalSelector( String message ) {
		this.message = message;
	}
	
	public NumericalSelector addOption( String message, Object returnValue ) {
		options.add( new Option( message, returnValue ) );
		return this;
	}
	
	@Override
	public void setParent( IMenu parent ) {
		this.parent = parent;
		options.add( new Option( "back", null  ) );
	}

	@Override
	public Object display( IConnection connection ) {

		connection.write( "\n" + message );
		for (int i = 0; i < options.size(); i++) {
			connection.write( "\n [" + i + "] " +  options.get( i ).text );
		}
		
		boolean found = false;
		Object result = null;
		
		while ( !found ) {
			
			connection.write( "\n> ");
			
			String input = connection.read();
			
			try {
				int choice = Integer.parseInt( input.trim() );
				if ( choice < 0  || choice > options.size() - 1) throw new Exception();
				
				result = options.get( choice ).value;
				if ( result == null ) {
					result = parent.display( connection );
				} 
				
				found = true;
				
			} catch ( Exception e ) {
				connection.write( "\nEnter a valid number!");
			}
		}
		
		return result;
	}
}
