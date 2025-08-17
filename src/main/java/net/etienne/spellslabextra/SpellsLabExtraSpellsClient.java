package net.etienne.spellslabextra;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomModels;

import java.util.List;

public class SpellsLabExtraSpellsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CustomModels.registerModelIds(List.of(
                Identifier.of(SpellsLabExtraSpells.MOD_ID, "projectile/arrow"),
                Identifier.of(SpellsLabExtraSpells.MOD_ID, "projectile/laser"),
                Identifier.of(SpellsLabExtraSpells.MOD_ID, "projectile/stone_fist")
        ));
    }
}
