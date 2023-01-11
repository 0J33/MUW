buttons[4-j][4-i].setBounds(330+(4-i)*160,90+(j)*130,30,30);


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


buttons[4-j][4-i].setBounds((int) ((330+(4-i)*160)*Game.getScale()),(int) ((90+(j)*130)*Game.getScale()),(int) (30*Game.getScale()),(int) (30*Game.getScale()));
