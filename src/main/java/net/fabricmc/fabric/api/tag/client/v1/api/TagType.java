package net.fabricmc.fabric.api.tag.client.v1.api;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public record TagType(Class<?> type, String folder, @Nullable RegistryKey<?> registryKey) {
    private static final Collection<TagType> TYPES = new HashSet<>();
    public static final TagType BLOCK = new TagType(Block.class, "blocks", Registry.BLOCK_KEY);
    public static final TagType ENCHANTMENT = new TagType(Enchantment.class, "enchantment", Registry.ENCHANTMENT_KEY);
    public static final TagType ENTITY_TYPE = new TagType(EntityType.class, "entity_types", Registry.ENTITY_TYPE_KEY);
    public static final TagType FLUID = new TagType(Fluid.class, "fluids", Registry.FLUID_KEY);
    public static final TagType ITEM = new TagType(Item.class, "items", Registry.ITEM_KEY);
    //todo does dynamic registry work? or do we not care as only IDs are returned?
    public static final TagType BIOME = new TagType(Biome.class, "worldgen/biome", Registry.BIOME_KEY);

    public TagType {
        TYPES.add(this);
    }

    public static Optional<TagType> getTypeForRegistry(@NotNull RegistryKey<?> registryKey) {
        for (TagType type : TYPES) {
            if (registryKey.equals(type.registryKey)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
