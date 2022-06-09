package net.fabricmc.fabric.api.tag.client.v1.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.client.v1.api.LocalTag;
import net.fabricmc.fabric.api.tag.client.v1.api.LocalTags;
import net.fabricmc.fabric.api.tag.client.v1.api.TagType;
import net.fabricmc.fabric.api.tag.client.v1.impl.DataLoader;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
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

		var x = new DataLoader();
		x.loadTag(new LocalTag(TagType.BLOCK, ConventionalBlockTags.ORES.id()));

		LOGGER.info(String.valueOf(LocalTags.getOrCreateLocalTag(TagType.BLOCK, ConventionalBlockTags.ORES.id())));
	}
}
