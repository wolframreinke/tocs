package de.tungsten.tocs.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.tungsten.tocs.LogLevel;
import de.tungsten.tocs.Logger;
import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.config.ConfigurationType;
import de.tungsten.tocs.engine.maps.IMapProvider;
import de.tungsten.tocs.engine.maps.Map;
import de.tungsten.tocs.engine.maps.XMLMapProvider;

/*
 * Noch nicht dokumentiert, hier ändert sich eh alles wieder.
 * 
 */

public class TOCSServer extends Thread {
	
	private static final String LOG_NAME				= "(CORE) TOCSServer";
	
	private static final String CONFIG_FILE_PATH		= "tocs.conf";
	
	private	static final String CONFIG_PORT				= "port";
	private static final int	DEFAULT_PORT			= 10024;
	
	private	static final String	CONFIG_LOGLEVEL			= "logLevel";
	private static final int	DEFAULT_LOGLEVEL		= 4;
	
	private static final String CONFIG_MAP_LOADER		= "mapLoader";
	private static final String DEFAULT_MAP_LOADER		= XMLMapProvider.CONFIGURATION_NAME;
	
	private final Configuration configuration;
	private static Map map = null;
	private final Logger logger;
	
	private List<Connection> connections = new ArrayList<Connection>();
	
	public TOCSServer() throws FatalServerError {
		super( "TOCS Server-Thread" ); 	// Dem Thread-Konstruktor den richtigen
										// Namen mitgeben.
		
		// Logger initialisieren
		logger = Logger.getInstance();
		logger.setOutputStream( System.out );
		
		// Konfigurations-Datei laden
		configuration = Configuration.getInstance();
		configuration.loadFromFile( CONFIG_FILE_PATH );
		
		// Log-Level aus den Konfigurationen auslesen
		logger.setLogLevel( (int) configuration.getValue( 
				CONFIG_LOGLEVEL, 
				ConfigurationType.INTEGER,
				DEFAULT_LOGLEVEL ) );
		
		// Den MapProvider aus den Konfigurationen auslesen
		IMapProvider provider = null;
		List<IMapProvider> providers = new ArrayList<IMapProvider>();
		String providerID = (String) configuration.getValue( 
				CONFIG_MAP_LOADER, 
				ConfigurationType.STRING, 
				DEFAULT_MAP_LOADER );
		
		// Vergleichsliste; Alle verfügbaren MapProvider kommen in diese
		// Liste und werden dann mit den Wünschen des Benutzers in der
		// Konfigurations-Datei vergleichen
		providers.add( new XMLMapProvider() );
		
		for (IMapProvider current : providers) {
			if ( current.getConfigurationName().equals( providerID ) )
				provider = current;
		}
		
		try {
			// Das könnte eine NullPointerException auslösen, wenn provider
			// null ist, ansonsten wird die Map geladen/bereitgestellt.
			TOCSServer.map = provider.provideMap();
			
		} 
		catch ( NullPointerException npe ) {
			// Kein passender Maploader
			logger.log( LogLevel.FATAL, LOG_NAME, "The map loader \"" + providerID + "\" was not found." );
			throw new FatalServerError( npe );
		}
		catch ( Exception e ) {
			// Der Maploader will die Map nicht laden, warum auch immer.
			logger.log( LogLevel.FATAL, LOG_NAME, "The map loader \"" + provider.getConfigurationName() + "\" could not load the desired map." );
			throw new FatalServerError( e );
		}
	}
	
	@Override
	public void run() {
		
		try {
			// Port aus der Konfig-Datei auslesen
			int port = (int) configuration.getValue( 
					CONFIG_PORT, 
					ConfigurationType.INTEGER, 
					DEFAULT_PORT );
			
			// Socket für diesen Port erstellen
			ServerSocket server = new ServerSocket( port );
			logger.log( LogLevel.INFO, LOG_NAME, "Listening at port " + port + "." );
			
			// Wenn der Benutzer shutdown() aufruft, wird dieser Thread
			// interruptet.
			while ( !isInterrupted() ) {
				
				// client aus Netzwerk accepten
				Socket client = server.accept();
				
				logger.log( LogLevel.INFO, LOG_NAME, "New Connection to " + client.getInetAddress() + "." );
				
				// Zu den Connections adden
				Connection connection = new Connection( client );
				connections.add( connection );
			}
			
			// Socket konventions-konform schließen
			server.close();
			logger.log( LogLevel.INFO, LOG_NAME, "Server down." );
			
		} catch (IOException e) {
			// Fataler Fehler, Server konnte nicht upgesettet werden.
			logger.log( LogLevel.FATAL, LOG_NAME, "Couldn't set up server. Detailed Message: \"" + e.getMessage() +"\"." );
		}
	}
	
	public void shutDown() {
		
		// Alle aktiven Connections abtöten
		for ( Connection current : connections ) {
			current.close();
		}
		interrupt(); 	// Thread beenden, die dazugehörige Schleife ist in
						// run()
		
	}
	
	// TODO Diese Methode sollte ich woanders platzieren
	public static Map getMap() {
		return map; 
	}
}
