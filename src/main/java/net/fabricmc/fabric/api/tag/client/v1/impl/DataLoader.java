package net.fabricmc.fabric.api.tag.client.v1.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.tag.client.v1.api.LocalTag;
import net.fabricmc.fabric.api.tag.client.v1.api.LocalTags;
import net.fabricmc.fabric.api.tag.client.v1.api.TagType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagFile;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class DataLoader {
    public HashSet<Identifier> loadTag(LocalTag localTag) {
        var list = new HashSet<TagEntry>();
        HashSet<Path> tagFiles = getTagFiles(localTag.type(), localTag.tagId());
        for (Path tagPath : tagFiles) {
            try (BufferedReader tagReader = Files.newBufferedReader(tagPath)) {
                JsonElement jsonElement = JsonParser.parseReader(tagReader);
                Optional<TagFile> maybeTagFile = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement))
                        .get().left();

                maybeTagFile.ifPresent(tagFile -> {
                    if (tagFile.replace()) {
                        list.clear();
                    }

                    list.addAll(tagFile.entries());
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        var ids = new HashSet<Identifier>();
        for (TagEntry tagEntry : list) {
            //todo check return value?
            tagEntry.resolve(new TagEntry.ValueGetter<>() {
                @Nullable
                @Override
                public Identifier direct(Identifier id) {
                    return id;
                }

                @Nullable
                @Override
                public Collection<Identifier> tag(Identifier id) {
                    LocalTag tag = new LocalTag(localTag.type(), id);
                    return LocalTags.LOCAL_TAG_CACHE.computeIfAbsent(tag, t -> LocalTags.getOrCreateLocalTag(localTag.type(), id));
                }
            }, ids::add);
        }

        return ids;
    }

    public HashSet<Path> getTagFiles(TagType tagType, Identifier identifier) {
        return getTagFiles(tagType.folder(), identifier);
    }

    public HashSet<Path> getTagFiles(String tagType, Identifier identifier) {
        String tagFile = "data/%s/tags/%s/%s.json".formatted(identifier.getNamespace(), tagType, identifier.getPath());
        return getResourcePaths(tagFile);
    }

    public HashSet<Path> getResourcePaths(String path) {
        HashSet<Path> out = new HashSet<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            mod.findPath(path).ifPresent(out::add);
        }

        return out;
    }
}
