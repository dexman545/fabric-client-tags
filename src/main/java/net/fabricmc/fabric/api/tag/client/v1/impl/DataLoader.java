package net.fabricmc.fabric.api.tag.client.v1.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.tag.client.v1.api.ClientTags;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagFile;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class DataLoader {
    /**
     * Parsing based on {@link net.minecraft.tag.TagGroupLoader#loadTags(net.minecraft.resource.ResourceManager)}
     */
    public HashSet<Identifier> loadTag(TagKey<?> tagKey) {
        var list = new HashSet<TagEntry>();
        HashSet<Path> tagFiles = getTagFiles(tagKey.registry(), tagKey.id());
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
                throw new RuntimeException(e);//todo not throw
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
                    TagKey<?> tag = TagKey.of(tagKey.registry(), id);
                    return ClientTags.getOrCreateLocalTag(tag);
                }
            }, ids::add);
        }

        return ids;
    }

    public HashSet<Path> getTagFiles(RegistryKey<? extends Registry<?>> registryKey, Identifier identifier) {
        return getTagFiles(TagManagerLoader.getPath(registryKey), identifier);
    }

    public HashSet<Path> getTagFiles(String tagType, Identifier identifier) {
        String tagFile = "data/%s/%s/%s.json".formatted(identifier.getNamespace(), tagType, identifier.getPath());
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
