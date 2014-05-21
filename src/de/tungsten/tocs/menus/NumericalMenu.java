package de.tungsten.tocs.menus;

import java.util.ArrayList;
import java.util.List;

import de.tungsten.tocs.net.IConnection;

/**
 * Ein <code>NumericalMenu</code> gibt dem Benutzer die Auswahl zwischen 
 * verschiedenen Optionen. Um eine der Optionen zu wählen, muss der Benutzer
 * eine Zahl eingeben.
 * <p>
 * Ein Beispiel:
 * <pre>
 * Which weapon do you want do buy?
 *  [0] AK-47
 *  [1] ...
 *  ...
 * </pre>
 * <p>
 * Gibt der Benutzer eine gültige Zahl ein, wird er an das mit dieser Option
 * verknüpfte Menu weitergeleitet (siehe {@link #addOption(String, IMenu)}).
 * 
 * @author tungsten
 *
 */
public class NumericalMenu implements IMenu {

	/**
	 * Eine Option in einem <code>NumericalMenu</code>. Eine Option besteht
	 * aus einem anzuzeigenden text und dem Menu, an das der Benutzer
	 * weitergeleitet wird, wenn er diese Option wählt.
	 * 
	 * @author tungsten
	 *
	 */
	private class Option {
		
		// Public ist nicht die schönste art, aber die Klasse ist ja
		// private.
		public String text;
		public IMenu target;
		
		public Option( String text, IMenu target ) {
			this.target = target;
			this.text = text;
		}
	}
	
	/**
	 * Die Nachricht, die als Überschrift für das Menu angezeigt wird.
	 * Für ein Beispiel, siehe {@link NumericalMenu}.
	 */
	private String message;
	
	/**
	 * Die nach der {@link #message} folgenden Optionen. Die Optionen werden
	 * in der Reihenfolge angezeigt, in der sie hinzugefügt wurden. Für ein
	 * Beispiel, siehe {@link NumericalMenu}.
	 */
	private List<Option> options = new ArrayList<Option>();
	
	/**
	 * Erstellt eine neue Instanz dieser Klasse und legt die Überschrift des
	 * Menus fest. Für ein Beispiel, siehe {@link NumericalMenu}.
	 * 
	 * @param message	Die Überschrift des Menus.
	 */
	public NumericalMenu( String message ) {
		this( null, message );
	}
	
	/**
	 * Erstellt eine neue Instanz diseer Klasse und legt die Überschrift, sowie
	 * das übergeordnete Menu fest. Das übergeordnete Menu kann über die
	 * Option "back" erreicht werden. Für ein Beispiel, siehe
	 * {@link NumericalMenu}.
	 * 
	 * @param parent	Das übergeordnete Menu.
	 * @param message	Die Überschrift dieses Menus.
	 */
	public NumericalMenu( IMenu parent, String message ) {
		this.message = message;
		
		if ( parent != null )
			setParent( parent );
	}
	
	/**
	 * Fügt eine Option hinzu, die dem Benutzer beim Aufruf von
	 * {@link #display(IConnection)} angezeigt wird. Wenn der Benutzer diese
	 * Option auswählt, dann wird er an das gegebene <code>target</code>
	 * weitergeleitet.
	 * 
	 * @param message	Die textuelle Beschreibung des Option.
	 * @param target	Das Submenu, an das der Benutzer bei Auswahl dieser
	 * 					Option weitergeleitet wird.
	 * @return			Dieses <code>NumericalMenu</code> selbst, für's 
	 * 					Chaining.
	 */
	public NumericalMenu addOption( String message, IMenu target ) {
		
		options.add( new Option( message, target ) );
		target.setParent( this ); 	// Dieses Menu ist das übergeordnete Menu
									// des neuen Submenus.
		
		return this;
	}
	
	@Override
	public void setParent( IMenu parent ) {
		options.add( new Option( "back", parent  ) );
	}
	
	/**
	 * Zeigt dieses <code>Menu</code> dem über die <code>connection</code>
	 * erreichbaren Benutzer an. Für ein Beispiel, wie der Output dieser
	 * Methode aussehen kann, siehe {@link NumericalMenu}.
	 */
	@Override
	public Object display( IConnection connection ) {

		// Erstmal alle Optionen einblenden
		connection.write( "\n" + message );
		for (int i = 0; i < options.size(); i++) {
			connection.write( "\n [" + i + "] " + options.get( i ).text );
		}
		
		boolean found = false;
		Object result = null;
		
		// Ausführen, bis der Benutzer gute Werte eingibt.
		while ( !found ) {
			
			// Eingabeaufforderung
			connection.write( "\n> " );
			
			// Eingabe einlesen
			String input = connection.read();
			
			try {
				// Checken, ob der Benutzer valide Werte eingegeben hat.
				int choice = Integer.parseInt( input.trim() );
				if ( choice < 0 || choice > options.size() - 1 ) throw new Exception();
				
				// Den Benutzer an das gewählt submenü weiterleiten.
				result = options.get( choice ).target.display( connection );
				found = true;
				
			} catch ( Exception e ) {
				connection.write( "\nEnter a valid number!" );
			}
			
		}
		
		return result;
		
	}

}
