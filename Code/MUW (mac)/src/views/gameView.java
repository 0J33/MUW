package views;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.xml.stream.events.StartDocument;

import engine.*;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.*;
import model.effects.Disarm;
import model.world.*;

@SuppressWarnings({ "serial", "unused" })
public class gameView  extends JFrame implements ActionListener{
	
	ArrayList<Damageable> targets;

	private JButton exit;
	private JButton endTurn;
	
	private JLabel background;
	
	private JButton move,attack;
	private JButton dUp,dDown,dRight,dLeft;
	private JLabel dir;
	private JTextArea a1,a2,a3,lad;
	private JLabel al;
	private JButton a1u,a2u,a3u,alu;
	
	private JTextArea a4;
	private JButton a4u;
	
	private JButton info;
	
	private JLabel cur;
	private JLabel arrow;
	private JLabel effects;
	
	private JLabel TO1,TO2,TO3,TO4,TO5,TO6;
	
	private JTextArea e11,e12,e13,e21,e22,e23;
	private JLabel e11n,e12n,e13n,e21n,e22n,e23n;
	private String e11s,e12s,e13s,e21s,e22s,e23s;

	private Player p1,p2;
	private Game game;
	
	private Direction direction;
	
	private JLabel p1n,p2n,pc;
	
	private int x;
	private int y;
	
	private String lads = "";
	
	private JLabel st;
	private JLabel st2;
	
	private Champion[] arr  = new Champion[6];
	
	private JButton[][] buttons = new JButton[5][5];
	private JTextArea[][] texts = new JTextArea[5][5];
	private JLabel[][] images = new JLabel[5][5];
	
	private JButton fsb, sclb, rst;
	
	private JProgressBar hb11, hb12, hb13, hb21, hb22, hb23;
	
	@SuppressWarnings("static-access")
	public gameView(Game game, Player p1, Player p2) throws IOException{

		URL themesound = (getClass().getResource("/resources/theme.wav"));
		URL hoversound  = (getClass().getResource("/resources/hover.wav"));
		URL selectsound = (getClass().getResource("/resources/select.wav"));
		URL attacksound = (getClass().getResource("/resources/attack.wav"));
		URL castsound = (getClass().getResource("/resources/cast.wav"));
		URL diesound = (getClass().getResource("/resources/die.wav"));
		URL healsound = (getClass().getResource("/resources/heal.wav"));
		URL leadersound = (getClass().getResource("/resources/leader.wav"));
		URL movesound = (getClass().getResource("/resources/move.wav"));
		
		this.game=game;
		this.p1=p1;
		this.p2=p2;
		
		game.placeChampions();
		
		for (Champion c : game.getFirstPlayer().getTeam())
			game.getTurnOrder().insert(c);
		for (Champion c : game.getSecondPlayer().getTeam())
			game.getTurnOrder().insert(c);

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
		this.getContentPane().setBackground(new java.awt.Color(0, 110, 110));
		
		arrow = new JLabel("^");
		arrow.setBounds(515,700,300,20);
		arrow.setForeground(Color.white);
		this.add(arrow);
		arrow.setVisible(true);
		arrow.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		TO1 = new JLabel();
		TO1.setIcon(null);
		TO1.setBounds(500,635,100,100);
		this.add(TO1);
		TO1.setVisible(true);
		
		TO2 = new JLabel();
		TO2.setIcon(null);
		TO2.setBounds(550,635,100,100);
		this.add(TO2);
		TO2.setVisible(true);
		
		TO3 = new JLabel();
		TO3.setIcon(null);
		TO3.setBounds(600,635,100,100);
		this.add(TO3);
		TO3.setVisible(true);
		
		TO4 = new JLabel();
		TO4.setIcon(null);
		TO4.setBounds(650,635,100,100);
		this.add(TO4);
		TO4.setVisible(true);
		
		TO5 = new JLabel();
		TO5.setIcon(null);
		TO5.setBounds(700,635,100,100);
		this.add(TO5);
		TO5.setVisible(true);
		
		TO6 = new JLabel();
		TO6.setIcon(null);
		TO6.setBounds(750,635,100,100);
		this.add(TO6);
		TO6.setVisible(true);
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			if(game.getTurnOrder().size()>0)
				
			arr[0]=null;
			arr[1]=null;
			arr[2]=null;
			arr[3]=null;
			arr[4]=null;
			arr[5]=null;
			ArrayList<Champion> temp = new ArrayList<Champion>();
			while(!(game.getTurnOrder().isEmpty())) {
				temp.add((Champion)game.getTurnOrder().remove());
			}
			for(int i=0;i<temp.size();i++) {
				game.getTurnOrder().insert(temp.get(i));
			}
			if(temp.size()>0) {
				for(int i=0;i<game.getTurnOrder().size();i++) {
					if(temp.get(i)!=null)
						this.arr[i]=(Champion) temp.get(i);
					else 
						this.arr[i]=null;
				}
			}
			
			if(game.getTurnOrder().size()==6) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO4.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[3].getName() + " 2.png")));
				TO5.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[4].getName() + " 2.png")));
				TO6.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[5].getName() + " 2.png")));
			}
			if(game.getTurnOrder().size()==5) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO4.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[3].getName() + " 2.png")));
				TO5.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[4].getName() + " 2.png")));
				TO6.setIcon(null);
			}
			if(game.getTurnOrder().size()==4) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO4.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[3].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
			}
			if(game.getTurnOrder().size()==3) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
				TO4.setIcon(null);
			}
			if(game.getTurnOrder().size()==2) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
				TO4.setIcon(null);
				TO3.setIcon(null);
				
			}
			if(game.getTurnOrder().size()==1) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
				TO4.setIcon(null);
				TO3.setIcon(null);
				TO2.setIcon(null);
			}
			
		}
		
		e11n = new JLabel("test");
		e11n.setBounds(10,25,200,20);
		e11n.setForeground(new java.awt.Color(150,255,255));
		this.add(e11n);
		e11n.setVisible(true);
		e11n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		e12n = new JLabel("test");
		e12n.setBounds(10,120,200,20);
		e12n.setForeground(new java.awt.Color(150,255,255));
		this.add(e12n);
		e12n.setVisible(true);
		e12n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		e13n = new JLabel("test");
		e13n.setBounds(10,215,200,20);
		e13n.setForeground(new java.awt.Color(150,255,255));
		this.add(e13n);
		e13n.setVisible(true);
		e13n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		e21n = new JLabel("test");
		e21n.setBounds(10,330,200,20);
		e21n.setForeground(new java.awt.Color(255,150,150));
		this.add(e21n);
		e21n.setVisible(true);
		e21n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		e22n = new JLabel("test");
		e22n.setBounds(10,425,200,20);
		e22n.setForeground(new java.awt.Color(255,150,150));
		this.add(e22n);
		e22n.setVisible(true);
		e22n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		e23n = new JLabel("test");
		e23n.setBounds(10,520,200,20);
		e23n.setForeground(new java.awt.Color(255,150,150));
		this.add(e23n);
		e23n.setVisible(true);
		e23n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		e11 = new JTextArea("");
		e11.setForeground(Color.WHITE);
		e11.setBounds(10,45,200,75);
		this.add(e11);
		e11.setVisible(true);
		e11.setEditable(false);
		e11.setOpaque(false);
		e11.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		e11.setFocusable(false);
		
		e12 = new JTextArea("");
		e12.setForeground(Color.WHITE);
		e12.setBounds(10,140,200,75);
		this.add(e12);
		e12.setVisible(true);
		e12.setEditable(false);
		e12.setOpaque(false);
		e12.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		e12.setFocusable(false);
		
		e13 = new JTextArea("");
		e13.setForeground(Color.WHITE);
		e13.setBounds(10,235,200,75);
		this.add(e13);
		e13.setVisible(true);
		e13.setEditable(false);
		e13.setOpaque(false);
		e13.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		e13.setFocusable(false);
		
		e21 = new JTextArea("");
		e21.setForeground(Color.WHITE);
		e21.setBounds(10,350,200,75);
		this.add(e21);
		e21.setVisible(true);
		e21.setEditable(false);
		e21.setOpaque(false);
		e21.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		e21.setFocusable(false);
		
		e22 = new JTextArea("");
		e22.setForeground(Color.WHITE);
		e22.setBounds(10,445,200,75);
		this.add(e22);
		e22.setVisible(true);
		e22.setEditable(false);
		e22.setOpaque(false);
		e22.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		e22.setFocusable(false);
		
		e23 = new JTextArea("");
		e23.setForeground(Color.WHITE);
		e23.setBounds(10,540,200,75);
		this.add(e23);
		e23.setVisible(true);
		e23.setEditable(false);
		e23.setOpaque(false);
		e23.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		e23.setFocusable(false);
		
		hb11 = new JProgressBar();
		hb11.setForeground(Color.green);
		hb11.setBounds(125, 25, 100, 20);
		this.add(hb11);
		hb11.setVisible(true);
		
		hb12 = new JProgressBar();
		hb12.setForeground(Color.green);
		hb12.setBounds(125, 120, 100, 20);
		this.add(hb12);
		hb12.setVisible(true);
		
		hb13 = new JProgressBar();
		hb13.setForeground(Color.green);
		hb13.setBounds(125, 215, 100, 20);
		this.add(hb13);
		hb13.setVisible(true);
		
		hb21 = new JProgressBar();
		hb21.setForeground(Color.green);
		hb21.setBounds(125, 330, 100, 20);
		this.add(hb21);
		hb21.setVisible(true);
		
		hb22 = new JProgressBar();
		hb22.setForeground(Color.green);
		hb22.setBounds(125, 425, 100, 20);
		this.add(hb22);
		hb22.setVisible(true);
		
		hb23 = new JProgressBar();
		hb23.setForeground(Color.green);
		hb23.setBounds(125, 520, 100, 20);
		this.add(hb23);
		hb23.setVisible(true);
		
		dUp = new JButton("");
		dUp.setBounds(1100,485,30,30);
		dUp.setOpaque(true);
		dUp.addActionListener(this);
		this.add(dUp);
		dUp.setVisible(true);
		dUp.setFocusable(false);
		dUp.setBackground(Color.gray);
		
		dDown = new JButton("");
		dDown.setBounds(1100,565,30,30);
		dDown.setOpaque(true);
		dDown.addActionListener(this);
		this.add(dDown);
		dDown.setVisible(true);
		dDown.setFocusable(false);
		dDown.setBackground(Color.gray);
		
		dLeft = new JButton("");
		dLeft.setBounds(1060,525,30,30);
		dLeft.setOpaque(true);
		dLeft.addActionListener(this);
		this.add(dLeft);
		dLeft.setVisible(true);
		dLeft.setFocusable(false);
		dLeft.setBackground(Color.gray);
		
		dRight = new JButton("");
		dRight.setBounds(1140,525,30,30);
		dRight.setOpaque(true);
		dRight.addActionListener(this);
		this.add(dRight);
		dRight.setVisible(true);
		dRight.setFocusable(false);
		dRight.setBackground(Color.gray);
		
		dir = new JLabel("NONE", SwingConstants.CENTER);
		dir.setBounds(1090,530,50,20);
		dir.setForeground(Color.white);
		this.add(dir);
		dir.setVisible(true);
		dir.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		move = new JButton("Move (AP:1)");
		move.setBounds(1150,485,120,30);
		move.setOpaque(true);
		move.addActionListener(this);
		this.add(move);
		move.setVisible(true);
		move.setFocusable(false);
		move.setBackground(Color.gray);
		move.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		move.setForeground(Color.white);
		
		attack = new JButton("Attack (AP:2)");
		attack.setBounds(1150,565,120,30);
		attack.setOpaque(true);
		attack.addActionListener(this);
		this.add(attack);
		attack.setVisible(true);
		attack.setFocusable(false);
		attack.setBackground(Color.gray);
		attack.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		attack.setForeground(Color.white);
		
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
		
		p1n = new JLabel("Player One: ");
		p1n.setBounds(10,5,200,20);
		p1n.setForeground(new java.awt.Color(150,255,255));
		this.add(p1n);
		p1n.setVisible(true);
		p1n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		p2n = new JLabel("Player Two: ");
		p2n.setBounds(10,310,200,20);
		p2n.setForeground(new java.awt.Color(255,150,150));
		this.add(p2n);
		p2n.setVisible(true);
		p2n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		pc = new JLabel("Current Player: ");
		pc.setBounds(800,650,300,20);
		pc.setForeground(Color.white);
		this.add(pc);
		pc.setVisible(true);
		pc.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		cur = new JLabel("Current Champion: ");
		cur.setBounds(500,650,300,20);
		cur.setForeground(Color.white);
		this.add(cur);
		cur.setVisible(true);
		cur.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		st = new JLabel("Selected Target: ");
		st.setBounds(250,650,300,20);
		st.setForeground(Color.white);
		this.add(st);
		st.setVisible(true);
		st.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		st2 = new JLabel();
		st2.setIcon(null);
		st2.setBounds(300,635,100,100);
		this.add(st2);
		st2.setVisible(true);
		
		a1 = new JTextArea("");
		a1.setForeground(Color.WHITE);
		a1.setBounds(1075,50,200,75);
		this.add(a1);
		a1.setVisible(true);
		a1.setEditable(false);
		a1.setOpaque(false);
		a1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a1.setFocusable(false);
		
		a1u = new JButton("Cast Ability");
		a1u.setBounds(1075,125,200,30);
		a1u.setOpaque(true);
		a1u.addActionListener(this);
		this.add(a1u);
		a1u.setVisible(true);
		a1u.setFocusable(false);
		a1u.setBackground(Color.gray);
		a1u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		a1u.setForeground(Color.white);
		
		a2 = new JTextArea("");
		a2.setForeground(Color.WHITE);
		a2.setBounds(1075,155,200,75);
		this.add(a2);
		a2.setVisible(true);
		a2.setEditable(false);
		a2.setOpaque(false);
		a2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a2.setFocusable(false);
		
		a2u = new JButton("Cast Ability");
		a2u.setBounds(1075,230,200,30);
		a2u.setOpaque(true);
		a2u.addActionListener(this);
		this.add(a2u);
		a2u.setVisible(true);
		a2u.setFocusable(false);
		a2u.setBackground(Color.gray);
		a2u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		a2u.setForeground(Color.white);
		
		a3 = new JTextArea("");
		a3.setForeground(Color.WHITE);
		a3.setBounds(1075,260,200,75);
		this.add(a3);
		a3.setVisible(true);
		a3.setEditable(false);
		a3.setOpaque(false);
		a3.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a3.setFocusable(false);
		
		a3u = new JButton("Cast Ability");
		a3u.setBounds(1075,335,200,30);
		a3u.setOpaque(true);
		a3u.addActionListener(this);
		this.add(a3u);
		a3u.setVisible(true);
		a3u.setFocusable(false);
		a3u.setBackground(Color.gray);
		a3u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		a3u.setForeground(Color.white);
		
		lad = new JTextArea(lads);
		lad.setForeground(Color.WHITE);
		lad.setBounds(1075,390,200,50);
		this.add(lad);
		lad.setVisible(true);
		lad.setEditable(false);
		lad.setOpaque(false);
		lad.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		lad.setFocusable(false);
		
		alu = new JButton("Use Leader Ability");
		alu.setBounds(1075,440,200,30);
		alu.setOpaque(true);
		alu.addActionListener(this);
		this.add(alu);
		alu.setVisible(true);
		alu.setFocusable(false);
		alu.setBackground(Color.gray);
		alu.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		alu.setForeground(Color.white);
		
		a4 = new JTextArea("");
		a4.setForeground(Color.WHITE);
		a4.setBounds(10,615,200,75);
		this.add(a4);
		a4.setVisible(true);
		a4.setEditable(false);
		a4.setOpaque(false);
		a4.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
		a4.setFocusable(false);
		
		a4u = new JButton("Cast Ability");
		a4u.setBounds(10,690,200,30);
		a4u.setOpaque(true);
		a4u.addActionListener(this);
		this.add(a4u);
		a4u.setFocusable(false);
		a4u.setBackground(Color.gray);
		a4u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		a4u.setForeground(Color.white);
		a4u.setVisible(false);
		
		exit = new JButton("Exit Game");
		exit.setBounds(1150,675,100,30);
		exit.setOpaque(true);
		exit.addActionListener(this);
		this.add(exit);
		exit.setVisible(true);
		exit.setFocusable(false);
		exit.setBackground(Color.gray);
		exit.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		exit.setForeground(Color.white);
		
		endTurn = new JButton("End Turn");
		endTurn.setBounds(1150,625,100,30);
		endTurn.setOpaque(true);
		endTurn.addActionListener(this);
		this.add(endTurn);
		endTurn.setVisible(true);
		endTurn.setFocusable(false);
		endTurn.setBackground(Color.gray);
		endTurn.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		endTurn.setForeground(Color.white);

		for(int i=4;i>-1;i--){
			for(int j=0;j<5;j++){

				buttons[4-j][4-i] = new JButton("");
				buttons[4-j][4-i].setForeground(Color.white);
				buttons[4-j][4-i].setBounds(330+(4-i)*160,90+(j)*130,30,30);
				this.add(buttons[4-j][4-i]);
				buttons[4-j][4-i].setVisible(true);
				buttons[4-j][4-i].setOpaque(false);
				buttons[4-j][4-i].setBackground(new Color(0,0,0,0));
				buttons[4-j][4-i].addActionListener(this);

				images[4-j][4-i] = new JLabel();
				images[4-j][4-i].setIcon(null);
				images[4-j][4-i].setBounds(315+(4-i)*160,40+(j)*130,100,100);
				this.add(images[4-j][4-i]);
				images[4-j][4-i].setVisible(true);

				texts[4-j][4-i] = new JTextArea("");
				texts[4-j][4-i].setForeground(Color.white);
				texts[4-j][4-i].setBounds(255+(4-i)*160,5+(j)*130,155,130);
				this.add(texts[4-j][4-i]);
				texts[4-j][4-i].setVisible(true);
				texts[4-j][4-i].setEditable(false);
				texts[4-j][4-i].setOpaque(false);
				texts[4-j][4-i].setBackground(new Color(0,0,0,0)); 
				texts[4-j][4-i].setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 10));
			}
		}

		
		background = new JLabel();
		background.setBounds(250,5,800,650);
		this.getContentPane().add(background);
		background.setIcon(new ImageIcon(getClass().getResource("/resources/bg.png")));
		background.setVisible(true);
		add(background, BorderLayout.CENTER);
		
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
		
		dUp.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(dUp.isEnabled()==true) music.playSound(hoversound);
			}
		});
		dDown.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(dDown.isEnabled()==true) music.playSound(hoversound);
			}
		});
		dRight.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(dRight.isEnabled()==true) music.playSound(hoversound);
			}
		});
		dLeft.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(dLeft.isEnabled()==true) music.playSound(hoversound);
			}
		});
		move.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(move.isEnabled()==true) music.playSound(hoversound);
			}
		});
		attack.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(attack.isEnabled()==true) music.playSound(hoversound);
			}
		});
		a1u.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(a1u.isEnabled()==true) music.playSound(hoversound);
			}
		});
		a2u.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(a2u.isEnabled()==true) music.playSound(hoversound);
			}
		});
		a3u.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(a3u.isEnabled()==true) music.playSound(hoversound);
			}
		});
		a4u.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(a4u.isEnabled()==true) music.playSound(hoversound);
			}
		});
		alu.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(alu.isEnabled()==true) music.playSound(hoversound);
			}
		});
		endTurn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(endTurn.isEnabled()==true) music.playSound(hoversound);
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
				if(sclb.isEnabled()==true) music.playSound(hoversound);
			}
		});
		rst.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(rst.isEnabled()==true) music.playSound(hoversound);
			}
		});
		exit.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(exit.isEnabled()==true) music.playSound(hoversound);
			}
		});
		
	   /*this.addKeyListener(new KeyListener() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	        }

	        @Override
	        public void keyPressed(KeyEvent e) {

	        	/*if(e.getKeyCode()==KeyEvent.VK_SHIFT) {
	        		endTurn.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
	        		exit.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_F11) {
	        		fsb.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_ENTER) {
	        		attack.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_SPACE) {
	        		move.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_W || e.getKeyCode()==KeyEvent.VK_UP) {
	        		dUp.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_S || e.getKeyCode()==KeyEvent.VK_DOWN) {
	        		dDown.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_D || e.getKeyCode()==KeyEvent.VK_RIGHT) {
	        		dRight.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_A || e.getKeyCode()==KeyEvent.VK_LEFT) {
	        		dLeft.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_1) {
	        		a1u.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_2) {
	        		a2u.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_3) {
	        		a3u.doClick();
	        	}
	        	if(e.getKeyCode()==KeyEvent.VK_L) {
	        		alu.doClick();
	        	}
				if(e.getKeyCode()==KeyEvent.VK_4) {
	        		if(a4u.isShowing())
						a4u.doClick();
	        	}
	            //System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
	        }

	        @Override
	        public void keyReleased(KeyEvent e) {
	        }
	    });*/
		
	    update();
	    
	    scaleUpdate();
	    
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
	
	public String getTeam(Champion c) {
		
		if(game.getFirstPlayer().getTeam().contains(c)) {
			return game.getFirstPlayer().getName();
		}
		if(game.getSecondPlayer().getTeam().contains(c)) {
			return game.getSecondPlayer().getName();
		}
		return null;
		
	}
	
	public void update(){
		
		if(game.getFirstPlayer().getTeam().size()>0)
		hb11.setValue(game.getFirstPlayer().getTeam().get(0).getCurrentHP()*100/game.getFirstPlayer().getTeam().get(0).getMaxHP());
		else hb11.setVisible(false);
		if(game.getFirstPlayer().getTeam().size()>1)
		hb12.setValue(game.getFirstPlayer().getTeam().get(1).getCurrentHP()*100/game.getFirstPlayer().getTeam().get(1).getMaxHP());
		else hb12.setVisible(false);
		if(game.getFirstPlayer().getTeam().size()>2)
		hb13.setValue(game.getFirstPlayer().getTeam().get(2).getCurrentHP()*100/game.getFirstPlayer().getTeam().get(2).getMaxHP());
		else hb13.setVisible(false);
		if(game.getSecondPlayer().getTeam().size()>0)
		hb21.setValue(game.getSecondPlayer().getTeam().get(0).getCurrentHP()*100/game.getSecondPlayer().getTeam().get(0).getMaxHP());
		else hb21.setVisible(false);
		if(game.getSecondPlayer().getTeam().size()>1)
		hb22.setValue(game.getSecondPlayer().getTeam().get(1).getCurrentHP()*100/game.getSecondPlayer().getTeam().get(1).getMaxHP());
		else hb22.setVisible(false);
		if(game.getSecondPlayer().getTeam().size()>2)
		hb23.setValue(game.getSecondPlayer().getTeam().get(2).getCurrentHP()*100/game.getSecondPlayer().getTeam().get(2).getMaxHP());
		else hb23.setVisible(false);
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			
			for(int i=0 ; i<game.getFirstPlayer().getTeam().size() ; i++) {
				if(game.getFirstPlayer().getTeam().get(i).getCurrentHP()<=0.1) {
					targets.add(game.getFirstPlayer().getTeam().get(i));
				}
			}
			for(int i=0 ; i<game.getSecondPlayer().getTeam().size() ; i++) {
				if(game.getSecondPlayer().getTeam().get(i).getCurrentHP()<=0.1) {
					targets.add(game.getSecondPlayer().getTeam().get(i));
				}
			}
			game.cleanup(targets);
			
		}
		
		p1n.setText("Player One: " + game.getFirstPlayer().getName());
		p2n.setText("Player Two: " + game.getSecondPlayer().getName());
		
		if(game.getCurrentChampion()!=null) {
			if(game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())) {
				pc.setText("Current Player: " +game.getFirstPlayer().getName() + "(AP Remaining: " + game.getCurrentChampion().getCurrentActionPoints() + ")");
				pc.setForeground(new java.awt.Color(150,255,255));
				cur.setText("Current Champion: " + game.getCurrentChampion().getName());
				cur.setForeground(new java.awt.Color(150,255,255));
			}else if(game.getSecondPlayer().getTeam().contains(game.getCurrentChampion())) {
				pc.setText("Current Player: " +game.getSecondPlayer().getName() + "(AP Remaining: " + game.getCurrentChampion().getCurrentActionPoints() + ")");
				pc.setForeground(new java.awt.Color(255,150,150));
				cur.setText("Current Champion: " + game.getCurrentChampion().getName());
				cur.setForeground(new java.awt.Color(255,150,150));
			}
		}
		
		if(direction == Direction.UP) {
			dir.setText("UP");
		} else if(direction == Direction.DOWN) {
			dir.setText("DOWN");
		} else if(direction == Direction.RIGHT) {
			dir.setText("RIGHT");
		} else if(direction == Direction.LEFT) {
			dir.setText("LEFT");
		} else {
			dir.setText("NONE");
		}
		
		if(direction == Direction.UP) {
			dUp.setEnabled(false);
		} else {
			dUp.setEnabled(true);
		}
		if(direction == Direction.DOWN) {
			dDown.setEnabled(false);
		} else {
			dDown.setEnabled(true);
		}
		if(direction == Direction.RIGHT) {
			dRight.setEnabled(false);
		} else {
			dRight.setEnabled(true);
		}
		if(direction == Direction.LEFT) {
			dLeft.setEnabled(false);
		} else {
			dLeft.setEnabled(true);
		}
		
		for(int i=0; i<5 ; i++) {
			for(int j=0; j<5 ; j++) {
				if(game.getBoard()[i][j]==null) {
					texts[i][j].setText("");
					images[i][j].setIcon(null);
					buttons[i][j].setVisible(false);
				} else if(game.getBoard()[i][j] instanceof Cover) {
					texts[i][j].setText("HP: \n" + ((Cover)(game.getBoard()[i][j])).getCurrentHP());
					images[i][j].setIcon(new ImageIcon(getClass().getResource("/resources/metal box 3.png")));
					buttons[i][j].setVisible(true);
				} else if(game.getBoard()[i][j] instanceof Champion) {
					texts[i][j].setText(((Champion)(game.getBoard()[i][j])).getName() + " (Team: " + getTeam(((Champion)(game.getBoard()[i][j]))) + ")\nLeader: " + isLeader(((Champion)(game.getBoard()[i][j]))) + " (Type: " + getType(((Champion)(game.getBoard()[i][j]))) + ")\nHP: " + ((Champion)(game.getBoard()[i][j])).getCurrentHP() + "/" + ((Champion)(game.getBoard()[i][j])).getMaxHP() + " (" +(((((Champion)(game.getBoard()[i][j])).getCurrentHP()*100)/(((Champion)(game.getBoard()[i][j])).getMaxHP()))) + "%)" + "\nMana: " + ((Champion)(game.getBoard()[i][j])).getMana() + " \nAP: " + ((Champion)(game.getBoard()[i][j])).getCurrentActionPoints() + "/" + ((Champion)(game.getBoard()[i][j])).getMaxActionPointsPerTurn() +  " \nSpeed: " + ((Champion)(game.getBoard()[i][j])).getSpeed() + " \nAR: " + ((Champion)(game.getBoard()[i][j])).getAttackRange() + " \nAD: " + ((Champion)(game.getBoard()[i][j])).getAttackDamage());
					images[i][j].setIcon(new ImageIcon(getClass().getResource("/resources/" + ((Champion)(game.getBoard()[i][j])).getName() + ".png")));
					buttons[i][j].setVisible(true);
				}
			}
		}
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0){
			a1u.setText("Cast Ability (AP:" + game.getCurrentChampion().getAbilities().get(0).getRequiredActionPoints() + ")");
			a2u.setText("Cast Ability (AP:" + game.getCurrentChampion().getAbilities().get(1).getRequiredActionPoints() + ")");
			a3u.setText("Cast Ability (AP:" + game.getCurrentChampion().getAbilities().get(2).getRequiredActionPoints() + ")");
		}
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			a1.setText("" + game.getCurrentChampion().getAbilities().get(0).getName() + "   " + getAbilityType(game.getCurrentChampion().getAbilities().get(0)) + "\nAOE: " + game.getCurrentChampion().getAbilities().get(0).getCastArea() + (game.getCurrentChampion().getAbilities().get(0).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||game.getCurrentChampion().getAbilities().get(0).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  game.getCurrentChampion().getAbilities().get(0).getCastRange()) + "\nMana Cost: " + game.getCurrentChampion().getAbilities().get(0).getManaCost() + "\nCooldown: " + game.getCurrentChampion().getAbilities().get(0).getCurrentCooldown() + "/" + game.getCurrentChampion().getAbilities().get(0).getBaseCooldown() + "\n" + getAbilityAE(game.getCurrentChampion().getAbilities().get(0)) );
			a2.setText("" + game.getCurrentChampion().getAbilities().get(1).getName() + "   " + getAbilityType(game.getCurrentChampion().getAbilities().get(1)) + "\nAOE: " + game.getCurrentChampion().getAbilities().get(1).getCastArea() + (game.getCurrentChampion().getAbilities().get(1).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||game.getCurrentChampion().getAbilities().get(1).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  game.getCurrentChampion().getAbilities().get(1).getCastRange()) + "\nMana Cost: " + game.getCurrentChampion().getAbilities().get(1).getManaCost() + "\nCooldown: " + game.getCurrentChampion().getAbilities().get(1).getCurrentCooldown() + "/" + game.getCurrentChampion().getAbilities().get(1).getBaseCooldown() + "\n" + getAbilityAE(game.getCurrentChampion().getAbilities().get(1)) );
			a3.setText("" + game.getCurrentChampion().getAbilities().get(2).getName() + "   " + getAbilityType(game.getCurrentChampion().getAbilities().get(2)) + "\nAOE: " + game.getCurrentChampion().getAbilities().get(2).getCastArea() + (game.getCurrentChampion().getAbilities().get(2).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||game.getCurrentChampion().getAbilities().get(2).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  game.getCurrentChampion().getAbilities().get(2).getCastRange()) + "\nMana Cost: " + game.getCurrentChampion().getAbilities().get(2).getManaCost() + "\nCooldown: " + game.getCurrentChampion().getAbilities().get(2).getCurrentCooldown() + "/" + game.getCurrentChampion().getAbilities().get(2).getBaseCooldown() + "\n" + getAbilityAE(game.getCurrentChampion().getAbilities().get(2)) );
		}
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			if(game.getCurrentChampion().getAbilities().size()==4) {
				a4u.setVisible(true);
				a4u.setText("Cast Ability (AP:" + game.getCurrentChampion().getAbilities().get(3).getRequiredActionPoints() + ")");
				a4.setText("" + game.getCurrentChampion().getAbilities().get(3).getName() + "   " + getAbilityType(game.getCurrentChampion().getAbilities().get(3)) + "\nAOE: " + game.getCurrentChampion().getAbilities().get(3).getCastArea() + (game.getCurrentChampion().getAbilities().get(3).getCastArea().toString()==AreaOfEffect.SELFTARGET.toString()||game.getCurrentChampion().getAbilities().get(3).getCastArea().toString()==AreaOfEffect.SURROUND.toString()?"":"   Cast Range: " +  game.getCurrentChampion().getAbilities().get(3).getCastRange()) + "\nMana Cost: " + game.getCurrentChampion().getAbilities().get(3).getManaCost() + "\nCooldown: " + game.getCurrentChampion().getAbilities().get(3).getCurrentCooldown() + "/" + game.getCurrentChampion().getAbilities().get(3).getBaseCooldown() + "\n" + getAbilityAE(game.getCurrentChampion().getAbilities().get(3)) );
				a4u.setEnabled(true);
			}
			else {
				a4u.setVisible(false);
				a4u.setEnabled(false);
				a4u.setText("Cast Ability");
				a4.setText("");
			}
		} 
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			
			if(game.getFirstPlayer().getTeam().size()>0) {
				e11n.setText(game.getFirstPlayer().getTeam().get(0).getName() + "");
				e11s="";
				for(int i=0;i<game.getFirstPlayer().getTeam().get(0).getAppliedEffects().size();i++) {
					e11s=e11s + game.getFirstPlayer().getTeam().get(0).getAppliedEffects().get(i).getName() + "(" + game.getFirstPlayer().getTeam().get(0).getAppliedEffects().get(i).getDuration() + " turns)" +"\n";
				}
				e11.setText(e11s);
			} else {
				e11n.setText("");
				e11s="";
				e11.setText(e11s);
			}
			
			if(game.getFirstPlayer().getTeam().size()>1) {
				e12n.setText(game.getFirstPlayer().getTeam().get(1).getName() + "");
				e12s="";
				for(int i=0;i<game.getFirstPlayer().getTeam().get(1).getAppliedEffects().size();i++) {
					e12s=e12s + game.getFirstPlayer().getTeam().get(1).getAppliedEffects().get(i).getName() + "(" + game.getFirstPlayer().getTeam().get(1).getAppliedEffects().get(i).getDuration() + " turns)" +"\n";
				}
				e12.setText(e12s);
			} else {
				e12n.setText("");
				e12s="";
				e12.setText(e12s);
			}
			
			if(game.getFirstPlayer().getTeam().size()>2) {
				e13n.setText(game.getFirstPlayer().getTeam().get(2).getName() + "");
				e13s="";
				for(int i=0;i<game.getFirstPlayer().getTeam().get(2).getAppliedEffects().size();i++) {
					e13s=e13s + game.getFirstPlayer().getTeam().get(2).getAppliedEffects().get(i).getName() + "(" + game.getFirstPlayer().getTeam().get(2).getAppliedEffects().get(i).getDuration() + " turns)" +"\n";
				}
				e13.setText(e13s);
			} else {
				e13n.setText("");
				e13s="";
				e13.setText(e13s);
			}
			
			if(game.getSecondPlayer().getTeam().size()>0) {
				e21n.setText(game.getSecondPlayer().getTeam().get(0).getName() + "");
				e21s="";
				for(int i=0;i<game.getSecondPlayer().getTeam().get(0).getAppliedEffects().size();i++) {
					e21s=e21s + game.getSecondPlayer().getTeam().get(0).getAppliedEffects().get(i).getName() + "(" + game.getSecondPlayer().getTeam().get(0).getAppliedEffects().get(i).getDuration() + " turns)" +"\n";
				}
				e21.setText(e21s);
			} else {
				e21n.setText("");
				e21s="";
				e21.setText(e21s);
			}
			
			if(game.getSecondPlayer().getTeam().size()>1) {
				e22n.setText(game.getSecondPlayer().getTeam().get(1).getName() + "");
				e22s="";
				for(int i=0;i<game.getSecondPlayer().getTeam().get(1).getAppliedEffects().size();i++) {
					e22s=e22s + game.getSecondPlayer().getTeam().get(1).getAppliedEffects().get(i).getName() + "(" + game.getSecondPlayer().getTeam().get(1).getAppliedEffects().get(i).getDuration() + " turns)" +"\n";
				}
				e22.setText(e22s);
			} else {
				e22n.setText("");
				e22s="";
				e22.setText(e22s);
			}
			
			if(game.getSecondPlayer().getTeam().size()>2) {
				e23n.setText(game.getSecondPlayer().getTeam().get(2).getName() + "");
				e23s="";
				for(int i=0;i<game.getSecondPlayer().getTeam().get(2).getAppliedEffects().size();i++) {
					e23s=e23s + game.getSecondPlayer().getTeam().get(2).getAppliedEffects().get(i).getName() + "(" + game.getSecondPlayer().getTeam().get(2).getAppliedEffects().get(i).getDuration() + " turns)" +"\n";
				}
				e23.setText(e23s);
			} else {
				e23n.setText("");
				e23s="";
				e23.setText(e23s);
			}
			
		}
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			if(game.getCurrentChampion().equals(game.getFirstPlayer().getLeader()) || (game.getCurrentChampion().equals(game.getSecondPlayer().getLeader()))){
				if(getType(game.getCurrentChampion())=="Hero") {
					lads="Removes all negative effects from the \nplayer's entire team and adds an Embrace \neffect to them which lasts for 2 turns.";
				}else if(getType(game.getCurrentChampion())=="Villain") {
					lads="Immediately eliminates (knocks out) \nall enemy champions with less than \n30% health points.";
				} else if(getType(game.getCurrentChampion())=="AntiHero") {
					lads="All champions on the board except for the \nleaders of each team will be stunned for \n2 turns.";
				}
			}else {
				lads="";
			}
			lad.setText(lads);
		}
		
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			
			if(game.getCurrentChampion().getCurrentActionPoints()<1) {
				move.setEnabled(false);
			}else {
				move.setEnabled(true);
			}
			
			if(game.getCurrentChampion().getCurrentActionPoints()<2) {
				attack.setEnabled(false);
			}else {
				attack.setEnabled(true);
			}
			
			if(game.getCurrentChampion().getCurrentActionPoints()<game.getCurrentChampion().getAbilities().get(0).getRequiredActionPoints() || game.getCurrentChampion().getAbilities().get(0).getCurrentCooldown()!=0) {
				a1u.setEnabled(false);
			}else {
				a1u.setEnabled(true);
			}
			
			if(game.getCurrentChampion().getCurrentActionPoints()<game.getCurrentChampion().getAbilities().get(1).getRequiredActionPoints() || game.getCurrentChampion().getAbilities().get(1).getCurrentCooldown()!=0) {
				a2u.setEnabled(false);
			}else {
				a2u.setEnabled(true);
			}
			
			if(game.getCurrentChampion().getCurrentActionPoints()<game.getCurrentChampion().getAbilities().get(2).getRequiredActionPoints() || game.getCurrentChampion().getAbilities().get(2).getCurrentCooldown()!=0) {
				a3u.setEnabled(false);
			}else {
				a3u.setEnabled(true);
			}
			
			if(game.getCurrentChampion().getAbilities().size()>3) {
				if(game.getCurrentChampion().getCurrentActionPoints()<game.getCurrentChampion().getAbilities().get(3).getRequiredActionPoints() || game.getCurrentChampion().getAbilities().get(3).getCurrentCooldown()!=0) {
					a4u.setEnabled(false);
				}else {
					a4u.setEnabled(true);
				}			
			}
			
			if(p1.getLeader()==game.getCurrentChampion()) {
				if(!game.isFirstLeaderAbilityUsed()) {
					alu.setEnabled(true);
				}else {
					alu.setEnabled(false);
				}
			} else {
				if(p2.getLeader()==game.getCurrentChampion()) {
					if(!game.isSecondLeaderAbilityUsed()) {
						alu.setEnabled(true);
					}else {
						alu.setEnabled(false);
					}
				} else {
					alu.setEnabled(false);
				}
			}
		}
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			if(game.getTurnOrder().size()>0)
				
				this.arr[0]=null;
				this.arr[1]=null;
				this.arr[2]=null;
				this.arr[3]=null;
				this.arr[4]=null;
				this.arr[5]=null;
				ArrayList<Champion> temp = new ArrayList<Champion>();
				ArrayList<Champion> temp2 = new ArrayList<Champion>();
				while(!(game.getTurnOrder().isEmpty())) {
					temp.add((Champion)game.getTurnOrder().remove());
				}
				for(int i=0;i<temp.size();i++) {
					game.getTurnOrder().insert(temp.get(i));
				}
				for(int i=0;i<temp.size();i++) {
					if(temp.get(i).getCondition()!=Condition.INACTIVE)
						temp2.add(temp.get(i));
				}
				if(temp2.size()>0) {
					for(int i=0;i<temp2.size();i++) {
						if(temp2.get(i)!=null)
							this.arr[i]=(Champion) temp2.get(i);
						else 
							this.arr[i]=null;
					}
				}
			
			if(temp2.size()==6) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO4.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[3].getName() + " 2.png")));
				TO5.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[4].getName() + " 2.png")));
				TO6.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[5].getName() + " 2.png")));
			}
			if(temp2.size()==5) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO4.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[3].getName() + " 2.png")));
				TO5.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[4].getName() + " 2.png")));
				TO6.setIcon(null);
			}
			if(temp2.size()==4) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO4.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[3].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
			}
			if(temp2.size()==3) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO3.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[2].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
				TO4.setIcon(null);
			}
			if(temp2.size()==2) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO2.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[1].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
				TO4.setIcon(null);
				TO3.setIcon(null);
				
			}
			if(temp2.size()==1) {
				TO1.setIcon(new ImageIcon(getClass().getResource("/resources/" + arr[0].getName() + " 2.png")));
				TO6.setIcon(null);
				TO5.setIcon(null);
				TO4.setIcon(null);
				TO3.setIcon(null);
				TO2.setIcon(null);
			}
		}
		
		if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
			if(game.getBoard()[x][y] instanceof Cover) {
				st.setText("Selected Target: Cover");
			} else if(game.getBoard()[x][y] instanceof Champion) {
				st.setText("Selected Target: " + ((Champion)game.getBoard()[x][y]).getName());
			} else if(game.getBoard()[x][y] == null) {
				st.setText("Selected Target: null");
			}
			
			if(game.getBoard()[x][y] instanceof Cover) {
				st2.setIcon(new ImageIcon(getClass().getResource("/resources/mb3 2.png")));
				
				BufferedImage img_st = null;
				
				try {
				    img_st = ImageIO.read(getClass().getResource("/resources/mb3 2.png"));
				} catch (IOException e) {
				    e.printStackTrace();
				}
				
				Image dimg_st = img_st.getScaledInstance((int)(st2.getIcon().getIconWidth()*Game.getScale()),((int)(st2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				
				ImageIcon imageIcon_st = new ImageIcon(dimg_st);
				
				st2.setIcon(imageIcon_st);
				
			} else if(game.getBoard()[x][y] instanceof Champion) {
				st2.setIcon(new ImageIcon(getClass().getResource("/resources/" + ((Champion)game.getBoard()[x][y]).getName() + " 2.png")));
				BufferedImage img_st = null;
				
				try {
				    img_st = ImageIO.read(getClass().getResource("/resources/" + ((Champion)game.getBoard()[x][y]).getName() + " 2.png"));
				} catch (IOException e) {
				    e.printStackTrace();
				}
				
				Image dimg_st = img_st.getScaledInstance((int)(st2.getIcon().getIconWidth()*Game.getScale()),((int)(st2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				
				ImageIcon imageIcon_st = new ImageIcon(dimg_st);
				
				st2.setIcon(imageIcon_st);
			} else if(game.getBoard()[x][y] == null) {
				st2.setIcon(null);
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
		
		scaleUpdate();
		
		imageUpdate();
		
		repaint();
		revalidate();
		
		if(game.checkGameOver()!=null){
			JOptionPane.showMessageDialog(this, "Winner: " + game.checkGameOver().getName() , "Game Over", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
			//System.exit(0);
			/*try {
				mainMenu m = new mainMenu();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			frames[0].setVisible(true);
		}
	
	}
	
	public void imageUpdate() {
		
		for(int i=4;i>-1;i--){
			for(int j=0;j<5;j++){

				buttons[4-j][4-i].setBounds((int) ((330+(4-i)*160)*Game.getScale()),(int) ((90+(j)*130)*Game.getScale()),(int) (30*Game.getScale()),(int) (30*Game.getScale()));

				images[4-j][4-i].setBounds((int) ((315+(4-i)*160)*Game.getScale()),(int) ((40+(j)*130)*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
				
				if(game.getBoard()[i][j]==null) {
					images[i][j].setIcon(null);
				} else if(game.getBoard()[i][j] instanceof Cover) {
					
					BufferedImage img_temp = null;
					
					try {
					    img_temp = ImageIO.read(getClass().getResource("/resources/metal box 3.png"));
					} catch (IOException e) {
					    e.printStackTrace();
					}
					
					Image dimg_temp = img_temp.getScaledInstance((int)(images[i][j].getIcon().getIconWidth()*Game.getScale()),((int)(images[i][j].getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
					
					ImageIcon imageIcon_temp = new ImageIcon(dimg_temp);
					
					images[i][j].setIcon(imageIcon_temp);
					
				} else if(game.getBoard()[i][j] instanceof Champion) {
				
					BufferedImage img_temp = null;
					
					try {
					    img_temp = ImageIO.read(getClass().getResource("/resources/" + ((Champion)(game.getBoard()[i][j])).getName() + ".png"));
					} catch (IOException e) {
					    e.printStackTrace();
					}
					
					Image dimg_temp = img_temp.getScaledInstance((int)(images[i][j].getIcon().getIconWidth()*Game.getScale()),((int)(images[i][j].getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
					
					ImageIcon imageIcon_temp = new ImageIcon(dimg_temp);
					
					images[i][j].setIcon(imageIcon_temp);
				}
				
				texts[4-j][4-i].setBounds((int) ((255+(4-i)*160)*Game.getScale()),(int) ((5+(j)*130)*Game.getScale()),(int) (155*Game.getScale()),(int) (130*Game.getScale()));
				texts[4-j][4-i].setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
			}
		}
		
		BufferedImage img_1 = null;
		BufferedImage img_2 = null;
		BufferedImage img_3 = null;
		BufferedImage img_4 = null;
		BufferedImage img_5 = null;
		BufferedImage img_6 = null;
		
		try {
			
		    img_1 = null;
		    if(game.getTurnOrder().size()>0) {
		    	img_1 = ImageIO.read(getClass().getResource("/resources/" + arr[0].getName() + " 2.png"));
				Image dimg_1 = img_1.getScaledInstance((int)(TO1.getIcon().getIconWidth()*Game.getScale()),((int)(TO1.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				ImageIcon imageIcon_1 = new ImageIcon(dimg_1);
				TO1.setIcon(imageIcon_1);

		    }
		    img_2 = null;
		    if(game.getTurnOrder().size()>1) {
		    	img_2 = ImageIO.read(getClass().getResource("/resources/" + arr[1].getName() + " 2.png"));
				Image dimg_2 = img_2.getScaledInstance((int)(TO2.getIcon().getIconWidth()*Game.getScale()),((int)(TO2.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				ImageIcon imageIcon_2 = new ImageIcon(dimg_2);
				TO2.setIcon(imageIcon_2);

		    }
		    img_3 = null;
		    if(game.getTurnOrder().size()>2) {
		    	img_3 = ImageIO.read(getClass().getResource("/resources/" + arr[2].getName() + " 2.png"));
				Image dimg_3 = img_3.getScaledInstance((int)(TO3.getIcon().getIconWidth()*Game.getScale()),((int)(TO3.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				ImageIcon imageIcon_3 = new ImageIcon(dimg_3);
				TO3.setIcon(imageIcon_3);

		    }
		    img_4 = null;
		    if(game.getTurnOrder().size()>3) {
		    	img_4 = ImageIO.read(getClass().getResource("/resources/" + arr[3].getName() + " 2.png"));
				Image dimg_4 = img_4.getScaledInstance((int)(TO4.getIcon().getIconWidth()*Game.getScale()),((int)(TO4.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				ImageIcon imageIcon_4 = new ImageIcon(dimg_4);
				TO4.setIcon(imageIcon_4);

		    }
		    img_5 = null;
		    if(game.getTurnOrder().size()>4) {
		    	img_5 = ImageIO.read(getClass().getResource("/resources/" + arr[4].getName() + " 2.png"));
				Image dimg_5 = img_5.getScaledInstance((int)(TO5.getIcon().getIconWidth()*Game.getScale()),((int)(TO5.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				ImageIcon imageIcon_5 = new ImageIcon(dimg_5);
				TO5.setIcon(imageIcon_5);

		    }
		    img_6 = null;
		    if(game.getTurnOrder().size()>5) {
		    	img_6 = ImageIO.read(getClass().getResource("/resources/" + arr[5].getName() + " 2.png"));
				Image dimg_6 = img_6.getScaledInstance((int)(TO6.getIcon().getIconWidth()*Game.getScale()),((int)(TO6.getIcon().getIconHeight()*Game.getScale())),Image.SCALE_DEFAULT);
				ImageIcon imageIcon_6 = new ImageIcon(dimg_6);
				TO6.setIcon(imageIcon_6);

		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		BufferedImage img_bg = null;
		
		try {
		    img_bg = ImageIO.read(getClass().getResource("/resources/bg.png"));
		} catch (IOException e) {
		    e.printStackTrace();
		}

		Image dimg_bg = img_bg.getScaledInstance((int)(800*Game.getScale()),((int)(650*Game.getScale())),Image.SCALE_DEFAULT);
		
		ImageIcon imageIcon_bg = new ImageIcon(dimg_bg);
		
		background.setIcon(imageIcon_bg);
		
	}
	
	public void scaleUpdate() {
		
		arrow.setBounds((int) (515*Game.getScale()),(int) (700*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		arrow.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		TO1.setBounds((int) (500*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		TO2.setBounds((int) (550*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		TO3.setBounds((int) (600*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		TO4.setBounds((int) (650*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		TO5.setBounds((int) (700*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		TO6.setBounds((int) (750*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));
		
		e11n.setBounds((int) (10*Game.getScale()),(int) (25*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		e11n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		e12n.setBounds((int) (10*Game.getScale()),(int) (120*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		e12n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		e13n.setBounds((int) (10*Game.getScale()),(int) (215*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		e13n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		e21n.setBounds((int) (10*Game.getScale()),(int) (330*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		e21n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		e22n.setBounds((int) (10*Game.getScale()),(int) (425*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		e22n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		e23n.setBounds((int) (10*Game.getScale()),(int) (520*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		e23n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		e11.setBounds((int) (10*Game.getScale()),(int) (45*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		e11.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		e12.setBounds((int) (10*Game.getScale()),(int) (140*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		e12.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		e13.setBounds((int) (10*Game.getScale()),(int) (235*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		e13.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		e21.setBounds((int) (10*Game.getScale()),(int) (350*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		e21.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		e22.setBounds((int) (10*Game.getScale()),(int) (445*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		e22.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		e23.setBounds((int) (10*Game.getScale()),(int) (540*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		e23.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		dUp.setBounds((int) (1100*Game.getScale()),(int) (485*Game.getScale()),(int) (30*Game.getScale()),(int) (30*Game.getScale()));
		
		dDown.setBounds((int) (1100*Game.getScale()),(int) (565*Game.getScale()),(int) (30*Game.getScale()),(int) (30*Game.getScale()));
		
		dLeft.setBounds((int) (1060*Game.getScale()),(int) (525*Game.getScale()),(int) (30*Game.getScale()),(int) (30*Game.getScale()));
		
		dRight.setBounds((int) (1140*Game.getScale()),(int) (525*Game.getScale()),(int) (30*Game.getScale()),(int) (30*Game.getScale()));

		dir.setBounds((int) (1090*Game.getScale()),(int) (530*Game.getScale()),(int) (50*Game.getScale()),(int) (20*Game.getScale()));
		dir.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		move.setBounds((int) (1150*Game.getScale()),(int) (485*Game.getScale()),(int) (120*Game.getScale()),(int) (30*Game.getScale()));
		move.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		attack.setBounds((int) (1150*Game.getScale()),(int) (565*Game.getScale()),(int) (120*Game.getScale()),(int) (30*Game.getScale()));
		attack.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		info.setBounds((int) (1075*Game.getScale()),(int) (675*Game.getScale()),(int) (60*Game.getScale()),(int) (30*Game.getScale()));
		info.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));

		p1n.setBounds((int) (10*Game.getScale()),(int) (5*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		p1n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		p2n.setBounds((int) (10*Game.getScale()),(int) (310*Game.getScale()),(int) (200*Game.getScale()),(int) (20*Game.getScale()));
		p2n.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		pc.setBounds((int) (800*Game.getScale()),(int) (650*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		pc.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		cur.setBounds((int) (500*Game.getScale()),(int) (650*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		cur.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		st.setBounds((int) (250*Game.getScale()),(int) (650*Game.getScale()),(int) (300*Game.getScale()),(int) (20*Game.getScale()));
		st.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		st2.setBounds((int) (300*Game.getScale()),(int) (635*Game.getScale()),(int) (100*Game.getScale()),(int) (100*Game.getScale()));

		a1.setBounds((int) (1075*Game.getScale()),(int) (50*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		a1.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));

		a1u.setBounds((int) (1075*Game.getScale()),(int) (125*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		a1u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		a2.setBounds((int) (1075*Game.getScale()),(int) (155*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		a2.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		a2u.setBounds((int) (1075*Game.getScale()),(int) (230*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		a2u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		a3.setBounds((int) (1075*Game.getScale()),(int) (260*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		a3.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		a3u.setBounds((int) (1075*Game.getScale()),(int) (335*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		a3u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		lad.setBounds((int) (1075*Game.getScale()),(int) (390*Game.getScale()),(int) (200*Game.getScale()),(int) (50*Game.getScale()));
		lad.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		alu.setBounds((int) (1075*Game.getScale()),(int) (440*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		alu.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		a4.setBounds((int) (10*Game.getScale()),(int) (615*Game.getScale()),(int) (200*Game.getScale()),(int) (75*Game.getScale()));
		a4.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (10*Game.getScale())));
		
		a4u.setBounds((int) (10*Game.getScale()),(int) (690*Game.getScale()),(int) (200*Game.getScale()),(int) (30*Game.getScale()));
		a4u.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		exit.setBounds((int) (1150*Game.getScale()),(int) (675*Game.getScale()),(int) (100*Game.getScale()),(int) (30*Game.getScale()));
		exit.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		endTurn.setBounds((int) (1150*Game.getScale()),(int) (625*Game.getScale()),(int) (100*Game.getScale()),(int) (30*Game.getScale()));
		endTurn.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		background.setBounds((int) (250*Game.getScale()),(int) (5*Game.getScale()),(int) (800*Game.getScale()),(int) (650*Game.getScale()));
		
		fsb.setBounds((int) (1210*Game.getScale()),0,(int) (75*Game.getScale()),(int) (30*Game.getScale()));
		fsb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		sclb.setBounds((int) (1120*Game.getScale()),0,(int) (90*Game.getScale()),(int) (30*Game.getScale()));
		sclb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		rst.setBounds((int) (1050*Game.getScale()),0,(int) (70*Game.getScale()),(int) (30*Game.getScale()));
		rst.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));

		hb11.setBounds((int) (125*Game.getScale()), (int) (25*Game.getScale()), (int) (100*Game.getScale()), (int) (20*Game.getScale()));

		hb12.setBounds((int) (125*Game.getScale()), (int) (120*Game.getScale()), (int) (100*Game.getScale()), (int) (20*Game.getScale()));

		hb13.setBounds((int) (125*Game.getScale()), (int) (215*Game.getScale()), (int) (100*Game.getScale()), (int) (20*Game.getScale()));

		hb21.setBounds((int) (125*Game.getScale()), (int) (330*Game.getScale()), (int) (100*Game.getScale()), (int) (20*Game.getScale()));

		hb22.setBounds((int) (125*Game.getScale()), (int) (425*Game.getScale()), (int) (100*Game.getScale()), (int) (20*Game.getScale()));

		hb23.setBounds((int) (125*Game.getScale()), (int) (520*Game.getScale()), (int) (100*Game.getScale()), (int) (20*Game.getScale()));
		
	}
	
	public boolean isLeader(Champion c) {
		
		if(game.getFirstPlayer().getLeader().equals(c)) {
			return true;
		}
		if(game.getSecondPlayer().getLeader().equals(c)) {
			return true;
		}
		return false;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		URL themesound = (getClass().getResource("/resources/theme.wav"));
		URL hoversound  = (getClass().getResource("/resources/hover.wav"));
		URL selectsound = (getClass().getResource("/resources/select.wav"));
		URL attacksound = (getClass().getResource("/resources/attack.wav"));
		URL castsound = (getClass().getResource("/resources/cast.wav"));
		URL diesound = (getClass().getResource("/resources/die.wav"));
		URL healsound = (getClass().getResource("/resources/heal.wav"));
		URL leadersound = (getClass().getResource("/resources/leader.wav"));
		URL movesound = (getClass().getResource("/resources/move.wav"));
		
		for(int i=4;i>-1;i--){
			for(int j=0;j<5;j++){
				if(e.getSource() == buttons[4-j][4-i]){
					music.playSound(selectsound);
					this.x=4-j;
					this.y=4-i;
				}
			}
		}
		
		if(e.getSource() == dUp){
			music.playSound(selectsound);
			direction = Direction.UP;
		}
		
		if(e.getSource() == dDown){
			music.playSound(selectsound);
			direction = Direction.DOWN;
		}
		
		if(e.getSource() == dRight){
			music.playSound(selectsound);
			direction = Direction.RIGHT;
		}
		
		if(e.getSource() == dLeft){
			music.playSound(selectsound);
			direction = Direction.LEFT;
		}
		
		if(e.getSource() == move) {
			if(direction==null) {
				JOptionPane.showMessageDialog(this, "Please Select Direction");
			}else {
				try {
					game.move(direction);
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (UnallowedMovementException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		
		if(e.getSource() == attack) {
			if(direction==null) {
				JOptionPane.showMessageDialog(this, "Please Select Direction");
			}else {
				try {
					music.playSound(attacksound);
					game.attack(direction);
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (ChampionDisarmedException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (InvalidTargetException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		
		if(e.getSource() == a1u){
			if(game.getCurrentChampion().getAbilities().get(0).getCastArea()==AreaOfEffect.SELFTARGET || game.getCurrentChampion().getAbilities().get(0).getCastArea()==AreaOfEffect.TEAMTARGET || game.getCurrentChampion().getAbilities().get(0).getCastArea()==AreaOfEffect.SURROUND) {
				try {
					game.castAbility(game.getCurrentChampion().getAbilities().get(0));
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (AbilityUseException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
			if(game.getCurrentChampion().getAbilities().get(0).getCastArea()==AreaOfEffect.SINGLETARGET) {
					try {
						try {
							game.castAbility(game.getCurrentChampion().getAbilities().get(0),x,y);

						} catch (InvalidTargetException e1) {
							// TODO Auto-generated catch block
							music.playSound(selectsound);
							JOptionPane.showMessageDialog(this, "" + e1.getMessage());
							e1.printStackTrace();
						}
					} catch (NotEnoughResourcesException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					} catch (AbilityUseException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					}
				}
			if(game.getCurrentChampion().getAbilities().get(0).getCastArea()==AreaOfEffect.DIRECTIONAL) {
				try {
					if(direction==null) {
						JOptionPane.showMessageDialog(this, "Please Select Direction" );
					}else {
						game.castAbility(game.getCurrentChampion().getAbilities().get(0),direction);
						direction=null;
					}
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (AbilityUseException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		
		if(e.getSource() == a2u){
			if(game.getCurrentChampion().getAbilities().get(1).getCastArea()==AreaOfEffect.SELFTARGET || game.getCurrentChampion().getAbilities().get(1).getCastArea()==AreaOfEffect.TEAMTARGET || game.getCurrentChampion().getAbilities().get(1).getCastArea()==AreaOfEffect.SURROUND) {
				try {
					game.castAbility(game.getCurrentChampion().getAbilities().get(1));
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (AbilityUseException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
			if(game.getCurrentChampion().getAbilities().get(1).getCastArea()==AreaOfEffect.SINGLETARGET) {
					try {
						try {
							game.castAbility(game.getCurrentChampion().getAbilities().get(1),x,y);
						} catch (InvalidTargetException e1) {
							// TODO Auto-generated catch block
							music.playSound(selectsound);
							JOptionPane.showMessageDialog(this, "" + e1.getMessage());
							e1.printStackTrace();
						}
					} catch (NotEnoughResourcesException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					} catch (AbilityUseException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					}
				}
			if(game.getCurrentChampion().getAbilities().get(1).getCastArea()==AreaOfEffect.DIRECTIONAL) {
				try {
					if(direction==null) {
						JOptionPane.showMessageDialog(this, "Please Select Direction" );
					} else {
						game.castAbility(game.getCurrentChampion().getAbilities().get(1),direction);
						direction=null;
					}
				} catch (NotEnoughResourcesException e1) {
					music.playSound(selectsound);
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (AbilityUseException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		
		if(e.getSource() == a3u){
			if(game.getCurrentChampion().getAbilities().get(2).getCastArea()==AreaOfEffect.SELFTARGET || game.getCurrentChampion().getAbilities().get(2).getCastArea()==AreaOfEffect.TEAMTARGET || game.getCurrentChampion().getAbilities().get(2).getCastArea()==AreaOfEffect.SURROUND) {
				try {
					game.castAbility(game.getCurrentChampion().getAbilities().get(2));
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (AbilityUseException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
			if(game.getCurrentChampion().getAbilities().get(2).getCastArea()==AreaOfEffect.SINGLETARGET) {

					try {
						try {
							game.castAbility(game.getCurrentChampion().getAbilities().get(2),x,y);
						} catch (InvalidTargetException e1) {
							// TODO Auto-generated catch block
							music.playSound(selectsound);
							JOptionPane.showMessageDialog(this, "" + e1.getMessage());
							e1.printStackTrace();
						}
					} catch (NotEnoughResourcesException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					} catch (AbilityUseException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						music.playSound(selectsound);
						JOptionPane.showMessageDialog(this, "" + e1.getMessage());
						e1.printStackTrace();
					}
				}

			if(game.getCurrentChampion().getAbilities().get(2).getCastArea()==AreaOfEffect.DIRECTIONAL) {
				try {
					if(direction==null) {
						JOptionPane.showMessageDialog(this, "Please Select Direction" );
					} else {
						game.castAbility(game.getCurrentChampion().getAbilities().get(2),direction);
						direction=null;
					}
				} catch (NotEnoughResourcesException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (AbilityUseException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		
		if(e.getSource() == a4u){
			try {
				try {
					//System.out.println("" + game.getCurrentChampion().getAbilities().get(3).getName());
					game.castAbility(game.getCurrentChampion().getAbilities().get(3),x,y);
					//game.castAbility(new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50),x,y);
				} catch (InvalidTargetException e1) {
					// TODO Auto-generated catch block
					music.playSound(selectsound);
					JOptionPane.showMessageDialog(this, "" + e1.getMessage());
					e1.printStackTrace();
				}
			} catch (NotEnoughResourcesException e1) {
				// TODO Auto-generated catch block
				music.playSound(selectsound);
				JOptionPane.showMessageDialog(this, "" + e1.getMessage());
				e1.printStackTrace();
			} catch (AbilityUseException e1) {
				// TODO Auto-generated catch block
				music.playSound(selectsound);
				JOptionPane.showMessageDialog(this, "" + e1.getMessage());
				e1.printStackTrace();
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				music.playSound(selectsound);
				JOptionPane.showMessageDialog(this, "" + e1.getMessage());
				e1.printStackTrace();
			}
		}

		if(e.getSource() == endTurn){
			if(game.checkGameOver() == null){
				game.endTurn();	
				direction=null;
				music.playSound(selectsound);
				
				if(game.getFirstPlayer().getTeam().size()>0 || game.getSecondPlayer().getTeam().size()>0) {
					for(int i=0;i<5;i++) {
						for(int j=0;j<5;j++) {
							if(game.getBoard()[i][j]==null) {
								x=i;
								y=j;
								break;
							}
						}
					}
				}
				
				direction=null;
				
				update();
				
				JOptionPane.showMessageDialog(this, (game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())?game.getFirstPlayer().getName():game.getSecondPlayer().getName()) + "'s Turn" + "\nChampion: " + game.getCurrentChampion().getName() , "Next Turn", JOptionPane.INFORMATION_MESSAGE);
				
			}
			else{
				JOptionPane.showMessageDialog(this, "Winner: " + game.checkGameOver().getName() , "Game Over", JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
				//System.exit(0);	
			}
		}
		
		if(e.getSource()==alu) {
			try {
				game.useLeaderAbility();
			} catch (LeaderNotCurrentException e1) {
				// TODO Auto-generated catch block
				music.playSound(selectsound);
				JOptionPane.showMessageDialog(this, "" + e1.getMessage());
				e1.printStackTrace();
			} catch (LeaderAbilityAlreadyUsedException e1) {
				// TODO Auto-generated catch block
				music.playSound(selectsound);
				JOptionPane.showMessageDialog(this, "" + e1.getMessage());
				e1.printStackTrace();
			}
		}
		
		if(e.getSource() == exit){
			music.playSound(selectsound);
			this.dispose();
			System.exit(0);
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
	
	public void scaleSet() {
		if(Game.getScale()==1)
			Game.setScale(1.07); else
		if(Game.getScale()==1.07)
			Game.setScale(1.5); else
		if(Game.getScale()==1.5)
			Game.setScale(2); else
		if(Game.getScale()==2)
			Game.setScale(3); else
		if(Game.getScale()==3)
			Game.setScale(4); else
		if(Game.getScale()==4)
			Game.setScale(1);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[]args) throws IOException{
		
		Game game = new Game(new Player("test1"), new Player("test2"));
		gameView g = new gameView(game ,new Player("test1"), new Player("test2"));
		g.fsb.doClick();

	}

}
