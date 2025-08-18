package net.etienne.spellslabextra;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;
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
	private static void configureCooldown(Spell spell, float duration, float exhaust) {
		if (spell.cost == null) {
			spell.cost = new Spell.Cost();
		}
		spell.cost.cooldown = new Spell.Cost.Cooldown();
		spell.cost.cooldown.duration = duration;
		spell.cost.exhaust = exhaust;
	}
	private static void configureItemCost(Spell spell, String itemId) {
		if (spell.cost == null) {
			spell.cost = new Spell.Cost();
		}
		spell.cost.item = new Spell.Cost.Item();
		spell.cost.item.id = itemId;
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
	private static Spell.Impact createHeal(float coefficient) {
		var buff = new Spell.Impact();
		buff.action = new Spell.Impact.Action();
		buff.action.type = Spell.Impact.Action.Type.HEAL;
		buff.action.heal = new Spell.Impact.Action.Heal();
		buff.action.heal.spell_power_coefficient = coefficient;
		return buff;
	}
	private static Spell.Impact.TargetModifier createImpactModifier(String entityType) {
		var condition = new Spell.TargetCondition();
		condition.entity_type = entityType;
		var modifier = new Spell.Impact.TargetModifier();
		modifier.conditions = List.of(condition);
		return modifier;
	}
	private static void impactDeniedForMechanical(Spell.Impact impact) {
		var modifier = createImpactModifier("#spell_engine:mechanical");
		modifier.execute = TriState.DENY;
		impact.target_modifiers = List.of(modifier);
	}
	private static Spell.Impact.TargetModifier extraDamageAgainstUndead() {
		var modifier = createImpactModifier("#minecraft:undead");
		var powerModifier = new Spell.Impact.Modifier();
		powerModifier.power_multiplier = 0.5F;
		modifier.modifier = powerModifier;
		return modifier;
	}

	private static Spell.Impact.TargetModifier extraCritAgainstUndead() {
		var modifier = createImpactModifier("#minecraft:undead");
		var powerModifier = new Spell.Impact.Modifier();
		powerModifier.critical_chance_bonus = 1F;
		modifier.modifier = powerModifier;
		return modifier;
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
		spell.release.sound =  new Sound("minecraft:item.crossbow.shoot");

		spell.target.type = Spell.Target.Type.AIM;
		spell.target.aim = new Spell.Target.Aim();
		spell.target.aim.required = true;
		spell.target.aim.sticky = true;

		spell.deliver.type = Spell.Delivery.Type.METEOR;
		var meteor = new Spell.Delivery.Meteor();
		meteor.launch_height = 10;
		meteor.launch_radius = 1.5F;
		meteor.launch_properties.velocity = 2;
		meteor.launch_properties.extra_launch_count = 14;
		meteor.launch_properties.extra_launch_delay = 3;
		var projectile = new Spell.ProjectileData();
		projectile.divergence = 0;
		projectile.client_data = new Spell.ProjectileData.Client();
		projectile.client_data.travel_particles = new ParticleBatch[] {
				new ParticleBatch(
						"minecraft:crit",
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK,
						3, 0, 0,0)

		};
		var model = new Spell.ProjectileModel();
		model.model_id = "spellslabextra:projectile/arrow";
		model.scale = 1;
		projectile.client_data.model = model;

		meteor.projectile = projectile;
		spell.deliver.meteor = meteor;

		var damage = damageImpact(0.3F,0);
		damage.particles = new ParticleBatch[] {
				new ParticleBatch(
						SpellEngineParticles.dripping_blood.toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						3, 0.5F, 1F)
		};
		damage.sound = new Sound("archers:magic_arrow_impact");

		spell.impacts = List.of(damage);

		spell.area_impact = new Spell.AreaImpact();
		spell.area_impact.radius = 2;
		spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.NONE;
		spell.area_impact.sound = new Sound("minecraft:entity.arrow.hit");

		configureCooldown(spell, 15, 0);
		configureItemCost(spell, "arrow");

		return new Entry(id, spell, title, description, null);
	}
	public static final Entry holy_rain = add(holy_rain());
	private static Entry holy_rain() {
		var id = Identifier.of(MOD_ID, "holy_rain");
		var title = "";
		var description = "";
		var spell = SpellBuilder.createSpellActive();
		spell.school = SpellSchools.HEALING;
		spell.tier = 3;
		spell.range = 20;

		spell.active.cast.duration = 1.5F;
		spell.active.cast.animation = "spell_engine:one_handed_area_charge";
		spell.active.cast.sound = new Sound("spell_engine:generic_healing_casting");
		spell.active.cast.particles = new ParticleBatch[] {
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
						ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
						ParticleBatch.Rotation.LOOK,
						3, 0.01F, 0.1F,0).color(Color.HOLY.toRGBA())

		};

		spell.release.animation = "spell_engine:one_handed_area_release";
		spell.release.sound =  new Sound("spell_engine:generic_healing_release");

		spell.target.type = Spell.Target.Type.AIM;
		spell.target.aim = new Spell.Target.Aim();
		spell.target.aim.required = true;
		spell.target.aim.sticky = true;

		spell.deliver.type = Spell.Delivery.Type.METEOR;
		var meteor = new Spell.Delivery.Meteor();
		meteor.launch_height = 10;
		meteor.launch_radius = 3F;
		meteor.launch_properties.velocity = 1.2F;
		meteor.launch_properties.extra_launch_count = 29;
		meteor.launch_properties.extra_launch_delay = 3;
		var projectile = new Spell.ProjectileData();
		projectile.divergence = 0;
		projectile.client_data = new Spell.ProjectileData.Client();
		projectile.client_data.light_level = 10;
		projectile.client_data.travel_particles = new ParticleBatch[] {
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK,
						20, 0, 0,0).color(Color.HOLY.toRGBA()),
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
						ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
						ParticleBatch.Rotation.LOOK,
						2, 0.1F, 0.2F,0).color(Color.HOLY.toRGBA())

		};
		var model = new Spell.ProjectileModel();
		model.model_id = "spellslabextra:projectile/laser";
		model.scale = 1;
		model.light_emission = LightEmission.RADIATE;
		projectile.client_data.model = model;

		meteor.projectile = projectile;
		spell.deliver.meteor = meteor;

		var heal = createHeal(0.2F);
		impactDeniedForMechanical(heal);
		heal.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_IMPACT_1.id());
		heal.particles = new ParticleBatch[] {
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.HEAL,
								SpellEngineParticles.MagicParticles.Motion.ASCEND).id().toString(),
						ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
						20, 0.02F, 0.15F)
						.color(Color.NATURE.toRGBA())
		};
		var damage = damageImpact(0.2F,0);
		damage.target_modifiers = List.of(extraCritAgainstUndead());
		damage.particles = new ParticleBatch[] {
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.HOLY,
								SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						3, 0.5F, 1F)
		};
		damage.sound = new Sound("paladins:holy_beam_start_casting");


		spell.impacts = List.of(heal, damage);


		spell.area_impact = new Spell.AreaImpact();
		spell.area_impact.radius = 3;
		spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.NONE;

		configureCooldown(spell, 20, 0.4F);
		configureItemCost(spell, "runes:healing_stone");

		return new Entry(id, spell, title, description, null);
	}
	public static final Entry fire_missiles = add(fire_missiles());
	private static Entry fire_missiles() {
		var id = Identifier.of(MOD_ID, "fire_missiles");
		var spell = SpellBuilder.createSpellActive();
		spell.school = SpellSchools.FIRE;
		spell.range = 32;
		spell.tier = 3;
		var title = "";
		var description = "";

		spell.active.cast.duration = 1.5F;
		spell.active.cast.animation = "spell_engine:two_handed_channeling";
		spell.active.cast.channel_ticks = 12;
		spell.active.cast.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.flame.id().toString(),
						ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
						2, 0.05F, 0.1F)
		};
		spell.active.cast.sound = new Sound("spell_engine:generic_fire_casting");

		spell.release.sound = new Sound("spell_engine:generic_fire_release");

		spell.target.type = Spell.Target.Type.AIM;
		spell.target.aim = new Spell.Target.Aim();


		spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
		spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
		spell.deliver.projectile.direct_towards_target = true;
		spell.deliver.projectile.launch_properties.velocity = 1.7F;
		spell.deliver.projectile.direction_offsets = new Spell.Delivery.ShootProjectile.DirectionOffset[] {
				new Spell.Delivery.ShootProjectile.DirectionOffset(0, 0),
				new Spell.Delivery.ShootProjectile.DirectionOffset(15, 0),
				new Spell.Delivery.ShootProjectile.DirectionOffset(-15, 0)
		};
		var projectile = new Spell.ProjectileData();
		projectile.homing_angle = 0F;
		projectile.client_data = new Spell.ProjectileData.Client();
		projectile.client_data.model = new Spell.ProjectileModel();
		projectile.client_data.model.model_id = "wizards:projectile/fireball";
		projectile.client_data.travel_particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.flame_medium_a.id().toString(),
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK, 4,0.1F,0.1F, 0)
						.roll(18).color(Color.NATURE.toRGBA()),
				new ParticleBatch(
						SpellEngineParticles.flame_medium_b.id().toString(),
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK, 4,0F,0.1F, 0),
				new ParticleBatch(
						"smoke",
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK, 2,0F,0.1F, 0),
		};
		projectile.client_data.light_level = 10;
		projectile.client_data.model.scale = 0.8F;

		spell.deliver.projectile.projectile = projectile;

		var damage = damageImpact(0.3F,0);
		damage.particles = new ParticleBatch[]{
				new ParticleBatch(
						"lava",
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						15, 0.5F, 3.0F)
		};
		spell.impacts = List.of(damage);

		spell.area_impact = new Spell.AreaImpact();
		spell.area_impact.radius = 2.5F;
		spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
		spell.area_impact.sound = new Sound("wizards:fireball_impact");
		spell.area_impact.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.fire_explosion.id().toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						2, 0.2F, 0.5F)
		};

		configureCooldown(spell, 7,0);
		configureItemCost(spell, "runes:fire_stone");

		return new Entry(id, spell, title, description, null);
	}
	public static final Entry ice_barrage = add(ice_barrage());
	private static Entry ice_barrage() {
		var id = Identifier.of(MOD_ID, "ice_barrage");
		var spell = SpellBuilder.createSpellActive();
		spell.school = SpellSchools.FROST;
		spell.range = 10;
		spell.tier = 3;
		var title = "";
		var description = "";

		spell.active.cast.duration = 1F;
		spell.active.cast.animation = "spell_engine:one_handed_area_charge";
		spell.active.cast.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.snowflake.id().toString(),
						ParticleBatch.Shape.PIPE, ParticleBatch.Origin.CENTER,
						1, 0.1F, 0.2F)
		};
		spell.active.cast.sound = new Sound("spell_engine:generic_frost_casting");

		spell.release.sound = new Sound("spell_engine:generic_frost_release");
		spell.release.animation = "spell_engine:one_handed_projectile_release";

		spell.target.type = Spell.Target.Type.AIM;
		spell.target.aim = new Spell.Target.Aim();

		spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
		var shoot = new Spell.Delivery.ShootProjectile();
		shoot.launch_properties.velocity = 1.7F;
		shoot.launch_properties.extra_launch_count = 7;
		shoot.launch_properties.extra_launch_delay = 1;
		var projectile = new Spell.ProjectileData();
		projectile.divergence = 25;
		projectile.perks.pierce = 2;
		projectile.client_data = new Spell.ProjectileData.Client();
		projectile.client_data.travel_particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.FROST,
								SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK, 1,0.05F,0.1F, 0)
		};
		projectile.client_data.light_level = 12;
		projectile.client_data.model = new Spell.ProjectileModel();
		projectile.client_data.model.model_id = "wizards:projectile/frost_shard";
		projectile.client_data.model.scale = 0.8F;
		shoot.projectile = projectile;
		spell.deliver.projectile = shoot;

		var damage = damageImpact(0.3F,0);
		damage.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.frost_shard.id().toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK, 10,0.1F,0.2F, 0)
		};
		damage.sound = new Sound("wizards:frost_shard_impact");
		spell.impacts = List.of(damage);

		configureCooldown(spell, 15,0.3F);
		configureItemCost(spell, "runes:frost_stone");

		return new Entry(id, spell, title, description, null);
	}
	public static final Entry force_push = add(force_push());
	private static Entry force_push() {
		var id = Identifier.of(MOD_ID, "force_push");
		var title = "";
		var description = "";

		var spell = SpellBuilder.createSpellActive();
		spell.school = SpellSchools.ARCANE;
		spell.tier = 3;
		spell.range = 6;

		spell.active.cast.duration = 0.5F;
		spell.active.cast.animation = "spell_engine:one_handed_projectile_charge";
		spell.active.cast.sound = new Sound("spell_engine:generic_arcane_casting");
		spell.active.cast.particles = new ParticleBatch[] {
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPELL,
								SpellEngineParticles.MagicParticles.Motion.DECELERATE
						).id().toString(),
						ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
						ParticleBatch.Rotation.LOOK, 1, 0.05F, 0.1F, 0.0F)
						.color(Color.ARCANE.toRGBA())
		};

		spell.release.animation = "spell_engine:one_handed_projectile_release";
		spell.release.sound = new Sound("wizards:arcane_missile_release");

		spell.target.type = Spell.Target.Type.AIM;
		spell.target.aim = new Spell.Target.Aim();

		var damage = damageImpact(0.5F,5);
		damage.particles = new ParticleBatch[]{
				new ParticleBatch(
						"minecraft:poof",
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						20, 0.05F, 0.2F),
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.ARCANE,
								SpellEngineParticles.MagicParticles.Motion.BURST
						).id().toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						ParticleBatch.Rotation.LOOK, 20, 0.05F, 0.2F, 0.0F)
						.color(Color.ARCANE.toRGBA())
		};
		damage.sound = new Sound("wizards:arcane_blast_impact");

		var debuff = createEffectImpact(Identifier.of("minecraft:levitation"), 1);
		debuff.action.status_effect.amplifier = 4;
		debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
		debuff.action.status_effect.show_particles = false;

		spell.impacts = List.of(damage, debuff);

		configureItemCost(spell, "runes:arcane_stone");
		configureCooldown(spell, 15,0.3F);

		return new Entry(id, spell, title, description, null);
	}
	public static final Entry stone_fist = add(stone_fist());
	private static Entry stone_fist() {
		var id = Identifier.of(MOD_ID, "stone_fist");
		var spell = SpellBuilder.createSpellActive();
		spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
		spell.range = 6;
		spell.tier = 3;
		var title = "";
		var description = "";

		spell.active.cast.duration = 0.3F;
		spell.active.cast.animation = "spell_engine:one_handed_projectile_charge";
		spell.active.cast.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
						ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
						1, 0.05F, 0.1F).color(Color.HOLY.toRGBA())
		};
		spell.active.cast.sound = new Sound("spell_engine:generic_healing_casting");

		spell.release.sound = new Sound("spell_engine:generic_healing_release");
		spell.release.animation = "spell_engine:one_handed_projectile_release";

		spell.target.type = Spell.Target.Type.AIM;
		spell.target.aim = new Spell.Target.Aim();

		spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
		var shoot = new Spell.Delivery.ShootProjectile();
		shoot.launch_properties.velocity = 0.5F;
		var projectile = new Spell.ProjectileData();
		projectile.homing_angle = 1;
		projectile.client_data = new Spell.ProjectileData.Client();
		projectile.client_data.travel_particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
						ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
						5, 0F, 0.1F).color(Color.HOLY.toRGBA())
		};
		projectile.client_data.model = new Spell.ProjectileModel();
		projectile.client_data.model.model_id = "spellslabextra:projectile/stone_fist";
		projectile.client_data.model.scale = 1.0F;
		shoot.projectile = projectile;
		spell.deliver.projectile = shoot;

		var damage = damageImpact(0.4F,2);
		damage.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						25, 0.2F, 1.0F).color(Color.HOLY.toRGBA())
		};
		var damageHealingSchool = damageImpact(0.5F,0);
		damageHealingSchool.school = SpellSchools.HEALING;

		var debuff = createEffectImpact(Identifier.of("minecraft:slowness"), 5);
		debuff.action.status_effect.amplifier = 1;
		debuff.action.status_effect.amplifier_cap = 3;
		debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
		debuff.action.status_effect.show_particles = false;

		spell.impacts = List.of(damage, damageHealingSchool,debuff);

		spell.area_impact = new Spell.AreaImpact();
		spell.area_impact.radius = 4.0F;
		spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
		spell.area_impact.sound = Sound.withVolume(Identifier.of("minecraft:block.gravel.fall"), 5.0F);
		spell.area_impact.particles = new ParticleBatch[]{
				new ParticleBatch(
						SpellEngineParticles.MagicParticles.get(
								SpellEngineParticles.MagicParticles.Shape.SPARK,
								SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						100, 0.2F, 0.4F).color(Color.HOLY.toRGBA()),
				new ParticleBatch(
						"smoke",
						ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
						50, 0.1F, 0.3F)
		};

		configureCooldown(spell, 4,0.3F);
		configureItemCost(spell, "runes:healing_stone");

		return new Entry(id, spell, title, description, null);
	}

	@Override
	public void onInitialize() {
	}
}