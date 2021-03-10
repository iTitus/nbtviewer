package io.github.ititus.nbtviewer.common.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public final class TextComponentHelper {

    private TextComponentHelper() {
    }

    private static List<ITextComponent> extract(ITextComponent text) {
        if (text == null) {
            return Collections.emptyList();
        } else if (text.getSiblings().isEmpty()) {
            return Collections.singletonList(text);
        }

        LinkedList<ITextComponent> list = new LinkedList<>();

        ITextComponent copy = text.copy();
        for (Iterator<ITextComponent> it = copy.getSiblings().iterator(); it.hasNext(); ) {
            ITextComponent next = it.next();
            list.addAll(extract(next));
            it.remove();
        }

        list.addFirst(copy);

        return list;
    }

    public static List<ITextComponent> splitLines(ITextComponent text) {
        if (text == null) {
            return Collections.emptyList();
        }

        List<ITextComponent> lines = new ArrayList<>();

        List<ITextComponent> extracted = extract(text);
        if (!extracted.isEmpty()) {
            ITextComponent current = null;
            for (ITextComponent t : extracted) {
                if (t instanceof StringTextComponent && "\n".equals(((StringTextComponent) t).getText())) {
                    if (current == null) {
                        lines.add(new StringTextComponent(""));
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
