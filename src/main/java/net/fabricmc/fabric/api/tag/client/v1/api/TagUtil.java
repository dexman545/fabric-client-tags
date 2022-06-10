package net.fabricmc.fabric.api.tag.client.v1.api;

import net.fabricmc.fabric.api.tag.client.v1.test.ExampleMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Optional;

public class TagUtil {
    @SuppressWarnings("unchecked")
    public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
        var maybeRegistry = Registry.REGISTRIES.getOrEmpty(tagKey.registry().getValue());
        if (maybeRegistry.isPresent()) {
            if (tagKey.isOf(maybeRegistry.get().getKey())) {
                var registry = (Registry<T>) maybeRegistry.get();

                var maybeKey = registry.getKey(entry);

                // Check synced tag
                return maybeKey.filter(registryKey -> registry.entryOf(registryKey).isIn(tagKey))
                        .isPresent();
            }
        }

        return false;
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
                var ids = LocalTags.getOrCreateLocalTag(tagKey);
                return maybeKey.filter(registryKey -> ids.contains(registryKey.getValue())).isPresent();
            }
        }

        return false;
    }

    public static <T> boolean isIn(TagKey<T> tagKey, RegistryEntry<T> registryEntry) {
        registryEntry.isIn(tagKey);

        return false;
    }

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
            var ids = LocalTags.getOrCreateLocalTag(tagKey);
            return ids.contains(registryKey.getValue());
        }

        return false;
    }
}
