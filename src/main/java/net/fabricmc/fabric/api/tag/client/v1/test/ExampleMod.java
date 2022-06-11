package net.fabricmc.fabric.api.tag.client.v1.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.client.v1.api.ClientTags;
import net.fabricmc.fabric.api.tag.client.v1.api.TagUtil;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		LOGGER.info(String.valueOf(ClientTags.getOrCreateLocalTag(ConventionalBlockTags.ORES)));
		LOGGER.info(String.valueOf(ClientTags.getOrCreateLocalTag(ConventionalBiomeTags.AQUATIC)));

		LOGGER.info(String.valueOf(ClientTags.isInWithLocalFallback(ConventionalBlockTags.ORES, Blocks.DIAMOND_ORE)));
		LOGGER.info(String.valueOf(ClientTags.isInLocal(ConventionalBiomeTags.AQUATIC, BiomeKeys.OCEAN)));
		LOGGER.info(String.valueOf(ClientTags.isInWithLocalFallback(ConventionalBlockTags.ORES, Blocks.DIAMOND_BLOCK)));
		LOGGER.info(String.valueOf(TagUtil.isIn(ConventionalBlockTags.ORES, Blocks.DIAMOND_ORE)));
		LOGGER.info(String.valueOf(TagUtil.isIn(ConventionalBlockTags.ORES, Blocks.DIAMOND_BLOCK)));
	}
}
