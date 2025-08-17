package net.etienne.spellslabextra;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpellsLabExtraSpells implements ModInitializer {
	public static final String MOD_ID = "spellslabextra";

	public record Entry(Identifier id, Spell spell, String title, String description,
						@Nullable SpellTooltip.DescriptionMutator mutator) {
	}

	public static final List<Entry> entries = new ArrayList<>();

	private static Entry add(Entry entry) {
		entries.add(entry);
		return entry;
	}
	private static Spell.Impact damageImpact(float coefficient, float knockback) {
		var damage = new Spell.Impact();
		damage.action = new Spell.Impact.Action();
		damage.action.type = Spell.Impact.Action.Type.DAMAGE;
		damage.action.damage = new Spell.Impact.Action.Damage();
		damage.action.damage.spell_power_coefficient = coefficient;
		damage.action.damage.knockback = knockback;
		return damage;
	}
	private static void configureCooldown(Spell spell, float duration) {
		if (spell.cost == null) {
			spell.cost = new Spell.Cost();
		}
		spell.cost.cooldown = new Spell.Cost.Cooldown();
		spell.cost.cooldown.duration = duration;
	}
	private static Spell.Impact createEffectImpact(Identifier effectId, float duration) {
		var buff = new Spell.Impact();
		buff.action = new Spell.Impact.Action();
		buff.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
		buff.action.status_effect = new Spell.Impact.Action.StatusEffect();
		buff.action.status_effect.effect_id = effectId.toString();
		buff.action.status_effect.duration = duration;
		return buff;
	}
	public static final Entry arrow_rain = add(arrow_rain());
	private static Entry arrow_rain() {
		var id = Identifier.of(MOD_ID, "arrow_rain");
		var title = "";
		var description = "";
		var spell = SpellBuilder.createSpellActive();
		spell.school = ExternalSpellSchools.PHYSICAL_RANGED;
		spell.tier = 3;
		spell.range = 32;

		spell.active.cast.duration = 1.0F;
		spell.active.cast.animation = "spell_engine:archery_pull";
		spell.active.cast.animates_ranged_weapon = true;
		spell.active.cast.sound = new Sound("archers:bow_pull");

		spell.release.animation = "spell_engine:archery_release";


		return new Entry(id, spell, title, description, null);
	}


	@Override
	public void onInitialize() {
	}
}