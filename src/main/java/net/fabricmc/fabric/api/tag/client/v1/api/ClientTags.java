package net.fabricmc.fabric.api.tag.client.v1.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.client.v1.impl.DataLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class ClientTags {
    @ApiStatus.Internal
    public static final Map<TagKey<?>, Set<Identifier>> LOCAL_TAG_CACHE =
            Collections.synchronizedMap(new Object2ObjectOpenHashMap<>());
    private static final DataLoader LOADER = new DataLoader();

    public static Set<Identifier> getOrCreateLocalTag(TagKey<?> tagKey) {
        return LOCAL_TAG_CACHE.computeIfAbsent(tagKey, LOADER::loadTag);
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean isInWithLocalFallback(TagKey<T> tagKey, T entry) {
        var maybeRegistry = Registry.REGISTRIES.getOrEmpty(tagKey.registry().getValue());
        if (maybeRegistry.isPresent()) {
            if (tagKey.isOf(maybeRegistry.get().getKey())) {
                var registry = (Registry<T>) maybeRegistry.get();

                var maybeKey = registry.getKey(entry);

                // Check synced tag
                if (registry.containsTag(tagKey)) {
                    return maybeKey.filter(registryKey -> registry.entryOf(registryKey).isIn(tagKey))
                            .isPresent();
                }

                // Check local tags
                var ids = getOrCreateLocalTag(tagKey);
                return maybeKey.filter(registryKey -> ids.contains(registryKey.getValue())).isPresent();
            }
        }

        return false;
    }

    @Environment(EnvType.CLIENT)
    public static <T> boolean isInWithLocalFallback(TagKey<T> tagKey, RegistryEntry<T> registryEntry) {
        // Check if the tag exists in the dynamic registry first
        if (MinecraftClient.getInstance() != null) {
            if (MinecraftClient.getInstance().world != null) {
                if (MinecraftClient.getInstance().world.getRegistryManager() != null) {
                    Optional<? extends Registry<T>> maybeRegistry = MinecraftClient.getInstance().world
                            .getRegistryManager().getOptional(tagKey.registry());
                    if (maybeRegistry.isPresent()) {
                        if (maybeRegistry.get().containsTag(tagKey)) {
                            registryEntry.isIn(tagKey);
                        }
                    }
                }
            }
        }

        if (registryEntry.getKey().isPresent()) {
            return isInLocal(tagKey, registryEntry.getKey().get());
        }

        return false;
    }

    public static <T> boolean isInLocal(TagKey<T> tagKey, RegistryKey<T> registryKey) {
        if (tagKey.registry().getValue().equals(registryKey.getRegistry())) {
            // Check local tags
            var ids = getOrCreateLocalTag(tagKey);
            return ids.contains(registryKey.getValue());
        }

        return false;
    }
}
