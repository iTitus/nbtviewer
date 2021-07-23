package io.github.ititus.nbtviewer.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ititus.nbtviewer.NbtViewer;
import io.github.ititus.nbtviewer.client.gui.screen.NbtViewerScreen;
import io.github.ititus.nbtviewer.client.util.KeyHelper;
import io.github.ititus.nbtviewer.common.util.ComponentHelper;
import io.github.ititus.nbtviewer.common.util.NbtHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {

    public static final KeyMapping INSPECT_WORLD = new KeyMapping(
            "key.nbtviewer.inspect_world",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.nbtviewer"
    );
    public static final KeyMapping INSPECT_ITEM = new KeyMapping(
            "key.nbtviewer.inspect_item",
            KeyConflictContext.GUI,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.nbtviewer"
    );

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = NbtViewer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Lifecycle {

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(INSPECT_WORLD);
            ClientRegistry.registerKeyBinding(INSPECT_ITEM);
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = NbtViewer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Forge {

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onTooltip(ItemTooltipEvent event) {
            if (!KeyHelper.isActive(INSPECT_ITEM)) {
                return;
            }

            NbtHelper.getNbt(event.getItemStack())
                    .map(tag -> NbtHelper.toPrettyComponent(tag, " "))
                    .map(ComponentHelper::splitLines)
                    .ifPresent(event.getToolTip()::addAll);
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            while (INSPECT_WORLD.consumeClick()) {
                NbtViewerScreen.tryOpen();
            }
        }
    }
}
