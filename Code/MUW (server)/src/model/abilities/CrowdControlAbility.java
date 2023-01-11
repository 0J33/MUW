package model.abilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import model.effects.Effect;
import model.world.Champion;
import model.world.Damageable;
import views.music;

@SuppressWarnings("unused")
public class CrowdControlAbility extends Ability {
	private Effect effect;

	public CrowdControlAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area, int required,
			Effect effect) {
		super(name, cost, baseCoolDown, castRadius, area, required);
		this.effect = effect;

	}

	public Effect getEffect() {
		return effect;
	}

	@Override
	public void execute(ArrayList<Damageable> targets) throws CloneNotSupportedException {
		for(Damageable d: targets)
		{
			Champion c =(Champion) d;
			c.getAppliedEffects().add((Effect) effect.clone());
			effect.apply(c);
			URL castsound = (getClass().getResource("/resources/cast.wav"));
			music.playSound(castsound);
		}
		
	}

}
