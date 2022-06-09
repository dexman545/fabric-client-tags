package net.fabricmc.fabric.api.tag.client.v1.api;

import net.minecraft.util.Identifier;

public record LocalTag(TagType type, Identifier tagId) {
}
