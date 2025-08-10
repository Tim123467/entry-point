/* Program to verify whether a duplicate mission can occur in between Night Heists.
 * The output is produced in the console.
 */

import java.io.*;
import java.util.*;

public class EntryPointDuplicateMissions {
	/** The missions that can have daily challenges, in all caps and without "The" at the beginning. */
	private enum Mission {
		BLACKSITE, FINANCIER, DEPOSIT, LAKEHOUSE, WITHDRAWAL, SCIENTIST, SCRS, BLACK_DUSK,
		KILLHOUSE, AUCTION, GALA, CACHE, SETUP, LOCKUP, SCORE
	}
	
	final static Mission[] FREE_MISSIONS = {
		Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
		Mission.SCRS, Mission.BLACK_DUSK, Mission.KILLHOUSE
	};
	final static Mission[] FREELANCE_HEISTS = {
			Mission.SETUP, Mission.LOCKUP, Mission.SCORE
	};
	
	/** @return The Class object representing Mission. */
	@SuppressWarnings("unchecked")
	private static Class<Mission> getMissionClass() {
		return (Class<Mission>)Mission.SCRS.getClass(); //could be any mission here
	}
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader("EP Daily Challenges Wikitext.txt"));) {
			Map<Mission, Byte> numOccurrences = new EnumMap<>(getMissionClass());
			String line = readFile.readLine(); //first line of the txt file is not needed
			
			for (Mission m : FREE_MISSIONS) {
				numOccurrences.put(m, (byte)0);
			}
			
			//processes the txt file
			outer: while (true) {
				line = readFile.readLine();
				if (line == null) {break;} //stops the loop when there's no more lines to read
				
				for (Mission m : FREE_MISSIONS) {
					if (line.toUpperCase().contains(m.toString())) {
						numOccurrences.put(m, (byte)(numOccurrences.get(m) + 1));
						if (numOccurrences.get(m) > 1) {
							System.out.println("Duplicate detected. Mission: " + m);
							System.exit(0);
						}
						continue outer;
					}
				}
				for (Mission m : FREELANCE_HEISTS) {
					if (line.toUpperCase().contains(m.toString())) {
						for (Mission m2 : FREE_MISSIONS) {
							numOccurrences.put(m2, (byte)0);
						}
						continue outer;
					}
				}
				if (line.contains("Black Dusk")) {
					numOccurrences.put(Mission.BLACK_DUSK, (byte)(numOccurrences.get(Mission.BLACK_DUSK) + 1));
					if (numOccurrences.get(Mission.BLACK_DUSK) > 1) {
						System.out.println("Duplicate detected. Mission: BLACK_DUSK");
						System.exit(0);
					}
				}
			}
			
			System.out.println("No duplicates detected.");
		} catch (FileNotFoundException e) {
			System.out.println("The txt file does not exist or could not be found.");
			System.err.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("The txt file could not be read or closed.");
			System.err.println("IOException: " + e.getMessage());
		}
	}
}
