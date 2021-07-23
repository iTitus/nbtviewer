package io.github.ititus.nbtviewer.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public final class KeyHelper {

    private static final Logger L = LogManager.getLogger();

    private KeyHelper() {
    }

    /**
     * Checks if the given KeyMapping is active, using the bound key, context and modifiers.
     *
     * @param km KeyMapping
     * @return true if the KeyMapping is active, otherwise false
     */
    public static boolean isActive(KeyMapping km) {
        if (km == null) {
            return false;
        } else if (km.isDown()) {
            return true;
        } else if (km.isConflictContextAndModifierActive()) {
            return isKeyPressed(km);
        }

        return KeyModifier.isKeyCodeModifier(km.getKey()) && isKeyPressed(km);
    }

    /**
     * Checks if the key bound to a KeyMapping is pressed, ignoring context and modifiers.
     *
     * @param km KeyMapping
     * @return true if the key is pressed, otherwise false
     */
    public static boolean isKeyPressed(KeyMapping km) {
        if (km == null || km.getKey().getValue() == InputConstants.UNKNOWN.getValue()) {
            return false;
        }

        try {
            long handle = Minecraft.getInstance().getWindow().getWindow();
            return switch (km.getKey().getType()) {
                case KEYSYM -> InputConstants.isKeyDown(handle, km.getKey().getValue());
                case SCANCODE -> false;
                case MOUSE -> GLFW.glfwGetMouseButton(handle, km.getKey().getValue()) == GLFW.GLFW_PRESS;
            };
        } catch (Exception e) {
            L.warn("Unable to check keybind status of {}", toString(km), e);
        }

        return false;
    }

    public static String toString(KeyMapping km) {
        if (km == null) {
            return "null";
        }

        return km.getClass().getSimpleName() + "{" +
                "description=" + km.getName() +
                ", key=" + km.getKey() +
                ", category=" + km.getCategory() +
                ", modifier=" + km.getKeyModifier() +
                ", conflictContext=" + km.getKeyConflictContext() +
                '}';
    }
}
