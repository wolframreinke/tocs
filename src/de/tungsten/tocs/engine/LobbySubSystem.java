package de.tungsten.tocs.engine;

import java.io.File;

import de.tungsten.tocs.config.Configuration;
import de.tungsten.tocs.config.StringType;
import de.tungsten.tocs.menus.IMenu;
import de.tungsten.tocs.menus.NumericalMenu;
import de.tungsten.tocs.menus.NumericalSelector;
import de.tungsten.tocs.menus.ReturnValue;
import de.tungsten.tocs.net.IPlayerConnection;

/**
 * Ein <code>LobbySubSystem</code> zeigt einer {@link IPlayerConnection} die Lobby-Optionen an, wenn sie an
 * {@link #operate(IPlayerConnection)} übergeben wird. Zur Anzeige werden Subklassen von {@link IMenu} verwendet.
 * <br>
 * Folgende Optionen werden eingeblendet:
 * <ul>
 * <li> <b>Equipment</b> - Zeigt den {@link EquipmentStore} an, damit der Spieler hinter der <code>IPlayerConnection</code>
 * 		Ausr&uuml;stung kaufen kann.
 * <li>	<b>Skin</b> - Ändert den Skin des Spielers (d.h. seine <code>description</code>). Welche Skins angeboten werden,
 * 		hängt vom <code>Team</code> des Spielers ab.
 * <li>	<b>Ready</b> - Teilt der {@link Lobby} mit, dass dieser Spieler bereit zum spawnen ist. Wenn alle Spieler bereit
 * 		sind, werden sie gespawnt.
 * </ul>
 * <p>
 * Die Klasse <code>Lobby</code> stemmt den eigentlichen Verwaltungsaufwand, also den Countdown und ob alle Spieler 
 * bereits bereit sind. Damit sich die Threads der verschiedenen Spieler nicht blockieren, gibt es für jede 
 * <code>IPlayerConnection</code> ein <code>LobbySubSystem</code>, dass die Input/Output-Aufgaben erledigt. 
 * 
 * @author reinke.wolfram-tit13
 *
 */
public class LobbySubSystem implements ISubSystem {

	// Eintrag aus der Konfigurations-Datei für den EquipmentStore
	public	static final String CONFIG_STORE_PATH	= "storePath";
	private	static final String	DEFAULT_STORE_PATH 	= "data/default_store.xml";
	
	/**
	 * Dieses Flag ist auf <code>true</code>, solange nicht {@link #releasePlayer()} aufgerufen wurde. Wenn das
	 * Ende der Methode {@link #operate(IPlayerConnection)} erreicht wurde, wird solange gewartet, bis dieses
	 * Flag auf <code>false</code> steht. 
	 */
	private boolean waiting = true;
	
	/**
	 * Dieses Flag gibt an, ob der Spieler bereits die Option <code>Ready</code> gewählt hat (<code>true</code>).
	 * Wenn nicht, ist dieses Attribut <code>false</code>.
	 */
	private boolean ready = false;
	
	/**
	 * Die Lobby, zu dem dieses LobbySubSystem gehört. Sie wird über {@link Lobby#update()} benachrichtigt, wenn
	 * der Spieler die Option <code>Ready</code> gewählt hat.
	 */
	private Lobby lobby;
	
	/**
	 * Erstellt eine neue Instanz von <code>LobbySubSystem</code> und legt die <code>Lobby</code> fest,
	 * die benachrichtig wird, wenn der Spieler die Option <code>Ready</code> gewählt hat.
	 * 
	 * @param lobby
	 */
	public LobbySubSystem( Lobby lobby ) {
		this.lobby = lobby;
	}
	
	/**
	 * Verwendet die Subklassen von {@link IMenu}, um dem Spieler ein Textmenü anzuzeigen. Hier hat der Spieler folgende
	 * Optionen:
	 * <ul>
	 * <li> <b>Equipment</b> - Wenn der Spieler diese Option wählt, wird er an den {@link EquipmentStore} weitergeleitet.
	 * <li> <b>Skins</b> - Wenn der Spieler diese Option wählt, hat der die Wahl zwischen vom Team abhängigen Skins
	 * 		(der Skin beeinflusst die <code>description</code> des <code>Player</code>-Knotens).
	 * <li> <b>Ready</b> - Der Spieler wählt diese Option, um mitzuteilen, dass er bereit zum spawnen ist. Diese Info
	 * 		wird über <code>Lobby.update()</code> weitergeleitet.
	 * </ul>
	 * <p>
	 * Am Ende der Methode (wenn der Spieler <code>Ready</code> gewählt hat), wartet die sie solange, bis
	 * {@link #releasePlayer()} aufgerufen wird.
	 * 
	 * @param connection 
	 */
	@Override
	public void operate( IPlayerConnection connection ) {

		// Die Ausgabe des IMenus
		String result;
		
		do {
		
			// Ein Nummernmenü zur Auswahl des Skins
			// TODO das muss in eine Datei.
			NumericalSelector skinStore = new NumericalSelector( "Which outfit do you want to put on?" );
			if ( connection.getPlayer().getTeam() == Team.TERRORISTS ) {
				skinStore.addOption( "Mafia Don", 
						"_NAME_ looks like a mafia don, wearing a genteel pinstripe suit and "
						+ "smoking a cigar." );
				skinStore.addOption( "Al Qaeda Suicide Bomber",
						"_NAME_ is a arabian looking man, equipped with a suicide vest and wearing a "
						+ "white turban." );
				skinStore.addOption( "Ukrainian Separatist", 
						"The Ukrainian _NAME_ wears a seedy jacket and has a dusty old firearm in "
						+ "his hands." );
				skinStore.addOption( "Yakuza Hitman", 
						"This asian killer moves quick and precisly. Only a missing finger indicates, "
						+ "that _NAME_ is a Yakuza Hitman." );
			} else {
				skinStore.addOption( "SEAL Team 6",
						"_NAME_ is a fully armed member of the SEAL Team 6, wearing an olive green "
						+ "one-piece suit with heavy grey protectors all over his body." );
				skinStore.addOption( "GSG-9", "_NAME_ is obviosly member of the german GSG-9 "
						+ "forces, exclusivly wearing black clothes and bags. This makes him "
						+ "almost invisible when camping in the shadow." );
				skinStore.addOption( "Spetsnaz", 
						"_NAME_ is wearing gray-carmouflaged combat fatigues"
						+ " paired with a kevlar assault suit with the word 'Spetsnaz' in cyrillic "
						+ "letters." );
				skinStore.addOption( "Israel Defense Force", 
						"Hidden behind a strong amoring, _NAME_'s"
						+ " eyes are barely visibly. Ponderous he finds his way through the area." );
			}
			
			// Das übergeordnete Menü
			IMenu menu = new NumericalMenu( "You're in the game lobby. Here you can buy equipment and change your skin before the game starts." )
				.addOption( "Equipment - Buy weapons and armors", new ReturnValue( "weapons" ) )
				.addOption( "Skins - Change your look", skinStore )
				.addOption( "Ready", new ReturnValue( "ready" ) );
			
			result = (String) menu.display( connection );
			if ( result.equals( "weapons" ) ) {
				// Spieler hat den EquipmentStore gewählt
				
				try {
					// Store aus der Konfiguration laden
					String fileName = (String) Configuration.getInstance().getValue( 
							CONFIG_STORE_PATH, 
							StringType.getInstance(),
							DEFAULT_STORE_PATH );
					
					// Spieler einkaufen schicken
					EquipmentStore store = new EquipmentStore( new File( fileName ) );
					store.operate( connection );
					
				} catch (Exception e) {
					connection.write( "Sorry, the equipment store is currently not available." );
				}
				
			} else if ( !result.equals( "ready" ) ) {
				
				// Spieler hat die Skins gewählt, der Rückgabewert ist die neue Description des Spielers. In der 
				// Description wird _NAME_ mit dem Namen des Spielers ersetzt.
				result = result.replace( "_NAME_", connection.getPlayer().getNickname() );
				connection.getPlayer().setSkin( result );
			}

		} while ( result == null || !result.equals( "ready" ) );
		
		// Spieler ist bereit zum Spawnen, das muss der Lobby mitgeteilt werden.
		ready = true;
		lobby.update();
		
		// Warten bis spawnen erlaubt.
		while ( waiting ) {
			try {
				synchronized ( this ) {
					wait();
				}
			} catch ( InterruptedException e ) {}
		}
	}
	
	/**
	 * Gibt zurück, ob der Spieler bereits die Option <code>Ready</code> in {@link #operate(IPlayerConnection)} gewählt 
	 * hat. 
	 * 
	 * @return <code>true</code> wenn der Spieler die Option <code>Ready</code> gewählt hat, <code>false</code> sonst.
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * Teilt diesem SubSystem mit, dass der Spieler nun spawnen darf. Am Ende der Methode 
	 * {@link #operate(IPlayerConnection)} wird solange gewartet, bis diese Methode aufgerufen wird. Andererseits
	 * werden Spieler, die noch nicht <code>Ready</code> gewählt haben, nicht sofort gespawnt, sondern erst wenn sie
	 * diese Option schließlich auswählen, dann aber sofort.
	 * 
	 */
	public void releasePlayer() {
		waiting = false;
		synchronized ( this ) {
			notify(); // Objekt aufwecken. Dieses notify gehört zu dem wait in operate
		}
	}
}
