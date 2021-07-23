package io.github.ititus.nbtviewer.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class NbtHelper {

    private static final Logger L = LogManager.getLogger();

    private NbtHelper() {
    }

    public static Optional<Tag> getNbt(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(stack.getTag());
    }

    public static Optional<Tag> getNbt(Entity e) {
        if (e == null || !e.isAlive()) {
            return Optional.empty();
        }

        CompoundTag nbt = new CompoundTag();
        try {
            if (!e.saveAsPassenger(nbt)) {
                return Optional.empty();
            }
        } catch (Exception ex) {
            L.warn("Unable to read entity compound tag of {}", e, ex);
            return Optional.empty();
        }

        return Optional.of(nbt);
    }

    public static Optional<Tag> getNbt(BlockEntity tile) {
        if (tile == null || tile.isRemoved()) {
            return Optional.empty();
        }

        CompoundTag nbt = new CompoundTag();
        try {
            nbt = tile.save(nbt);
        } catch (Exception e) {
            L.warn("Unable to read block entity compound tag of {}", tile, e);
            return Optional.empty();
        }

        return Optional.of(nbt);
    }

    public static Component toPrettyComponent(Tag tag) {
        return toPrettyComponent(tag, "");
    }

    public static Component toPrettyComponent(Tag tag, String indent) {
        return new TextComponentTagVisitor(indent, 0).visit(tag);
    }
}
