package de.tungsten.tocs.engine;

import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Ein <code>SubSystem</code> bindet eine <code>IPlayerConnection</code> für sich, um bestimmte Aktionen auszuführen.
 * <p>
 * Eine Beispielimplementation ist {@link EquipmentStore}. Dem Spieler werden beim Aufruf von 
 * {@link #operate(IPlayerConnection)} einige Optionen angezeigt, aus denen er dann wählen kann. Sobald
 * er die Wahl getroffen hat, wird das <code>Player</code>-Objekt entsprechend modifiziert und die
 * Methode kehrt zurück.
 * 
 * @author reinke.wolfram-tit13
 *
 */
public interface ISubSystem {

	/**
	 * Ermöglicht das Ausführen von Aktionen im Kontext der gegebenen <code>IPlayerConnection</code>.
	 * 
	 * @param connection Die zu bearbeitende/bedienende <code>IPlayerConnection</code>.
	 */
	public abstract void operate( IPlayerConnection connection );
}
