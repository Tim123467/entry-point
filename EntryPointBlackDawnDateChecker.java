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
		boolean readPage;
		
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
				readPage = false;
				
				while (true) {
					line = readWeb.readLine();
					
					if (line == null) {
						if (!readPage) {
							System.out.println("User " + userID + " either does not exist or does not have the Black Dawn badge.");
						}
						break;
					}
					
					readPage = true;
					
					System.out.println("User ID: " + userID);
					System.out.println("Date: " + line.substring(37, 47));
					System.out.println("Time: " + line.substring(48, line.length() - 3) + " UTC");
				}
			} catch (Exception e) {
				System.out.println("Either this user does not exist, or there was a problem running the program.");
				continue;
			}
		}
		input.close();
	}
}
