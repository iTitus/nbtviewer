package io.github.ititus.nbtviewer.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class NbtHelper {

    private static final Logger L = LogManager.getLogger();

    private NbtHelper() {
    }

    public static Optional<INBT> getNbt(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(stack.getTag());
    }

    public static Optional<INBT> getNbt(Entity e) {
        if (e == null || !e.isAlive()) {
            return Optional.empty();
        }

        CompoundNBT nbt = new CompoundNBT();
        try {
            if (!e.writeUnlessRemoved(nbt)) {
                return Optional.empty();
            }
        } catch (Exception ex) {
            L.warn("Unable to read entity nbt of {}", e, ex);
            return Optional.empty();
        }

        return Optional.of(nbt);
    }

    public static Optional<INBT> getNbt(TileEntity tile) {
        if (tile == null || tile.isRemoved()) {
            return Optional.empty();
        }

        CompoundNBT nbt = new CompoundNBT();
        try {
            nbt = tile.write(nbt);
        } catch (Exception e) {
            L.warn("Unable to read tile entity nbt of {}", tile, e);
            return Optional.empty();
        }

        return Optional.of(nbt);
    }
}
