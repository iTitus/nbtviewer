package io.github.ititus.nbtviewer;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {

    public static final KeyBinding INSPECT_WORLD = new KeyBinding(
            "key.nbtviewer.inspect_world",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.nbtviewer"
    );
    public static final KeyBinding INSPECT_ITEM = new KeyBinding(
            "key.nbtviewer.inspect_item",
            KeyConflictContext.GUI,
            KeyModifier.NONE,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.nbtviewer"
    );

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Lifecycle {

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(INSPECT_WORLD);
            ClientRegistry.registerKeyBinding(INSPECT_ITEM);
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Forge {

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onTooltip(ItemTooltipEvent event) {
            if (!KeyHelper.isActive(INSPECT_ITEM)) {
                return;
            }

            NbtHelper.getNbt(event.getItemStack())
                    .map(nbt -> nbt.toFormattedComponent(" ", 0))
                    .map(TextComponentHelper::splitLines)
                    .ifPresent(event.getToolTip()::addAll);
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            while (INSPECT_WORLD.isPressed()) {
                NbtViewerScreen.tryOpen();
            }
        }
    }
}
