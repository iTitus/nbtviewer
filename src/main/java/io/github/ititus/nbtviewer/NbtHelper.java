package io.github.ititus.nbtviewer;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;

import java.util.Optional;

public final class NbtHelper {

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
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
            return Optional.empty();
        }

        return Optional.of(nbt);
    }
}
