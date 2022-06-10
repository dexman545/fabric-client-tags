package net.fabricmc.fabric.api.tag.client.v1.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.client.v1.impl.DataLoader;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class LocalTags {
    @ApiStatus.Internal
    public static final Map<TagKey<?>, Set<Identifier>> LOCAL_TAG_CACHE =
            Collections.synchronizedMap(new Object2ObjectOpenHashMap<>());
    private static final DataLoader LOADER = new DataLoader();

    public static Set<Identifier> getOrCreateLocalTag(TagKey<?> tagKey) {
        return LOCAL_TAG_CACHE.computeIfAbsent(tagKey, LOADER::loadTag);
    }
}
