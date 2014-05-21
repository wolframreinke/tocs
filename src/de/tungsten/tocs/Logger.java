package de.tungsten.tocs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Schreibt Log-Nachrichten in den mit {@link #setOutputStream(OutputStream)}
 * festgelegten Stream. Die Nachrichten werden in {@link #log(LogLevel, String)}
 * geschrieben und dabei nach Log-Level gefiltert (Wenn das Log-Level z.B.
 * auf ERROR steht, werden nur ERROR und FATAL Nachrichten geschrieben).
 * <p>
 * Diese Klasse ist als Singleton implementiert, d.h. es gibt nur einen 
 * gemeinsamen Logger für alle Objekte in der Anwendung.
 * <p>
 * Da in TOCS viele Threads zum Einsatz kommen, sind alle Logging-Methoden
 * thread-safe (soweit ich das einschätzen kann).
 * 
 * @author Wolfram
 *
 */
public class Logger {

	/**
	 * Das Log-Level dieses Loggers, das zum Filtern der Nachrichten in
	 * {@link #log(LogLevel, String)} genutzt werden kann.
	 */
	private LogLevel logLevel = LogLevel.DEBUG;

	/**
	 * Der Stream, in den die Log-Daten geschrieben werden. Solange er nicht
	 * mit {@link #setOutputStream(OutputStream)} festgelegt wurde, gehen
	 * alle Logn-Nchrichten ins Leere.
	 */
	private BufferedWriter out;
	
	// Singleton implementation
	private static Logger instance = null;
	private Logger() {}
	
	/**
	 * Gibt die einzige existierende Instanz von <code>Logger</code>
	 * zurück. Dabei wird Lazy Initialization verwendet, die Instanz wird
	 * als erst erstellt, wenn sie zum ersten Mal gebraucht wird.
	 * 
	 * @return	Die einzige Instanz dieser Klasse.
	 */
	public static Logger getInstance() {
		if ( instance == null )
			instance = new Logger();
		
		return instance;
	}
	
	/**
	 * Legt den Stream fest, in den alle Log-Daten geschrieben werden. Solange
	 * diese Methode nicht aufgerufen wurde, werden keine Log-Nachrichten
	 * gexchrieben.
	 * 
	 * @param stream
	 */
	public synchronized void setOutputStream( OutputStream stream ) {
		out = new BufferedWriter( new OutputStreamWriter( stream ) );
	}
	
	/**
	 * Legt das Log-Level anhand eines integers fest. Diese Methode kann
	 * verwendet werden, um das Log-Level aus Dateien oder 
	 * Kommandozeilenargumenten zu entnehmen.
	 * <p>
	 * Das Log-Level wird verwendet, um Log-Nachrichten zu filtern. So werden
	 * nur Nachrichten geschrieben, die ein Log-Level gleich oder niedriger
	 * dem Log-Level dieses Loggers benötigen.
	 * 
	 * @see {@link LogLevel}
	 * 
	 * @param level	Das neue Log-Level des Loggers.
	 */
	public synchronized void setLogLevel( int level ) {
		logLevel = LogLevel.fromLevel( level );
	}
	
	/**
	 * Legt das Log-Level dieses Loggers fest. Das Log-Level wird verwendet,
	 * um Log-Nachrichten zu filtern. So werden nur Nachrichten geschrieben, 
	 * die ein Log-Level gleich oder niedriger dem Log-Level dieses Loggers
	 * benötigen.
	 * 
	 * @see {@link LogLevel}
	 * 
	 * @param level	Das neue Log-Level des Loggers.
	 */
	public synchronized void setLogLevel( LogLevel level ) {
		logLevel = level;
	}
	
	/**
	 * Schreibt die gegebene <code>message</code> in den mit 
	 * {@link #setOutputStream(OutputStream)} festgelegten Stream, wenn
	 * das gegebene Log-Level (<code>level</code>) kleiner oder
	 * gleich dem mit {@link #setLogLevel(LogLevel)} festgelegten Log-Level
	 * des Loggers ist. Als Sender der Nachricht wird die mit 
	 * <code>clientName</code> gegebenene Zeichenkette verwendet.
	 * <p>
	 * Die Nachricht wird in folgendem Format in den Stream geschrieben:
	 * <br>
	 * <code>
	 * '[' $LogLevel '] ' $Date ' - ' $clientName ': ' $message
	 * </code>
	 * 
	 * @param level			Das Log-Level als integer. Es wird erst in ein
	 * 						{@link LogLevel} umgewandelt weiterverwendet.
	 * @param clientName	Die Zeichenkette, die als Sender der Log-Nachricht
	 * 						angezeigt wird.
	 * @param message		Die zu schreibende Nachricht.
	 */
	public synchronized void log( int level, String clientName, String message ) {
		log( LogLevel.fromLevel( level ), clientName, message );
	}
	
	/**
	 * Schreibt die gegebene <code>message</code> in den mit 
	 * {@link #setOutputStream(OutputStream)} festgelegten Stream, wenn
	 * das gegebene Log-Level (<code>level</code>) kleiner oder
	 * gleich dem mit {@link #setLogLevel(LogLevel)} festgelegten Log-Level
	 * des Loggers ist. Als Sender der Nachricht wird die mit
	 * <code>clientName</code> übergebene Zeichenkette verwendet.
	 * <p>
	 * Die Nachricht wird in folgendem Format in den Stream geschrieben:
	 * <br>
	 * <code>
	 * '[' $LogLevel '] ' $Date ' - ' $clientName ': ' $message
	 * </code>
	 * 
	 * @param level			Das {@link LogLevel}, das zum Filtern verwendet wird.
	 * @param clientName	Die Zeichenkette, die als Sender der Nachricht angezeigt
	 * 						wird.
	 * @param message		Die zu schreibende Nachricht.
	 */
	public synchronized void log( LogLevel level, String clientName, String message ) {
		
		// Wenn kein Outputstream, dann keine Nachrichten.
		if ( out == null ) return;

		// Zu unwichtige Nachrichten ausfiltern
		if ( logLevel.getPriority() >= level.getPriority() ) {
			
			try {
				out.write( level.toString() + " " + (new Date()).toString() + " - " + clientName + ": " + message + "\n");
				out.flush();
				
			} catch (IOException e) {
				// TODO Das geht nicht.
				e.printStackTrace();
			}
		}
	}
}
