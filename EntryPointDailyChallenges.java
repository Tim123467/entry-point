/* Program used to translate Breakbar's txt file into wikitext. Note that I renamed the txt file to "EP Daily Challenges.txt"
 * The only edits that should be made are to the values of the String variables near the top: TXT_FILE_NAME, CURRENT_YEAR_AND_MONTH, and YEAR
 * (unless you want to try to improve the code).
 * The output is produced in the console.
 */

import java.io.*;

public class EntryPointDailyChallenges {
	final static String TXT_FILE_NAME = "EP Daily Challenges.txt"; //change depending on the name you set for Breakbar's txt file
	final static String CURRENT_YEAR_AND_MONTH = "2024-10"; //must be in the format "YYYY-MM"
	final static String YEAR = "2024"; //change manually for the YEAR you want the wikitext for (due to the console's limited number of lines)
	                                   //currently, the txt file covers the end of 2022 to most of 2025
	
	public static void main(String[] args) {
		try (BufferedReader readFile = new BufferedReader(new FileReader(TXT_FILE_NAME));) {
			String line, date, mission, tactic, color1, mod1, color2, mod2, color3, mod3; //using an array instead would make my code take up less lines,
			                                                                              //but it would also make my code harder to understand
			while (true) {
				line = readFile.readLine();
				if (line == null) {break;} //stops the loop when there's no more lines to read
				date = line.substring(0, line.indexOf(','));
				
				if (date.substring(0, 4).equals(YEAR)) { //checks if the YEAR matches the specified YEAR
					line = line.substring(line.indexOf(',')+1);
					mission = line.substring(0, line.indexOf(','));
					line = line.substring(line.indexOf(',')+1);
					tactic = line.substring(0, line.indexOf(','));
					line = line.substring(line.indexOf(',')+1);
					color1 = line.substring(0, line.indexOf(','));
					line = line.substring(line.indexOf(',')+1);
					mod1 = line.substring(0, line.indexOf(','));
					mod1 = mod1.equals("Takedown Limit") ? "Takedown Limit (4)" : (mod1.equals("Takedown Limit 6") ? "Takedown Limit (6)" : mod1);
					line = line.substring(line.indexOf(',')+1);
					color2 = line.substring(0, line.indexOf(','));
					line = line.substring(line.indexOf(',')+1);
					mod2 = line.substring(0, line.indexOf(','));
					mod2 = mod2.equals("Takedown Limit") ? "Takedown Limit (4)" : (mod2.equals("Takedown Limit 6") ? "Takedown Limit (6)" : mod2);
					line = line.substring(line.indexOf(',')+1);
					color3 = line.substring(0, line.indexOf(','));
					mod3 = line.substring(line.indexOf(',')+1);
					mod3 = mod3.equals("Takedown Limit") ? "Takedown Limit (4)" : (mod3.equals("Takedown Limit 6") ? "Takedown Limit (6)" : mod3);
					
					if (date.substring(8, 10).equals("01")) { //first day of the month
						if (date.substring(5, 7).equals("01")) { //first month of the YEAR
							System.out.print("'''Daily Challenges''' from " + YEAR + " will be displayed here.\n\n===January");
						} else {
							switch (date.substring(5, 7)) {
							case "02": System.out.print("|}\n===February"); break;
							case "03": System.out.print("|}\n===March"); break;
							case "04": System.out.print("|}\n===April"); break;
							case "05": System.out.print("|}\n===May"); break;
							case "06": System.out.print("|}\n===June"); break;
							case "07": System.out.print("|}\n===July"); break;
							case "08": System.out.print("|}\n===August"); break;
							case "09": System.out.print("|}\n===September"); break;
							case "10": System.out.print("|}\n===October"); break;
							case "11": System.out.print("|}\n===November"); break;
							case "12": System.out.print("|}\n===December"); break;
							default:
								System.out.println("\nThe part in the txt file corresponding with the month was invalid.");
								throw new RuntimeException();
							}
						}
						
						System.out.print(" " + YEAR + "===\n{| class=\"fandom-table article-table mw-collapsible mw-collapsed\" ");
						
						if (date.substring(0, 7).equals(CURRENT_YEAR_AND_MONTH)) { //current YEAR and current month
							System.out.println("data-expandtext=\"Show Previous and Upcoming Challenges\" data-collapsetext=\"Hide Previous and Upcoming Challenges\"");
						} else if (Short.parseShort(YEAR) < Short.parseShort(CURRENT_YEAR_AND_MONTH.substring(0, 4)) ||
								  (Short.parseShort(YEAR) == Short.parseShort(CURRENT_YEAR_AND_MONTH.substring(0, 4)) && Short.parseShort(date.substring(5, 7)) < Short.parseShort(CURRENT_YEAR_AND_MONTH.substring(5, 7)))) {
							//earlier YEAR OR same YEAR but earlier month
							System.out.println("data-expandtext=\"Show Previous Challenges\" data-collapsetext=\"Hide Previous Challenges\"");
						} else { //current YEAR and later month OR later YEAR
							System.out.println("data-expandtext=\"Show Upcoming Challenges\" data-collapsetext=\"Hide Upcoming Challenges\"");
						}
						
						System.out.println("!Date\n!Mission\n!Tactic\n!Modifiers");
					}
					
					System.out.print("|-\n!" + date + "\n|");
					
					if (mission.equals("The Scientist") && mod3.equals("No Knockouts")) { //invalid modifier combinations
						if (mod1.equals("No Suppressors") && mod2.equals("Hidden UI")) {
							System.out.println("The Scientist\n|Stealth\n|<span class=challenge-green>Fog</span>, <span class=challenge-green>No Suppressors</span>, <span class=challenge-purple>Hidden UI</span>");
							continue;
						} else if (mod1.equals("No Suppressors") || mod2.equals("No Suppressors")) {
							System.out.print("The Scientist\n|Stealth\n|<span class=challenge-green>" + mod1 + "</span>, <span class=challenge-");
							
							switch (color2) {
							case "G": System.out.print("green"); break;
							case "B": System.out.print("blue"); break;
							case "P": System.out.print("purple"); break;
							default:
								System.out.println("\nThe part in the txt file corresponding with the second modifier's color was invalid.");
								throw new RuntimeException();
							}
							System.out.println(">" + mod2 + "</span>, <span class=challenge-purple>Hidden UI</span>");
							continue;
						}
					}
					
					switch (mission) {
					case "The Auction": case "The Gala": case "The Cache": case "The Setup": case "The Lockup": case "The Score":
						System.out.print("{{Robux}} ");
					}
					
					System.out.print(mission + "\n|" + tactic + "\n|<span class=challenge-");
					
					switch (color1) {
					case "G": System.out.print("green"); break;
					case "B": System.out.print("blue"); break;
					case "P": System.out.print("purple"); break;
					case "R": System.out.print("red"); break;
					default:
						System.out.println("\nThe part in the txt file corresponding with the first modifier's color was invalid.");
						throw new RuntimeException();
					}
					System.out.print(">" + mod1 + "</span>, <span class=challenge-");
					
					switch (color2) {
					case "G": System.out.print("green"); break;
					case "B": System.out.print("blue"); break;
					case "P": System.out.print("purple"); break;
					case "R": System.out.print("red"); break;
					default:
						System.out.println("\nThe part in the txt file corresponding with the second modifier's color was invalid.");
						throw new RuntimeException();
					}
					System.out.print(">" + mod2 + "</span>, <span class=challenge-");
					
					switch (color3) {
					case "G": System.out.print("green"); break;
					case "B": System.out.print("blue"); break;
					case "P": System.out.print("purple"); break;
					case "R": System.out.print("red"); break;
					default:
						System.out.println("\nThe part in the txt file corresponding with the third modifier's color was invalid.");
						throw new RuntimeException();
					}
					System.out.println(">" + mod3 + "</span>");
				}
			}
			
			System.out.println("|}");
		} catch (FileNotFoundException e) {
			System.out.println("\nThe txt file does not exist or could not be found.");
			System.err.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("\nThe txt file could not be read or closed.");
			System.err.println("IOException: " + e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			System.out.println("\nA substring method returned an error: beginIndex was negative, or endIndex was greater than the length of the String, or beginIndex was greater than endIndex. "
						     + "This was caused by either an invalid line in the txt file or an invalid value for the CURRENT_YEAR_AND_MONTH variable.");
			System.err.println("IndexOutOfBoundsException: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("\nThe Short.parseShort method returned an error. "
					         + "This was caused by either an invalid date in the txt file or invalid values for the CURRENT_YEAR_AND_MONTH variable or for the YEAR variable.");
			System.err.println("NumberFormatException: " + e.getMessage());
		} catch (RuntimeException e) {
			System.err.println("RuntimeException: " + e.getMessage());
		}
	}
}
