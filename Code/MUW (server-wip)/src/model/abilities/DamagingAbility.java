package model.abilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import model.world.Damageable;
import views.music;

@SuppressWarnings("unused")
public class DamagingAbility extends Ability {

	private int damageAmount;

	public DamagingAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area, int required,
			int damageAmount) {
		super(name, cost, baseCoolDown, castRadius, area, required);
		this.damageAmount = damageAmount;
	}

	public int getDamageAmount() {
		return damageAmount;
	}

	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}

	@Override
	public void execute(ArrayList<Damageable> targets) {
		for (Damageable d : targets)

			d.setCurrentHP(d.getCurrentHP() - damageAmount);
		
		URL attacksound = (getClass().getResource("/resources/attack.wav"));
		music.playSound(attacksound);
		
	}
}
