package net.fabricmc.fabric.api.tag.client.v1.api;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.world.biome.Biome;

public record TagType(Class<?> type, String folder) {
    public static final TagType BLOCK = new TagType(Block.class, "blocks");
    public static final TagType ENCHANTMENT = new TagType(Enchantment.class, "enchantment");
    public static final TagType ENTITY_TYPE = new TagType(EntityType.class, "entity_types");
    public static final TagType FLUID = new TagType(Fluid.class, "fluids");
    public static final TagType ITEM = new TagType(Item.class, "items");
    //todo does dynamic registry work? or do we not care as IDs are returned?
    public static final TagType BIOME = new TagType(Biome.class, "worldgen/biome");
}
