package tools;

public enum PremadeRollables {

	
	
//	TEST("d20 Test");
	
	CHARACTER_CREATION(
			"[4d6 dl1 Str, 4d6 dl1 Dex, 4d6 dl1 Con, 4d6 dl1 Int, 4d6 dl1 Wis, 4d6 dl1 Cha] Ability Scores" + System.lineSeparator()
			+ "<d13 PHB Races;1 Dragonborn;2 Hill Dwarf;3 Mountain Dwarf;4 High Elf;5 Wood Elf;6 Forest Gnome;7 Rock Gnome;8 Half Elf;9 Half Orc;10 Lightfoot Halfling;11 Stout Halfling;12 Human;13 Tiefling>" + System.lineSeparator()
			+ "<d12 PHB Backgrounds;1 Acolyte;2 Charlatan;3 Criminal;4 Entertainer;5 Folk Hero;6 Guild Artisan;7 Hermit;8 Nobel;9 Outlander;10 Sage;11 Sailor; 12 Urchin>" + System.lineSeparator()
			+ "<d12 PHB Classes;1 Barbarian;2 Bard;3 Clearic;4 Druid;5 Fighter;6 Monk;7 Paladin;8 Ranger;9 Rogue;10 Sorcerer;11 Warlock;12 Wizard>" + System.lineSeparator()
			+ "<d20 Alignment;1 Lawful Evil;2-3 Neutral Evil;4 Chaotic Evil;5-6 Lawful Neutral;7-8 True Neutral;9-10 Chaotic Neutral;11-14 Lawful Good;15-17 Neutral Good;18-20 Chaotic Good>"),
//	PLAYER_UTILITY(""),
	DM_UTILITY(
	"<d100	Arctic Encounters L1"+ System.lineSeparator() +"1	1 giant owl"+ System.lineSeparator() +"2-5	1d6+3 kobolds\r\n" + 
	"6-8	1d4+3 trappers (commoners)\r\n" + 
	"9-10	1 owl\r\n" + 
	"11-12	2d4 blood hawks\r\n" + 
	"13-17	2d6 bandits\r\n" + 
	"18-20	1d3 winged kobolds with 1d6 kobolds\r\n" + 
	"21-25	The partially eaten carcass of a mammoth, from which 1d4 weeks of rations can be harvested.\r\n" + 
	"26-29	2d8 hunters tribal warriors\r\n" + 
	"30-35	1 half-ogre\r\n" + 
	"36-40	Single-file tracks in the snow that stop abruptly\r\n" + 
	"41-45	1d3 ice mephits\r\n" + 
	"46-50	1 brown bear\r\n" + 
	"51-53	1d6+1 orcs\r\n" + 
	"54-55	1 polar bear\r\n" + 
	"56-57	1d6 scouts\r\n" + 
	"58-60	1 saber-toothed tiger\r\n" + 
	"61-65	A frozen pond with a jagged hole in the ice that appears recently made\r\n" + 
	"66-68	1 berserker\r\n" + 
	"69-70	1 ogre\r\n" + 
	"71-72	1 griffon\r\n" + 
	"73-75	1 druid\r\n" + 
	"76-80	3d4 refugees (commoners) fleeing from orcs\r\n" + 
	"81	1d3 veterans\r\n" + 
	"82	1d4 orogs\r\n" + 
	"83	2 brown bears\r\n" + 
	"84	1 orc Eye of Gruumsh and 2d8 orcs\r\n" + 
	"85	1d3 winter wolves\r\n" + 
	"86-87	1d4 yetis\r\n" + 
	"88	1 half-ogre\r\n" + 
	"89	1d3 manticores\r\n" + 
	"90	1 bandit captain with 2d6 bandits\r\n" + 
	"91	1 revenant\r\n" + 
	"92-93	1 troll\r\n" + 
	"94-95	1 werebear\r\n" + 
	"96-97	1 young remorhaz\r\n" + 
	"98	1 mammoth\r\n" + 
	"99	1 young white dragon\r\n" + 
	"100	1 frost giant>"),
	ENCOUNTERS(""),
	DMG_CH0(""),
	DMG_CH1(""),
	DMG_CH2("");
	
	private String content;
	
	PremadeRollables(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
}
