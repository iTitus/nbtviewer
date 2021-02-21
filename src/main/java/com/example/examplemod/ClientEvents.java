package com.example.examplemod;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!event.getFlags().isAdvanced() || !Screen.hasShiftDown()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        CompoundNBT nbt = stack.getTag();
        if (nbt == null) {
            return;
        }

        ITextComponent text = nbt.toFormattedComponent(" ", 0);
        event.getToolTip().addAll(TextComponentHelper.splitLines(text));
    }
}
