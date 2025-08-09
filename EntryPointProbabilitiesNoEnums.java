/* Program that gives the probability of each modifier appearing on a certain mission.
 * This implementation avoids enums and uses HashMaps instead of EnumMaps.
 * The output is produced in the console.
 */

import java.io.*;
import java.util.*;

public class EntryPointProbabilitiesNoEnums {
	final static String[] STEALTH_MISSIONS = {
			"blacksite", "financier", "deposit", "lakehouse", "withdrawal", "scientist", "scrs", "killhouse",
			"auction", "gala", "cache", "setup", "lockup"
	};
	final static String[] STEALTH_MISSIONS_NO_EXPANSIONS = {
		"blacksite", "financier", "deposit", "lakehouse", "withdrawal", "scientist", "scrs", "killhouse"
	};
	final static String[] LOUD_MISSIONS = {
		"blacksite", "financier", "deposit", "lakehouse", "withdrawal", "scientist", "scrs", "black dusk",
		"killhouse", "lockup", "score"
	};
	final static String[] LOUD_MISSIONS_NO_EXPANSIONS = {
		"blacksite", "financier", "deposit", "lakehouse", "withdrawal", "scientist", "scrs", "black dusk", "killhouse"
	};
	final static String[] STEALTH_MODIFIERS = {
		"misplaced gear", "boarded up", "fog", "no suppressors", "no safecracking",
		"weapon scanners", "no scrambler", "bloodless", "unskilled", "no hybrid classes",
		"cascade arsenal", "reinforced doors", "heavy bags", "no equipment bags",
		"reinforced locks", "no interrogation", "extra cameras",
		"unintimidating", "hidden ui", "faster detection", "reinforced cameras",
		"hidden detection bars", "no lockpicks", "inexperienced", "fifteen minutes",
		"takedown limit", "no knockouts", "no moving bodies", "no disguise"
	};
	final static String[] LOUD_MODIFIERS = {
		"no aegis armor", "boarded up", "fog",
		"unskilled", "small arms only", "criminal arsenal", "no hybrid classes", "cascade arsenal",
		"flashbang frenzy", "reinforced doors", "armera arsenal", "no equipment bags",
		"reinforced locks", "glass cannon", "mandatory headshots", "no heavy armor",
		"weaker medkits", "hidden ui", "aegis academy", "flashbang revenge",
		"one shot", "inexperienced", "less health",
		"no explosives", "shield swarm", "explosive flashbangs", "explosive revenge"
	};
	
	/** @param s An instance of the Short class.
	 * @return An instance of the Short class with its value 1 above the value of s. */
	private static Short increment(Short s) {
		return Short.valueOf((short)(s.shortValue() + 1));
	}
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader("EP Daily Challenges Wikitext.txt"));) {
			//The Short values will represent the number of occurrences
			Map<String, Map<String, Short>> stealth2021 = new HashMap<>((int)(STEALTH_MISSIONS.length / 0.75 + 1));
			Map<String, Map<String, Short>> loud2021 = new HashMap<>((int)(LOUD_MISSIONS.length / 0.75 + 1));
			Map<String, Map<String, Short>> stealth2022 = new HashMap<>((int)(STEALTH_MISSIONS.length / 0.75 + 1));
			Map<String, Map<String, Short>> loud2022 = new HashMap<>((int)(LOUD_MISSIONS.length / 0.75 + 1));
			Map<String, Short> stealth2021missions = new HashMap<>((int)(STEALTH_MISSIONS.length / 0.75 + 1));
			Map<String, Short> loud2021missions = new HashMap<>((int)(LOUD_MISSIONS.length / 0.75 + 1));
			Map<String, Short> stealth2022missions = new HashMap<>((int)(STEALTH_MISSIONS.length / 0.75 + 1));
			Map<String, Short> loud2022missions = new HashMap<>((int)(LOUD_MISSIONS.length / 0.75 + 1));
			
			for (String m : STEALTH_MISSIONS) {
				stealth2021missions.put(m, Short.valueOf((short)0));
				stealth2022missions.put(m, Short.valueOf((short)0));
				stealth2021.put(m, new HashMap<>((int)(STEALTH_MODIFIERS.length / 0.75 + 1)));
				stealth2022.put(m, new HashMap<>((int)(STEALTH_MODIFIERS.length / 0.75 + 1)));
				for (String mo : STEALTH_MODIFIERS) {
					stealth2021.get(m).put(mo, Short.valueOf((short)0));
					stealth2022.get(m).put(mo, Short.valueOf((short)0));
				}
			}
			for (String m : LOUD_MISSIONS) {
				loud2021missions.put(m, Short.valueOf((short)0));
				loud2022missions.put(m, Short.valueOf((short)0));
				loud2021.put(m, new HashMap<>((int)(LOUD_MODIFIERS.length / 0.75 + 1)));
				loud2022.put(m, new HashMap<>((int)(LOUD_MODIFIERS.length / 0.75 + 1)));
				for (String mo : LOUD_MODIFIERS) {
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
					strings[2] = readFile.readLine().toLowerCase();
					
					strings[0] = strings[0].contains("The") ? strings[0].substring(strings[0].indexOf('e') + 1).trim().toLowerCase() : "black dusk";
					strings[4] = strings[2].substring(strings[2].lastIndexOf('>', strings[2].length() - 2) + 1, strings[2].lastIndexOf('<')).trim();
					strings[2] = strings[2].substring(0, strings[2].lastIndexOf(',', strings[2].length() - 2));
					strings[3] = strings[2].substring(strings[2].lastIndexOf('>', strings[2].length() - 2) + 1, strings[2].lastIndexOf('<')).trim();
					strings[2] = strings[2].substring(strings[2].indexOf('>') + 1, strings[2].indexOf('<', 2)).trim();
					
					strings[2] = strings[2].endsWith(")") ? strings[2].substring(0, strings[2].indexOf('(')).trim() : strings[2];
					strings[3] = strings[3].endsWith(")") ? strings[3].substring(0, strings[3].indexOf('(')).trim() : strings[3];
					strings[4] = strings[4].endsWith(")") ? strings[4].substring(0, strings[4].indexOf('(')).trim() : strings[4];
					
					//increments the values in the HashMaps
					if (is2021) {
						if (strings[1].contains("stealth") && !strings[0].equals("black dusk")) {
							stealth2021missions.put(strings[0], increment(stealth2021missions.get(strings[0])));
							stealth2021.get(strings[0]).put(strings[2], increment(stealth2021.get(strings[0]).get(strings[2])));
							stealth2021.get(strings[0]).put(strings[3], increment(stealth2021.get(strings[0]).get(strings[3])));
							stealth2021.get(strings[0]).put(strings[4], increment(stealth2021.get(strings[0]).get(strings[4])));
						} else if (strings[1].contains("loud")) {
							loud2021missions.put(strings[0], increment(loud2021missions.get(strings[0])));
							loud2021.get(strings[0]).put(strings[2], increment(loud2021.get(strings[0]).get(strings[2])));
							loud2021.get(strings[0]).put(strings[3], increment(loud2021.get(strings[0]).get(strings[3])));
							loud2021.get(strings[0]).put(strings[4], increment(loud2021.get(strings[0]).get(strings[4])));
						} else if (!(strings[0].equals("black dusk") && strings[1].contains("stealth"))) {
							System.out.println("The part in the txt file corresponding with a tactic in the 2021 daily challenges was invalid. The daily challenge entry with the invalid tactic is listed below:");
							for (String s : strings) {
								System.out.println(s);
							}
							throw new RuntimeException();
						}
					} else { //not 2021
						if (strings[1].contains("stealth")) {
							stealth2022missions.put(strings[0], increment(stealth2022missions.get(strings[0])));
							stealth2022.get(strings[0]).put(strings[2], increment(stealth2022.get(strings[0]).get(strings[2])));
							stealth2022.get(strings[0]).put(strings[3], increment(stealth2022.get(strings[0]).get(strings[3])));
							stealth2022.get(strings[0]).put(strings[4], increment(stealth2022.get(strings[0]).get(strings[4])));
						} else if (strings[1].contains("loud")) {
							loud2022missions.put(strings[0], increment(loud2022missions.get(strings[0])));
							loud2022.get(strings[0]).put(strings[2], increment(loud2022.get(strings[0]).get(strings[2])));
							loud2022.get(strings[0]).put(strings[3], increment(loud2022.get(strings[0]).get(strings[3])));
							loud2022.get(strings[0]).put(strings[4], increment(loud2022.get(strings[0]).get(strings[4])));
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
			for (String m : STEALTH_MODIFIERS) {
				System.out.println(m + ":");
				for (String mi : STEALTH_MISSIONS) {
					System.out.println(mi + ": " + ((double)stealth2022.get(mi).get(m).shortValue() / stealth2022missions.get(mi).shortValue()));
				}
				System.out.println();
			}
			System.out.println("Loud:");
			for (String m : LOUD_MODIFIERS) {
				System.out.println(m + ":");
				for (String mi : LOUD_MISSIONS) {
					System.out.println(mi + ": " + ((double)loud2022.get(mi).get(m).shortValue() / loud2022missions.get(mi).shortValue()));
				}
				System.out.println();
			}
			
			//displays which modifiers were removed from certain missions from 2021 to 2022
			for (String m : STEALTH_MODIFIERS) {
				if (m.equals("boarded up") || m.equals("no equipment bags")) {continue;} //these were removed from the stealth pool
				for (String mi : STEALTH_MISSIONS_NO_EXPANSIONS) {
					if (stealth2021.get(mi).get(m).shortValue() != 0 &&
						stealth2022.get(mi).get(m).shortValue() == 0) {
						System.out.println(m + " was removed from " + mi);
					}
				}
			}
			for (String m : LOUD_MODIFIERS) {
				for (String mi : LOUD_MISSIONS_NO_EXPANSIONS) {
					if (loud2021.get(mi).get(m).shortValue() != 0 &&
						loud2022.get(mi).get(m).shortValue() == 0) {
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
