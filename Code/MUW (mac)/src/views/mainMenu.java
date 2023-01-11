package views;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import engine.Game;
import views.mainMenu;

@SuppressWarnings({ "serial", "unused", "deprecation" })
public class mainMenu extends JFrame implements ActionListener{
	
	private JLabel playerOne,playerTwo,background;
	private JTextField playerOneName;
	private JTextField playerTwoName;
	private JButton start;
	
	private JButton exit;
	
	private JButton fsb, sclb, rst;
	
	public mainMenu() throws IOException{
	
		URL themesound = (getClass().getResource("/resources/theme.wav"));
		URL hoversound  = (getClass().getResource("/resources/hover.wav"));
		URL selectsound = (getClass().getResource("/resources/select.wav"));
		music.playTheme(themesound);
			
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setUndecorated(true);

		this.setSize(1920,1080);
		this.setVisible(true);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("Marvel - Ultimate War");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane().setBackground(new java.awt.Color(110, 0, 0));
		
		playerOne = new JLabel("Player One Name:");
		playerOne.setBounds(350,450,150,30);
		playerOne.setForeground(Color.black);
		this.add(playerOne);
		playerOne.setVisible(true);
		playerOne.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 18));
		playerOne.setForeground(new java.awt.Color(100,255,255));
		playerOneName = new JTextField();
		playerOneName.setBounds(350,500,150,30);
		this.add(playerOneName);
		playerOneName.setVisible(true);
		playerOneName.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		
		playerTwo = new JLabel("Player Two Name:");
		playerTwo.setBounds(550,450,150,30);
		playerTwo.setForeground(Color.black);
		this.add(playerTwo);
		playerTwo.setVisible(true);
		playerTwo.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 18));
		playerTwo.setForeground(new java.awt.Color(255,100,100));
		playerTwoName = new JTextField();
		playerTwoName.setBounds(550,500,150,30);
		this.add(playerTwoName);
		playerTwoName.setVisible(true);
		playerTwoName.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
	
		start = new JButton("Start New Game");
		start.setBounds(775,500,150,30);
		start.setOpaque(true);
		start.addActionListener(this);
		this.add(start);
		start.setVisible(true);
		start.setFocusable(false);
		start.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, 14));
		start.setBackground(Color.gray);
		start.setForeground(Color.white);

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
		
		background = new JLabel();
		background.setBounds(0,0,1280,720);
		this.getContentPane().add(background);
		background.setIcon(new ImageIcon(getClass().getResource("/resources/bg_menu.png")));
		//System.out.print(getClass().getResource("/resources/marvel_800_500.png") + "");
		background.setVisible(true);
		add(background, BorderLayout.CENTER);
		
		start.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				music.playSound(hoversound);
			}
		});
		fsb.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				music.playSound(hoversound);
			}
		});
		sclb.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				music.playSound(hoversound);
			}
		});
		rst.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				music.playSound(hoversound);
			}
		});
		exit.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				music.playSound(hoversound);
			}
		});
		
	    this.addKeyListener(new KeyListener() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	        }

	        @Override
	        public void keyPressed(KeyEvent e) {
	        	if(e.getKeyCode()==KeyEvent.VK_ENTER || e.getKeyCode()==KeyEvent.VK_SPACE) {
	        		start.doClick();
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
	    
	    update();
	    
	    scaleUpdate();

		revalidate();
		repaint();
		
	}
	
	public void makeSound(File file){
		
	    try{
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(file));
	        clip.start();
	    } catch (Exception e){
	        e.printStackTrace();
	    }
	    
	}
	
	public static void playTheme(File file){
		
		try{
		Clip theme = AudioSystem.getClip();
        theme.open(AudioSystem.getAudioInputStream(file));
        theme.start();
        theme.loop(Clip.LOOP_CONTINUOUSLY);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void stopTheme(File file) {
		
		try{
		Clip theme = AudioSystem.getClip();
        theme.open(AudioSystem.getAudioInputStream(file));
        theme.stop();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		URL themesound = (getClass().getResource("/resources/theme.wav"));
		URL hoversound  = (getClass().getResource("/resources/hover.wav"));
		URL selectsound = (getClass().getResource("/resources/select.wav"));
		
		if(e.getSource() == start){
			music.playSound(selectsound);
			if(playerOneName.getText().equals("")){
				JOptionPane.showMessageDialog(this, "Please Enter Player One Name");
			}
			else if(playerTwoName.getText().equals("")){
				JOptionPane.showMessageDialog(this, "Please Enter Player Two Name");
			}
			else if(playerOneName.getText().length()>8){
				JOptionPane.showMessageDialog(this, "Please Enter Shorter Name (Player 1)");
			}
			else if(playerTwoName.getText().length()>8){
				JOptionPane.showMessageDialog(this, "Please Enter Shorter Name (Player 2)");				
			}else if (playerOneName.getText().equals(playerTwoName.getText())){
				JOptionPane.showMessageDialog(this, "Players cannot have the same name");
			}
			else{
				this.setVisible(false);
				try {
					this.add(new controller(playerOneName.getText(),playerTwoName.getText()));
					
				} catch (IOException e1) {
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
		
		revalidate();
		repaint();
		
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
	
	public void update() {
	
		Frame[] frames = Frame.getFrames();
    	if(frames[frames.length-1].getExtendedState()!=0) {
	    	fsb.setText("FS:On");
    	} else
    		
    	if(frames[frames.length-1].getExtendedState()==0) {
	    	fsb.setText("FS:Off");
    	}
    	
		sclb.setText("SCL:" + Game.getScale());
		
		imageUpdate();
		
		revalidate();
		repaint();
	}
	
	public void imageUpdate() {
		
		BufferedImage img_bg = null;
		
		try {
		    img_bg = ImageIO.read(getClass().getResource("/resources/bg_menu.png"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		Image dimg_bg = img_bg.getScaledInstance((int)(1280*Game.getScale()),((int)(720*Game.getScale())),Image.SCALE_DEFAULT);
		
		ImageIcon imageIcon_bg = new ImageIcon(dimg_bg);
		
		background.setIcon(imageIcon_bg);
		
	}
	
	public void scaleUpdate() {
				
		playerOne.setBounds((int) (350*Game.getScale()),(int) (450*Game.getScale()),(int) (150*Game.getScale()),(int) (30*Game.getScale()));
		playerOne.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (18*Game.getScale())));

		playerOneName.setBounds((int) (350*Game.getScale()),(int) (500*Game.getScale()),(int) (150*Game.getScale()),(int) (30*Game.getScale()));
		playerOneName.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		playerTwo.setBounds((int) (550*Game.getScale()),(int) (450*Game.getScale()),(int) (150*Game.getScale()),(int) (30*Game.getScale()));
		playerTwo.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (18*Game.getScale())));

		playerTwoName.setBounds((int) (550*Game.getScale()),(int) (500*Game.getScale()),(int) (150*Game.getScale()),(int) (30*Game.getScale()));
		playerTwoName.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
	
		start.setBounds((int) (775*Game.getScale()),(int) (500*Game.getScale()),(int) (150*Game.getScale()),(int) (30*Game.getScale()));
		start.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		exit.setBounds((int) (1150*Game.getScale()),(int) (675*Game.getScale()),(int) (100*Game.getScale()),(int) (30*Game.getScale()));;
		exit.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		fsb.setBounds((int) (1210*Game.getScale()),0,(int) (75*Game.getScale()),(int) (30*Game.getScale()));
		fsb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		sclb.setBounds((int) (1120*Game.getScale()),0,(int) (90*Game.getScale()),(int) (30*Game.getScale()));
		sclb.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		rst.setBounds((int) (1050*Game.getScale()),0,(int) (70*Game.getScale()),(int) (30*Game.getScale()));
		rst.setFont(new Font("Verdana Pro Cond Semibold", Font.PLAIN, (int) (14*Game.getScale())));
		
		background.setBounds(0,0,(int) (1280*Game.getScale()),(int) (720*Game.getScale()));
		
		repaint();
		revalidate();
		
	}
	
	@SuppressWarnings("unused")
	public static void main(String[]args )throws IOException{
		mainMenu m = new mainMenu();
	}

}
