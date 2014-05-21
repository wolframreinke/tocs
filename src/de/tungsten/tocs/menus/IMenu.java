package de.tungsten.tocs.menus;

import de.tungsten.tocs.net.IConnection;

/**
 * <code>IMenu</code> ist die gemeinsame Schnittstelle aller Text-Men�s im
 * Spiel. Um ein einheitliches "Look 'n' Feel" zu erzeugen k�nnen die
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
	 * Wenn das Menu eine "Zur�ck"-Funktion bieten soll, muss der Parameter
	 * <code>parent</code> gespeichert werden, und dessen 
	 * {@link #display(IConnection)}-Methode aufgerufen werden, sobald der 
	 * User diese Option w�hlt.
	 * <p>
	 * Wenn keine "Zur�ck"-Funktionalit�t hinzugef�gt werden soll, muss die
	 * Methode nicht implementiert werden.
	 * 
	 * @param parent	Das Menu, das diese Menu als Submenu hat.
	 */
	public abstract void setParent( IMenu parent );
	
	/**
	 * Zeigt das Menu �ber die gegebene <code>connection</code> an und wertet
	 * die Eingaben des Benutzers aus.
	 * <p>
	 * Wenn m�glich, sollte die {@link #setParent(IMenu)}-Methode von Submenus
	 * aufgerufen werden.
	 * 
	 * @param connection	Die <code>connection</code> �ber die der User 
	 * 						erreicht werden kann.	
	 * @return				Den R�ckgabe-Wert, der zu der Eingabe des Benutzers
	 * 						passt.
	 */
	public abstract Object display( IConnection connection );
}
