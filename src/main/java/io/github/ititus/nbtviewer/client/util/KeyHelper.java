package io.github.ititus.nbtviewer.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public final class KeyHelper {

    private static final Logger L = LogManager.getLogger();

    private KeyHelper() {
    }

    /**
     * Checks if the given KeyBinding is active, using the bound key, context and modifiers.
     *
     * @param kb KeyBinding
     * @return true if the KeyBinding is active, otherwise false
     */
    public static boolean isActive(KeyBinding kb) {
        if (kb == null) {
            return false;
        } else if (kb.isKeyDown()) {
            return true;
        } else if (kb.isConflictContextAndModifierActive()) {
            return isKeyPressed(kb);
        }

        return KeyModifier.isKeyCodeModifier(kb.getKey()) && isKeyPressed(kb);
    }

    /**
     * Checks if the key bound to a KeyBinding is pressed, ignoring context and modifiers.
     *
     * @param kb KeyBinding
     * @return true if the key is pressed, otherwise false
     */
    public static boolean isKeyPressed(KeyBinding kb) {
        if (kb == null || kb.getKey().getKeyCode() == InputMappings.INPUT_INVALID.getKeyCode()) {
            return false;
        }

        try {
            long handle = Minecraft.getInstance().getMainWindow().getHandle();
            switch (kb.getKey().getType()) {
                case KEYSYM:
                    return InputMappings.isKeyDown(handle, kb.getKey().getKeyCode());
                case SCANCODE:
                    return false;
                case MOUSE:
                    return GLFW.glfwGetMouseButton(handle, kb.getKey().getKeyCode()) == GLFW.GLFW_PRESS;
            }
        } catch (Exception e) {
            L.warn("Unable to check keybind status of {}", toString(kb), e);
        }

        return false;
    }

    public static String toString(KeyBinding kb) {
        if (kb == null) {
            return "null";
        }

        return kb.getClass().getSimpleName() + "{" +
                "description=" + kb.getKeyDescription() +
                ", key=" + kb.getKey() +
                ", category=" + kb.getKeyCategory() +
                ", modifier=" + kb.getKeyModifier() +
                ", conflictContext=" + kb.getKeyConflictContext() +
                '}';
    }
}
