import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DailyChallengesGUI implements Runnable, ActionListener, ChangeListener {
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
		String output = "";
		
		for (int i = 0; i < array.length; i++) {
			output += array[i].equals("ui") ? "UI" : array[i].equals("scrs") ? "SCRS" : (array[i].substring(0, 1).toUpperCase() + array[i].substring(1) + " ");
		}
		
		return output.trim();
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
		BLANK(null), MISPLACED_GEAR(Color.GREEN), NO_AEGIS_ARMOR(Color.GREEN), BOARDED_UP(Color.GREEN),
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
		
		private Modifier(Color color) {
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
	}
	
	/** The missions that can have daily challenges, in all caps. */
	private enum Mission {
		BLANK(null, null),
		THE_BLACKSITE(new Modifier[]{Modifier.BLANK, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.HIDDEN_UI, Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
				Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL,
				Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS,
				Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT,
				Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_FINANCIER(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.HIDDEN_DETECTION_BARS,
				Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES, Modifier.NO_MOVING_BODIES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY,
				Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY,
				Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM,
				Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_DEPOSIT(new Modifier[]{Modifier.BLANK, Modifier.MISPLACED_GEAR, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING,
				Modifier.WEAPON_SCANNERS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.NO_INTERROGATION,
				Modifier.EXTRA_CAMERAS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES, Modifier.NO_MOVING_BODIES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS,
				Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
				Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_LAKEHOUSE(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING,
				Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS,
				Modifier.INEXPERIENCED, Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY,
				Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY,
				Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.FLASHBANG_REVENGE,
				Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM,
				Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_WITHDRAWAL(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS,
				Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES, Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.BOARDED_UP, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY,
				Modifier.CRIMINAL_ARSENAL, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY,
				Modifier.REINFORCED_DOORS, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
				Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_SCIENTIST(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION,
				Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.NO_KNOCKOUTS},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.ARMERA_ARSENAL,
				Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS,
				Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE,
				Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM,
				Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_SCRS(new Modifier[]{Modifier.BLANK, Modifier.MISPLACED_GEAR, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.BLOODLESS,
				Modifier.UNSKILLED, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.EXTRA_CAMERAS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED, Modifier.TAKEDOWN_LIMIT},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.FOG, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS,
				Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
				Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		BLACK_DUSK(null,
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.REINFORCED_DOORS,
				Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES,
				Modifier.SHIELD_SWARM, Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_KILLHOUSE(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.BLOODLESS, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES,
				Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.NO_INTERROGATION, Modifier.UNINTIMIDATING,
				Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES,
				Modifier.NO_MOVING_BODIES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.FOG, Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.FLASHBANG_FRENZY,
				Modifier.REINFORCED_DOORS, Modifier.NO_EQUIPMENT_BAGS, Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.INEXPERIENCED, Modifier.LESS_HEALTH, Modifier.SHIELD_SWARM,
				Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_AUCTION(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.NO_HYBRID_CLASSES,
				Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI,
				Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED,
				Modifier.FIFTEEN_MINUTES, Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES, Modifier.NO_DISGUISE},
			null),
		THE_GALA(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS, Modifier.UNSKILLED,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS,
				Modifier.NO_MOVING_BODIES},
			null),
		THE_CACHE(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS,
				Modifier.UNSKILLED, Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS,
				Modifier.HIDDEN_DETECTION_BARS, Modifier.INEXPERIENCED, Modifier.FIFTEEN_MINUTES},
			null),
		THE_SETUP(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.NO_SAFECRACKING, Modifier.NO_SCRAMBLER, Modifier.BLOODLESS,
				Modifier.NO_HYBRID_CLASSES, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS, Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS,
				Modifier.UNINTIMIDATING, Modifier.FASTER_DETECTION, Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS,
				Modifier.NO_LOCKPICKS, Modifier.FIFTEEN_MINUTES, Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES, Modifier.NO_DISGUISE},
			null),
		THE_LOCKUP(new Modifier[]{Modifier.BLANK, Modifier.FOG, Modifier.NO_SUPPRESSORS, Modifier.BLOODLESS, Modifier.CASCADE_ARSENAL, Modifier.REINFORCED_DOORS,
				Modifier.HEAVY_BAGS, Modifier.REINFORCED_LOCKS, Modifier.UNINTIMIDATING, Modifier.HIDDEN_UI, Modifier.FASTER_DETECTION,
				Modifier.REINFORCED_CAMERAS, Modifier.HIDDEN_DETECTION_BARS, Modifier.NO_LOCKPICKS, Modifier.INEXPERIENCED,
				Modifier.FIFTEEN_MINUTES, Modifier.TAKEDOWN_LIMIT, Modifier.NO_MOVING_BODIES},
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.UNSKILLED, Modifier.SMALL_ARMS_ONLY, Modifier.CRIMINAL_ARSENAL,
				Modifier.FLASHBANG_FRENZY, Modifier.ARMERA_ARSENAL, Modifier.NO_EQUIPMENT_BAGS, Modifier.GLASS_CANNON,
				Modifier.MANDATORY_HEADSHOTS, Modifier.NO_HEAVY_ARMOR, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI, Modifier.AEGIS_ACADEMY,
				Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.LESS_HEALTH, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM,
				Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE}),
		THE_SCORE(null,
			new Modifier[]{Modifier.BLANK, Modifier.NO_AEGIS_ARMOR, Modifier.CRIMINAL_ARSENAL, Modifier.FLASHBANG_FRENZY, Modifier.NO_EQUIPMENT_BAGS,
				Modifier.REINFORCED_LOCKS, Modifier.GLASS_CANNON, Modifier.MANDATORY_HEADSHOTS, Modifier.WEAKER_MEDKITS, Modifier.HIDDEN_UI,
				Modifier.AEGIS_ACADEMY, Modifier.FLASHBANG_REVENGE, Modifier.ONE_SHOT, Modifier.NO_EXPLOSIVES, Modifier.SHIELD_SWARM,
				Modifier.EXPLOSIVE_FLASHBANGS, Modifier.EXPLOSIVE_REVENGE});
		
		private final Modifier[] stealthMods;
		private final Modifier[] loudMods;
		
		private Mission(Modifier[] stealthMods, Modifier[] loudMods) {
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
	private class CustomDateModel extends SpinnerDateModel {
		private static final long serialVersionUID = Objects.hashCode(Integer.valueOf(416)); //removes the warning from using the default serialVersionUID
		private Calendar cal;
		
		public CustomDateModel() {
			cal = new GregorianCalendar(TimeZone.getTimeZone("America/Toronto"));
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
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Toronto"));
		long difference;
		byte remainder;
		
		cal.setTime((Date)dateSpinner.getValue());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 12, 0, 0); //sets time to noon
		cal.set(Calendar.MILLISECOND, 0);
		difference = cal.getTimeInMillis();
		
		cal.set(2021, 8, 10, 12, 0, 0); //noon of the first Freelance Heist daily challenge
		cal.set(Calendar.MILLISECOND, 0);
		difference -= cal.getTimeInMillis();
		
		difference = (long)(difference / (double)86400000 + 0.5); //convert to days
		difference += 1; //it's one off for some reason
		remainder = (byte)(difference % 10);
		
		switch (remainder) {
		case 0: return FREELANCE_HEISTS;
		case 5: return NIGHT_HEISTS;
		default: return FREE_MISSIONS;
		}
	}
	
	public void run() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("EP Daily Challenges Wikitext Generator");
		GridBagLayout layout = new GridBagLayout();
		JPanel contentPane = new JPanel(layout);
		GridBagConstraints remainder = new GridBagConstraints();
		JLabel lblNote = new JLabel("Changing a component will reset every component below it."),
			datePrompt = new JLabel("Select the date (in day/month/year format):"), missionPrompt = new JLabel("Select the mission:"),
			tacticPrompt = new JLabel("Select the tactic:"), mod1Prompt = new JLabel("Select modifier 1:"),
			mod2Prompt = new JLabel("Select modifier 2:"), mod3Prompt = new JLabel("Select modifier 3:");
		dateSpinner = new JSpinner(new CustomDateModel());
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
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
		
		//sets timezone and overrides default date formatting
		dateEditor.getFormat().setTimeZone(TimeZone.getTimeZone("America/Toronto"));
		dateSpinner.setEditor(dateEditor);
		
		contentPane.add(lblNote, remainder);
		
		contentPane.add(datePrompt);
		dateSpinner.addChangeListener(this);
		contentPane.add(dateSpinner, remainder);
		
		contentPane.add(missionPrompt);
		cbbMission.setActionCommand("mission");
		cbbMission.addActionListener(this);
		contentPane.add(cbbMission, remainder);
		
		contentPane.add(tacticPrompt);
		
		rbStealth.setActionCommand("tactic");
		rbStealth.addActionListener(this);
		rbStealth.setEnabled(false);
		contentPane.add(rbStealth);
		
		rbLoud.setActionCommand("tactic");
		rbLoud.addActionListener(this);
		rbLoud.setEnabled(false);
		contentPane.add(rbLoud, remainder);
		
		contentPane.add(mod1Prompt);
		cbbMod1.setActionCommand("mod1");
		cbbMod1.addActionListener(this);
		cbbMod1.setEnabled(false);
		contentPane.add(cbbMod1, remainder);
		
		contentPane.add(mod2Prompt);
		cbbMod2.setActionCommand("mod2");
		cbbMod2.addActionListener(this);
		cbbMod2.setEnabled(false);
		contentPane.add(cbbMod2, remainder);
		
		contentPane.add(mod3Prompt);
		cbbMod3.setActionCommand("generate");
		cbbMod3.addActionListener(this);
		cbbMod3.setEnabled(false);
		contentPane.add(cbbMod3, remainder);
		
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
		
		//set list of available missions
		cbbMission.removeAllItems();
		for (Mission item : availableMissions()) {
			cbbMission.addItem(item);
		}
		
		//disable all components below
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
		
		blockActions = false; //allows actionPerformed to be called again
	}
	
	/** The method that runs when any other component's value changes (or in the case of a radio button, when it's clicked).
	 * dateSpinner triggers the stateChanged method above. */
	public void actionPerformed(ActionEvent event) {
		if (blockActions) {
			return;
		}
		blockActions = true; //prevents unwanted recursion
		try {
			switch (event.getActionCommand()) {
			case "mission": //mission selected
				for (AbstractButton b : Collections.list(bgTactic.getElements())) { //this part is needed to update radio buttons during method execution
					bgTactic.remove(b);
				}
				
				mission: switch (cbbMission.getSelectedItem()) {
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
					
					return;
				case Mission.THE_AUCTION, Mission.THE_GALA, Mission.THE_CACHE, Mission.THE_SETUP: //stealth only
					//set & disable tactic
					rbStealth.setSelected(true);
					rbStealth.setEnabled(false);
					
					rbLoud.setSelected(false);
					rbLoud.setEnabled(false);
					break mission;
				case Mission.THE_SCORE: //loud only
					//set & disable tactic
					rbStealth.setSelected(false);
					rbStealth.setEnabled(false);
					
					rbLoud.setSelected(true);
					rbLoud.setEnabled(false);
					break mission;
				default:
					//enable tactic
					rbStealth.setSelected(false);
					rbStealth.setEnabled(true);
					
					rbLoud.setSelected(false);
					rbLoud.setEnabled(true);
					
					//ensures both buttons cannot be pressed simultaneously
					bgTactic.add(rbStealth);
					bgTactic.add(rbLoud);
					
					//disable all components below tactic
					cbbMod1.removeAllItems();
					cbbMod1.setEnabled(false);
					
					cbbMod2.removeAllItems();
					cbbMod2.setEnabled(false);
					
					cbbMod3.removeAllItems();
					cbbMod3.setEnabled(false);
					
					txtTemplate.setText(null);
					txtDailyChallenges.setText(null);
					
					return;
				}
			case "tactic":
				//enable mod1
				cbbMod1.removeAllItems();
				if (rbStealth.isSelected()) {
					for (Modifier item : ((Mission)cbbMission.getSelectedItem()).getStealthMods()) {
						cbbMod1.addItem(item);
					}
				} else { //loud selected
					for (Modifier item : ((Mission)cbbMission.getSelectedItem()).getLoudMods()) {
						cbbMod1.addItem(item);
					}
				}
				cbbMod1.setEnabled(true);
				
				//disable all components below mod1
				cbbMod2.removeAllItems();
				cbbMod2.setEnabled(false);
				
				cbbMod3.removeAllItems();
				cbbMod3.setEnabled(false);
				
				txtTemplate.setText(null);
				txtDailyChallenges.setText(null);
				
				return;
			case "mod1": //logic for adding items to cbbMod2 & cbbMod3: add blank, skip mods with easier difficulty colors, skip already-selected mods
				if (cbbMod1.getSelectedItem().equals(Modifier.BLANK)) {
					//disable all components below mod1
					cbbMod2.removeAllItems();
					cbbMod2.setEnabled(false);
					
					cbbMod3.removeAllItems();
					cbbMod3.setEnabled(false);
					
					txtTemplate.setText(null);
					txtDailyChallenges.setText(null);
					
					return;
				}
				
				cbbMod2.removeAllItems();
				if (rbStealth.isSelected()) {
					for (Modifier item : ((Mission)cbbMission.getSelectedItem()).getStealthMods()) {
						if (item.equals(Modifier.BLANK) ||
								(item.getColor().compareTo(((Modifier)cbbMod1.getSelectedItem()).getColor()) >= 0 && !item.equals(cbbMod1.getSelectedItem()))) {
							cbbMod2.addItem(item);
						}
					}
				} else { //loud selected
					for (Modifier item : ((Mission)cbbMission.getSelectedItem()).getLoudMods()) {
						if (item.equals(Modifier.BLANK) ||
								(item.getColor().compareTo(((Modifier)cbbMod1.getSelectedItem()).getColor()) >= 0 && !item.equals(cbbMod1.getSelectedItem()))) {
							cbbMod2.addItem(item);
						}
					}
				}
				cbbMod2.setEnabled(true);
				
				//disable all components below mod2
				cbbMod3.removeAllItems();
				cbbMod3.setEnabled(false);
				
				txtTemplate.setText(null);
				txtDailyChallenges.setText(null);
				
				return;
			case "mod2":
				if (cbbMod2.getSelectedItem().equals(Modifier.BLANK)) {
					//disable all components below mod2
					cbbMod3.removeAllItems();
					cbbMod3.setEnabled(false);
					
					txtTemplate.setText(null);
					txtDailyChallenges.setText(null);
					
					return;
				}
				
				cbbMod3.removeAllItems();
				if (rbStealth.isSelected()) {
					for (Modifier item : ((Mission)cbbMission.getSelectedItem()).getStealthMods()) {
						if (item.equals(Modifier.BLANK) ||
								(item.getColor().compareTo(((Modifier)cbbMod2.getSelectedItem()).getColor()) >= 0 && !item.equals(cbbMod1.getSelectedItem()) && !item.equals(cbbMod2.getSelectedItem()))) {
							cbbMod3.addItem(item);
						}
					}
				} else { //loud selected
					for (Modifier item : ((Mission)cbbMission.getSelectedItem()).getLoudMods()) {
						if (item.equals(Modifier.BLANK) ||
								(item.getColor().compareTo(((Modifier)cbbMod2.getSelectedItem()).getColor()) >= 0 && !item.equals(cbbMod1.getSelectedItem()) && !item.equals(cbbMod2.getSelectedItem()))) {
							cbbMod3.addItem(item);
						}
					}
				}
				cbbMod3.setEnabled(true);
				
				//disable all components below mod3
				txtTemplate.setText(null);
				txtDailyChallenges.setText(null);
				
				return;
			case "generate":
				if (cbbMod3.getSelectedItem().equals(Modifier.BLANK)) {
					//disable all components below mod3
					txtTemplate.setText(null);
					txtDailyChallenges.setText(null);
					
					return;
				}
				Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Toronto"));
				cal.setTime((Date)dateSpinner.getValue());
				
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
					+ "<div style=\"text-align:center;\">The [[daily challenge]] changes in:<br /><span class=\"daily-countdown\" style=\"font-size:35px;\">\n"
					+ "</span></div>\n"
					+ "{| class=\"article-table\" style=\"margin-left:auto;margin-right:auto;\"\n"
					+ "<!-- Change the mission here -->\n"
					+ "! colspan=\"3\" style=\"text-align:center;\"|" + cbbMission.getSelectedItem() + " (" + (rbStealth.isSelected() ? "Stealth" : "Loud") + ")\n"
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
					+ "!" + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "\n"
					+ "|" + cbbMission.getSelectedItem() + "\n"
					+ "|" + (rbStealth.isSelected() ? "Stealth\n" : "Loud\n")
					+ "|<span class=challenge-" + ((Modifier)cbbMod1.getSelectedItem()).getColor() + ">" + cbbMod1.getSelectedItem() + "</span>, "
					+ "<span class=challenge-" + ((Modifier)cbbMod2.getSelectedItem()).getColor() + ">" + cbbMod2.getSelectedItem() + "</span>, "
					+ "<span class=challenge-" + ((Modifier)cbbMod3.getSelectedItem()).getColor() + ">" + cbbMod3.getSelectedItem() + "</span>");
				
				frame.pack(); //resizes the window
			}
		} finally {
			blockActions = false; //allows actionPerformed to be called again
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new DailyChallengesGUI());
	}
}
