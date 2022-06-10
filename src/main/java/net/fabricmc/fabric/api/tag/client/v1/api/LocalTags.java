package net.fabricmc.fabric.api.tag.client.v1.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.client.v1.impl.DataLoader;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class LocalTags {
    @ApiStatus.Internal
    public static final Map<LocalTag, Set<Identifier>> LOCAL_TAG_CACHE =
            Collections.synchronizedMap(new Object2ObjectOpenHashMap<>());
    private static final DataLoader LOADER = new DataLoader();

    public static Set<Identifier> getOrCreateLocalTag(TagType type, Identifier tagId) {
        return getOrCreateLocalTag(new LocalTag(type, tagId));
    }

    public static Set<Identifier> getOrCreateLocalTag(LocalTag tag) {
        return LOCAL_TAG_CACHE.computeIfAbsent(tag, LOADER::loadTag);
    }

    public static Set<Identifier> getOrCreateLocalTag(TagKey<?> tagKey) {
        Optional<TagType> maybeType = TagType.getTypeForRegistry(tagKey.registry());
        if (maybeType.isPresent()) {
            return getOrCreateLocalTag(maybeType.get(), tagKey.id());
        }

        return new HashSet<>();
    }
}
