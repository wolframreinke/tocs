package de.tungsten.tocs.engine.parsing;

/**
 * Eine <code>Preposition</code> ist ein spezielles Wort in der Eingabe des Spielers, 
 * das verwendet wird, um ihr Struktur zu verleihen. Der Instruktion des Spielers werden
 * mittels Präposition sozusagen Argumente übergeben.
 * <b>Beispiel:</b>
 * <p><code>
 * put the ball from the table into the box
 * </code><p>
 * Hier sind die beiden Präpositionen "from" und "into". Erstere gibt an, von wo der
 * Ball genommen werden soll, die zweite gibt an, wohin er gelegt werden soll.
 * <p>
 * Es gibt drei Arten von Präpositionen, denen jeweils eine Menge von englischen Wörten
 * zugeordnet ist, nämlich
 * <ul>
 * <li> {@link #WITH},
 * <li>	{@link #OF}, und
 * <li> {@link #TO}.
 * </ul>
 * 
 * @author tungsten
 *
 */
public enum Preposition {
	
	/**
	 * <code>WITH</code> enthält folgende englische Präpositionen: "with" und "using". Wenn
	 * der Spieler einer dieser Wörter verwendet, zeigt das an, dass die entsprechenden
	 * Objekte als Argumente an die Methode übergeben möchte. 
	 * <br><b>Beispiel: </b>
	 * <p><code>
	 * lock the door with the key
	 * </code><p> 
	 * wird daher zu etwas wie
	 * <p><code>
	 * door.lock( key );
	 * </code><p>
	 */
	WITH 	( new String[] {"with", "using"} ),
	
	/**
	 * <code>TO</code> enthält folgende englische Präpositionen: "to", "into", "onto",
	 * "over", "under" und "at". Diese Präposition wird vom Spieler benutzt, um das Ziel
	 * der Operation zu spezifizieren.
	 * <br><b>Beispiel:</b>
	 * <p><code>
	 * put the ball into the box
	 * </code><p>
	 * Hier ist das Ziel der Operation "put" die Box (wegen "into"). Der Ball ist nicht
	 * das Ziel dieser Instruktion, sondern der Präfix (siehe {@link Intsruction}).
	 */
	TO		( new String[] {"to", "into", "onto", "over", "under", "at"} ),
	
	/**
	 * <code>OF</code> enthält folgende englische Präpositionen: "of", "in", "on" und 
	 * "from". Diese Präposition wird verwendet, um einen "Suchpfad" für einen Knoten zu
	 * erstellen (siehe {@link NodeLocation}).
	 * <br><b>Beispiel:</b>
	 * <p><code>
	 * take the ball on the desk
	 * </code><p>
	 * Hier ist der Suchpfad zu dem Ball <code>room -> desk -> ball</code>, wobei der
	 * Raum immer imlizit hinzugefügt wird.
	 */
	OF		( new String[] {"of", "in", "on", "from"} );
	
	String[] content;
	
	Preposition( String[] s ) {
		this.content = s;
	}
	
	/**
	 * Gibt die Präposition zurück, die den gegebenen String enthält. Wenn keine passende
	 * Präposition gefunden wird, wird <code>null</code> zurückgegeben. Diese Methode
	 * ignoriert, ob der gegebene String groß oder klein geschrieben wurde.
	 * 
	 * 
	 * @param s		Der String der mit den einzelnen Präpositionen verglichen wird.
	 * @return		Die zum gegebenen String passende Präposition oder <code>null</code>,
	 * 				wenn keine der Präpositionen passt.
	 */
	public static Preposition fromString( String s ) {
		
		for (Preposition current : Preposition.values()) {
			for (String content : current.content) {
				if ( content.equalsIgnoreCase( s ) ) return current;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return content[0];
	}
}
