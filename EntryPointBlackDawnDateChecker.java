/**
 * Program used to check what date a specified user has earned the Black Dawn badge.
 */
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class EntryPointBlackDawnDateChecker {	
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String line;
		long userID;
		short lineNum;
		
		outer: while (true) {
			do {
				System.out.print("Enter a valid Roblox user ID (or enter 0 to quit): ");
				try {
					userID = Long.parseLong(input.nextLine().trim());
					
					if (userID == 0) {break outer;}
				} catch (NumberFormatException e) {
					userID = -1;
					continue;
				}
			} while (userID < 0);
			
			try (BufferedReader readWeb = new BufferedReader(new InputStreamReader(new URI("https://badges.roblox.com/v1/users/" + userID + "/badges/2124422246/awarded-date").toURL().openStream()));) {
				lineNum = 0;
				
				while (true) {
					line = readWeb.readLine();
					
					if (line == null) {
						if (lineNum == 0) {
							System.out.println("This user either does not exist or does not have the Black Dawn badge.");
						}
						break;
					}
					
					lineNum++;
					
					System.out.println("Date: " + line.substring(37, 47));
					System.out.println("Time: " + line.substring(48, line.length() - 3));
				}
			} catch (IOException e) {
				System.out.println("This user does not exist.");
				continue;
			} catch (Exception e) {
				continue;
			}
		}
		input.close();
	}
}
