package de.tungsten.tocs.menus;

import de.tungsten.tocs.net.IConnection;

/**
 * <code>IMenu</code> ist die gemeinsame Schnittstelle aller Text-Menüs im
 * Spiel. Um ein einheitliches "Look 'n' Feel" zu erzeugen können die
 * Implementationen verwendet werden, um den User zu einer Eingabe aufzufordern.
 * 
 * @author tungsten
 *
 */
public interface IMenu {

	/*
	 * Die Funktionsweise dieses Interfaces wird am besten in den
	 * Implementationen in diesem Package offenbar.
	 */
	
	/**
	 * Wenn das Menu eine "Zurück"-Funktion bieten soll, muss der Parameter
	 * <code>parent</code> gespeichert werden, und dessen 
	 * {@link #display(IConnection)}-Methode aufgerufen werden, sobald der 
	 * User diese Option wählt.
	 * <p>
	 * Wenn keine "Zurück"-Funktionalität hinzugefügt werden soll, muss die
	 * Methode nicht implementiert werden.
	 * 
	 * @param parent	Das Menu, das diese Menu als Submenu hat.
	 */
	public abstract void setParent( IMenu parent );
	
	/**
	 * Zeigt das Menu über die gegebene <code>connection</code> an und wertet
	 * die Eingaben des Benutzers aus.
	 * <p>
	 * Wenn möglich, sollte die {@link #setParent(IMenu)}-Methode von Submenus
	 * aufgerufen werden.
	 * 
	 * @param connection	Die <code>connection</code> über die der User 
	 * 						erreicht werden kann.	
	 * @return				Den Rückgabe-Wert, der zu der Eingabe des Benutzers
	 * 						passt.
	 */
	public abstract Object display( IConnection connection );
}
