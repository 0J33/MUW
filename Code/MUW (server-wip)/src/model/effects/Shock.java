package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
		
	}

	@Override
	public void apply(Champion c) {
		c.setSpeed((int) (c.getSpeed()*0.9));
		c.setAttackDamage((int) (c.getAttackDamage()*0.9));
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);
		
	}

	@Override
	public void remove(Champion c) {
		c.setSpeed((int) (c.getSpeed()/0.9));
		c.setAttackDamage((int) (c.getAttackDamage()/0.9));
		c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);
		c.setSpeed((int) ( ((int)(c.getSpeed()+2)/5)*5) );
		c.setAttackDamage((int) ( ((int)(c.getAttackDamage()+2)/5)*5) );
	}

}
