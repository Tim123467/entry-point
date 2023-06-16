/* Program that gives the probability of each modifier appearing on a certain mission.
 * The output is produced in the console.
 */

import java.io.*;
import java.util.*;

public class EntryPointProbabilities {
	/** The missions that can have daily challenges, in all caps and without "The" at the beginning.
	 * I could've split this enum into one for stealth missions and one for loud missions, but that would require more typing. */
	public enum Mission {
		BLACKSITE, FINANCIER, DEPOSIT, LAKEHOUSE, WITHDRAWAL, SCIENTIST, SCRS, BLACK_DUSK,
		KILLHOUSE, AUCTION, GALA, CACHE, SETUP, LOCKUP, SCORE
	}
	
	/** All modifiers that can be picked for a daily challenge, in all caps and with spaces replaced with underscores.
	 * I could've split this enum into one for stealth modifiers and one for loud modifiers, but that would require more typing.*/
	public enum Modifier {
		MISPLACED_GEAR, NO_AEGIS_ARMOR, BOARDED_UP, FOG, NO_SUPPRESSORS, NO_SAFECRACKING,
		WEAPON_SCANNERS, NO_SCRAMBLER, BLOODLESS, UNSKILLED, SMALL_ARMS_ONLY, CRIMINAL_ARSENAL, NO_HYBRID_CLASSES,
		CASCADE_ARSENAL, FLASHBANG_FRENZY, REINFORCED_DOORS, HEAVY_BAGS, ARMERA_ARSENAL, NO_EQUIPMENT_BAGS,
		REINFORCED_LOCKS, GLASS_CANNON, MANDATORY_HEADSHOTS, NO_INTERROGATION, EXTRA_CAMERAS, NO_HEAVY_ARMOR,
		UNINTIMIDATING, WEAKER_MEDKITS, HIDDEN_UI, FASTER_DETECTION, REINFORCED_CAMERAS, AEGIS_ACADEMY,
		FLASHBANG_REVENGE, ONE_SHOT, HIDDEN_DETECTION_BARS, NO_LOCKPICKS, INEXPERIENCED, FIFTEEN_MINUTES, LESS_HEALTH,
		NO_EXPLOSIVES, TAKEDOWN_LIMIT, SHIELD_SWARM, EXPLOSIVE_FLASHBANGS, NO_KNOCKOUTS, NO_MOVING_BODIES, NO_DISGUISE, EXPLOSIVE_REVENGE
	}
	
	/** @return the Class object representing Mission. */
	@SuppressWarnings("unchecked")
	public static Class<Mission> getMissionClass() {
		return (Class<Mission>)Mission.SCRS.getClass(); //could be any mission here
	}
	
	/** @return the Class object representing Modifier. */
	@SuppressWarnings("unchecked")
	public static Class<Modifier> getModifierClass() {
		return (Class<Modifier>)Modifier.FOG.getClass(); //could be any modifier here
	}
	
	/** @param s an instance of the Short class.
	 * @return an instance of the Short class with its value 1 above the value of s. */
	public static Short increment(Short s) {
		return Short.valueOf((short)(s.shortValue() + 1));
	}
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader(new File("EP Daily Challenges Wikitext.txt")));) {
			//The Short values will represent the number of occurrences
			Map<Mission, Map<Modifier, Short>> stealth2021 = new EnumMap<>(getMissionClass());
			Map<Mission, Map<Modifier, Short>> loud2021 = new EnumMap<>(getMissionClass());
			Map<Mission, Map<Modifier, Short>> stealth2022 = new EnumMap<>(getMissionClass());
			Map<Mission, Map<Modifier, Short>> loud2022 = new EnumMap<>(getMissionClass());
			Map<Mission, Short> stealth2021missions = new EnumMap<>(getMissionClass());
			Map<Mission, Short> loud2021missions = new EnumMap<>(getMissionClass());
			Map<Mission, Short> stealth2022missions = new EnumMap<>(getMissionClass());
			Map<Mission, Short> loud2022missions = new EnumMap<>(getMissionClass());
			final Mission[] stealthMissions = {
				Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
				Mission.SCRS, Mission.KILLHOUSE, Mission.AUCTION, Mission.GALA, Mission.CACHE, Mission.SETUP, Mission.LOCKUP
			};
			final Mission[] stealthMissionsNoExpansions = {
					Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
					Mission.SCRS, Mission.KILLHOUSE
				};
			final Mission[] loudMissions = {
				Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
				Mission.SCRS, Mission.BLACK_DUSK, Mission.KILLHOUSE, Mission.LOCKUP, Mission.SCORE
			};
			final Mission[] loudMissionsNoExpansions = {
					Mission.BLACKSITE, Mission.FINANCIER, Mission.DEPOSIT, Mission.LAKEHOUSE, Mission.WITHDRAWAL, Mission.SCIENTIST,
					Mission.SCRS, Mission.BLACK_DUSK, Mission.KILLHOUSE
				};
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
			for (Mission m : stealthMissions) {
				stealth2021missions.put(m, Short.valueOf((short)0));
				stealth2022missions.put(m, Short.valueOf((short)0));
				stealth2021.put(m, new EnumMap<>(getModifierClass()));
				stealth2022.put(m, new EnumMap<>(getModifierClass()));
				for (Modifier mo : stealthModifiers) {
					stealth2021.get(m).put(mo, Short.valueOf((short)0));
					stealth2022.get(m).put(mo, Short.valueOf((short)0));
				}
			}
			for (Mission m : loudMissions) {
				loud2021missions.put(m, Short.valueOf((short)0));
				loud2022missions.put(m, Short.valueOf((short)0));
				loud2021.put(m, new EnumMap<>(getModifierClass()));
				loud2022.put(m, new EnumMap<>(getModifierClass()));
				for (Modifier mo : loudModifiers) {
					loud2021.get(m).put(mo, Short.valueOf((short)0));
					loud2022.get(m).put(mo, Short.valueOf((short)0));
				}
			}
			
			String[] strings = new String[5]; //represents mission, tactic, and each modifier respectively
			boolean is2021 = true;
			
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
					if (is2021) {
						if (strings[1].contains("stealth") && !strings[0].equals("BLACK_DUSK")) {
							stealth2021missions.put(Mission.valueOf(strings[0]), increment(stealth2021missions.get(Mission.valueOf(strings[0]))));
							stealth2021.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[2]), increment(stealth2021.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[2]))));
							stealth2021.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[3]), increment(stealth2021.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[3]))));
							stealth2021.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[4]), increment(stealth2021.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[4]))));
						} else if (strings[1].contains("loud")) {
							loud2021missions.put(Mission.valueOf(strings[0]), increment(loud2021missions.get(Mission.valueOf(strings[0]))));
							loud2021.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[2]), increment(loud2021.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[2]))));
							loud2021.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[3]), increment(loud2021.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[3]))));
							loud2021.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[4]), increment(loud2021.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[4]))));
						} else if (!(strings[0].equals("BLACK_DUSK") && strings[1].contains("stealth"))) {
							System.out.println("The part in the txt file corresponding with a tactic in the 2021 daily challenges was invalid. The daily challenge entry with the invalid tactic is listed below:");
							for (String s : strings) {
								System.out.println(s);
							}
							throw new RuntimeException();
						}
					} else { //not 2021
						if (strings[1].contains("stealth")) {
							stealth2022missions.put(Mission.valueOf(strings[0]), increment(stealth2022missions.get(Mission.valueOf(strings[0]))));
							stealth2022.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[2]), increment(stealth2022.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[2]))));
							stealth2022.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[3]), increment(stealth2022.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[3]))));
							stealth2022.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[4]), increment(stealth2022.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[4]))));
						} else if (strings[1].contains("loud")) {
							loud2022missions.put(Mission.valueOf(strings[0]), increment(loud2022missions.get(Mission.valueOf(strings[0]))));
							loud2022.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[2]), increment(loud2022.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[2]))));
							loud2022.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[3]), increment(loud2022.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[3]))));
							loud2022.get(Mission.valueOf(strings[0])).put(Modifier.valueOf(strings[4]), increment(loud2022.get(Mission.valueOf(strings[0])).get(Modifier.valueOf(strings[4]))));
						} else {
							System.out.println("The part in the txt file corresponding with a tactic in the 2022 or later daily challenges was invalid. The daily challenge entry with the invalid tactic is listed below:");
							for (String s : strings) {
								System.out.println(s);
							}
							throw new RuntimeException();
						}
					}
				} else if (strings[0].equals("===January 2022===")) {
					is2021 = false;
				}
			}
			
			//displays how often modifiers appear as decimals
			System.out.println("Stealth:");
			for (Modifier m : stealthModifiers) {
				System.out.println(m + ":");
				for (Mission mi : stealthMissions) {
					System.out.println(mi + ": " + ((double)stealth2022.get(mi).get(m).shortValue() / stealth2022missions.get(mi).shortValue()));
				}
				System.out.println();
			}
			System.out.println("Loud:");
			for (Modifier m : loudModifiers) {
				System.out.println(m + ":");
				for (Mission mi : loudMissions) {
					System.out.println(mi + ": " + ((double)loud2022.get(mi).get(m).shortValue() / loud2022missions.get(mi).shortValue()));
				}
				System.out.println();
			}
			
			//displays which modifiers were removed from certain missions from 2021 to 2022
			for (Modifier m : stealthModifiers) {
				if (m.equals(Modifier.BOARDED_UP) || m.equals(Modifier.NO_EQUIPMENT_BAGS)) {continue;} //these were removed from the stealth pool
				for (Mission mi : stealthMissionsNoExpansions) {
					if ((double)stealth2021.get(mi).get(m).shortValue() != 0 &&
						(double)stealth2022.get(mi).get(m).shortValue() == 0) {
						System.out.println(m + " was removed from " + mi);
					}
				}
			}
			for (Modifier m : loudModifiers) {
				for (Mission mi : loudMissionsNoExpansions) {
					if ((double)loud2021.get(mi).get(m).shortValue() != 0 &&
						(double)loud2022.get(mi).get(m).shortValue() == 0) {
						System.out.println(m + " was removed from " + mi);
					}
				}
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
