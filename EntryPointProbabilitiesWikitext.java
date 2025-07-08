/* Program used to translate the output of EntryPointProbabilities.java into wikitext.
 * The only edits that should be made are to the value of txtFileName
 * (unless you want to try to improve the code).
 * The output is produced in the console.
 */

import java.io.*;
import java.util.*;

public class EntryPointProbabilitiesWikitext {
	final static String txtFileName = "EP Probabilities Raw.txt"; //change depending on the name you set for the output of EntryPointProbabilities.java
	
	/** This is needed because enums cannot override their compareTo method. */
	static final Comparator<Modifier> STRING_ORDER = new Comparator<Modifier>() {
		public int compare(Modifier m1, Modifier m2) {
			return m1.toString().compareTo(m2.toString());
		}
	};
	
	private enum Color {
		GREEN, BLUE, PURPLE, RED
	}
	
	/** All modifiers that can be picked for a daily challenge, in all caps and with spaces replaced with underscores.
	 * I could've split this enum into one for stealth modifiers and one for loud modifiers, but that would require more typing. */
	private enum Modifier {
		MISPLACED_GEAR(Color.GREEN), NO_AEGIS_ARMOR(Color.GREEN), BOARDED_UP(Color.GREEN),
		FOG(Color.GREEN), NO_SUPPRESSORS(Color.GREEN), NO_SAFECRACKING(Color.GREEN),
		WEAPON_SCANNERS(Color.BLUE), NO_SCRAMBLER(Color.BLUE), BLOODLESS(Color.BLUE), UNSKILLED(Color.BLUE), SMALL_ARMS_ONLY(Color.BLUE),
		CRIMINAL_ARSENAL(Color.BLUE), NO_HYBRID_CLASSES(Color.BLUE), CASCADE_ARSENAL(Color.BLUE), FLASHBANG_FRENZY(Color.BLUE), REINFORCED_DOORS(Color.BLUE),
		HEAVY_BAGS(Color.BLUE), ARMERA_ARSENAL(Color.BLUE), NO_EQUIPMENT_BAGS(Color.BLUE), REINFORCED_LOCKS(Color.BLUE), GLASS_CANNON(Color.BLUE),
		MANDATORY_HEADSHOTS(Color.BLUE), NO_INTERROGATION(Color.BLUE), EXTRA_CAMERAS(Color.BLUE), NO_HEAVY_ARMOR(Color.BLUE),
		UNINTIMIDATING(Color.PURPLE), WEAKER_MEDKITS(Color.PURPLE), HIDDEN_UI(Color.PURPLE), FASTER_DETECTION(Color.PURPLE), REINFORCED_CAMERAS(Color.PURPLE),
		AEGIS_ACADEMY(Color.PURPLE), FLASHBANG_REVENGE(Color.PURPLE), ONE_SHOT(Color.PURPLE), HIDDEN_DETECTION_BARS(Color.PURPLE),
		NO_LOCKPICKS(Color.PURPLE), INEXPERIENCED(Color.PURPLE), FIFTEEN_MINUTES(Color.PURPLE), LESS_HEALTH(Color.PURPLE),
		NO_EXPLOSIVES(Color.RED), TAKEDOWN_LIMIT(Color.RED), SHIELD_SWARM(Color.RED), EXPLOSIVE_FLASHBANGS(Color.RED),
		NO_KNOCKOUTS(Color.RED), NO_MOVING_BODIES(Color.RED), NO_DISGUISE(Color.RED), EXPLOSIVE_REVENGE(Color.RED);
		
		private final Color color;
		
		Modifier(Color color) {
			this.color = color;
		}
		
		/**
		 * @return The string representation of the modifier's color in lowercase.
		 */
		String getColor() {
			return color.toString().toLowerCase();
		}
	}
	
	/**
	 * System.out.println();
	 */
	private static void p() {
		System.out.println();
	}
	
	/**
	 * System.out.println(s);
	 */
	private static void p(String s) {
		System.out.println(s);
	}
	
	/**
	 * A method specifically designed for this program.
	 * @return The input string but in title case (with a built-in exception for Hidden UI).
	 */
	private static String titleCase(String input) {
		String[] array = input.trim().toLowerCase().split(" ");
		String output = "";
		
		for (int i = 0; i < array.length; i++) {
			output += array[i].equals("ui") ? "UI" : (array[i].substring(0, 1).toUpperCase() + array[i].substring(1) + " ");
		}
		
		return output.trim();
	}
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader(txtFileName));) {
			final Modifier[] stealthModifiers = {
				Modifier.MISPLACED_GEAR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING,
				Modifier.WEAPON_SCANNERS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.NO_EQUIPMENT_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.NO_INTERROGATION, Modifier.EXTRA_CAMERAS,
				Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.TAKEDOWN_LIMIT, Modifier.NO_KNOCKOUTS, Modifier.NO_MOVING_BODIES, Modifier.NO_DISGUISE
			};
			final Modifier[] loudModifiers = {
				Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG,
				Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL,
				Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR,
				Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE,
				Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH,
				Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE
			};
			String[] stealthModifierStrings = new String[stealthModifiers.length];
			String[] loudModifierStrings = new String[loudModifiers.length];
			for (int i = 0; i < stealthModifiers.length; i++) {
				stealthModifierStrings[i] = stealthModifiers[i].toString();
			}
			for (int i = 0; i < loudModifiers.length; i++) {
				loudModifierStrings[i] = loudModifiers[i].toString();
			}
			Arrays.sort(stealthModifiers, STRING_ORDER);
			Arrays.sort(loudModifiers, STRING_ORDER);
			Arrays.sort(stealthModifierStrings);
			Arrays.sort(loudModifierStrings); //sorting is a prerequisite for binary search
			String line = readFile.readLine(); //first line of the txt file is not needed
			boolean isStealth = true;
			
			p("Credit to General_Gunner for the idea. This blog post will only count [[Daily Challenges|daily challenges]] starting in 2022 due to the removal of certain modifiers from certain missions around that time. Note that these percentages weren't extracted from the game; they were just taken from the list of daily challenges on the wiki.");
			//Discord username, not Github username
			p();
			p("A blank cell means 0% chance.");
			p("==Stealth==");
			p("{| class=\"wikitable sortable\"");
			p("!");
			p("!Blacksite");
			p("!Financier");
			p("!Deposit");
			p("!Lakehouse");
			p("!Withdrawal");
			p("!Scientist");
			p("!SCRS");
			p("!Killhouse");
			p("!Auction");
			p("!Gala");
			p("!Cache");
			p("!Setup");
			p("!Lockup");
			
			while (true) {
				line = readFile.readLine().trim();
				if (line.contains("was")) {break;} //the last part of the txt file is not needed
				if (line.isBlank()) {continue;} //skips blank lines
				
				if (isStealth) {
					if (line.equals("Loud:")) {
						isStealth = false;
						
						p("|}");
						p();
						p("==Loud==");
						p("{| class=\"wikitable sortable\"");
						p("!");
						p("!Blacksite");
						p("!Financier");
						p("!Deposit");
						p("!Lakehouse");
						p("!Withdrawal");
						p("!Scientist");
						p("!SCRS");
						p("!Black Dusk");
						p("!Killhouse");
						p("!Lockup");
						p("!Score");
					} else if (Arrays.binarySearch(stealthModifierStrings, line.substring(0, line.length() - 1)) >= 0) { //is a stealth modifier
						p("|-");
						System.out.print("|<span class=\"challenge-" + stealthModifiers[Arrays.binarySearch(stealthModifierStrings, line.substring(0, line.length() - 1))].getColor() + "\">");
						p(titleCase(stealthModifiers[Arrays.binarySearch(stealthModifierStrings, line.substring(0, line.length() - 1))].toString().replace('_', ' ')) + "</span>");
					} else if (line.contains(".")) { //mission: number
						if (line.substring(line.indexOf(':') + 2).equals("0.0")) {
							p("|");
						} else {
							p("|" + ((float)Math.round(Float.parseFloat(line.substring(line.indexOf(':') + 2)) * 1000) / 10) + "%");
						}
					} else {
						p(line);
						throw new RuntimeException();
					}
				} else //is loud
				if (Arrays.binarySearch(loudModifierStrings, line.substring(0, line.length() - 1)) >= 0) { //is a loud modifier
					p("|-");
					System.out.print("|<span class=\"challenge-" + loudModifiers[Arrays.binarySearch(loudModifierStrings, line.substring(0, line.length() - 1))].getColor() + "\">");
					p(titleCase(loudModifiers[Arrays.binarySearch(loudModifierStrings, line.substring(0, line.length() - 1))].toString().replace('_', ' ')) + "</span>");
				} else if (line.contains(".")) { //mission: number
					if (line.substring(line.indexOf(':') + 2).equals("0.0")) {
						p("|");
					} else {
						p("|" + ((float)Math.round(Float.parseFloat(line.substring(line.indexOf(':') + 2)) * 1000) / 10) + "%");
					}
				} else {
					p(line);
					throw new RuntimeException();
				}
			}
			
			p("|}");
			p("{{Tim123467Nav}}");
			p("[[Category:Blog posts]]");
		} catch (FileNotFoundException e) {
			p("The txt file does not exist or could not be found.");
			System.err.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			p("The txt file could not be read or closed.");
			System.err.println("IOException: " + e.getMessage());
		} catch (NumberFormatException e) {
			p("\nThe Float.parseFloat method returned an error. This was caused by an invalid probability value.");
			System.err.println("NumberFormatException: " + e.getMessage());
		} catch (RuntimeException e) {
			System.err.println("RuntimeException: " + e.getMessage());
		}
	}
}
