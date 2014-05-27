package de.tungsten.tocs.testcases;

import java.util.Scanner;

import org.junit.Test;

import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.engine.Chatroom;
import de.tungsten.tocs.engine.Team;
import de.tungsten.tocs.engine.WeaponType;
import de.tungsten.tocs.engine.maps.IMapProvider;
import de.tungsten.tocs.engine.maps.Map;
import de.tungsten.tocs.engine.maps.XMLMapProvider;
import de.tungsten.tocs.engine.nodes.Player;
import de.tungsten.tocs.engine.nodes.WeaponNode;
import de.tungsten.tocs.engine.parsing.Parsers;
import de.tungsten.tocs.engine.parsing.instances.DescribeParser;
import de.tungsten.tocs.engine.parsing.instances.GoParser;
import de.tungsten.tocs.engine.parsing.instances.PutParser;
import de.tungsten.tocs.engine.parsing.instances.SayParser;
import de.tungsten.tocs.engine.parsing.instances.ShootParser;
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
		
		//Logger.getInstance().setOutputStream( System.out );
		//Logger.getInstance().setLogLevel( LogLevel.DEBUG );
		
		IMapProvider mapProvider = new XMLMapProvider();
		Map map = null;
		
		try {
			map = mapProvider.provideMap();
		} catch (Exception e) {
			
			System.out.println( "Map Loading Failed." );
		}
		
		WeaponType type = new WeaponType( "AKS-74", "An old russian AKS-74", 31, 3, 10, 200 );
		WeaponNode weapon = type.newInstance();
		weapon.reload();
		
		Player tungsten = new Player( "tungsten", Team.COUNTER_TERRORISTS );
		weapon.move( tungsten.getInventory() );
		
		Player terrorist = new Player( "n00b", Team.TERRORISTS );
		
		DummyConnection connection1 = new DummyConnection( tungsten );
		DummyConnection connection2 = new DummyConnection( terrorist );
		map.spawn( connection1 );

		
		Parsers ips = Parsers.getInstance();
		ips.addParser( new GoParser() );
		ips.addParser( new DescribeParser() );
		ips.addParser( new TakeParser() );
		ips.addParser( new PutParser() );
		ips.addParser( new ShootParser() );
		ips.addParser( new SayParser() );
		
		Chatroom.getInstance().register( connection1 );
		Chatroom.getInstance().register( connection2 );
		
		Scanner scanner = new Scanner( System.in );
		
		String input;
		do {
			connection1.write( "\n\n> " );
			
			input = scanner.nextLine();
			ips.parse( input, connection1 );
			
		} while ( !input.equals( "exit" ) );
		
		scanner.close();
	}

}
