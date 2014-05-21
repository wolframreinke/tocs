package de.tungsten.tocs.engine.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ein Knoten im Spiel.
 * <p>
 * Die Umgebung in TOCS (oder anderen mit dieser Engine laufenden
 * Text-Only-Games) wird als Baum von Objekten (den <code>Node</code>s)
 * modelliert. So k�nnen Objekte ineinander platziert werden, wie z.B. eine
 * Kiste in einem Raum und ein Ball in der Kiste.
 * <p>
 * Die einzelnen Knoten haben alle die Basisklasse <code>Node</code>, k�nnen
 * aber sonst verschieden sein, so gibt es beispielsweise verschlie�bare
 * Knoten, Feuerwaffen-Knoten und die Spieler, die auch Knoten sind.
 * <p>
 * Jeder Knoten hat genau einen Parent-Knoten und beliebig viele Child-Knoten
 * (es gibt zwar semantische Begrenzungen, z.B. kann der Spieler nur 
 * ein bestimmtes Gewicht tragen, aber technisch ist die Zahl unbegrenzt) haben. 
 * Jeder Knoten hat zumindest eine ID von Typ long, die automatisch hochgez�hlt 
 * wird, und zu Debugging-Zwecken verwendet werden kann. Au�erdem hat jeder 
 * Knoten eine Menge von Namen, einen Identifier, eine Beschreibung und ein 
 * Gewicht.
 * <p>
 * Diese Klasse macht es auch m�glich, Knoten Eigenschaften zuzuweisen, ohne
 * gleich eine neue Subklasse definieren zu m�ssen (obwohl das oft zu bevorzugen
 * ist). Jedem Knoten k�nnen Attribute zugewiesen werden, deren Inhalt man �ber
 * einen String-Schl�ssel auslesen/ver�ndern kann. So k�nnen z.B. Ereignisse
 * geskriptet werden, wie eine Katze, die nach einem Schuss in die Luft aufwacht
 * (das Attribut "schlafend" k�nnte sich hier von true nach false �ndern). 
 * 
 *  
 * @author tungsten
 *
 */
public class Node {
	
	// Die Identifier f�r ein paar "standardisierte" Knoten
	public static final String NODE_ROOT						= "root";
	public static final String NODE_INVENTORY					= "backpack";
	public static final String NODE_HANDS						= "hands";
	
	// Schl�ssel f�r einige standardisierte Attribute
	public static final String ATTR_DESCRIPTION_HANDLER			= "DESCRIPTION_HANDLER";
	public static final String ATTR_HIDDEN						= "HIDDEN";
	
	/**
	 * Die zuletzt vergebene ID. Dieser Wert wird bei der Erstellung eines
	 * neuen Knotens automatisch hochgez�hlt.
	 */
	private static int CURRENT_ID = Integer.MIN_VALUE;
	
	/**
	 * Die eindeutige Identifikationsnummer dieses Knotens.
	 */
	private int id;
	
	/**
	 * Die Namen, die dieser <code>Node</code> hat. Auf die meisten Objekte
	 * im realen Leben kann man sich mit mehr als einem Wort/Begriff beziehen.
	 * Damit Spieler das Spiel m�glichst intuitiv bedienen k�nnen, werden hier
	 * alle Begriffe gespeichert, die f�r den Knoten verwendet werden k�nnten.
	 * <br>
	 * <b>Beispiel: </b> { "Sofa", "Couch", "Liege", ... }
	 */
	private String[] names;
	
	/**
	 * Die Beschreibung dieses Knotens. Die Beschreibung wird angezeigt,
	 * wenn der Spieler sie mittels "describe [NAME DES KNOTENS]" o.�.
	 * anfordert.
	 * 
	 * <p>
	 * Die Beschreibung ist eigentlich otional,
	 * da sie nur angezeigt wird, wenn der Spieler sie verlangt (d.h. eigentlich
	 * m�sste eine eigene Klasse <code>DescribeableNode</code> existieren, die
	 * diese Eigenschaft enth�lt), aber da eine Beschreibung eigentlich Teil
	 * jedes Knotens in jedem Text-Adventure ist, wurde er hier direkt 
	 * hinzugef�gt.
	 */
	protected String description;
	
	/**
	 * Der Parent-Knoten. Diese Referenz wird f�r eine globale Suche nach 
	 * Knoten ben�tigt (eine globale Suche ist die lokale Suche unter dem
	 * Root-Element, das Root-Element kann nur �ber <code>parent</code>
	 * ermittelt werden).
	 * <p>
	 * Dieser Knoten muss auch in den {@link #children} des <code>parent</code>-
	 * Knotens vorhanden sein, um die Konsistenz zu erhalten.
	 */
	protected Node parent;
	
	/**
	 * Die Kind-Knoten dieses Knotens. Alle Kind-Knoten haben als {@link #parent}
	 * diesen Knoten.
	 */
	protected List<Node> children;
	
	/**
	 * Das Gewicht dieses Knotens. Der Spieler kann nur ein bestimmtes Gewicht
	 * tragen. TODO Dieses Attribut sollte vielleicht so nicht existieren.
	 */
	protected int weight;
	
	/**
	 * Die dynamischen Eigenschaften dieses Knotens.
	 * Siehe {@link Node}.
	 */
	protected Map<String, Object> attributes;
	
	/**
	 * Erstellt einen neuen Root-<code>Node</code>, also einen Knoten ohne
	 * Parent-Knoten mit einer Default-Beschreibung und Default-Namen.
	 */
	public Node() {
		this( new String[]{ "root" }, "The root node" );
	}
	
	/**
	 * Erstellt einen Knoten ohne Parent-Knoten (Root-<code>Node</code>). Dabei
	 * werden die Namen und die Beschreibung festgelegt.
	 * Das Gewicht muss separat �ber {@link #setWeight(int)} eingestellt werden. 
	 * Standardm��ig wird hier -1 angenommen.
	 * 
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						�ber die sich der Spieler auf diesen Knoten
	 * 						beziehen kann. Dieses Array muss mindestens die 
	 * 						L�nge 1 haben, andernfalls wird eine
	 * 						<code>NodeException</code> geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Dieser Wert darf
	 * 						darf nicht <code>null</code> sein, sonst wird eine
	 * 						<code>NodeException</code> geworfen.
	 */
	public Node( String[] names, String description ) {
		this( null, names, description );
	}
	
	/**
	 * Erstellt einen Knoten unter Angabe des Parent-Knotens, der Namen und der
	 * Beschreibung. Das Gewicht muss separat �ber {@link #setWeight(int)}
	 * eingestellt werden. Standardm��ig wird hier -1 angenommen.
	 * 
	 * @param parent		Der diesem Knoten �bergeordnete Knoten. Wird 
	 * 						<code>null</code> �bergeben, stellt dieser
	 * 						Knoten einen Root-Knoten dar.
	 * @param names			Die Namen dieses Knotens, also die Zeichenketten,
	 * 						�ber die sich Spieler auf diesen Knoten beziehen
	 * 						k�nnen. Dieses Array muss mindestens die L�nge
	 * 						1 haben, andernfalls wird eine 
	 * 						<code>NodeException</code> geworfen.
	 * @param description	Die Beschreibung dieses Knotens. Dieser Wert darf
	 * 						nicht <code>null</code> sein, sonst wird eine
	 * 						<code>NodeException</code> geworfen.
	 */
	public Node( Node parent, String[] names, String description ) {
		
		// Dass CURRENT_ID �berl�uft ist seeeeehr unwahrscheinlich
		this.id = CURRENT_ID++;
		
		// Bei fehlerhaften Parametern eine Exception werfen
		if ( names == null
				|| names.length < 1 
				|| description == null )
			throw new NodeException( this.id, "Node initialization failed." );
		
		this.names = names;
		
		this.parent = null;
		this.move( parent );
		
		this.description = description;
		normalizeDescription(); // Beschreibung normieren
		
		this.children = new ArrayList<Node>();
		this.attributes = new HashMap<String, Object>();
		
		// Default-m��ig k�nnen von diesem Knoten unendliche viele Exemplare
		// getragen werden.
		this.weight = -1;
	}
	
	/**
	 * Gibt die im Knostruktor definierte, eindeutige Identifikationsnummer
	 * dieses Knotens zur�ck. Diese Nummer kann zu Debugging-Zwecken verwendet
	 * werden und wird in allen geworfenen Exceptions mit �bergeben.
	 * 
	 * @return Die Identifikationsnummer dieses Knotens.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Gibt den (potentiell mehrdeutigen) Identifikationsstring dieses Knotens
	 * zur�ck. Dieser String wird immer angezeigt, wenn das Spiel den Knoten
	 * auf der Konsole ausgibt. Es ist somit der Standard-Name dieses
	 * Knotens.
	 * <p>
	 * Als Identifikationsstring wird der {@link #names Name} mit dem Index
	 * 0 verwendet.
	 * 
	 * @return	Den (potentiell mehrdeutigen) Identifikationsstring dieses
	 * 			Knotens.
	 */
	public String getIdentifier() {
		// Annahme zu Debugging-Zwecken
		assert (names != null && names.length > 0) : "Knoten " + id + "'s Namen-Array ist ung�ltig.";
		
		return names[ 0 ];
	}

	/**
	 * Gibt die {@link #names Namen} dieses Knotens zur�ck, also die Strings,
	 * �ber die sich ein Spieler auf diesen Knoten beziehen kann.
	 * 
	 * @return	Die Namen dieses Knotens. Bei konsistentem Zustand des Knotens
	 * 			ist das Array nicht <code>null</code> und hat mindestens die
	 * 			L�nge 1.
	 */
	public String[] getNames() {
		return names;
	}
	
	/**
	 * Generiert die Beschreibung und gibt sie zur�ck. Dabei werden auch
	 * die Namen aller Knoten, die sich direkt unterhalb dieses Knotens befinden
	 * zur R�ckgabe hinzugef�gt.
	 * 
	 * @return	Die generierte Beschreibung dieses Knotens.
	 */
	public String getDescription() {
		
		return getDescription( 1 );
	}
	
	/**
	 * Generiert die Beschreibung dieses Knotens und gibt sie zur�ck. Dabei
	 * werden die Namen aller Knoten, die sich maximal <code>depth</code>
	 * Ebenen unterhalb dieses Knotens befinden zur R�ckgabe hinzugef�gt.
	 * <p>
	 * Wenn dieser Knoten das {@link #attributes Attribut} 
	 * <code>"{@value #ATTR_DESCRIPTION_HANDLER}"</code> besitzt, wird
	 * der entsprechende <code>IValueCalculator</code> zur Generierung
	 * der Beschreibung verwendet.
	 * <p>
	 * <b>Wichtig:</b> Diese Methode ist noch nicht vollst�ndig implementiert.
	 * Als Suchtiefe wird bisher immer 1 verwendet.
	 * 
	 * @param depth	Die Anzahl der Ebenen, die sich eine Knoten unterhalb dieses
	 * 				Knotens befinden darf, um noch im R�ckgabewert enthalten
	 * 				zu sein (also quasi die Suchtiefe).
	 * @return		Die generierte Beschreibung dieses Knotens.
	 */
	public String getDescription( int depth ) {
		
		// Wenn ATTR_DESCRIPTION_HANDLER Attribut vorhanden, dann dieses
		// zum Generieren verwenden, sonst Default-BEschreibung.
		String desc;
		if (   attributes.containsKey( ATTR_DESCRIPTION_HANDLER ) 
			&& attributes.get( ATTR_DESCRIPTION_HANDLER) instanceof IValueCalculator<?>) {
			try {
				desc = (String) ((IValueCalculator<?>) attributes.get( ATTR_DESCRIPTION_HANDLER )).calculate( this );
			} catch ( Exception e ) {
				desc = description;
			}
			
		} else desc = description;
		
		// Kind-Knoten mit zur�ckgeben
		// TODO Momentan noch nicht Rekursiv
		if ( depth != 0 ) {
			
			for (Node child : children) {
				String prefix = "\n\t";
				for (int i = 0; i < depth; i++) {
					prefix += "\t";
				}
				desc += prefix + child.getIdentifier();
			}
			
		}
		return desc;
	}
	
	/**
	 * Gibt dem diesen Knoten �bergeordneten Knoten (den Parent-Knoten)
	 * zur�ck. Es die Zusicherung, dass der Parent-Knoten auch 
	 * diesen Knoten als {@link #children Kind} hat.
	 * 
	 * @return	Den Parent-Knoten dieses Knotens, oder <code>null</code> wenn
	 * 			dieser Knoten ein Root-Knoten ist.
	 */
	public Node getParent() {
		return parent;
	}
	
	/**
	 * Gibt die Kind-Knoten dieses Knotens zur�ck. Alle Kind-Knoten haben
	 * als {@link #parent Parent}-Knoten diesen Knoten.
	 * 
	 * @return	Die Kind-Knoten dieses Knotens.
	 */
	public List<Node> getChildren() {
		return children;
	}
	
	/**
	 * F�gt diesem Knoten ein Kind hinzu. Dabei wird dieser Knoten zum
	 * {@link #parent Parent} des hinzuzuf�genden Knotens.
	 * 
	 * @param child		Der hinzuzuf�gende Knoten. Wenn <code>null</code>
	 * 					�bergeben wird, passiert nichts.
	 * @return			Dieser Knoten, zwecks chaining. Sollten Subklassen
	 * 					das hinzuf�gen eines Knotens verhindern wollen,
	 * 					k�nnen sie auch <code>null</code> zur�ckgeben.
	 */
	public Node addChild( Node child) {
		if ( child != null ) {
			child.parent = this;
			children.add( child );
		}
		
		return this;
	}
	
	/**
	 * Entfernt den gegebenen Knoten von den Kind-Knoten dieses Knotens. Dabei
	 * wird der zu entfernende Knoten zu einem Root-Knoten (d.h. 
	 * {@link #parent} wird auf <code>null</code> gesetzt).
	 * 
	 * @param child	Der zu entfernenden Knoten.
	 */
	public void removeChild( Node child ) {
		child.parent = null;
		children.remove( child );
	}
	
	/**
	 * Gibt das Gewicht dieses Knotens zur�ck. Spieler k�nnen nur ein
	 * bestimmtes Maximal-Gewicht tragen. Ein Gewicht von -1 hei�t, dass von
	 * diesem Knoten beliebig viele Exemplare getragen werden k�nnen.
	 * 
	 * @return	Das Gewicht dieses Knotens.
	 */
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * Legt das Gewicht dieses Knotens fest. Spieler k�nnen nur ein
	 * bestimmtes Maximal-Gewicht tragen. Ein Gewicht von -1 hei�t, dass von
	 * diesem Knoten beliebig viele Exemplare getragen werden k�nnen.
	 * 
	 * @param weight	Das neue Gewicht dieses Knotens.
	 * @return			Dieser Knoten, zwecks chaining.
	 */
	public Node setWeight( int weight ) {
		this.weight = weight;
		
		return this;
	}
	
	/**
	 * Gibt die Attribute dieses Knotens zur�ck. Die Attribute erm�glichen es
	 * Eigenschaften und Ereignisse zu skripten, ohne daf�r neue Klassen von
	 * <code>Node</code> ableiten zu m�ssen. Bei h�ufig verwendeten 
	 * Funktionalit�ten ist der Vererbungs-Ansatz allerdings vorzuziehen.
	 * <p>
	 * Die Attribute werden als Mapping von Strings zur konkreten
	 * Objekten beliebigen Types realisiert. So k�nnen einzelne Attribute
	 * �ber eine Schl�ssel-Zeichenkette angesprochen werden.
	 * <p>
	 * <b>Beispiel: </b><br>
	 * Ein Blatt Papier im Spiel soll seinen Zustand von leer zu beschrieben
	 * wechseln (vielleicht auch mit dynamischen Text?). Zu diesem Zweck wird
	 * das Attribut mit dem Schl�ssel "beschrieben" hinzugef�gt. Der Typ des
	 * gemappten Wertes ist boolean. Nun kann der Wert �ber den Schl�ssel in
	 * Skripts abgefragt und ge�ndert werden.
	 * 
	 * @return	Die Attribute dieses Knotens.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	/**
	 * Gibt den Wert des Attributes mit dem Schl�ssel <code>key</code> 
	 * zur�ck. F�r mehr Details, siehe {@link #getAttributes()}.
	 * <p>
	 * Dieser Aufruf ist gleichbedeutend mit:
	 * <pre>
	 * getAttributes().get( key );
	 * </pre>
	 * 
	 * @param key	Der Schl�ssel des gew�nschten Wertes.
	 * @return		Der Wert mit dem gegebenen Schl�ssel.
	 */
	public Object getAttribute( String key ) {
		
		return attributes.get( key );
		
	}
	
	/**
	 * Sucht nach Knoten, die den gebenen {@link #getIdentifier() Identifier}
	 * haben. Dabei wird maximal <code>depth</code> Ebenen unter diesem Knoten
	 * gesucht. Der erste gefundene Knoten wird als Ergebnis zur�ckgegeben,
	 * daher ist auch die globale Suche nach dem passenden Knoten selten 
	 * zielf�hrend (da der Identifier potentiell mehrdeutig ist).
	 * <p>
	 * Die globale Suche nach Knoten ist dennoch mit 
	 * <code>getRoot().findByIdentifier( identifier, -1 )</code> m�glich. Die
	 * Suchtiefe von -1 steht dabei f�r unbegrenzte Suchtiefe.
	 * <p>
	 * Diese Suche ist eine Tiefensuche (Deep-First), daher wird nicht der
	 * Knoten zur�ckgegeben, der sich am n�chsten zu diesem Knoten befindet,
	 * sondern der Knoten, der sich am weitesten "links" (also kleine 
	 * Indices) in den Kind-Knoten befindet.
	 * 
	 * @param identifier	Der Identifier, nach dem gesucht werden soll.
	 * @param depth			Die Suchtiefe. Negative Werte stehen f�r eine
	 * 						unbegrenzte Suchtiefe, 0 ist mit einem direkten
	 * 						Vergleich gleichzusetzen.
	 * @return				Der erste gefundene Knoten mit dem gegebenen
	 * 						Identifier (Suchweise siehe oben). Wird kein
	 * 						passender Knoten gefunden, wird <code>null</code>
	 * 						zur�ckgegebene.
	 */
	public Node findByIdentifier( String identifier, int depth ) {
		
		if ( names[ 0 ].equals( identifier ) ) return this;
		else {

			// Der == Operator wird absichtlich verwendet, um unbegrenzte
			// Suchtiefe zu erm�glichen
			if ( depth == 0 ) return null;
			else {
				
				for (Node child : children) {
					
					Node result = child.findByIdentifier( identifier, depth - 1 );
					// Ersten Treffer sofort zur�ckgeben
					if ( result != null ) return result;
				}
				
			}
			
			return null;
		}
	}
	
	/**
	 * Sucht nach Knoten, die dem gegebenen Suchpr�dikat gen�gen. Dabei wird
	 * maximal <code>depth</code> Ebenen unterhalb dieses Knotens gesucht.
	 * Als Ergebnis wird eine Liste mit allen passenden Knoten zur�ckgegeben.
	 * <p>
	 * Die globale Suche nach Knoten ist mit
	 * <code>getRoot().find( predicate, -1 )</code> m�glich. Die -1 steht
	 * hier f�r eine unbegrenzte Suchtiefe.
	 * <p>
	 * Diese Suche ist eine Tiefensuche (Deep-First), d.h. die Ergebnisse sind
	 * nicht prim�r nach ihrer N�he zu diesem Knoten geordnet, sondern danach,
	 * wie weit "links" (niedriche Indices) sie sich in den Kind-B�umen dieses
	 * Knotens befinden.
	 * 
	 * @param predicate	Das Suchpr�dikat, dem die gesuchten Knoten gen�gen.
	 * @param depth		Die maximale Suchtiefe. Negative Werte stehen f�r
	 * 					eine unbegrenzte Suchtiefe. 0 entspricht einem
	 * 					direkten Vergleich.
	 * @return			Alle Knoten die der Suchbedingung gen�gen. Eine weiterer
	 * 					Filter ist die Suchtiefe (siehe oben). Die Werte sind
	 * 					wie in der Beschreibung der Methode vermerkt geordnet.
	 */
	public List<Node> find( INodePredicate predicate, int depth ) {
		
		List<Node> result = new ArrayList<Node>();
		
		if ( predicate.matches( this ) ) result.add( this );
		
		// Hier wird absichtlich == verwendet, um unbegrenzte Suchtiefen zu
		// erm�glichen.
		if ( depth == 0 ) return result;
		else {
			
			for (Node child : children) {
				result.addAll( child.find( predicate, depth - 1 ) );
			}
			
			return result;
		}
		
	}
	
	/**
	 * Bewegt diesen Knoten zum gegebenen Knoten, d.h. dieser Knoten wird
	 * ein Kind-Knoten des gegebenen Knotens, und der gegebene knoten
	 * wird der Parent-Knoten dieses Knotens.
	 * <p>
	 * Wenn das <code>target</code> <code>null</code> ist, passiert nichts.
	 * <p>
	 * Hat dieser Knoten einen Parent-Knoten, so wird er aus den Kind-Knoten
	 * des Parent-Knotens entfernt.
	 * 
	 * @param target	Der Knoten, zu welchem dieser Knoten hinzugef�gt werden
	 * 					soll.
	 */
	public synchronized void move( Node target ) {
		
		if ( target != null ) {
			// If this node has no parent, it can be moved though.
			if ( parent != null )
				parent.removeChild( this );
			
			target.addChild( this );
			
			parent = target;
		}
	}
	
	/**
	 * Gibt den Root-Knoten des Baumes zur�ck, in dem sich dieser Knoten
	 * befindet. Ist der Knoten selbst der Root-Knoten, wird er auch selbst
	 * zur�ckgegeben.
	 * 
	 * @return	Der Root-Knoten dieses Knotens.
	 */
	public Node getRoot() {
		if ( parent == null ) return this;
		else return parent.getRoot();
	}
	
	/**
	 * Passt die Beschreibung so an, dass sie dem Spieler in einheitlichem
	 * Format pr�sentiert werden kann. Das umfasst haupts�chlich das entfernen/
	 * ersetzen von Leerzeichen, Tabulatoren und Zeilenumbr�chen.
	 */
	private void normalizeDescription() {
		
		while ( description.contains( "  " ) )
			description = description.replace( "  ", " " );
		
		description = description.replace( "\t", "" );
		description = description.replace( "\n ", "\n" );
		

	}
}
