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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Client tags are tags loaded from the available mod datapacks, allowing for the use of tags client-side that
 * do not exist when tags are synced to the client from the server, or overloaded by world-specific datapacks.
 */
public class ClientTags {
    private static final Map<TagKey<?>, Set<Identifier>> LOCAL_TAG_CACHE =
            Collections.synchronizedMap(new Object2ObjectOpenHashMap<>());
    private static final DataLoader LOADER = new DataLoader();

    /**
     * Load a tag into the cache, loading any contained tags along with it.
     *
     * @param tagKey the {@code TagKey} to load.
     * @return a set of {@code Identifier}s this tag contains.
     */
    public static Set<Identifier> getOrCreateLocalTag(TagKey<?> tagKey) {
        return LOCAL_TAG_CACHE.computeIfAbsent(tagKey, LOADER::loadTag);
    }

    /**
     * Checks if an entry is in a tag.
     * <p>
     * If the tag does synced tag does exist, it is queried. If it does not exist,
     * the tag loaded from the available mods is checked.
     *
     * @param tagKey the {@code TagKey} to being checked.
     * @param entry the entry to check.
     * @return if the entry is in the given tag.
     */
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

    /**
     * Checks if an entry is in a tag, for use with entries from a dynamic registry,
     * such as {@link net.minecraft.world.biome.Biome}s.
     * <p>
     * If the tag does synced tag does exist, it is queried. If it does not exist,
     * the tag loaded from the available mods is checked.
     * <p>
     * Client-side only.
     *
     * @param tagKey the {@code TagKey} to being checked.
     * @param registryEntry the entry to check.
     * @return if the entry is in the given tag.
     */
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

    /**
     * Checks if an entry is in a tag produced from the available mods.
     *
     * @param tagKey the {@code TagKey} to being checked.
     * @param registryKey the entry to check.
     * @return if the entry is in the given tag.
     */
    public static <T> boolean isInLocal(TagKey<T> tagKey, RegistryKey<T> registryKey) {
        if (tagKey.registry().getValue().equals(registryKey.getRegistry())) {
            // Check local tags
            var ids = getOrCreateLocalTag(tagKey);
            return ids.contains(registryKey.getValue());
        }

        return false;
    }
}
