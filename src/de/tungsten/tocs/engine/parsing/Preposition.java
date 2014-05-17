package de.tungsten.tocs.engine.parsing;

/**
 * Eine <code>Preposition</code> ist ein spezielles Wort in der Eingabe des Spielers, 
 * das verwendet wird, um ihr Struktur zu verleihen. Der Instruktion des Spielers werden
 * mittels Pr�position sozusagen Argumente �bergeben.
 * <b>Beispiel:</b>
 * <p><code>
 * put the ball from the table into the box
 * </code><p>
 * Hier sind die beiden Pr�positionen "from" und "into". Erstere gibt an, von wo der
 * Ball genommen werden soll, die zweite gibt an, wohin er gelegt werden soll.
 * <p>
 * Es gibt drei Arten von Pr�positionen, denen jeweils eine Menge von englischen W�rten
 * zugeordnet ist, n�mlich
 * <ul>
 * <li> {@link #WITH},
 * <li>	{@link #OF}, und
 * <li> {@link #TO}.
 * </ul>
 * 
 * @author Wolfram
 *
 */
public enum Preposition {
	
	/**
	 * <code>WITH</code> enth�lt folgende englische Pr�positionen: "with" und "using". Wenn
	 * der Spieler einer dieser W�rter verwendet, zeigt das an, dass die entsprechenden
	 * Objekte als Argumente an die Methode �bergeben m�chte. 
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
	 * <code>TO</code> enth�lt folgende englische Pr�positionen: "to", "into", "onto",
	 * "over", "under" und "at". Diese Pr�position wird vom Spieler benutzt, um das Ziel
	 * der Operation zu spezifizieren.
	 * <br><b>Beispiel:</b>
	 * <p><code>
	 * put the ball into the box
	 * </code><p>
	 * Hier ist das Ziel der Operation "put" die Box (wegen "into"). Der Ball ist nicht
	 * das Ziel dieser Instruktion, sondern der Pr�fix (siehe {@link Intsruction}).
	 */
	TO		( new String[] {"to", "into", "onto", "over", "under", "at"} ),
	
	/**
	 * <code>OF</code> enth�lt folgende englische Pr�positionen: "of", "in", "on" und 
	 * "from". Diese Pr�position wird verwendet, um einen "Suchpfad" f�r einen Knoten zu
	 * erstellen (siehe {@link NodeLocation}).
	 * <br><b>Beispiel:</b>
	 * <p><code>
	 * take the ball on the desk
	 * </code><p>
	 * Hier ist der Suchpfad zu dem Ball <code>room -> desk -> ball</code>, wobei der
	 * Raum immer imlizit hinzugef�gt wird.
	 */
	OF		( new String[] {"of", "in", "on", "from"} );
	
	String[] content;
	
	Preposition( String[] s ) {
		this.content = s;
	}
	
	/**
	 * Gibt die Pr�position zur�ck, die den gegebenen String enth�lt. Wenn keine passende
	 * Pr�position gefunden wird, wird <code>null</code> zur�ckgegeben. Diese Methode
	 * ignoriert, ob der gegebene String gro� oder klein geschrieben wurde.
	 * 
	 * 
	 * @param s		Der String der mit den einzelnen Pr�positionen verglichen wird.
	 * @return		Die zum gegebenen String passende Pr�position oder <code>null</code>,
	 * 				wenn keine der Pr�positionen passt.
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
