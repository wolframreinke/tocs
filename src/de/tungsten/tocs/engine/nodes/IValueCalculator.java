package de.tungsten.tocs.engine.nodes;


/**
 * Berechnet einen Wert in Abhängigkeit von einem {@link Node}.
 * <p>
 * Dieses Interface wird für das Scripten von Ereignissen und Eigenschaften von Knoten in TOCS benötigt.
 * Ein Beispiel ist die Methode <code>getDescription()</code> in <code>Node</code>. Hier wird erst
 * geprüft, ob der Modder ein Script für die <code>description</code> hinzugefügt hat. Wenn, dann wird
 * der entsprechende <code>IValueCalculator</code> verwendet, um die <code>description</code> dynamisch
 * zu berechnen.
 * <p>
 * Dieses Interface ist momentan noch nicht großartig in Benutzung; Vielleicht wird es wieder entfernt.
 * 
 * @author reinke.wolfram-tit13
 *
 * @param <T>	Der Typ des Rückgabewertes der Kalkulation.
 */
public interface IValueCalculator<T> {

	/**
	 * Berechnet einen Wert des Types T in Abhängigkeit von einem <code>Node</code>.
	 * 
	 * @param target	Der Knoten, für den der Wert berechnet werden soll.
	 * @return			Der berechnete Wert.
	 */
	public abstract T calculate( Node target );
}
