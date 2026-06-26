import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List; //this is needed due to having imported java.awt.List, which I do not use

public class DailyChallengesGUI implements Runnable, ActionListener, ChangeListener {
	private final static TimeZone TIMEZONE = TimeZone.getTimeZone("America/Toronto");
	private static JFrame frame;
	private static JSpinner dateSpinner;
	private static JComboBox<Mission> cbbMission;
	private static ButtonGroup bgTactic;
	private static JRadioButton rbStealth, rbLoud;
	private static JComboBox<Modifier> cbbMod1, cbbMod2, cbbMod3;
	private static JTextArea txtTemplate, txtDailyChallenges;
	private static boolean blockActions;

	/** A method specifically designed for this program.
	 * @return The input string but in title case (with a built-in exception for Hidden UI and SCRS). */
	private static String titleCase(String input) {
		String[] array = input.trim().toLowerCase().split(" ");
		StringBuilder output = new StringBuilder(22);

		for (String s : array) {
			output.append(s.equals("ui") ? "UI" : s.equals("scrs") ? "SCRS" :
					(s.substring(0, 1).toUpperCase() + s.substring(1) + " "));
		}

		return output.toString().trim();
	}

	/** Modifier color. */
	private enum Color {
		GREEN, BLUE, PURPLE, RED;

		/** @return The color in lowercase. */
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/** All modifiers that can be picked for a daily challenge, in all caps and with spaces replaced with underscores. */
	private enum Modifier {
		BLANK(null),
		BOARDED_UP(Color.GREEN), FOG(Color.GREEN), MISPLACED_GEAR(Color.GREEN), NO_AEGIS_ARMOR(Color.GREEN), NO_SAFECRACKING(Color.GREEN),
		NO_SUPPRESSORS(Color.GREEN),
		ARMERA_ARSENAL(Color.BLUE), BLOODLESS(Color.BLUE), CASCADE_ARSENAL(Color.BLUE), CRIMINAL_ARSENAL(Color.BLUE), EXTRA_CAMERAS(Color.BLUE),
		FLASHBANG_FRENZY(Color.BLUE), GLASS_CANNON(Color.BLUE), HEAVY_BAGS(Color.BLUE), MANDATORY_HEADSHOTS(Color.BLUE),
		NO_EQUIPMENT_BAGS(Color.BLUE), NO_HEAVY_ARMOR(Color.BLUE), NO_HYBRID_CLASSES(Color.BLUE), NO_INTERROGATION(Color.BLUE),
		NO_SCRAMBLER(Color.BLUE), REINFORCED_DOORS(Color.BLUE), REINFORCED_LOCKS(Color.BLUE), SMALL_ARMS_ONLY(Color.BLUE), UNSKILLED(Color.BLUE),
		WEAPON_SCANNERS(Color.BLUE),
		AEGIS_ACADEMY(Color.PURPLE), FASTER_DETECTION(Color.PURPLE), FIFTEEN_MINUTES(Color.PURPLE), FLASHBANG_REVENGE(Color.PURPLE),
		HIDDEN_DETECTION_BARS(Color.PURPLE), HIDDEN_UI(Color.PURPLE), INEXPERIENCED(Color.PURPLE), LESS_HEALTH(Color.PURPLE),
		NO_LOCKPICKS(Color.PURPLE), ONE_SHOT(Color.PURPLE), REINFORCED_CAMERAS(Color.PURPLE), UNINTIMIDATING(Color.PURPLE),
		WEAKER_MEDKITS(Color.PURPLE),
		EXPLOSIVE_FLASHBANGS(Color.RED), EXPLOSIVE_REVENGE(Color.RED), NO_DISGUISE(Color.RED), NO_EXPLOSIVES(Color.RED), NO_KNOCKOUTS(Color.RED),
		NO_MOVING_BODIES(Color.RED), SHIELD_SWARM(Color.RED), TAKEDOWN_LIMIT(Color.RED);

		private final Color color;

		//array of incompatible modifier combos; all combos listed below have not appeared together
		private final static Collection<Modifier>[] INCOMPATIBLE_MODS = new Collection[]{
			EnumSet.of(Modifier.ARMERA_ARSENAL, Modifier.CASCADE_ARSENAL, Modifier.CRIMINAL_ARSENAL, Modifier.SMALL_ARMS_ONLY),
			//Armera Arsenal would make Criminal Arsenal redundant, but both would override or be overridden by Cascade Arsenal
			//Small Arms Only is assumed to be incompatible with the arsenal modifiers
			EnumSet.of(Modifier.BLOODLESS, Modifier.NO_SUPPRESSORS),
			//Bloodless & No Suppressors are assumed to be incompatible
			EnumSet.of(Modifier.EXPLOSIVE_REVENGE, Modifier.FLASHBANG_REVENGE),
			//Explosive Revenge & Flashbang Revenge are assumed to be incompatible
			EnumSet.of(Modifier.GLASS_CANNON, Modifier.LESS_HEALTH),
			//Glass Cannon & Less Health are assumed to be incompatible
			EnumSet.of(Modifier.HIDDEN_DETECTION_BARS, Modifier.HIDDEN_UI),
			//Hidden UI would make Hidden Detection Bars redundant
			EnumSet.of(Modifier.NO_HYBRID_CLASSES, Modifier.INEXPERIENCED, Modifier.UNSKILLED),
			//No Hybrid Classes is assumed to be incompatible since it hasn't appeared with those two modifiers
			//Inexperienced & Unskilled would override each other
			EnumSet.of(Modifier.NO_AEGIS_ARMOR, Modifier.NO_HEAVY_ARMOR),
			//No Heavy Armor would make No Aegis Armor redundant
			EnumSet.of(Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING),
			//Unintimidating would make No Interrogation redundant
			EnumSet.of(Modifier.NO_KNOCKOUTS, Modifier.NO_SUPPRESSORS),
			//No Knockouts & No Suppressors are confirmed incompatible
			EnumSet.of(Modifier.NO_LOCKPICKS, Modifier.REINFORCED_LOCKS),
			//No Lockpicks would make Reinforced Locks redundant
			EnumSet.of(Modifier.REINFORCED_DOORS, Modifier.REINFORCED_LOCKS),
			//Reinforced Doors & Reinforced Locks would override each other
		};

		Modifier(Color color) {
			this.color = color;
		}

		Color getColor() {
			return color;
		}

		/** Displayed text in combo box.
		 * @return The name of this modifier in title case. */
		public String toString() {
			if (super.toString().equals("BLANK")) {
				return "";
			}
			return titleCase(super.toString().replace('_', ' '));
		}

		/**
		 * Two modifiers are compatible if a daily challenge can have both modifiers at the same time.
		 * Some assumptions are made about modifier incompatibility (listed in comments), so if they are wrong,
		 * then this method will need to be edited.
		 * @param anotherModifier The modifier to check if it is compatible with this modifier.
		 * @return True if both modifiers are compatible, false otherwise.
		 */
		boolean isCompatible(Object anotherModifier) {
			if (this.equals(anotherModifier) || !(anotherModifier instanceof Modifier)) {
				return false;
			}

			for (Collection<Modifier> combo : INCOMPATIBLE_MODS) {
				if (combo.contains(this) && combo.contains((Modifier)anotherModifier)) {
					return false;
				}
			}

			return true;
		}
	}

	/** The missions that can have daily challenges, in all caps. */
	private enum Mission {
		BLANK(null, null),
		THE_BLACKSITE(new Modifier[]{Modifier.BLANK, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED,
				Modifier.FIFTEEN_MINUTES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
						Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL,
						Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS,
						Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT,
						Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS,
						Modifier.EXPLOSIVE_REVENGE}),
		THE_FINANCIER(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.BLOODLESS,
				Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.NO_MOVING_BODIES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.UNSKILLED,
						Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL,
						Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS,
						Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR,
						Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT,
						Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS,
						Modifier.EXPLOSIVE_REVENGE}),
		THE_DEPOSIT(new Modifier[]{Modifier.BLANK, Modifier.MISPLACED_GEAR, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING,
				Modifier.WEAPON_SCANNERS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.NO_INTERROGATION,
				Modifier.EXTRA_CAMERAS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.NO_MOVING_BODIES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.SMALL_ARMS_ONLY,
						Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY,
						Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS,
						Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI,
						Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH,
						Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_LAKEHOUSE(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.TAKEDOWN_LIMIT,
				Modifier.NO_MOVING_BODIES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.UNSKILLED,
						Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL,
						Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS,
						Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI,
						Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
						Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_WITHDRAWAL(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS,
				Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.UNSKILLED,
						Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL,
						Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS,
						Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR,
						Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT,
						Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS,
						Modifier.EXPLOSIVE_REVENGE}),
		THE_SCIENTIST(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION,
				Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.NO_KNOCKOUTS},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY,
						Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY,
						Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
						Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
						Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
						Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_SCRS(new Modifier[]{Modifier.BLANK, Modifier.MISPLACED_GEAR, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING,
				Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS,
				Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.EXTRA_CAMERAS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI,
				Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS,
				Modifier.INEXPERIENCED, Modifier.TAKEDOWN_LIMIT},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY,
						Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY,
						Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS,
						Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI,
						Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH,
						Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		BLACK_DUSK(null,
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
						Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS,
						Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
						Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
						Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
						Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_KILLHOUSE(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES, Modifier.NO_MOVING_BODIES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.FOG, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
						Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS,
						Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI,
						Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH,
						Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_AUCTION(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS,
				Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES, Modifier.TAKEDOWN_LIMIT,
				Modifier.NO_MOVING_BODIES, Modifier.NO_DISGUISE},
				null),
		THE_GALA(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS,
				Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_MOVING_BODIES},
				null),
		THE_CACHE(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.NO_SCRAMBLER,
				Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS,
				Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES},
				null),
		THE_SETUP(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.NO_SCRAMBLER,
				Modifier.BLOODLESS, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION,
				Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.FIFTEEN_MINUTES,
				Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES, Modifier.NO_DISGUISE},
				null),
		THE_LOCKUP(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.BLOODLESS, Modifier.CASCADE_ARSENAL,
				Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI,
				Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS,
				Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES, Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES},
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
						Modifier.FLASHBANG_FRENZY, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.GLASS_CANNON,
						Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
						Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM,
						Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_SCORE(null,
				new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.CRIMINAL_ARSENAL, Modifier.FLASHBANG_FRENZY,
						Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS,
						Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT,
						Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE});

		private final Modifier[] stealthMods;
		private final Modifier[] loudMods;

		Mission(Modifier[] stealthMods, Modifier[] loudMods) {
			if (!Objects.isNull(stealthMods)) {
				Arrays.sort(stealthMods);
			}
			if (!Objects.isNull(loudMods)) {
				Arrays.sort(loudMods);
			}
			this.stealthMods = stealthMods;
			this.loudMods = loudMods;
		}

		Modifier[] getStealthMods() {
			return stealthMods;
		}

		Modifier[] getLoudMods() {
			return loudMods;
		}

		/** Displayed text in combo box.
		 * @return The name of this mission in title case. */
		public String toString() {
			if (super.toString().equals("BLANK")) {
				return "";
			}
			return titleCase(super.toString().replace('_', ' '));
		}
	}

	/** This subclass changes the functionality of the up and down buttons. */
	private static class CustomDateModel extends SpinnerDateModel {
		//removes the warning from using the default serialVersionUID
		private static final long serialVersionUID = Objects.hashCode(416);

		private Calendar cal;

		public CustomDateModel() {
			cal = new GregorianCalendar(TIMEZONE);
		}

		/** Up button. */
		public Object getNextValue() {
			cal.setTime(getDate());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		}

		/** Down button. */
		public Object getPreviousValue() {
			cal.setTime(getDate());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			return cal.getTime();
		}
	}

	private final static Mission[] FREE_MISSIONS = {Mission.BLANK, Mission.THE_BLACKSITE, Mission.THE_FINANCIER, Mission.THE_DEPOSIT,
			Mission.THE_LAKEHOUSE, Mission.THE_WITHDRAWAL, Mission.THE_SCIENTIST, Mission.THE_SCRS, Mission.BLACK_DUSK, Mission.THE_KILLHOUSE};
	private final static Mission[] NIGHT_HEISTS = {Mission.BLANK, Mission.THE_AUCTION, Mission.THE_GALA, Mission.THE_CACHE};
	private final static Mission[] FREELANCE_HEISTS = {Mission.BLANK, Mission.THE_SETUP, Mission.THE_LOCKUP, Mission.THE_SCORE};

	/**
	 * @return The array of Missions corresponding to dateSpinner's date.
	 */
	private static Mission[] availableMissions() {
		Calendar cal = new GregorianCalendar(TIMEZONE);
		long difference;
		byte remainder;

		cal.setTime((Date)dateSpinner.getValue());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 12, 0, 0); //sets time to noon
		cal.set(Calendar.MILLISECOND, 0);
		difference = cal.getTimeInMillis();

		cal.set(2021, Calendar.AUGUST, 10, 12, 0, 0); //noon of the first Freelance Heist daily challenge
		cal.set(Calendar.MILLISECOND, 0);
		difference -= cal.getTimeInMillis();

		difference = (long)(difference / (double)86400000 + 0.5); //convert to days
		remainder = (byte)(difference % 10);

		return switch (remainder) {
			case 0 -> FREELANCE_HEISTS;
			case 5 -> NIGHT_HEISTS;
			default -> FREE_MISSIONS;
		};
	}

	public void run() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("EP Daily Challenges Wikitext Generator");
		GridBagLayout layout = new GridBagLayout();
		JPanel contentPane = new JPanel(layout);
		GridBagConstraints remainder = new GridBagConstraints(),
				width2 = new GridBagConstraints();
		JLabel lblNote = new JLabel("Changing a component will reset every component below it."),
				datePrompt = new JLabel("Select the date (in year-month-day format):"), missionPrompt = new JLabel("Select the mission:"),
				tacticPrompt = new JLabel("Select the tactic:"), mod1Prompt = new JLabel("Select modifier 1:"),
				mod2Prompt = new JLabel("Select modifier 2:"), mod3Prompt = new JLabel("Select modifier 3:");
		JLabel[] lblBreaks = new JLabel[6];
		dateSpinner = new JSpinner(new CustomDateModel());
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
		JButton btnPrevDate = new JButton("Previous Date"),
				btnNextDate = new JButton("Next Date");
		cbbMission = new JComboBox<>(availableMissions());
		bgTactic = new ButtonGroup();
		rbStealth = new JRadioButton("Stealth");
		rbLoud = new JRadioButton("Loud");
		cbbMod1 = new JComboBox<>();
		cbbMod2 = new JComboBox<>();
		cbbMod3 = new JComboBox<>();
		JLabel lblTemplate = new JLabel("Text to copy and paste into Template:DailyChallenge's source code:"),
				lblDailyChallenges = new JLabel("Text to copy and paste into Daily Challenges's source code, before the \"|}\" at the end:");
		txtTemplate = new JTextArea();
		txtDailyChallenges = new JTextArea();
		blockActions = false;

		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		remainder.gridwidth = GridBagConstraints.REMAINDER;
		width2.gridwidth = 2;

		//sets timezone and overrides default date formatting
		dateEditor.getFormat().setTimeZone(TIMEZONE);
		dateSpinner.setEditor(dateEditor);

		for (int i = 0; i < 6; i++) {
			lblBreaks[i] = new JLabel();
		}

		contentPane.add(lblNote, remainder);

		contentPane.add(datePrompt);
		dateSpinner.addChangeListener(this);
		contentPane.add(dateSpinner, width2);
		btnPrevDate.setActionCommand("prevDate");
		btnPrevDate.addActionListener(this);
		contentPane.add(btnPrevDate);
		btnNextDate.setActionCommand("nextDate");
		btnNextDate.addActionListener(this);
		contentPane.add(btnNextDate);
		contentPane.add(lblBreaks[0], remainder);

		contentPane.add(missionPrompt);
		cbbMission.setActionCommand("mission");
		cbbMission.addActionListener(this);
		contentPane.add(cbbMission, width2);
		contentPane.add(lblBreaks[1], remainder);

		contentPane.add(tacticPrompt);

		rbStealth.setActionCommand("tactic");
		rbStealth.addActionListener(this);
		rbStealth.setEnabled(false);
		contentPane.add(rbStealth);

		rbLoud.setActionCommand("tactic");
		rbLoud.addActionListener(this);
		rbLoud.setEnabled(false);
		contentPane.add(rbLoud);
		contentPane.add(lblBreaks[2], remainder);

		contentPane.add(mod1Prompt);
		cbbMod1.setActionCommand("mod1");
		cbbMod1.addActionListener(this);
		cbbMod1.setEnabled(false);
		contentPane.add(cbbMod1, width2);
		contentPane.add(lblBreaks[3], remainder);

		contentPane.add(mod2Prompt);
		cbbMod2.setActionCommand("mod2");
		cbbMod2.addActionListener(this);
		cbbMod2.setEnabled(false);
		contentPane.add(cbbMod2, width2);
		contentPane.add(lblBreaks[4], remainder);

		contentPane.add(mod3Prompt);
		cbbMod3.setActionCommand("generate");
		cbbMod3.addActionListener(this);
		cbbMod3.setEnabled(false);
		contentPane.add(cbbMod3, width2);
		contentPane.add(lblBreaks[5], remainder);

		contentPane.add(lblTemplate, remainder);
		txtTemplate.setEditable(false);
		contentPane.add(txtTemplate, remainder);

		contentPane.add(lblDailyChallenges, remainder);
		txtDailyChallenges.setEditable(false);
		contentPane.add(txtDailyChallenges, remainder);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(true);
	}

	/** The method that runs when dateSpinner's value changes. */
	public void stateChanged(ChangeEvent event) {
		blockActions = true; //prevents actionPerformed from being called during this method's execution

		//disable all components below mission
		for (AbstractButton b : Collections.list(bgTactic.getElements())) { //this part is needed to update radio buttons during method execution
			bgTactic.remove(b);
		}

		rbStealth.setSelected(false);
		rbStealth.setEnabled(false);

		rbLoud.setSelected(false);
		rbLoud.setEnabled(false);

		cbbMod1.removeAllItems();
		cbbMod1.setEnabled(false);

		cbbMod2.removeAllItems();
		cbbMod2.setEnabled(false);

		cbbMod3.removeAllItems();
		cbbMod3.setEnabled(false);

		txtTemplate.setText(null);
		txtDailyChallenges.setText(null);

		//set list of available missions
		cbbMission.removeAllItems();
		for (Mission item : availableMissions()) {
			cbbMission.addItem(item);
		}

		blockActions = false; //allows actionPerformed to be called again
	}

	/**
	 * @param modNumber The modifier number (1, 2, or 3).
	 * @return A string representing the takedown limit amount to include in Daily Challenges's source code, if the modifier is Takedown Limit.
	 * An empty string otherwise.
	 */
	private static String takedownLimitNumber(int modNumber) {
		switch (modNumber) {
			case 1:
				if (!cbbMod1.getSelectedItem().equals(Modifier.TAKEDOWN_LIMIT)) {
					return "";
				}
				break;
			case 2:
				if (!cbbMod2.getSelectedItem().equals(Modifier.TAKEDOWN_LIMIT)) {
					return "";
				}
				break;
			case 3:
				if (!cbbMod3.getSelectedItem().equals(Modifier.TAKEDOWN_LIMIT)) {
					return "";
				}
				break;
			default: throw new IllegalArgumentException();
		}
		if (cbbMission.getSelectedItem().equals(Mission.THE_SCRS)) {
			return " (6)";
		}
		return " (4)";
	}

	/** The method that runs when any other component's value changes (or in the case of a radio button, when it's clicked).
	 * dateSpinner triggers the stateChanged method above. */
	public void actionPerformed(ActionEvent event) {
		Modifier[] allModOptions;

		if (blockActions) {
			return;
		}
		blockActions = true; //prevents unwanted recursion

		outer: switch (event.getActionCommand()) {
			case "prevDate":
				dateSpinner.setValue(dateSpinner.getPreviousValue());
				break;
			case "nextDate":
				dateSpinner.setValue(dateSpinner.getNextValue());
				break;
			case "mission": //mission selected
				//this part is needed to update radio buttons during method execution
				for (AbstractButton b : Collections.list(bgTactic.getElements())) {
					bgTactic.remove(b);
				}

				switch (cbbMission.getSelectedItem()) {
					case Mission.BLANK:
						//disable all components below mission
						rbStealth.setSelected(false);
						rbStealth.setEnabled(false);

						rbLoud.setSelected(false);
						rbLoud.setEnabled(false);

						cbbMod1.removeAllItems();
						cbbMod1.setEnabled(false);

						cbbMod2.removeAllItems();
						cbbMod2.setEnabled(false);

						cbbMod3.removeAllItems();
						cbbMod3.setEnabled(false);

						txtTemplate.setText(null);
						txtDailyChallenges.setText(null);

						break outer;
					case Mission.THE_AUCTION, Mission.THE_GALA, Mission.THE_CACHE, Mission.THE_SETUP: //stealth only
						//set & disable tactic
						rbStealth.setSelected(true);
						rbStealth.setEnabled(false);

						rbLoud.setSelected(false);
						rbLoud.setEnabled(false);
						break; //falls through to case "tactic"
					case Mission.BLACK_DUSK, Mission.THE_SCORE: //loud only
						//set & disable tactic
						rbStealth.setSelected(false);
						rbStealth.setEnabled(false);

						rbLoud.setSelected(true);
						rbLoud.setEnabled(false);
						break; //falls through to case "tactic"
					default:
						//disable all components below tactic
						cbbMod1.removeAllItems();
						cbbMod1.setEnabled(false);

						cbbMod2.removeAllItems();
						cbbMod2.setEnabled(false);

						cbbMod3.removeAllItems();
						cbbMod3.setEnabled(false);

						txtTemplate.setText(null);
						txtDailyChallenges.setText(null);

						//enable tactic
						rbStealth.setSelected(false);
						rbStealth.setEnabled(true);

						rbLoud.setSelected(false);
						rbLoud.setEnabled(true);

						//ensures both buttons cannot be pressed simultaneously
						bgTactic.add(rbStealth);
						bgTactic.add(rbLoud);

						break outer;
				}
			case "tactic": //radio button clicked
				//disable all components below mod1
				cbbMod2.removeAllItems();
				cbbMod2.setEnabled(false);

				cbbMod3.removeAllItems();
				cbbMod3.setEnabled(false);

				txtTemplate.setText(null);
				txtDailyChallenges.setText(null);

				List<Modifier> mod1Options = new ArrayList<>(30);

				//fill & enable mod1
				cbbMod1.removeAllItems();

				if (rbStealth.isSelected()) {
					allModOptions = ((Mission)cbbMission.getSelectedItem()).getStealthMods();
				} else { //loud selected
					allModOptions = ((Mission)cbbMission.getSelectedItem()).getLoudMods();
				}
				mod1Options.addAll(Arrays.asList(allModOptions));
				mod1Options.remove(0); //temporarily remove blank

				//skip mod1s that aren't compatible with any mod3 or any mod2
				verify: for (int i = 0; i < mod1Options.size(); i++) {
					for (Modifier mod2 : allModOptions) {
						if (!mod2.equals(Modifier.BLANK) &&
								mod2.getColor().compareTo(mod1Options.get(i).getColor()) >= 0 && mod2.isCompatible(mod1Options.get(i))) {
							for (Modifier mod3 : allModOptions) {
								if (!mod3.equals(Modifier.BLANK) &&
										mod3.getColor().compareTo(mod2.getColor()) >= 0 && mod3.isCompatible(mod1Options.get(i)) &&
										mod3.isCompatible(mod2)) {
									continue verify;
								}
							}
						}
					}
					mod1Options.remove(mod1Options.get(i));
					i--;
				}

				mod1Options.add(0, Modifier.BLANK);
				for (Modifier item : mod1Options) {
					cbbMod1.addItem(item);
				}
				cbbMod1.setEnabled(true);

				break;
			case "mod1": //1st modifier selected
				/* logic for adding items to cbbMod2 & cbbMod3: add blank, skip mods with easier difficulty colors, skip already-selected mods, and
				 * skip mod2s that aren't compatible with any mod3 */

				//disable all components below mod2
				cbbMod3.removeAllItems();
				cbbMod3.setEnabled(false);

				txtTemplate.setText(null);
				txtDailyChallenges.setText(null);

				if (cbbMod1.getSelectedItem().equals(Modifier.BLANK)) {
					//disable mod2
					cbbMod2.removeAllItems();
					cbbMod2.setEnabled(false);

					break;
				}

				List<Modifier> mod2Options = new ArrayList<>(30);

				cbbMod2.removeAllItems();

				if (rbStealth.isSelected()) {
					allModOptions = ((Mission)cbbMission.getSelectedItem()).getStealthMods();
				} else { //loud selected
					allModOptions = ((Mission)cbbMission.getSelectedItem()).getLoudMods();
				}

				for (Modifier item : allModOptions) {
					if (!item.equals(Modifier.BLANK) &&
							(item.getColor().compareTo(((Modifier)cbbMod1.getSelectedItem()).getColor()) >= 0 && item.isCompatible(cbbMod1.getSelectedItem()))) {
						mod2Options.add(item);
					}
				}

				//skip mod2s that aren't compatible with any mod3
				verify: for (int i = 0; i < mod2Options.size(); i++) {
					for (Modifier mod3 : allModOptions) {
						if (!mod3.equals(Modifier.BLANK) &&
								mod3.getColor().compareTo(mod2Options.get(i).getColor()) >= 0 && mod3.isCompatible(cbbMod1.getSelectedItem())
								&& mod3.isCompatible(mod2Options.get(i))) {
							continue verify;
						}
					}
					mod2Options.remove(mod2Options.get(i));
					i--;
				}
				mod2Options.add(0, Modifier.BLANK);
				for (Modifier item : mod2Options) {
					cbbMod2.addItem(item);
				}
				cbbMod2.setEnabled(true);

				break;
			case "mod2": //2nd modifier selected
				//disable all components below mod3
				txtTemplate.setText(null);
				txtDailyChallenges.setText(null);

				if (cbbMod2.getSelectedItem().equals(Modifier.BLANK)) {
					//disable mod3
					cbbMod3.removeAllItems();
					cbbMod3.setEnabled(false);

					break;
				}

				cbbMod3.removeAllItems();

				if (rbStealth.isSelected()) {
					allModOptions = ((Mission)cbbMission.getSelectedItem()).getStealthMods();
				} else { //loud selected
					allModOptions = ((Mission)cbbMission.getSelectedItem()).getLoudMods();
				}

				for (Modifier item : allModOptions) {
					if (item.equals(Modifier.BLANK) ||
							(item.getColor().compareTo(((Modifier)cbbMod2.getSelectedItem()).getColor()) >= 0 && item.isCompatible(cbbMod1.getSelectedItem())
								&& item.isCompatible(cbbMod2.getSelectedItem()))) {
						cbbMod3.addItem(item);
					}
				}

				cbbMod3.setEnabled(true);

				if (cbbMod3.getItemCount() > 2) {
					break;
				}
				//else only blank and the single modifier
				cbbMod3.removeItem(Modifier.BLANK);
				//falls through to case "generate"
			case "generate": //3rd modifier selected, which should only be possible when all other components are selected
				if (cbbMod3.getSelectedItem().equals(Modifier.BLANK)) {
					//disable all components below mod3
					txtTemplate.setText(null);
					txtDailyChallenges.setText(null);

					break;
				}
				Calendar cal = new GregorianCalendar(TIMEZONE);
				cal.setTime((Date)dateSpinner.getValue());
				String month = cal.get(Calendar.MONTH) + 1 < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1) + "",
						day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + "";
				//The +1 is needed because java.util.Calendar months are 0-based

				txtTemplate.setText("<noinclude>\n"
						+ "If you are updating the daily challenge, please change the following lines:\n"
						+ "* The mission and tactic\n"
						+ "** Example: The Deposit (Stealth)\n"
						+ "** For expansion missions, include <nowiki>{{Robux}}</nowiki> before the title (without the stuff in the triangle brackets).\n"
						+ "*** Example: {{Robux}} The Score (Loud)\n"
						+ "* The 3 modifier names and their colors\n"
						+ "* The 3 modifier descriptions (you only have to change the modifier name in the '''ModifierDescription''' template)\n"
						+ "You can use the \"Preview\" button at the bottom of the editor to make sure everything has been changed correctly.\n"
						+ "</noinclude>\n"
						+ "<div style=\"text-align:center;\">The [[daily challenge]] changes in:<br /><span class=\"daily-countdown\" style=\"font-size:35px;\"></span></div>\n"
						+ "{| class=\"article-table\" style=\"margin-left:auto;margin-right:auto;\"\n"
						+ "<!-- Change the mission here -->\n"
						+ "! colspan=\"3\" style=\"text-align:center;\"|" + (Arrays.asList(FREE_MISSIONS).contains(cbbMission.getSelectedItem()) ? "" : "{{Robux}} ") + cbbMission.getSelectedItem() + " (" + (rbStealth.isSelected() ? "Stealth" : "Loud") + ")\n"
						+ "|-\n"
						+ "<!-- Change the modifier names here (and the colors to blue/green/purple/red) -->\n"
						+ "! style=\"text-align:center;\"|<span class=\"challenge-" + ((Modifier)cbbMod1.getSelectedItem()).getColor() + "\">" + cbbMod1.getSelectedItem() + "</span>\n"
						+ "! style=\"text-align:center;\"|<span class=\"challenge-" + ((Modifier)cbbMod2.getSelectedItem()).getColor() + "\">" + cbbMod2.getSelectedItem() + "</span>\n"
						+ "! style=\"text-align:center;\"|<span class=\"challenge-" + ((Modifier)cbbMod3.getSelectedItem()).getColor() + "\">" + cbbMod3.getSelectedItem() + "</span>\n"
						+ "|-\n"
						+ "<!-- Change the modifier names in the templates here (insert the name without spaces, for Takedown Limit there is \"TakedownLimit\" for 4 and \"TakedownLimit6\" for 6 depending on the mission) -->\n"
						+ "| style=\"width: 33%;\" |{{ModifierDescription|" + (cbbMod1.getSelectedItem().equals(Modifier.TAKEDOWN_LIMIT) && cbbMission.getSelectedItem().equals(Mission.THE_SCRS) ? "TakedownLimit6" : cbbMod1.getSelectedItem().toString().replace(" ", "")) + "}}\n"
						+ "| style=\"width: 33%;\" |{{ModifierDescription|" + (cbbMod2.getSelectedItem().equals(Modifier.TAKEDOWN_LIMIT) && cbbMission.getSelectedItem().equals(Mission.THE_SCRS) ? "TakedownLimit6" : cbbMod2.getSelectedItem().toString().replace(" ", "")) + "}}\n"
						+ "| style=\"width: 33%;\" |{{ModifierDescription|" + (cbbMod3.getSelectedItem().equals(Modifier.TAKEDOWN_LIMIT) && cbbMission.getSelectedItem().equals(Mission.THE_SCRS) ? "TakedownLimit6" : cbbMod3.getSelectedItem().toString().replace(" ", "")) + "}}\n"
						+ "|}");
				txtDailyChallenges.setText("|-\n"
						+ "!" + cal.get(Calendar.YEAR) + "-" + month + "-" + day + "\n"
						+ "|" + (Arrays.asList(FREE_MISSIONS).contains(cbbMission.getSelectedItem()) ? "" : "{{Robux}} ") + cbbMission.getSelectedItem() + "\n"
						+ "|" + (rbStealth.isSelected() ? "Stealth\n" : "Loud\n")
						+ "|<span class=challenge-" + ((Modifier)cbbMod1.getSelectedItem()).getColor() + ">" + cbbMod1.getSelectedItem() + takedownLimitNumber(1) + "</span>, "
						+ "<span class=challenge-" + ((Modifier)cbbMod2.getSelectedItem()).getColor() + ">" + cbbMod2.getSelectedItem() + takedownLimitNumber(2) + "</span>, "
						+ "<span class=challenge-" + ((Modifier)cbbMod3.getSelectedItem()).getColor() + ">" + cbbMod3.getSelectedItem() + takedownLimitNumber(3) + "</span>");

				frame.pack(); //resizes the window
		}

		blockActions = false; //allows actionPerformed to be called again
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new DailyChallengesGUI());
	}
}
