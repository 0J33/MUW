package model.abilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import model.world.Damageable;
import views.music;

@SuppressWarnings("unused")
public  class HealingAbility extends Ability {
	private int healAmount;

	public HealingAbility(String name,int cost, int baseCoolDown, int castRadius, AreaOfEffect area,int required, int healingAmount) {
		super(name,cost, baseCoolDown, castRadius, area,required);
		this.healAmount = healingAmount;
	}

	public int getHealAmount() {
		return healAmount;
	}

	public void setHealAmount(int healAmount) {
		this.healAmount = healAmount;
	}

	
	@Override
	public void execute(ArrayList<Damageable> targets) {
		for (Damageable d : targets)

			d.setCurrentHP(d.getCurrentHP() + healAmount);
		
		URL healsound = (getClass().getResource("/resources/heal.wav"));
		music.playSound(healsound);

	}
	

}
