package de.tungsten.tocs.net;

/**
 * Wenn diese Exception geworfen wurde, ist es dem Server unm�glich, in einen
 * geordneten Zustand zur�ckzukehren. Wenn diese Exception aufgefangen wird,
 * darf der entsprechende {@link TOCSServer} nicht weiter betrieben werden,
 * da sein Verhalten undefiniert ist.
 * 
 * @author Wolfram
 *
 */
@SuppressWarnings("serial")
public class FatalServerError extends Exception {

	public FatalServerError( Throwable cause ) {
		super( cause );
	}
}
