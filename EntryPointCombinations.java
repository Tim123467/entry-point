/**
 * Program that calculates and outputs the total number of possible combinations of daily challenges.
 * The output is produced in the console.
 */
import java.io.*;
import java.util.*;

public class EntryPointCombinations {
	final static String txtFileName = "EP Probabilities Raw.txt"; //change depending on the name you set for the output of EntryPointProbabilities.java
	
	/** The missions that can have daily challenges, in all caps and without "The" at the beginning.
	 * I could've split this enum into one for stealth missions and one for loud missions, but that would require more typing. */
	private enum Mission {
		BLACKSITE, FINANCIER, DEPOSIT, LAKEHOUSE, WITHDRAWAL, SCIENTIST, SCRS, BLACK_DUSK,
		KILLHOUSE, AUCTION, GALA, CACHE, SETUP, LOCKUP, SCORE
	}
	
	/** @return The Class object representing Mission. */
	@SuppressWarnings("unchecked")
	private static Class<Mission> getMissionClass() {
		return (Class<Mission>)Mission.SCRS.getClass(); //could be any mission here
	}
	
	/** @param s An instance of the Short class.
	 * @return A short with its value 1 above the value of s. */
	private static short increment(Short s) {
		return (short)(s.shortValue() + 1);
	}
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader(txtFileName));) {
			Map<Mission, Short> stealthNumModifiers = new EnumMap<>(getMissionClass());
			Map<Mission, Short> loudNumModifiers = new EnumMap<>(getMissionClass());
			Map<Mission, Short> stealthNumCombos = new EnumMap<>(getMissionClass());
			Map<Mission, Short> loudNumCombos = new EnumMap<>(getMissionClass());
			//highest num combo value is 2,925 which is below the max value for shorts (32,767)
			final Mission[] stealthMissions = {
				Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
				Mission.SCRS, Mission.KILLHOUSE, Mission.AUCTION, Mission.GALA, Mission.CACHE, Mission.SETUP, Mission.LOCKUP
			};
			final Mission[] loudMissions = {
				Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
				Mission.SCRS, Mission.BLACK_DUSK, Mission.KILLHOUSE, Mission.LOCKUP, Mission.SCORE
			};
			String line = readFile.readLine(); //first line of the txt file is not needed
			String mission;
			boolean isStealth = true;
			int totalStealth = 0, totalLoud = 0;
			
			for (Mission m : stealthMissions) {
				stealthNumModifiers.put(m, (short)0);
			}
			for (Mission m : loudMissions) {
				loudNumModifiers.put(m, (short)0);
			}
			
			//processes the txt file
			while (true) {
				line = readFile.readLine().trim();
				if (line.contains("was")) {break;} //the last part of the txt file is not needed
				if (line.equals("Loud:")) {
					isStealth = false;
					continue;
				}
				if (line.endsWith("0.0") || !line.contains(".")) {continue;}
				
				mission = line.substring(0, line.indexOf(':'));
				if (isStealth) {
					stealthNumModifiers.put(Mission.valueOf(mission), increment(stealthNumModifiers.get(Mission.valueOf(mission))));
				} else { //is loud
					loudNumModifiers.put(Mission.valueOf(mission), increment(loudNumModifiers.get(Mission.valueOf(mission))));
				}
			}
			
			//calculate raw num combos for each mission
			for (Mission m : stealthMissions) {
				stealthNumCombos.put(m, (short)(stealthNumModifiers.get(m) * (stealthNumModifiers.get(m) - 1) * (stealthNumModifiers.get(m) - 2) / 6));
			}
			for (Mission m : loudMissions) {
				loudNumCombos.put(m, (short)(loudNumModifiers.get(m) * (loudNumModifiers.get(m) - 1) * (loudNumModifiers.get(m) - 2) / 6));
			}
			
			//remove No Knockouts & No Suppressors combos
			stealthNumCombos.put(Mission.SCIENTIST, (short)(stealthNumCombos.get(Mission.SCIENTIST) - stealthNumModifiers.get(Mission.SCIENTIST) + 2));
			
			System.out.println("Stealth:");
			for (Mission m : stealthMissions) {
				totalStealth += stealthNumCombos.get(m);
				System.out.println(m.toString() + ": " + stealthNumCombos.get(m));
			}
			System.out.println("Total stealth: " + totalStealth);
			System.out.println("\nLoud:");
			for (Mission m : loudMissions) {
				totalLoud += loudNumCombos.get(m);
				System.out.println(m.toString() + ": " + loudNumCombos.get(m));
			}
			System.out.println("Total loud: " + totalLoud);
			System.out.println("\nGrand total: " + (totalStealth + totalLoud));
		} catch (FileNotFoundException e) {
			System.out.println("The txt file does not exist or could not be found.");
			System.err.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("The txt file could not be read or closed.");
			System.err.println("IOException: " + e.getMessage());
		}
	}
}
