package net.fabricmc.fabric.api.tag.client.v1.api;

import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

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

    public static <T> boolean isIn(TagKey<T> tagKey, RegistryEntry<T> registryEntry) {
        registryEntry.isIn(tagKey);

        return false;
    }

}
