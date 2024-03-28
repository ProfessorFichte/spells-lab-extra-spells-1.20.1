package net.etienne.spellslabextra;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpellsLabExtraSpells implements ModInitializer {
	public static final String MOD_ID = "spellslabextra";

	@Override
	public void onInitialize() {
		ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("spellslabextrapools"),
				FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(), ResourcePackActivationType.ALWAYS_ENABLED);
	}
}