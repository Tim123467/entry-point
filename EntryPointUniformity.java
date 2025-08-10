/**
 * Program that gives the uniformity of each mission.
 * The output is produced in the console.
 */
import java.io.*;
import java.util.*;

public class EntryPointUniformity {
	/** The missions that can have daily challenges, in all caps and without "The" at the beginning.
	 * I could've split this enum into one for stealth missions and one for loud missions, but that would require more typing. */
	private enum Mission {
		BLACKSITE, FINANCIER, DEPOSIT, LAKEHOUSE, WITHDRAWAL, SCIENTIST, SCRS, BLACK_DUSK,
		KILLHOUSE, AUCTION, GALA, CACHE, SETUP, LOCKUP, SCORE
	}
	
	/** All modifiers that can be picked for a daily challenge, in all caps and with spaces replaced with underscores.
	 * I could've split this enum into one for stealth modifiers and one for loud modifiers, but that would require more typing.*/
	private enum Modifier {
		MISPLACED_GEAR, NO_AEGIS_ARMOR, BOARDED_UP, FOG, NO_SUPPRESSORS, NO_SAFECRACKING,
		WEAPON_SCANNERS, NO_SCRAMBLER, BLOODLESS, UNSKILLED, SMALL_ARMS_ONLY, CRIMINAL_ARSENAL, NO_HYBRID_CLASSES,
		CASCADE_ARSENAL, FLASHBANG_FRENZY, REINFORCED_DOORS, HEAVY_BAGS, ARMERA_ARSENAL, NO_EQUIPMENT_BAGS,
		REINFORCED_LOCKS, GLASS_CANNON, MANDATORY_HEADSHOTS, NO_INTERROGATION, EXTRA_CAMERAS, NO_HEAVY_ARMOR,
		UNINTIMIDATING, WEAKER_MEDKITS, HIDDEN_UI, FASTER_DETECTION, REINFORCED_CAMERAS, AEGIS_ACADEMY,
		FLASHBANG_REVENGE, ONE_SHOT, HIDDEN_DETECTION_BARS, NO_LOCKPICKS, INEXPERIENCED, FIFTEEN_MINUTES, LESS_HEALTH,
		NO_EXPLOSIVES, TAKEDOWN_LIMIT, SHIELD_SWARM, EXPLOSIVE_FLASHBANGS, NO_KNOCKOUTS, NO_MOVING_BODIES, NO_DISGUISE, EXPLOSIVE_REVENGE
	}
	
	final static Mission[] STEALTH_MISSIONS = {
		Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
		Mission.SCRS, Mission.KILLHOUSE, Mission.AUCTION, Mission.GALA, Mission.CACHE, Mission.SETUP, Mission.LOCKUP
	};
	final static Mission[] LOUD_MISSIONS = {
		Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
		Mission.SCRS, Mission.BLACK_DUSK, Mission.KILLHOUSE, Mission.LOCKUP, Mission.SCORE
	};
	final static Modifier[] STEALTH_MODIFIERS = {
		Modifier.MISPLACED_GEAR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING,
		Modifier.WEAPON_SCANNERS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
		Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.NO_EQUIPMENT_BAGS,
		Modifier.REINFORCED_LOCKS, Modifier.NO_INTERROGATION, Modifier.EXTRA_CAMERAS,
		Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
		Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
		Modifier.TAKEDOWN_LIMIT, Modifier.NO_KNOCKOUTS, Modifier.NO_MOVING_BODIES, Modifier.NO_DISGUISE
	};
	final static Modifier[] LOUD_MODIFIERS = {
		Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG,
		Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL,
		Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS,
		Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR,
		Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE,
		Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH,
		Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE
	};
	
	/** @return The Class object representing Mission. */
	@SuppressWarnings("unchecked")
	private static Class<Mission> getMissionClass() {
		return (Class<Mission>)Mission.SCRS.getClass(); //could be any mission here
	}
	
	/** @return The Class object representing Modifier. */
	@SuppressWarnings("unchecked")
	private static Class<Modifier> getModifierClass() {
		return (Class<Modifier>)Modifier.FOG.getClass(); //could be any modifier here
	}
	
	/** @param s An instance of the Short class.
	 * @return An instance of the Short class with its value 1 above the value of s. */
	private static Short increment(Short s) {
		return Short.valueOf((short)(s.shortValue() + 1));
	}
	
	/**
	 * @return The input string but in title case.
	 */
	private static String titleCase(String input) {
		String[] array = input.trim().toLowerCase().split(" ");
		String output = "";
		
		for (int i = 0; i < array.length; i++) {
			output += array[i].substring(0, 1).toUpperCase() + array[i].substring(1) + " ";
		}
		
		return output.trim();
	}
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader("EP Daily Challenges Wikitext.txt"));) {
			//Part 1: processing txt file
			Map<Mission, Map<Modifier, Short>> stealthOccurrences = new EnumMap<>(getMissionClass());
			Map<Mission, Map<Modifier, Short>> loudOccurrences = new EnumMap<>(getMissionClass());
			Map<Mission, Short> stealthMissionOccurrences = new EnumMap<>(getMissionClass());
			Map<Mission, Short> loudMissionOccurrences = new EnumMap<>(getMissionClass());
			for (Mission mi : STEALTH_MISSIONS) {
				stealthMissionOccurrences.put(mi, Short.valueOf((short)0));
				stealthOccurrences.put(mi, new EnumMap<>(getModifierClass()));
				for (Modifier mo : STEALTH_MODIFIERS) {
					stealthOccurrences.get(mi).put(mo, Short.valueOf((short)0));
				}
			}
			for (Mission mi : LOUD_MISSIONS) {
				loudMissionOccurrences.put(mi, Short.valueOf((short)0));
				loudOccurrences.put(mi, new EnumMap<>(getModifierClass()));
				for (Modifier mo : LOUD_MODIFIERS) {
					loudOccurrences.get(mi).put(mo, Short.valueOf((short)0));
				}
			}
			
			String[] strings = new String[5]; //represents mission, tactic, and each modifier respectively
			boolean is2022 = false;
			
			//processes the txt file
			while (true) {
				strings[0] = readFile.readLine();
				if (strings[0] == null) {break;} //stops the loop when there's no more lines to read
				
				if (strings[0].startsWith("|The") || strings[0].startsWith("|Black") || strings[0].startsWith("|{{Robux")) {
					strings[1] = readFile.readLine().toLowerCase();
					strings[2] = readFile.readLine().toUpperCase();
					
					strings[0] = strings[0].contains("The") ? strings[0].substring(strings[0].indexOf('e') + 1).trim().toUpperCase() : "BLACK_DUSK";
					strings[4] = strings[2].substring(strings[2].lastIndexOf('>', strings[2].length() - 2) + 1, strings[2].lastIndexOf('<')).trim();
					strings[2] = strings[2].substring(0, strings[2].lastIndexOf(',', strings[2].length() - 2));
					strings[3] = strings[2].substring(strings[2].lastIndexOf('>', strings[2].length() - 2) + 1, strings[2].lastIndexOf('<')).trim();
					strings[2] = strings[2].substring(strings[2].indexOf('>') + 1, strings[2].indexOf('<', 2)).trim();
					
					strings[2] = strings[2].endsWith(")") ? strings[2].substring(0, strings[2].indexOf('(')).trim() : strings[2];
					strings[3] = strings[3].endsWith(")") ? strings[3].substring(0, strings[3].indexOf('(')).trim() : strings[3];
					strings[4] = strings[4].endsWith(")") ? strings[4].substring(0, strings[4].indexOf('(')).trim() : strings[4];
					strings[2] = strings[2].replace(' ', '_');
					strings[3] = strings[3].replace(' ', '_');
					strings[4] = strings[4].replace(' ', '_');
					
					//increments the values in the maps
					if (is2022) {
						if (strings[1].contains("stealth")) {
							stealthMissionOccurrences.put(Mission.valueOf(strings[0]), increment(stealthMissionOccurrences.get(Mission.valueOf(strings[0]))));
							stealthOccurrences.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[2]), increment(stealthOccurrences.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[2]))));
							stealthOccurrences.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[3]), increment(stealthOccurrences.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[3]))));
							stealthOccurrences.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[4]), increment(stealthOccurrences.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[4]))));
						} else if (strings[1].contains("loud")) {
							loudMissionOccurrences.put(Mission.valueOf(strings[0]), increment(loudMissionOccurrences.get(Mission.valueOf(strings[0]))));
							loudOccurrences.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[2]), increment(loudOccurrences.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[2]))));
							loudOccurrences.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[3]), increment(loudOccurrences.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[3]))));
							loudOccurrences.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[4]), increment(loudOccurrences.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[4]))));
						} else {
							System.out.println("The part in the txt file corresponding with a tactic in the 2022 or later daily challenges was invalid. The daily challenge entry with the invalid tactic is listed below:");
							for (String s : strings) {
								System.out.println(s);
							}
							throw new RuntimeException();
						}
					}
				} else if (strings[0].equals("===January 2022===")) {
					is2022 = true;
				}
			}
			
			//Part 2: uniformity calculations
			double stealthFreq, loudFreq, gini, entropy, error;
			String mission;
			
			for (Mission mi : STEALTH_MISSIONS) {
				gini = 0;
				entropy = 0;
				error = 0;
				for (Modifier mo : STEALTH_MODIFIERS) {
					stealthFreq = (double)stealthOccurrences.get(mi).get(mo).shortValue() / stealthMissionOccurrences.get(mi).shortValue() / 3;
					//division by 3 at the end is for normalization
					gini += stealthFreq * stealthFreq;
					entropy += stealthFreq == 0 ? 0 : (stealthFreq * Math.log(stealthFreq) / Math.log(2));
					error = Math.max(stealthFreq, error);
				}
				gini = 1 - gini;
				entropy *= -1;
				error = 1 - error;
				
				mission = titleCase(mi.toString().replace('_', ' '));
				switch (mission) {
				case "Scrs": mission = "The SCRS";
				case "Black Dusk": break;
				default: mission = "The " + mission;
				}
				System.out.println(mission + " stealth has gini = " + gini + ", entropy = " + entropy + ", and error = " + error + ".");
			}
			System.out.println();
			for (Mission mi : LOUD_MISSIONS) {
				gini = 0;
				entropy = 0;
				error = 0;
				for (Modifier mo : LOUD_MODIFIERS) {
					loudFreq = (double)loudOccurrences.get(mi).get(mo).shortValue() / loudMissionOccurrences.get(mi).shortValue() / 3;
					gini += loudFreq * loudFreq;
					entropy += loudFreq == 0 ? 0 : (loudFreq * Math.log(loudFreq) / Math.log(2));
					error = Math.max(loudFreq, error);
				}
				gini = 1 - gini;
				entropy *= -1;
				error = 1 - error;
				
				mission = titleCase(mi.toString().replace('_', ' '));
				switch (mission) {
				case "Scrs": mission = "The SCRS";
				case "Black Dusk": break;
				default: mission = "The " + mission;
				}
				System.out.println(mission + " loud has gini = " + gini + ", entropy = " + entropy + ", and error = " + error + ".");
			}
		} catch (FileNotFoundException e) {
			System.out.println("The txt file does not exist or could not be found.");
			System.err.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("The txt file could not be read or closed.");
			System.err.println("IOException: " + e.getMessage());
		} catch (NullPointerException e) {
			System.out.println("The part in the txt file corresponding with a mission or modifier was invalid.");
			System.err.println("NullPointerException: " + e.getMessage());
		} catch (RuntimeException e) {
			System.err.println("RuntimeException: " + e.getMessage());
		}
	}
}
