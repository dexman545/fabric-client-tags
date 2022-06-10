package net.fabricmc.fabric.api.tag.client.v1.api;

import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class TagUtil {
    @SuppressWarnings("unchecked")
    public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
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
}
