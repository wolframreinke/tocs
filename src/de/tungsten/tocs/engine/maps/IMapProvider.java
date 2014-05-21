package de.tungsten.tocs.engine.maps;

/**
 * Ein <code>IMapProvider</code> ist f�hig, eine {@link Map} zu generieren
 * oder zu laden. Auf diese <code>Map</code> kann mit
 * {@link #provideMap()} zugegriffen werden.
 * <p>
 * Wann die <code>Map</code> erstellt wird (im Konstruktor, in einer weiteren
 * Methode, in <code>provideMap</code> selbst) ist nicht definiert und bleibt
 * den Implementattionen �berlassen.
 * 
 * @author tungsten
 *
 */
public interface IMapProvider {

	/**
	 * Gibt den Namen zur�ck, den der <code>IMapLoader</code> in Konfigurations-
	 * Dateien hat. So kann der Provider anhand von Zeichenketten idendifiziert
	 * werden.
	 * 
	 * @return 	Den Namen dieses <code>IMapProvider</code>s in Textform.
	 */
	public abstract String getConfigurationName();
	
	/**
	 * Gibt ein <code>Map</code>-Objekt zur�ck, das von diesem 
	 * <code>IMapProvider</code> generiert/geladen wird/wurde.
	 * Das <code>Map</code>-Objekt ist vollst�ndig initialisiert, und kann
	 * ohne weitere Ma�nahmen verwendet werden.
	 * 
	 * @return				Ein vollst�ndig initialisiertes <code>Map</code>-
	 * 						Objekt.
	 * @throws Exception	Wenn die <code>Map</code> nicht geladen/generiert
	 * 						werden konnte.
	 */
	public abstract Map provideMap() throws Exception;
}
