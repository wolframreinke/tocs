package de.tungsten.tocs;

import de.tungsten.tocs.net.FatalServerError;
import de.tungsten.tocs.net.TOCSServer;

public class Main {

	public static void main(String[] args) throws FatalServerError {			
		
		TOCSServer server = new TOCSServer();
		server.start();
	}

}
