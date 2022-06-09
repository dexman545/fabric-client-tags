package net.fabricmc.fabric.api.tag.client.v1.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.client.v1.impl.DataLoader;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public class LocalTags {
    public static final Map<LocalTag, Set<Identifier>> LOCAL_TAG_CACHE = new Object2ObjectOpenHashMap<>();
    public static final DataLoader LOADER = new DataLoader();

    public static Set<Identifier> getOrCreateLocalTag(TagType type, Identifier tagId) {
        LocalTag tag = new LocalTag(type, tagId);
        return LOCAL_TAG_CACHE.computeIfAbsent(tag, LOADER::loadTag);
    }
}
