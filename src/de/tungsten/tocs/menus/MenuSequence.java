package de.tungsten.tocs.menus;

import java.util.ArrayList;
import java.util.List;

import de.tungsten.tocs.net.IConnection;

/**
 * Eine <code>MenuSequence</code> zeigt die mit {@link #addMenu(IMenu)}
 * hinzugef�gten Submenus nacheinander in der Reiehenfolge auf, in der
 * sie hinzugef�gt wurden, sobald {@link #display(IConnection)} aufgerufen
 * wird. Die R�ckgabewerte der einzelnen Submenus werden dabei in einer
 * Liste verwaltet.
 * 
 * @author Wolfram
 *
 */
public class MenuSequence implements IMenu {

	/**
	 * Die Submenus, die nacheinander angezeigt werden, wenn
	 * {@link #display(IConnection)} aufgerufen wird.
	 */
	private List<IMenu> targets = new ArrayList<IMenu>();
	
	/**
	 * F�gt der Sequenz ein Submenu hinzu, welches dann beim Aufruf von
	 * {@link #display(IConnection)} angezeigt wird.
	 * 
	 * @param menu	Das hinzuzuf�gende Menu
	 * @return		Diese <code>MenuSequence</code>, f�r's Chaining.
	 */
	public MenuSequence addMenu( IMenu menu ) {
		targets.add( menu );
		
		return this;
	}
	
	@Override
	public void setParent(IMenu parent) {
		/* Es gibt kein zur�ck */		
	}

	/**
	 * F�hrt die in {@link #targets} gespeicherten Menu nacheinander, in der
	 * Reihenfolge in der sie hinzugef�gt worden sind, aus. Die Ergebnisse
	 * der Submenus werden in einer Liste gespeichert, die dann zur�ckgegeben
	 * wird.
	 * 
	 * @param connection	Die <code>connection</code> zu Player. Es werden
	 * 						keine Nachrichten gesendet/empfangen, die Referenz
	 * 						wird lediglich an die Submenus weitergereicht.
	 * @return				Die Ergebnisse der Submen�s in Form einer Liste.
	 */
	@Override
	public List<Object> display( IConnection connection ) {

		List<Object> results = new ArrayList<Object>();
		
		for (IMenu current : targets) {
			results.add( current.display( connection ) );
		}
		
		return results;
	}

	
}
