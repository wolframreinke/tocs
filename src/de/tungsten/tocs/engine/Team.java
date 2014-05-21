package de.tungsten.tocs.engine;

/**
 * In Counterstrike gibt es Terroristen ({@link #TERRORISTS}) und 
 * Counterterrorists ({@link #COUNTER_TERRORISTS}). Dieses <code>enum</code>
 * verwaltet au�erdem, wie viele Mitgleider jedes Team hat, sodass Spieler
 * automatisch dem unterlegenen Team zugeordnet werden k�nnen.
 * 
 * @author tungsten
 *
 */
public enum Team {
	
	/**
	 * Das Team der bombenlegenden Terroristen.
	 */
	TERRORISTS,
	
	/**
	 * Das Team der bombenentsch�rfenden Polizisten/Einsatztruppen.
	 */
	COUNTER_TERRORISTS;
	
	private static int terroristCount = 0;
	private static int counterTerroristCount = 0;
	
	/**
	 * Teilt der Enumeration mit, dass ein Spieler zu einem Team hinzugef�gt
	 * wurde, damit automatische Teamzuweisungen berechnet werden k�nnen.
	 * 
	 * @param team
	 */
	public static void incrementTeam( Team team ) {
		switch (team) {
		case TERRORISTS:
			terroristCount++;
			break;

		default:
			counterTerroristCount++;
			break;
		}
	}
	
	/**
	 * .
	 * 
	 */
	public static void resetCounts() {
		terroristCount = 0;
		counterTerroristCount = 0;
	}
	
	/**
	 * Gibt das unterlegenere der beiden Teams zur�ck, damit Spieler
	 * die "automatic assignment" gew�hlt haben, diesem zugeordnet werden
	 * k�nnen.
	 * 
	 * @return	Das unterlegenere Team (nach Anzahl der Mitglieder dieses
	 * 			Teams).
	 */
	public static Team assignAutomaticly() {
		if ( terroristCount < counterTerroristCount )
			return Team.TERRORISTS;
		else 
			return Team.COUNTER_TERRORISTS;
	}
}
