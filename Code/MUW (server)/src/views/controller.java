package views;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import engine.*;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Hero;
import model.world.Villain;


@SuppressWarnings({ "serial", "unused" })
public class controller extends JFrame implements ActionListener{
	
	private JLabel champ,type,name,maxHP,mana,AP,speed,AR,AD,abilities,la,a1,a2,a3,ps;
	
	private JTextArea a1d,a2d,a3d,lad;
	
	private String lads = "";
	
	private JLabel p1t, p2t, p1c1, p1c2, p1c3, p2c1, p2c2, p2c3;
	private JLabel p1l, p2l;
	private JButton p1l1, p1l2, p1l3, p2l1, p2l2, p2l3;
	
	private JButton addt1, addt2, c1, c2;
	
	private JLabel background;
	
	private static ArrayList<Champion> allChampions;
	private static ArrayList<Ability> allAbilities;
	
	private static int i;

	private Player p1 = new Player("temp");
	private Player p2 = new Player("temp");
	private Game game = new Game(p1,p2);
	
	private JButton CaptainAmerica,Deadpool,DrStrange,Electro,GhostRider,Hela,Hulk,Iceman,Ironman,Loki,Quicksilver,Spiderman,Thor,Venom,YellowJacket;
	private JLabel CaptainAmerica2,Deadpool2,DrStrange2,Electro2,GhostRider2,Hela2,Hulk2,Iceman2,Ironman2,Loki2,Quicksilver2,Spiderman2,Thor2,Venom2,YellowJacket2;
	
	private JButton exit, startGame;
	
	private JButton fsb, info, sclb, rst;
	
	public void loadAbilities(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/resources/Abilities.csv")));   
		//BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Ability a = null;
			AreaOfEffect ar = null;
			switch (content[5]) {
			case "SINGLETARGET":
				ar = AreaOfEffect.SINGLETARGET;
				break;
			case "TEAMTARGET":
				ar = AreaOfEffect.TEAMTARGET;
				break;
			case "SURROUND":
				ar = AreaOfEffect.SURROUND;
				break;
			case "DIRECTIONAL":
				ar = AreaOfEffect.DIRECTIONAL;
				break;
			case "SELFTARGET":
				ar = AreaOfEffect.SELFTARGET;
				break;

			}
			Effect e = null;
			if (content[0].equals("CC")) {
				switch (content[7]) {
				case "Disarm":
					e = new Disarm(Integer.parseInt(content[8]));
					break;
				case "Dodge":
					e = new Dodge(Integer.parseInt(content[8]));
					break;
				case "Embrace":
					e = new Embrace(Integer.parseInt(content[8]));
					break;
				case "PowerUp":
					e = new PowerUp(Integer.parseInt(content[8]));
					break;
				case "Root":
					e = new Root(Integer.parseInt(content[8]));
					break;
				case "Shield":
					e = new Shield(Integer.parseInt(content[8]));
					break;
				case "Shock":
					e = new Shock(Integer.parseInt(content[8]));
					break;
				case "Silence":
					e = new Silence(Integer.parseInt(content[8]));
					break;
				case "SpeedUp":
					e = new SpeedUp(Integer.parseInt(content[8]));
					break;
				case "Stun":
					e = new Stun(Integer.parseInt(content[8]));
					break;
				}
			}
			switch (content[0]) {
			case "CC":
				a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
				break;
			case "DMG":
				a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			case "HEL":
				a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			}
			allAbilities.add(a);
			line = br.readLine();
		}
		br.close();
	}

	public void loadChampions(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/resources/Champions.csv")));
		//BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Champion c = null;
			switch (content[0]) {
			case "A":
				c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;

			case "H":
				c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			case "V":
				c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			}

			c.getAbilities().add(findAbilityByName(content[8]));
			c.getAbilities().add(findAbilityByName(content[9]));
			c.getAbilities().add(findAbilityByName(content[10]));
			allChampions.add(c);
			line = br.readLine();
		}
		br.close();
	}

	private static Ability findAbilityByName(String name) {
		for (Ability a : allAbilities) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}
	
	@SuppressWarnings("static-access")
	public controller(String playerOneName, String playerTwoName) throws IOException{
		
		URL hoversound  = (getClass().getResource("/resources/hover.wav"));
		URL selectsound = (getClass().getResource("/resources/select.wav"));
		
		p1 = new Player(playerOneName);
		p2 = new Player(playerTwoName);
		game = new Game (p1,p2);
		
		allChampions = new ArrayList<Champion>();
		allAbilities = new ArrayList<Ability>();
		
		try {
			loadAbilities((getClass().getResource("/resources/Abilities.csv")) + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, "" + e.getMessage());
			e.printStackTrace();
		}
		try {
			loadChampions((getClass().getResource("/resources/Champions.csv")) + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, "" + e.getMessage());
			e.printStackTrace();
		}
		
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//this.setUndecorated(true);

		Frame[] frames = Frame.getFrames();
		if(frames.length>1){
			if(frames[frames.length-2].getExtendedState()!=0) {
				this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
				this.setUndecorated(true);
			}
		}
		
		this.getContentPane().setLayout(null);
		this.setSize(1920,1080);
		this.setResizable(false);
		this.setTitle("Marvel - Ultimate War");
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane().setBackground(new java.awt.Color(75, 0, 75));
		
		p1t = new JLabel(p1.getName() + " Team: ");
		p1t.setBounds(10,450,150,20);
		p1t.setForeground(Color.black);
		this.add(p1t);
		p1t.setVisible(true);
		p1t.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		p1t.setForeground(new java.awt.Color(100,255,255));
		
		p2t = new JLabel(p2.getName() + " Team: ");
		p2t.setBounds(10,575,150,20);
		p2t.setForeground(Color.black);
		this.add(p2t);
		p2t.setVisible(true);
		p2t.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		p2t.setForeground(new java.awt.Color(255,100,100));
		
		p1c1 = new JLabel();
		p1c1.setIcon(null);
		p1c1.setBounds(10,485,100,100);
		this.add(p1c1);
		p1c1.setVisible(true);
		
		p1c2 = new JLabel();
		p1c2.setIcon(null);
		p1c2.setBounds(210,485,100,100);
		this.add(p1c2);
		p1c2.setVisible(true);
		
		p1c3 = new JLabel();
		p1c3.setIcon(null);
		p1c3.setBounds(410,485,100,100);
		this.add(p1c3);
		p1c3.setVisible(true);
		
		p2c1 = new JLabel();
		p2c1.setIcon(null);
		p2c1.setBounds(10,610,100,100);
		this.add(p2c1);
		p2c1.setVisible(true);
		
		p2c2 = new JLabel();
		p2c2.setIcon(null);
		p2c2.setBounds(210,610,100,100);
		this.add(p2c2);
		p2c2.setVisible(true);
		
		p2c3 = new JLabel();
		p2c3.setIcon(null);
		p2c3.setBounds(410,610,100,100);
		this.add(p2c3);
		p2c3.setVisible(true);
		
		p1l = new JLabel(p1.getName() + " Leader: Not Chosen");
		p1l.setBounds(550,475,300,20);
		p1l.setForeground(Color.black);
		this.add(p1l);
		p1l.setVisible(true);
		p1l.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		p1l.setForeground(new java.awt.Color(100,255,255));
		
		p2l = new JLabel(p2.getName() + " Leader: Not Chosen");
		p2l.setBounds(550,600,300,20);
		p2l.setForeground(Color.black);
		this.add(p2l);
		p2l.setVisible(true);
		p2l.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		p2l.setForeground(new java.awt.Color(255,100,100));
		
		p1l1 = new JButton("Set Leader");
		p1l1.setBounds(10,470,100,20);
		p1l1.setOpaque(true);
		p1l1.addActionListener(this);
		this.add(p1l1);
		p1l1.setVisible(true);
		p1l1.setFocusable(false);
		p1l1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 12));
		p1l1.setBackground(Color.gray);
		p1l1.setForeground(Color.white);
		
		p1l2 = new JButton("Set Leader");
		p1l2.setBounds(210,470,100,20);
		p1l2.setOpaque(true);
		p1l2.addActionListener(this);
		this.add(p1l2);
		p1l2.setVisible(true);
		p1l2.setFocusable(false);
		p1l2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 12));
		p1l2.setBackground(Color.gray);
		p1l2.setForeground(Color.white);
		
		p1l3 = new JButton("Set Leader");
		p1l3.setBounds(410,470,100,20);
		p1l3.setOpaque(true);
		p1l3.addActionListener(this);
		this.add(p1l3);
		p1l3.setVisible(true);
		p1l3.setFocusable(false);
		p1l3.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 12));
		p1l3.setBackground(Color.gray);
		p1l3.setForeground(Color.white);
		
		p2l1 = new JButton("Set Leader");
		p2l1.setBounds(10,595,100,20);
		p2l1.setOpaque(true);
		p2l1.addActionListener(this);
		this.add(p2l1);
		p2l1.setVisible(true);
		p2l1.setFocusable(false);
		p2l1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 12));
		p2l1.setBackground(Color.gray);
		p2l1.setForeground(Color.white);
		
		p2l2 = new JButton("Set Leader");
		p2l2.setBounds(210,595,100,20);
		p2l2.setOpaque(true);
		p2l2.addActionListener(this);
		this.add(p2l2);
		p2l2.setVisible(true);
		p2l2.setFocusable(false);
		p2l2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 12));
		p2l2.setBackground(Color.gray);
		p2l2.setForeground(Color.white);
		
		p2l3 = new JButton("Set Leader");
		p2l3.setBounds(410,595,100,20);
		p2l3.setOpaque(true);
		p2l3.addActionListener(this);
		this.add(p2l3);
		p2l3.setVisible(true);
		p2l3.setFocusable(false);
		p2l3.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 12));
		p2l3.setBackground(Color.gray);
		p2l3.setForeground(Color.white);

		champ = new JLabel();
		champ.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(i).getName() + ".png")));
		champ.setBounds(835,90,100,100);
		this.add(champ);
		champ.setVisible(true);
		
		type = new JLabel("Type: Hero");
		type.setBounds(825,200,150,20);
		type.setForeground(Color.black);
		this.add(type);
		type.setVisible(true);
		type.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		type.setForeground(Color.white);
		
		name = new JLabel("" + allChampions.get(i).getName());
		name.setBounds(825,65,150,20);
		name.setForeground(Color.black);
		this.add(name);
		name.setVisible(true);
		name.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		name.setForeground(Color.white);
		
		maxHP = new JLabel("Health: " + allChampions.get(i).getMaxHP());
		maxHP.setBounds(825,220,150,20);
		maxHP.setForeground(Color.black);
		this.add(maxHP);
		maxHP.setVisible(true);
		maxHP.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		maxHP.setForeground(Color.white);
		
		mana = new JLabel("Mana: " + allChampions.get(i).getMana());
		mana.setBounds(825,240,150,20);
		mana.setForeground(Color.black);
		this.add(mana);
		mana.setVisible(true);
		mana.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		mana.setForeground(Color.white);
		
		AP = new JLabel("Action Points Per Turn: " + allChampions.get(i).getMaxActionPointsPerTurn());
		AP.setBounds(825,260,300,20);
		AP.setForeground(Color.black);
		this.add(AP);
		AP.setVisible(true);
		AP.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		AP.setForeground(Color.white);
		
		speed = new JLabel("Speed: " + allChampions.get(i).getSpeed());
		speed.setBounds(825,280,150,20);
		speed.setForeground(Color.black);
		this.add(speed);
		speed.setVisible(true);
		speed.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		speed.setForeground(Color.white);
		
		AR = new JLabel("Attack Range: " + allChampions.get(i).getAttackRange());
		AR.setBounds(825,300,150,20);
		AR.setForeground(Color.black);
		this.add(AR);
		AR.setVisible(true);
		AR.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		AR.setForeground(Color.white);
		
		AD = new JLabel("Attack Damage: " + allChampions.get(i).getAttackDamage());
		AD.setBounds(825,320,150,20);
		AD.setForeground(Color.black);
		this.add(AD);
		AD.setVisible(true);
		AD.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		AD.setForeground(Color.white);
		
		abilities = new JLabel("Abilities: ");
		abilities.setBounds(1075,65,150,20);
		abilities.setForeground(Color.black);
		this.add(abilities);
		abilities.setVisible(true);
		abilities.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		abilities.setForeground(Color.white);
		
		la = new JLabel("Leader Ability: ");
		la.setBounds(1075,400,150,20);
		la.setForeground(Color.black);
		this.add(la);
		la.setVisible(true);
		la.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		la.setForeground(Color.white);
		
		a1d = new JTextArea("");
		a1d.setForeground(Color.WHITE);
		a1d.setBounds(1075,100,200,100);
		this.add(a1d);
		a1d.setVisible(true);
		a1d.setEditable(false);
		a1d.setOpaque(false);
		a1d.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a1d.setFocusable(false);
		
		a2d = new JTextArea("");
		a2d.setForeground(Color.WHITE);
		a2d.setBounds(1075,200,200,100);
		this.add(a2d);
		a2d.setVisible(true);
		a2d.setEditable(false);
		a2d.setOpaque(false);
		a2d.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a2d.setFocusable(false);

		a3d = new JTextArea("");
		a3d.setForeground(Color.WHITE);
		a3d.setBounds(1075,300,200,100);
		this.add(a3d);
		a3d.setVisible(true);
		a3d.setEditable(false);
		a3d.setOpaque(false); 
		a3d.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a3d.setFocusable(false);
		
		lad = new JTextArea(lads);
		lad.setForeground(Color.WHITE);
		lad.setBounds(1075,425,200,50);
		this.add(lad);
		lad.setVisible(true);
		lad.setEditable(false);
		lad.setOpaque(false);
		lad.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		lad.setFocusable(false);
		
		info = new JButton("Info");
		info.setBounds(1075,675,60,30);
		info.setOpaque(true);
		info.addActionListener(this);
		this.add(info);
		info.setVisible(true);
		info.setFocusable(false);
		info.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		info.setBackground(Color.gray);
		info.setForeground(Color.white);
		
		addt1 = new JButton("Add Champion To " + p1.getName() + "'s Team");
		addt1.setBounds(770,475,300,30);
		addt1.setOpaque(true);
		addt1.addActionListener(this);
		this.add(addt1);
		addt1.setVisible(true);
		addt1.setFocusable(false);
		addt1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		addt1.setBackground(Color.gray);
		addt1.setForeground(Color.white);
		addt1.setForeground(new java.awt.Color(0,0,150));
		
		addt2 = new JButton("Add Champion To " + p2.getName() + "'s Team");
		addt2.setBounds(770,600,300,30);
		addt2.setOpaque(true);
		addt2.addActionListener(this);
		this.add(addt2);
		addt2.setVisible(true);
		addt2.setFocusable(false);
		addt2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		addt2.setBackground(Color.gray);
		addt2.setForeground(Color.white);
		addt2.setForeground(new java.awt.Color(150,0,0));
		
		c1 = new JButton("Clear " + p1.getName() + "'s Team");
		c1.setBounds(1075,475,200,30);
		c1.setOpaque(true);
		c1.addActionListener(this);
		this.add(c1);
		c1.setVisible(true);
		c1.setFocusable(false);
		c1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		c1.setBackground(Color.gray);
		c1.setForeground(Color.white);
		c1.setForeground(new java.awt.Color(0,0,150));
		
		c2 = new JButton("Clear " + p2.getName() + "'s Team");
		c2.setBounds(1075,600,200,30);
		c2.setOpaque(true);
		c2.addActionListener(this);
		this.add(c2);
		c2.setVisible(true);
		c2.setFocusable(false);
		c2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		c2.setBackground(Color.gray);
		c2.setForeground(Color.white);
		c2.setForeground(new java.awt.Color(150,0,0));
		
		exit = new JButton("Exit Game");
		exit.setBounds(1150,675,100,30);
		exit.setOpaque(true);
		exit.addActionListener(this);
		this.add(exit);
		exit.setVisible(true);
		exit.setFocusable(false);
		exit.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		exit.setBackground(Color.gray);
		exit.setForeground(Color.white);
		
		startGame = new JButton("Start Game");
		startGame.setBounds(910,675,150,30);
		startGame.setOpaque(true);
		startGame.addActionListener(this);
		this.add(startGame);
		startGame.setVisible(true);
		startGame.setFocusable(false);
		startGame.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		startGame.setBackground(Color.gray);
		startGame.setForeground(Color.white);
		
		fsb = new JButton("FS:Off");
		fsb.setBounds(1210,0,75,30);
		fsb.setOpaque(true);
		fsb.addActionListener(this);
		this.add(fsb);
		fsb.setVisible(true);
		fsb.setFocusable(false);
		fsb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		fsb.setBackground(Color.gray);
		fsb.setForeground(Color.white);
		
		sclb = new JButton("SCL:1.0");
		sclb.setBounds(1120,0,90,30);
		sclb.setOpaque(true);
		sclb.addActionListener(this);
		this.add(sclb);
		sclb.setVisible(true);
		sclb.setFocusable(false);
		sclb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		sclb.setBackground(Color.gray);
		sclb.setForeground(Color.white);
		
		rst = new JButton("RST");
		rst.setBounds(1050,0,70,30);
		rst.setOpaque(true);
		rst.addActionListener(this);
		this.add(rst);
		rst.setVisible(true);
		rst.setFocusable(false);
		rst.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		rst.setBackground(Color.gray);
		rst.setForeground(Color.white);
		
		CaptainAmerica = new JButton("");
		CaptainAmerica.setBounds(0,0,150,150);
		CaptainAmerica.setOpaque(false);
		CaptainAmerica.setBackground(new Color(0,0,0,0));
		CaptainAmerica.addActionListener(this);
		this.add(CaptainAmerica);
		CaptainAmerica.setVisible(true);
		CaptainAmerica.setFocusable(false);
		
		Deadpool = new JButton("");
		Deadpool.setBounds(150,0,150,150);
		Deadpool.setOpaque(false);
		Deadpool.setBackground(new Color(0,0,0,0));
		Deadpool.addActionListener(this);
		this.add(Deadpool);
		Deadpool.setVisible(true);
		Deadpool.setFocusable(false);
		
		DrStrange = new JButton("");
		DrStrange.setBounds(300,0,150,150);
		DrStrange.setOpaque(false);
		DrStrange.setBackground(new Color(0,0,0,0));
		DrStrange.addActionListener(this);
		this.add(DrStrange);
		DrStrange.setVisible(true);
		DrStrange.setFocusable(false);

		Electro = new JButton("");
		Electro.setBounds(450,0,150,150);
		Electro.setOpaque(false);
		Electro.setBackground(new Color(0,0,0,0));
		Electro.addActionListener(this);
		this.add(Electro);
		Electro.setVisible(true);
		Electro.setFocusable(false);
		
		GhostRider = new JButton("");
		GhostRider.setBounds(600,0,150,150);
		GhostRider.setOpaque(false);
		GhostRider.setBackground(new Color(0,0,0,0));
		GhostRider.addActionListener(this);
		this.add(GhostRider);
		GhostRider.setVisible(true);
		GhostRider.setFocusable(false);
		
		Hela = new JButton("");
		Hela.setBounds(0,150,150,150);
		Hela.setOpaque(false);
		Hela.setBackground(new Color(0,0,0,0));
		Hela.addActionListener(this);
		this.add(Hela);
		Hela.setVisible(true);
		Hela.setFocusable(false);
		
		Hulk = new JButton("");
		Hulk.setBounds(150,150,150,150);
		Hulk.setOpaque(false);
		Hulk.setBackground(new Color(0,0,0,0));
		Hulk.addActionListener(this);
		this.add(Hulk);
		Hulk.setVisible(true);
		Hulk.setFocusable(false);
		
		Iceman = new JButton("");
		Iceman.setBounds(300,150,150,150);
		Iceman.setOpaque(false);
		Iceman.setBackground(new Color(0,0,0,0));
		Iceman.addActionListener(this);
		this.add(Iceman);
		Iceman.setVisible(true);
		Iceman.setFocusable(false);
		
		Ironman = new JButton("");
		Ironman.setBounds(450,150,150,150);
		Ironman.setOpaque(false);
		Ironman.setBackground(new Color(0,0,0,0));
		Ironman.addActionListener(this);
		this.add(Ironman);
		Ironman.setVisible(true);
		Ironman.setFocusable(false);
		
		Loki = new JButton("");
		Loki.setBounds(600,150,150,150);
		Loki.setOpaque(false);
		Loki.setBackground(new Color(0,0,0,0));
		Loki.addActionListener(this);
		this.add(Loki);
		Loki.setVisible(true);
		Loki.setFocusable(false);
		
		Quicksilver = new JButton("");
		Quicksilver.setBounds(0,300,150,150);
		Quicksilver.setOpaque(false);
		Quicksilver.setBackground(new Color(0,0,0,0));
		Quicksilver.addActionListener(this);
		this.add(Quicksilver);
		Quicksilver.setVisible(true);
		Quicksilver.setFocusable(false);
		
		Spiderman = new JButton("");
		Spiderman.setBounds(150,300,150,150);
		Spiderman.setOpaque(false);
		Spiderman.setBackground(new Color(0,0,0,0));
		Spiderman.addActionListener(this);
		this.add(Spiderman);
		Spiderman.setVisible(true);
		Spiderman.setFocusable(false);
		
		Thor = new JButton("");
		Thor.setBounds(300,300,150,150);
		Thor.setOpaque(false);
		Thor.setBackground(new Color(0,0,0,0));
		Thor.addActionListener(this);
		this.add(Thor);
		Thor.setVisible(true);
		Thor.setFocusable(false);
		
		Venom = new JButton("");
		Venom.setBounds(450,300,150,150);
		Venom.setOpaque(false);
		Venom.setBackground(new Color(0,0,0,0));
		Venom.addActionListener(this);
		this.add(Venom);
		Venom.setVisible(true);
		Venom.setFocusable(false);
		
		YellowJacket = new JButton("");
		YellowJacket.setBounds(600,300,150,150);
		YellowJacket.setOpaque(false);
		YellowJacket.setBackground(new Color(0,0,0,0));
		YellowJacket.addActionListener(this);
		this.add(YellowJacket);
		YellowJacket.setVisible(true);
		YellowJacket.setFocusable(false);
		
		CaptainAmerica2 = new JLabel();
		CaptainAmerica2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(0).getName() + ".png")));
		CaptainAmerica2.setBounds(50,25,100,100);
		this.add(CaptainAmerica2);
		CaptainAmerica2.setVisible(true);
		
		Deadpool2 = new JLabel();
		Deadpool2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(1).getName() + ".png")));
		Deadpool2.setBounds(200,25,100,100);
		this.add(Deadpool2);
		Deadpool2.setVisible(true);
		
		DrStrange2 = new JLabel();
		DrStrange2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(2).getName() + ".png")));
		DrStrange2.setBounds(350,25,100,100);
		this.add(DrStrange2);
		DrStrange2.setVisible(true);
		
		Electro2 = new JLabel();
		Electro2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(3).getName() + ".png")));
		Electro2.setBounds(500,25,100,100);
		this.add(Electro2);
		Electro2.setVisible(true);
		
		GhostRider2 = new JLabel();
		GhostRider2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(4).getName() + ".png")));
		GhostRider2.setBounds(650,25,100,100);
		this.add(GhostRider2);
		GhostRider2.setVisible(true);
		
		Hela2 = new JLabel();
		Hela2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(5).getName() + ".png")));
		Hela2.setBounds(50,175,100,100);
		this.add(Hela2);
		Hela2.setVisible(true);
		
		Hulk2 = new JLabel();
		Hulk2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(6).getName() + ".png")));
		Hulk2.setBounds(175,175,100,100);
		this.add(Hulk2);
		Hulk2.setVisible(true);
		
		Iceman2 = new JLabel();
		Iceman2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(7).getName() + ".png")));
		Iceman2.setBounds(350,175,100,100);
		this.add(Iceman2);
		Iceman2.setVisible(true);
		
		Ironman2 = new JLabel();
		Ironman2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(8).getName() + ".png")));
		Ironman2.setBounds(500,175,100,100);
		this.add(Ironman2);
		Ironman2.setVisible(true);
		
		Loki2 = new JLabel();
		Loki2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(9).getName() + ".png")));
		Loki2.setBounds(650,175,100,100);
		this.add(Loki2);
		Loki2.setVisible(true);
		
		Quicksilver2 = new JLabel();
		Quicksilver2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(10).getName() + ".png")));
		Quicksilver2.setBounds(50,325,100,100);
		this.add(Quicksilver2);
		Quicksilver2.setVisible(true);
		
		Spiderman2 = new JLabel();
		Spiderman2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(11).getName() + ".png")));
		Spiderman2.setBounds(200,325,100,100);
		this.add(Spiderman2);
		Spiderman2.setVisible(true);
		
		Thor2 = new JLabel();
		Thor2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(12).getName() + ".png")));
		Thor2.setBounds(350,325,100,100);
		this.add(Thor2);
		Thor2.setVisible(true);
		
		Venom2 = new JLabel();
		Venom2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(13).getName() + ".png")));
		Venom2.setBounds(500,325,100,100);
		this.add(Venom2);
		Venom2.setVisible(true);
		
		YellowJacket2 = new JLabel();
		YellowJacket2.setIcon(new ImageIcon(getClass().getResource("/resources/" + allChampions.get(14).getName() + ".png")));
		YellowJacket2.setBounds(650,325,100,100);
		this.add(YellowJacket2);
		YellowJacket2.setVisible(true);
		
		background = new JLabel();
		background.setBounds(0,0,750,450);
		this.getContentPane().add(background);
		background.setIcon(new ImageIcon(getClass().getResource("/resources/bg_select.png")));
		//System.out.print(getClass().getResource("/resources/marvel_800_500.png") + "");
		background.setVisible(true);
		add(background, BorderLayout.CENTER);
		
		startGame.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(startGame.isEnabled()==true) music.playSound(hoversound);
			}
		});
		p1l1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(p1l1.isEnabled()==true) music.playSound(hoversound);
			}
		});
		p1l2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(p1l2.isEnabled()==true) music.playSound(hoversound);
			}
		});
		p1l3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(p1l3.isEnabled()==true) music.playSound(hoversound);
			}
		});
		p2l1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(p2l1.isEnabled()==true) music.playSound(hoversound);
			}
		});
		p2l2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(p2l3.isEnabled()==true) music.playSound(hoversound);
			}
		});
		p2l3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(p2l3.isEnabled()==true) music.playSound(hoversound);
			}
		});
		addt1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(addt1.isEnabled()==true) music.playSound(hoversound);
			}
		});
		addt2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(addt2.isEnabled()==true) music.playSound(hoversound);
			}
		});
		c1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(c1.isEnabled()==true) music.playSound(hoversound);
			}
		});
		c2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(c2.isEnabled()==true) music.playSound(hoversound);
			}
		});
		CaptainAmerica.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(CaptainAmerica.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Deadpool.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Deadpool.isEnabled()==true) music.playSound(hoversound);
			}
		});
		DrStrange.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(DrStrange.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Electro.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Electro.isEnabled()==true) music.playSound(hoversound);
			}
		});
		GhostRider.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(GhostRider.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Hela.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Hela.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Hulk.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Hulk.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Iceman.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Iceman.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Ironman.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Ironman.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Loki.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Loki.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Quicksilver.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Quicksilver.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Spiderman.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Spiderman.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Thor.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Thor.isEnabled()==true) music.playSound(hoversound);
			}
		});
		Venom.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(Venom.isEnabled()==true) music.playSound(hoversound);
			}
		});
		YellowJacket.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(YellowJacket.isEnabled()==true) music.playSound(hoversound);
			}
		});
		info.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(info.isEnabled()==true) music.playSound(hoversound);
			}
		});
		fsb.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(fsb.isEnabled()==true) music.playSound(hoversound);
			}
		});
		sclb.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				music.playSound(hoversound);
			}
		});
		exit.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(exit.isEnabled()==true) music.playSound(hoversound);
			}
		});
		
	    this.addKeyListener(new KeyListener() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	        }

	        @Override
	        public void keyPressed(KeyEvent e) {
	        	if(e.getKeyCode()==KeyEvent.VK_ENTER || e.getKeyCode()==KeyEvent.VK_SPACE) {
	        		startGame.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_F11) {
	        		fsb.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
	        		exit.doClick();
	        	}
	            //System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
	        }

	        @Override
	        public void keyReleased(KeyEvent e) {
	        }
	    });
		
		revalidate();
		repaint();
		
		update();
		
		scaleUpdate();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		 URL hoversound  = (getClass().getResource("/resources/hover.wav"));
		 URL selectsound = (getClass().getResource("/resources/select.wav"));
		
		if(e.getSource() == startGame){
			music.playSound(selectsound);
			if(p1.getTeam().size()<3){
				JOptionPane.showMessageDialog(this, "Player One Team Is Not Full");

			}
			else if(p2.getTeam().size()<3){
				JOptionPane.showMessageDialog(this, "Player Two Team Is Not Full");
			} else if(p1.getLeader()==null || p2.getLeader()==null){
				JOptionPane.showMessageDialog(this, "Leader(s) Not Chosen");
			} else {
				this.setVisible(false);
				try {
					
					this.add(new gameView(game,p1,p2));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
				this.setVisible(true);
			}
			
		}
		
		if(e.getSource() == exit){
			music.playSound(selectsound);
			this.dispose();
			System.exit(0);
		}
		
		if(e.getSource() == CaptainAmerica){
			music.playSound(selectsound);
			i=0;
		}
		
		if(e.getSource() == Deadpool){
			music.playSound(selectsound);
			i=1;
		}
		
		if(e.getSource() == DrStrange){
			music.playSound(selectsound);
			i=2;
		}
		
		if(e.getSource() == Electro){
			music.playSound(selectsound);
			i=3;
		}
		
		if(e.getSource() == GhostRider){
			music.playSound(selectsound);
			i=4;
		}
		
		if(e.getSource() == Hela){
			music.playSound(selectsound);
			i=5;
		}
		
		if(e.getSource() == Hulk){
			music.playSound(selectsound);
			i=6;
		}
		
		if(e.getSource() == Iceman){
			music.playSound(selectsound);
			i=7;
		}
		
		if(e.getSource() == Ironman){
			music.playSound(selectsound);
			i=8;
		}
		
		if(e.getSource() == Loki){
			music.playSound(selectsound);
			i=9;
		}
		
		if(e.getSource() == Quicksilver){
			music.playSound(selectsound);
			i=10;
		}
		
		if(e.getSource() == Spiderman){
			music.playSound(selectsound);
			i=11;
		}
		
		if(e.getSource() == Thor){
			music.playSound(selectsound);
			i=12;
		}
		
		if(e.getSource() == Venom){
			music.playSound(selectsound);
			i=13;
		}
		
		if(e.getSource() == YellowJacket){
			music.playSound(selectsound);
			i=14;
		}
		
		if(e.getSource() == p1l1){
			music.playSound(selectsound);
			if(p1.getTeam().size()>0) {
				p1.setLeader(p1.getTeam().get(0));
			}else
				JOptionPane.showMessageDialog(this, "No Champion");
		}
		
		if(e.getSource() == p1l2){
			music.playSound(selectsound);
			if(p1.getTeam().size()>1) {
				p1.setLeader(p1.getTeam().get(1));
			}else
				JOptionPane.showMessageDialog(this, "No Champion");
		}
		
		if(e.getSource() == p1l3){
			music.playSound(selectsound);
			if(p1.getTeam().size()>2) {
				p1.setLeader(p1.getTeam().get(2));
			}else
				JOptionPane.showMessageDialog(this, "No Champion");
		}
		
		if(e.getSource() == p2l1){
			music.playSound(selectsound);
			if(p2.getTeam().size()>0) {
				p2.setLeader(p2.getTeam().get(0));
			}else
				JOptionPane.showMessageDialog(this, "No Champion");
		}
		
		if(e.getSource() == p2l2){
			music.playSound(selectsound);
			if(p2.getTeam().size()>1) {
				p2.setLeader(p2.getTeam().get(1));
			}else
				JOptionPane.showMessageDialog(this, "No Champion");
		}
		
		if(e.getSource() == p2l3){
			music.playSound(selectsound);
			if(p2.getTeam().size()>2) {
				p2.setLeader(p2.getTeam().get(2));
			}else
				JOptionPane.showMessageDialog(this, "No Champion");
		}
		
		if(e.getSource() == addt1){
			music.playSound(selectsound);
			if(p1.getTeam().size()<3){
				if(!p1.getTeam().contains(allChampions.get(i))&&!p2.getTeam().contains(allChampions.get(i))) {
					p1.getTeam().add(allChampions.get(i));
				}else {
					JOptionPane.showMessageDialog(this, "Champion Already Selected");
				}
			}
			else{
				JOptionPane.showMessageDialog(this, "Your Team Is Full");
			}
		}
		
		if(e.getSource() == addt2){
			music.playSound(selectsound);
			if(p2.getTeam().size()<3){
				if(!p1.getTeam().contains(allChampions.get(i))&&!p2.getTeam().contains(allChampions.get(i))) {
					p2.getTeam().add(allChampions.get(i));
				}else {
					JOptionPane.showMessageDialog(this, "Champion Already Selected");
				}
			}
			else{
				JOptionPane.showMessageDialog(this, "Your Team Is Full");
			}
		}
		
		if(e.getSource() == c1) {
			music.playSound(selectsound);
			if(p1.getTeam().size()==0) {
				JOptionPane.showMessageDialog(this, "Team Already Empty");
			} else {
				p1.setLeader(null);
				p1.getTeam().removeAll(allChampions);
			}
		}
		
		if(e.getSource() == c2) {
			music.playSound(selectsound);
			if(p2.getTeam().size()==0) {
				JOptionPane.showMessageDialog(this, "Team Already Empty");
			} else {
				p2.setLeader(null);
				p2.getTeam().removeAll(allChampions);
			}
		}
		
		if(e.getSource() == info) {
			music.playSound(selectsound);
			JOptionPane.showMessageDialog(this,"Effects:\nDisarm: Target cannot use normal attacks. Gain a SIGNLETARGET damaging ability called 'Punch'. Mana Cost: 0, Damaging Amount: 50, Cooldown: 1, AR: 1, AP cost: 1. \nDodge: Target has 50% chance of dodging normal attacks. \nEmbrace: Heals the target by 20%. \nPowerUp: Increase damaging and healing abilities of the target by 20%. \nRoot: Target cannot move. \nShield: Block the next attack or damaging ability cast on target. Once an attack or ability is blocked, the effect should be removed. Increase target speed by 2%. \nShock: Decrease target speed by 10%. Decrease the target's normal attack damage by 10%. Decrease AP by 1. \nSilence: Target cannot use abilities. Increase AP by 2. \nSpeedUp: Increase speed by 15%. Increase AP by 1. \nStun: Target is not allowed to play their turn for the duration.\n\n" 
			        + "Attacking: (extra damage = x1.5)\nHeroes: they deal extra damage when attacking villains.\n"
					+ "Villains: they deal extra damage when attacking heroes.\n"
					+ "Anti-Heroes: when being attacked or attacking a hero or villain, the antihero will always act as the opposite type. If attacking an antihero, damage is calculated normally.\n"
					+ "");
		}
		
		if(e.getSource() == fsb) {
			music.playSound(selectsound);
			Frame[] frames = Frame.getFrames();
	    	if(frames[frames.length-1].getExtendedState()!=0) {
		    	frames[frames.length-1].dispose();
		    	frames[frames.length-1].setExtendedState(0);
		    	frames[frames.length-1].setUndecorated(false);
		    	frames[frames.length-1].setSize(1920,1080);
		    	frames[frames.length-1].setVisible(true);
	    	} else
	    		
	    	if(frames[frames.length-1].getExtendedState()==0) {
		    	frames[frames.length-1].dispose();
		    	frames[frames.length-1].setExtendedState(JFrame.MAXIMIZED_BOTH);
		    	frames[frames.length-1].setUndecorated(true);
		    	frames[frames.length-1].setSize(1920,1080);
		    	frames[frames.length-1].setVisible(true);
	    	}
		}
		
		if(e.getSource() == sclb){
			music.playSound(selectsound);
			scaleSet();
			scaleUpdate();
		}
		
		if(e.getSource() == rst){
			music.playSound(selectsound);
			Game.setScale(1);
			scaleUpdate();
		}
		
		update();
		
	}
	
	public String getType(Champion c) {
		
		if(c.getName().equals("Captain America")||c.getName().equals("Dr Strange")||c.getName().equals("Hulk")||c.getName().equals("Iceman")||c.getName().equals("Ironman")||c.getName().equals("Spiderman")||c.getName().equals("Thor")) {
			return("Hero");
		}else if(c.getName().equals("Electro")||c.getName().equals("Hela")||c.getName().equals("Loki")||c.getName().equals("Quicksilver")||c.getName().equals("Yellow Jacket")) {
			return("Villain");
		}else if(c.getName().equals("Deadpool")||c.getName().equals("Ghost Rider")||c.getName().equals("Venom")) {
			return("AntiHero");
		}
		return null;
		
	}
	
	public String getAbilityType(Ability a) {
		if(a instanceof HealingAbility) {
			return "(Healing Ability)";
		} else if(a instanceof DamagingAbility) {
			return "(Damaging Ability)";
		} else if(a instanceof CrowdControlAbility) {
			return "(Crowd Control Ability)";
		}
		return null;
	}
	
	public String getAbilityAE(Ability a) {
		if(a instanceof HealingAbility) {
			return "Healing Amount: " + (((HealingAbility) a).getHealAmount());
		} else if(a instanceof DamagingAbility) {
			return "Damaging Amount: " + ((DamagingAbility) a).getDamageAmount();
		} else if(a instanceof CrowdControlAbility) {
			return "Effect: " + ((CrowdControlAbility) a).getEffect().getName() + "(" + ((CrowdControlAbility) a).getEffect().getDuration() + " turns)";
		}
		return "";
	}
	
	public void update() {
		
		champ.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(i).getName() + ".png")));
		
		CaptainAmerica2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(0).getName() + ".png")));
		Deadpool2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(1).getName() + ".png")));
		DrStrange2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(2).getName() + ".png")));
		Electro2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(3).getName() + ".png")));
		GhostRider2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(4).getName() + ".png")));
		Hela2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(5).getName() + ".png")));
		Hulk2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(6).getName() + ".png")));
		Iceman2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(7).getName() + ".png")));
		Ironman2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(8).getName() + ".png")));
		Loki2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(9).getName() + ".png")));
		Quicksilver2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(10).getName() + ".png")));
		Spiderman2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(11).getName() + ".png")));
		Thor2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(12).getName() + ".png")));
		Venom2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(13).getName() + ".png")));
		YellowJacket2.setIcon(new ImageIcon(getClass().getResource("/resources/" + (String)allChampions.get(14).getName() + ".png")));
		
		if(allChampions.get(i).getName().equals("Captain America")||allChampions.get(i).getName().equals("Dr Strange")||allChampions.get(i).getName().equals("Hulk")||allChampions.get(i).getName().equals("Iceman")||allChampions.get(i).getName().equals("Ironman")||allChampions.get(i).getName().equals("Spiderman")||allChampions.get(i).getName().equals("Thor")) {
			type.setText("Type: Hero");
		}else if(allChampions.get(i).getName().equals("Electro")||allChampions.get(i).getName().equals("Hela")||allChampions.get(i).getName().equals("Loki")||allChampions.get(i).getName().equals("Quicksilver")||allChampions.get(i).getName().equals("Yellow Jacket")) {
			type.setText("Type: Villain");
		}else if(allChampions.get(i).getName().equals("Deadpool")||allChampions.get(i).getName().equals("Ghost Rider")||allChampions.get(i).getName().equals("Venom")) {
			type.setText("Type: AntiHero");
		}
		
		name.setText("" + (String)allChampions.get(i).getName());
		maxHP.setText("Health: " + allChampions.get(i).getMaxHP());
		mana.setText("Mana: " + allChampions.get(i).getMana());
		AP.setText("Action Points Per Turn: " + allChampions.get(i).getMaxActionPointsPerTurn());
		speed.setText("Speed: " + allChampions.get(i).getSpeed());
		AR.setText("Attack Range: " + allChampions.get(i).getAttackRange());
		AD.setText("Attack Damage: " + allChampions.get(i).getAttackDamage());
		
		a1d.setText("" + allChampions.get(i).getAbilities().get(0).getName() + "   " + getAbilityType( allChampions.get(i).getAbilities().get(0)) + "\nAOE: " +  allChampions.get(i).getAbilities().get(0).getCastArea() + (allChampions.get(i).getAbilities().get(0).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||allChampions.get(i).getAbilities().get(0).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  allChampions.get(i).getAbilities().get(0).getCastRange()) + "\nMana Cost: " +  allChampions.get(i).getAbilities().get(0).getManaCost() + "\nCooldown: " +  allChampions.get(i).getAbilities().get(0).getBaseCooldown() + "\n" + getAbilityAE( allChampions.get(i).getAbilities().get(0)) + "\n" + "AP Cost: " + allChampions.get(i).getAbilities().get(0).getRequiredActionPoints() + "\n--------------------------------------------------");
		a2d.setText("" + allChampions.get(i).getAbilities().get(1).getName() + "   " + getAbilityType( allChampions.get(i).getAbilities().get(1)) + "\nAOE: " +  allChampions.get(i).getAbilities().get(1).getCastArea() + (allChampions.get(i).getAbilities().get(1).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||allChampions.get(i).getAbilities().get(1).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  allChampions.get(i).getAbilities().get(1).getCastRange()) + "\nMana Cost: " +  allChampions.get(i).getAbilities().get(1).getManaCost() + "\nCooldown: " +  allChampions.get(i).getAbilities().get(1).getBaseCooldown() + "\n" + getAbilityAE( allChampions.get(i).getAbilities().get(1)) + "\n" + "AP Cost: " + allChampions.get(i).getAbilities().get(1).getRequiredActionPoints() + "\n--------------------------------------------------");
		a3d.setText("" + allChampions.get(i).getAbilities().get(2).getName() + "   " + getAbilityType( allChampions.get(i).getAbilities().get(2)) + "\nAOE: " +  allChampions.get(i).getAbilities().get(2).getCastArea() + (allChampions.get(i).getAbilities().get(2).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||allChampions.get(i).getAbilities().get(2).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  allChampions.get(i).getAbilities().get(2).getCastRange()) + "\nMana Cost: " +  allChampions.get(i).getAbilities().get(2).getManaCost() + "\nCooldown: " +  allChampions.get(i).getAbilities().get(2).getBaseCooldown() + "\n" + getAbilityAE( allChampions.get(i).getAbilities().get(2)) + "\n" + "AP Cost: " + allChampions.get(i).getAbilities().get(0).getRequiredActionPoints() + "\n--------------------------------------------------");
		
		if(getType( allChampions.get(i))=="Hero") {
			lads="Removes all negative effects from the \nplayer's entire team and adds an Embrace \neffect to them which lasts for 2 turns.";
		}else if(getType( allChampions.get(i))=="Villain") {
			lads="Immediately eliminates (knocks out) \nall enemy champions with less than \n30% health points.";
		} else if(getType( allChampions.get(i))=="AntiHero") {
			lads="All champions on the board except for the \nleaders of each team will be stunned for \n2 turns.";
		}
		lad.setText(lads);

		if(p1.getLeader()!=null)
			p1l.setText(p1.getName() + " Leader: " + p1.getLeader().getName());
		if(p2.getLeader()!=null)
			p2l.setText(p2.getName() + " Leader: " + p2.getLeader().getName());
		
		if(p1.getTeam().size()>=1) {
			p1c1.setIcon(new ImageIcon(getClass().getResource("/resources/" + p1.getTeam().get(0).getName() + ".png")));
		} else {
			p1c1.setIcon(null);
			p1c2.setIcon(null);
			p1c3.setIcon(null);
		}
		if(p1.getTeam().size()>=2) {
			p1c2.setIcon(new ImageIcon(getClass().getResource("/resources/" + p1.getTeam().get(1).getName() + ".png")));
		}
		if(p1.getTeam().size()==3) {
			p1c3.setIcon(new ImageIcon(getClass().getResource("/resources/" + p1.getTeam().get(2).getName() + ".png")));
		}
		
		if(p2.getTeam().size()>=1) {
			p2c1.setIcon(new ImageIcon(getClass().getResource("/resources/" + p2.getTeam().get(0).getName() + ".png")));
		} else {
			p2c1.setIcon(null);
			p2c2.setIcon(null);
			p2c3.setIcon(null);
		}
		if(p2.getTeam().size()>=2) {
			p2c2.setIcon(new ImageIcon(getClass().getResource("/resources/" + p2.getTeam().get(1).getName() + ".png")));
		}
		if(p2.getTeam().size()==3) {
			p2c3.setIcon(new ImageIcon(getClass().getResource("/resources/" + p2.getTeam().get(2).getName() + ".png")));
		} 
		

		
		if(p1.getTeam().size()<1) {
			p1l1.setEnabled(true);
			p1l2.setEnabled(true);
			p1l3.setEnabled(true);
		}else
		if(p1.getLeader()!=null) {
			if(p1.getTeam().get(0).equals(p1.getLeader())) {
				p1l1.setEnabled(false);
			} else {
				p1l1.setEnabled(true);
			}
			if(p1.getTeam().get(1).equals(p1.getLeader())) {
				p1l2.setEnabled(false);
			} else {
				p1l2.setEnabled(true);
			}
			if(p1.getTeam().get(2).equals(p1.getLeader())) {
				p1l3.setEnabled(false);
			} else {
				p1l3.setEnabled(true);
			}
		}
		
		if(p2.getTeam().size()<1) {
			p2l1.setEnabled(true);
			p2l2.setEnabled(true);
			p2l3.setEnabled(true);
		}else
		if(p2.getLeader()!=null) {
			if(p2.getTeam().get(0).equals(p2.getLeader())) {
				p2l1.setEnabled(false);
			} else {
				p2l1.setEnabled(true);
			}
			if(p2.getTeam().get(1).equals(p2.getLeader())) {
				p2l2.setEnabled(false);
			} else {
				p2l2.setEnabled(true);
			}
			if(p2.getTeam().get(2).equals(p2.getLeader())) {
				p2l3.setEnabled(false);
			} else {
				p2l3.setEnabled(true);
			}
		}
		
		Frame[] frames = Frame.getFrames();
    	if(frames[frames.length-1].getExtendedState()!=0) {
	    	fsb.setText("FS:On");
    	} else
    		
    	if(frames[frames.length-1].getExtendedState()==0) {
	    	fsb.setText("FS:Off");
    	}
		
		sclb.setText("SCL:" + Game.getScale());
		
		imageUpdate();
		
		scaleUpdate();
		
	}
	
	public void imageUpdate() {
		
		BufferedImage img_bgs = null;
		BufferedImage img_champ = null;
		BufferedImage img_1 = null;
		BufferedImage img_2 = null;
		BufferedImage img_3 = null;
		BufferedImage img_4 = null;
		BufferedImage img_5 = null;
		BufferedImage img_6 = null;
		BufferedImage img_7 = null;
		BufferedImage img_8 = null;
		BufferedImage img_9 = null;
		BufferedImage img_10 = null;
		BufferedImage img_11 = null;
		BufferedImage img_12 = null;
		BufferedImage img_13 = null;
		BufferedImage img_14 = null;
		BufferedImage img_15 = null;
		BufferedImage img_t11 = null;
		BufferedImage img_t12 = null;
		BufferedImage img_t13 = null;
		BufferedImage img_t21 = null;
		BufferedImage img_t22 = null;
		BufferedImage img_t23 = null;
		
		
		
		try {
		    img_bgs = ImageIO.read(getClass().getResource("/resources/bg_select.png"));
		    img_champ = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(i).getName() + ".png"));
		    
		    img_1 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(0).getName() + ".png"));
		    img_2 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(1).getName() + ".png"));
		    img_3 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(2).getName() + ".png"));
		    img_4 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(3).getName() + ".png"));
		    img_5 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(4).getName() + ".png"));
		    img_6 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(5).getName() + ".png"));
		    img_7 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(6).getName() + ".png"));
		    img_8 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(7).getName() + ".png"));
		    img_9 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(8).getName() + ".png"));
		    img_10 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(9).getName() + ".png"));
		    img_11 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(10).getName() + ".png"));
		    img_12 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(11).getName() + ".png"));
		    img_13 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(12).getName() + ".png"));
		    img_14 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(13).getName() + ".png"));
		    img_15 = ImageIO.read(getClass().getResource("/resources/" + allChampions.get(14).getName() + ".png"));
		    
		    if(game.getFirstPlayer().getTeam().size()>0)
		    img_t11 = ImageIO.read(getClass().getResource("/resources/" + p1.getTeam().get(0).getName() + ".png"));
		    if(game.getFirstPlayer().getTeam().size()>1)
		    img_t12 = ImageIO.read(getClass().getResource("/resources/" + p1.getTeam().get(1).getName() + ".png"));
		    if(game.getFirstPlayer().getTeam().size()>2)
		    img_t13 = ImageIO.read(getClass().getResource("/resources/" + p1.getTeam().get(2).getName() + ".png"));
		    if(game.getSecondPlayer().getTeam().size()>0)
		    img_t21 = ImageIO.read(getClass().getResource("/resources/" + p2.getTeam().get(0).getName() + ".png"));
		    if(game.getSecondPlayer().getTeam().size()>1)
		    img_t22 = ImageIO.read(getClass().getResource("/resources/" + p2.getTeam().get(1).getName() + ".png"));
		    if(game.getSecondPlayer().getTeam().size()>2)
		    img_t23 = ImageIO.read(getClass().getResource("/resources/" + p2.getTeam().get(2).getName() + ".png"));
		    
 
		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		Image dimg_bgs = img_bgs.getScaledInstance((int)(750*Game.getScale()),((int)(450*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_champ = img_champ.getScaledInstance((int)(champ.getIcon().getIconWidth()*Game.getScale()),((int)(champ.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		
		Image dimg_1 = img_1.getScaledInstance((int)(CaptainAmerica2.getIcon().getIconWidth()*Game.getScale()),((int)(CaptainAmerica2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_2 = img_2.getScaledInstance((int)(Deadpool2.getIcon().getIconWidth()*Game.getScale()),((int)(Deadpool2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_3 = img_3.getScaledInstance((int)(DrStrange2.getIcon().getIconWidth()*Game.getScale()),((int)(DrStrange2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_4 = img_4.getScaledInstance((int)(Electro2.getIcon().getIconWidth()*Game.getScale()),((int)(Electro2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_5 = img_5.getScaledInstance((int)(GhostRider2.getIcon().getIconWidth()*Game.getScale()),((int)(GhostRider2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_6 = img_6.getScaledInstance((int)(Hela2.getIcon().getIconWidth()*Game.getScale()),((int)(Hela2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_7 = img_7.getScaledInstance((int)(Hulk2.getIcon().getIconWidth()*Game.getScale()),((int)(Hulk2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_8 = img_8.getScaledInstance((int)(Iceman2.getIcon().getIconWidth()*Game.getScale()),((int)(Iceman2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_9 = img_9.getScaledInstance((int)(Ironman2.getIcon().getIconWidth()*Game.getScale()),((int)(Ironman2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_10 = img_10.getScaledInstance((int)(Loki2.getIcon().getIconWidth()*Game.getScale()),((int)(Loki2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_11 = img_11.getScaledInstance((int)(Quicksilver2.getIcon().getIconWidth()*Game.getScale()),((int)(Quicksilver2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_12 = img_12.getScaledInstance((int)(Spiderman2.getIcon().getIconWidth()*Game.getScale()),((int)(Spiderman2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_13 = img_13.getScaledInstance((int)(Thor2.getIcon().getIconWidth()*Game.getScale()),((int)(Thor2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_14 = img_14.getScaledInstance((int)(Venom2.getIcon().getIconWidth()*Game.getScale()),((int)(Venom2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_15 = img_15.getScaledInstance((int)(YellowJacket2.getIcon().getIconWidth()*Game.getScale()),((int)(YellowJacket2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		
		Image dimg_t11=null;
		if(game.getFirstPlayer().getTeam().size()>0)
			dimg_t11 = img_t11.getScaledInstance((int)(p1c1.getIcon().getIconWidth()*Game.getScale()),((int)(p1c1.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_t12=null;
		if(game.getFirstPlayer().getTeam().size()>1)
			dimg_t12 = img_t12.getScaledInstance((int)(p1c2.getIcon().getIconWidth()*Game.getScale()),((int)(p1c2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_t13=null;
		if(game.getFirstPlayer().getTeam().size()>2)
			dimg_t13 = img_t13.getScaledInstance((int)(p1c3.getIcon().getIconWidth()*Game.getScale()),((int)(p1c3.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_t21=null;
		if(game.getSecondPlayer().getTeam().size()>0)
			dimg_t21 = img_t21.getScaledInstance((int)(p2c1.getIcon().getIconWidth()*Game.getScale()),((int)(p2c1.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_t22=null;
		if(game.getSecondPlayer().getTeam().size()>1)
			dimg_t22 = img_t22.getScaledInstance((int)(p2c2.getIcon().getIconWidth()*Game.getScale()),((int)(p2c2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		Image dimg_t23=null;
		if(game.getSecondPlayer().getTeam().size()>2)
			dimg_t23 = img_t23.getScaledInstance((int)(p2c3.getIcon().getIconWidth()*Game.getScale()),((int)(p2c3.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
		
		
		
		ImageIcon imageIcon_bgs = new ImageIcon(dimg_bgs);
		ImageIcon imageIcon_champ = new ImageIcon(dimg_champ);
		
		ImageIcon imageIcon_1 = new ImageIcon(dimg_1);
		ImageIcon imageIcon_2 = new ImageIcon(dimg_2);
		ImageIcon imageIcon_3 = new ImageIcon(dimg_3);
		ImageIcon imageIcon_4 = new ImageIcon(dimg_4);
		ImageIcon imageIcon_5 = new ImageIcon(dimg_5);
		ImageIcon imageIcon_6 = new ImageIcon(dimg_6);
		ImageIcon imageIcon_7 = new ImageIcon(dimg_7);
		ImageIcon imageIcon_8 = new ImageIcon(dimg_8);
		ImageIcon imageIcon_9 = new ImageIcon(dimg_9);
		ImageIcon imageIcon_10 = new ImageIcon(dimg_10);
		ImageIcon imageIcon_11 = new ImageIcon(dimg_11);
		ImageIcon imageIcon_12 = new ImageIcon(dimg_12);
		ImageIcon imageIcon_13 = new ImageIcon(dimg_13);
		ImageIcon imageIcon_14 = new ImageIcon(dimg_14);
		ImageIcon imageIcon_15 = new ImageIcon(dimg_15);
		
		ImageIcon imageIcon_t11=null;
		if(game.getFirstPlayer().getTeam().size()>0)
		imageIcon_t11 = new ImageIcon(dimg_t11);
		ImageIcon imageIcon_t12=null;
		if(game.getFirstPlayer().getTeam().size()>1)
		imageIcon_t12 = new ImageIcon(dimg_t12);
		ImageIcon imageIcon_t13=null;
		if(game.getFirstPlayer().getTeam().size()>2)
		imageIcon_t13 = new ImageIcon(dimg_t13);
		ImageIcon imageIcon_t21=null;
		if(game.getSecondPlayer().getTeam().size()>0)
		imageIcon_t21 = new ImageIcon(dimg_t21);
		ImageIcon imageIcon_t22=null;
		if(game.getSecondPlayer().getTeam().size()>1)
		imageIcon_t22 = new ImageIcon(dimg_t22);
		ImageIcon imageIcon_t23=null;
		if(game.getSecondPlayer().getTeam().size()>2)
		imageIcon_t23 = new ImageIcon(dimg_t23);
		
		
		
		background.setIcon(imageIcon_bgs);
		champ.setIcon(imageIcon_champ);
		
		CaptainAmerica2.setIcon(imageIcon_1);
		Deadpool2.setIcon(imageIcon_2);
		DrStrange2.setIcon(imageIcon_3);
		Electro2.setIcon(imageIcon_4);
		GhostRider2.setIcon(imageIcon_5);
		Hela2.setIcon(imageIcon_6);
		Hulk2.setIcon(imageIcon_7);
		Iceman2.setIcon(imageIcon_8);
		Ironman2.setIcon(imageIcon_9);
		Loki2.setIcon(imageIcon_10);
		Quicksilver2.setIcon(imageIcon_11);
		Spiderman2.setIcon(imageIcon_12);
		Thor2.setIcon(imageIcon_13);
		Venom2.setIcon(imageIcon_14);
		YellowJacket2.setIcon(imageIcon_15);
		
		p1c1.setIcon(imageIcon_t11);
		p1c2.setIcon(imageIcon_t12);
		p1c3.setIcon(imageIcon_t13);
		p2c1.setIcon(imageIcon_t21);
		p2c2.setIcon(imageIcon_t22);
		p2c3.setIcon(imageIcon_t23);
		
	}
	
	public void scaleUpdate() {
		
		p1t.setBounds((int) (10*Game.getScale()),(int) (450*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		p1t.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		p2t.setBounds((int) (10*Game.getScale()),(int) (575*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		p2t.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		p1c1.setBounds((int) (10*Game.getScale()),(int) (485*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));

		p1c2.setBounds((int) (210*Game.getScale()),(int) (485*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		p1c3.setBounds((int) (410*Game.getScale()),(int) (485*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));

		p2c1.setBounds((int) (10*Game.getScale()),(int) (610*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));

		p2c2.setBounds((int) (210*Game.getScale()),(int) (610*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));

		p2c3.setBounds((int) (410*Game.getScale()),(int) (610*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		p1l.setBounds((int) (550*Game.getScale()),(int) (475*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		p1l.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));

		p2l.setBounds((int) (550*Game.getScale()),(int) (600*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		p2l.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		p1l1.setBounds((int) (10*Game.getScale()),(int) (470*Game.getScale()),(int) (100*Game.getScale()),(int) (20*Game.getScale()));
		p1l1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (12*Game.getScale())));
		
		p1l2.setBounds((int) (210*Game.getScale()),(int) (470*Game.getScale()),(int) (100*Game.getScale()),(int) (20*Game.getScale()));
		p1l2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (12*Game.getScale())));
		
		p1l3.setBounds((int) (410*Game.getScale()),(int) (470*Game.getScale()),(int) (100*Game.getScale()),(int) (20*Game.getScale()));
		p1l3.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (12*Game.getScale())));
		
		p2l1.setBounds((int) (10*Game.getScale()),(int) (595*Game.getScale()),(int) (100*Game.getScale()),(int) (20*Game.getScale()));
		p2l1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (12*Game.getScale())));

		p2l2.setBounds((int) (210*Game.getScale()),(int) (595*Game.getScale()),(int) (100*Game.getScale()),(int) (20*Game.getScale()));
		p2l2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (12*Game.getScale())));

		p2l3.setBounds((int) (410*Game.getScale()),(int) (595*Game.getScale()),(int) (100*Game.getScale()),(int) (20*Game.getScale()));
		p2l3.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (12*Game.getScale())));

		champ.setBounds((int) (835*Game.getScale()),(int) (90*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		type.setBounds((int) (825*Game.getScale()),(int) (200*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		type.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		name.setBounds((int) (825*Game.getScale()),(int) (65*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		name.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		maxHP.setBounds((int) (825*Game.getScale()),(int) (220*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		maxHP.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		mana.setBounds((int) (825*Game.getScale()),(int) (240*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		mana.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		AP.setBounds((int) (825*Game.getScale()),(int) (260*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		AP.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		speed.setBounds((int) (825*Game.getScale()),(int) (280*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		speed.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		AR.setBounds((int) (825*Game.getScale()),(int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		AR.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));

		AD.setBounds((int) (825*Game.getScale()),(int) (320*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		AD.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		abilities.setBounds((int) (1075*Game.getScale()),(int) (65*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		abilities.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		la.setBounds((int) (1075*Game.getScale()),(int) (400*Game.getScale()),(int) (150*Game.getScale()),(int) (20*Game.getScale()));
		la.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		a1d.setBounds((int) (1075*Game.getScale()),(int) (100*Game.getScale()),(int) (200*Game.getScale()),(int) (100*Game.getScale()));
		a1d.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		a2d.setBounds((int) (1075*Game.getScale()),(int) (200*Game.getScale()),(int) (200*Game.getScale()),(int) (100*Game.getScale()));
		a2d.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));

		a3d.setBounds((int) (1075*Game.getScale()),(int) (300*Game.getScale()),(int) (200*Game.getScale()),(int) (100*Game.getScale()));
		a3d.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));

		lad.setBounds((int) (1075*Game.getScale()),(int) (425*Game.getScale()),(int) (200*Game.getScale()),(int) (50*Game.getScale()));
		lad.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		info.setBounds((int) (1075*Game.getScale()),(int) (675*Game.getScale()),(int) (60*Game.getScale()),(int) (30*Game.getScale()));
		info.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));

		addt1.setBounds((int) (770*Game.getScale()),(int) (475*Game.getScale()),(int) (300*Game.getScale()),(int) (30*Game.getScale()));
		addt1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		addt2.setBounds((int) (770*Game.getScale()),(int) (600*Game.getScale()),(int) (300*Game.getScale()),(int) (30*Game.getScale()));
		addt2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		c1.setBounds((int) (1075*Game.getScale()),(int) (475*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		c1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		c2.setBounds((int) (1075*Game.getScale()),(int) (600*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		c2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		exit.setBounds((int) (1150*Game.getScale()),(int) (675*Game.getScale()),(int) (100*Game.getScale()),(int) (30*Game.getScale()));
		exit.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		startGame.setBounds((int) (910*Game.getScale()),(int) (675*Game.getScale()),(int) (150*Game.getScale()),(int) (30*Game.getScale()));
		startGame.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		fsb.setBounds((int) (1210*Game.getScale()),0,(int) (75*Game.getScale()),(int) (30*Game.getScale()));
		fsb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		sclb.setBounds((int) (1120*Game.getScale()),0,(int) (90*Game.getScale()),(int) (30*Game.getScale()));
		sclb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		rst.setBounds((int) (1050*Game.getScale()),0,(int) (70*Game.getScale()),(int) (30*Game.getScale()));
		rst.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		CaptainAmerica.setBounds(0,0,(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Deadpool.setBounds((int) (150*Game.getScale()),0,(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		DrStrange.setBounds((int) (300*Game.getScale()),0,(int) (150*Game.getScale()),(int) (150*Game.getScale()));

		Electro.setBounds((int) (450*Game.getScale()),0,(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		GhostRider.setBounds((int) (600*Game.getScale()),0,(int) (150*Game.getScale()),(int) (150*Game.getScale()));

		Hela.setBounds(0,(int) (150*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Hulk.setBounds((int) (150*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Iceman.setBounds((int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Ironman.setBounds((int) (450*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));

		Loki.setBounds((int) (600*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Quicksilver.setBounds(0,(int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Spiderman.setBounds((int) (150*Game.getScale()),(int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));

		Thor.setBounds((int) (300*Game.getScale()),(int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		Venom.setBounds((int) (450*Game.getScale()),(int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));

		YellowJacket.setBounds((int) (600*Game.getScale()),(int) (300*Game.getScale()),(int) (150*Game.getScale()),(int) (150*Game.getScale()));
		
		CaptainAmerica2.setBounds((int) (50*Game.getScale()),(int) (25*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Deadpool2.setBounds((int) (200*Game.getScale()),(int) (25*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		DrStrange2.setBounds((int) (350*Game.getScale()),(int) (25*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Electro2.setBounds((int) (500*Game.getScale()),(int) (25*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		GhostRider2.setBounds((int) (650*Game.getScale()),(int) (25*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Hela2.setBounds((int) (50*Game.getScale()),(int) (175*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Hulk2.setBounds((int) (175*Game.getScale()),(int) (175*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Iceman2.setBounds((int) (350*Game.getScale()),(int) (175*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Ironman2.setBounds((int) (500*Game.getScale()),(int) (175*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Loki2.setBounds((int) (650*Game.getScale()),(int) (175*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Quicksilver2.setBounds((int) (50*Game.getScale()),(int) (325*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Spiderman2.setBounds((int) (200*Game.getScale()),(int) (325*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Thor2.setBounds((int) (350*Game.getScale()),(int) (325*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		Venom2.setBounds((int) (500*Game.getScale()),(int) (325*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		YellowJacket2.setBounds((int) (650*Game.getScale()),(int) (325*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		background.setBounds(0,0,(int) (750*Game.getScale()),(int) (450*Game.getScale()));
		
	}
	
	public void scaleSet() {
		if(Game.getScale()==1)
			Game.setScale(1.1); else
		if(Game.getScale()==1.1)
			Game.setScale(1.2); else
		if(Game.getScale()==1.2)
			Game.setScale(1.3); else
		if(Game.getScale()==1.3)
			Game.setScale(1.4); else
		if(Game.getScale()==1.4)
			Game.setScale(1.5); else
		if(Game.getScale()==1.5)
			Game.setScale(1.6);
		if(Game.getScale()==1.6)
			Game.setScale(1.7); else
		if(Game.getScale()==1.7)
			Game.setScale(1.8); else
		if(Game.getScale()==1.8)
			Game.setScale(1.9); else
		if(Game.getScale()==1.9)
			Game.setScale(2); else
		if(Game.getScale()==2)
			Game.setScale(2.1); else
		if(Game.getScale()==2.1)
			Game.setScale(2.2); else
		if(Game.getScale()==2.2)
			Game.setScale(2.3); else
		if(Game.getScale()==2.3)
			Game.setScale(2.4); else
		if(Game.getScale()==2.4)
			Game.setScale(2.5); else
		if(Game.getScale()==2.5)
			Game.setScale(2.6);
		if(Game.getScale()==2.6)
			Game.setScale(2.7); else
		if(Game.getScale()==2.7)
			Game.setScale(2.8); else
		if(Game.getScale()==2.8)
			Game.setScale(2.9); else
		if(Game.getScale()==2.9)
			Game.setScale(3); else
		if(Game.getScale()==3)
			Game.setScale(1);
	}

	public static void test(controller c){
		c.game.getFirstPlayer().getTeam().add(allChampions.get(6));
		c.game.getFirstPlayer().getTeam().add(allChampions.get(14));
		c.game.getFirstPlayer().getTeam().add(allChampions.get(4));
		c.game.getSecondPlayer().getTeam().add(allChampions.get(13));
		c.game.getSecondPlayer().getTeam().add(allChampions.get(1));
		c.game.getSecondPlayer().getTeam().add(allChampions.get(11));
		c.game.getFirstPlayer().setLeader(c.game.getFirstPlayer().getTeam().get(1));
		c.game.getSecondPlayer().setLeader(c.game.getSecondPlayer().getTeam().get(1));
		c.update();
		c.startGame.doClick();
	}

	@SuppressWarnings("unused")
	public static void main(String[]args) throws IOException{
		controller c = new controller("test1","test2");
		c.fsb.doClick();
		test(c);
	}
	
}
