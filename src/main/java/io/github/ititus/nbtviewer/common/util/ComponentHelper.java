package io.github.ititus.nbtviewer.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.*;

public final class ComponentHelper {

    private ComponentHelper() {
    }

    private static List<Component> extract(Component text) {
        if (text == null) {
            return Collections.emptyList();
        } else if (text.getSiblings().isEmpty()) {
            return Collections.singletonList(text);
        }

        LinkedList<Component> list = new LinkedList<>();

        Component copy = text.copy();
        for (Iterator<Component> it = copy.getSiblings().iterator(); it.hasNext(); ) {
            Component next = it.next();
            list.addAll(extract(next));
            it.remove();
        }

        list.addFirst(copy);

        return list;
    }

    public static List<Component> splitLines(Component text) {
        if (text == null) {
            return Collections.emptyList();
        }

        List<Component> lines = new ArrayList<>();

        List<Component> extracted = extract(text);
        if (!extracted.isEmpty()) {
            Component current = null;
            for (Component t : extracted) {
                if (t instanceof TextComponent && "\n".equals(((TextComponent) t).getText())) {
                    if (current == null) {
                        lines.add(new TextComponent(""));
                    } else {
                        lines.add(current);
                        current = null;
                    }

                    continue;
                }

                if (current == null) {
                    current = t;
                } else {
                    current.getSiblings().add(t);
                }
            }

            if (current != null) {
                lines.add(current);
            }
        }

        return lines;
    }
}
