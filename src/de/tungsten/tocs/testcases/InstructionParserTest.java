package de.tungsten.tocs.testcases;

import org.junit.Test;

import de.tungsten.tocs.engine.parsing.Instruction;
import de.tungsten.tocs.engine.parsing.InstructionParser;

public class InstructionParserTest {

	private class TestInstructionParser extends InstructionParser {
		
		public Instruction parsePublic( String input ) {
			return createInstruction( input );
		}
	}
	
	@Test
	public void testCreateInstruction() {

		final String[] testInputs = new String[] {
			"open book",
			"open the book",
			"open the book and the box",
			"open the book with the knife",
			"open the book in the box with the knife",
			"open the book in the box with the knife on the desk",
			"open the book and the chest in the box on the desk"
		};
		
		TestInstructionParser parser = new TestInstructionParser();
		for (String input : testInputs) {
			
			Instruction instruction = parser.parsePublic( input );
			System.out.println( instruction );
		}
	}

}
