package de.tungsten.tocs.testcases;

import java.util.Scanner;

import org.junit.Test;

import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.maps.IMapProvider;
import de.tungsten.tocs.engine.maps.Map;
import de.tungsten.tocs.engine.maps.XMLMapProvider;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.parsing.Parsers;
import de.tungsten.tocs.engine.parsing.instances.DescribeParser;
import de.tungsten.tocs.engine.parsing.instances.GoParser;
import de.tungsten.tocs.engine.parsing.instances.PutParser;
import de.tungsten.tocs.engine.parsing.instances.TakeParser;
import de.tungsten.tocs.net.IPlayerConnection;

public class XMLMapProviderTest {

	private class DummyConnection implements IPlayerConnection {

		private Player player;
		
		public DummyConnection( Player player ) {
			this.player = player;
		}
		
		@Override
		public boolean write(String message) {

			System.out.print( message );
			return true;
		}

		@Override
		public String read() {
			return "";
		}

		@Override
		public Player getPlayer() {
			return player;
		}
		
	}

	
	@Test
	public void test() {

		Configuration.getInstance().loadFromFile( "tocs.conf" );
		
		IMapProvider mapProvider = new XMLMapProvider();
		Map map = null;
		
		try {
			map = mapProvider.provideMap();
		} catch (Exception e) {
			
			System.out.println( "Map Loading Failed." );
		}
		
		DummyConnection connection = new DummyConnection( new Player( "tungsten", Team.COUNTER_TERRORISTS ) );
		map.spawn( connection );
		
		Parsers ips = Parsers.getInstance();
		
		ips.addParser( new GoParser() );
		ips.addParser( new DescribeParser() );
		ips.addParser( new TakeParser() );
		ips.addParser( new PutParser() );
		
		Scanner scanner = new Scanner( System.in );
		
		String input;
		do {
			connection.write( "\n\n> " );
			
			input = scanner.nextLine();
			ips.parse( input, connection );
			
		} while ( !input.equals( "exit" ) );
		
		scanner.close();
	}

}
