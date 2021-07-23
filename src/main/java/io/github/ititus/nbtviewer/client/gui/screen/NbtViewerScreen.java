package io.github.ititus.nbtviewer.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import io.github.ititus.nbtviewer.client.util.RayTraceHelper;
import io.github.ititus.nbtviewer.common.util.ComponentHelper;
import io.github.ititus.nbtviewer.common.util.NbtHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.List;
import java.util.Optional;

public class NbtViewerScreen extends Screen {

    private final List<Component> content;
    private NbtViewerPanel panel;

    public NbtViewerScreen(List<Component> content) {
        super(new TranslatableComponent("gui.nbtviewer.title"));
        this.content = content;
    }

    public static void tryOpen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused() || mc.screen != null) {
            return;
        }

        Level world = mc.level;
        if (world == null) {
            return;
        }

        Optional<HitResult> optResult = RayTraceHelper.rayTraceFromPlayerView();
        if (!optResult.isPresent()) {
            return;
        }

        HitResult r = optResult.get();
        if (r.getType() == HitResult.Type.MISS) {
            return;
        }

        Optional<Tag> optNbt;
        if (r instanceof BlockHitResult) {
            BlockPos pos = ((BlockHitResult) r).getBlockPos();
            if (!world.isLoaded(pos)) {
                return;
            }

            BlockState state = world.getBlockState(pos);
            if (!state.hasBlockEntity()) {
                return;
            }

            BlockEntity tile = world.getChunk(pos).getBlockEntity(pos);
            optNbt = NbtHelper.getNbt(tile);
        } else if (r instanceof EntityHitResult) {
            Entity e = ((EntityHitResult) r).getEntity();
            optNbt = NbtHelper.getNbt(e);
        } else {
            return;
        }

        optNbt
                .map(nbt -> NbtHelper.toPrettyComponent(nbt, " "))
                .map(ComponentHelper::splitLines)
                .map(NbtViewerScreen::new)
                .ifPresent(mc::setScreen);
    }

    @Override
    protected void init() {
        super.init();

        panel = new NbtViewerPanel(width - 20, height - 30, 20, 10);
        addRenderableWidget(panel);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width / 2, 10, 0xFFFFFF);
        panel.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private class NbtViewerPanel extends ScrollPanel implements NarratableEntry {

        public NbtViewerPanel(int width, int height, int top, int left) {
            super(minecraft, width, height, top, left);
        }

        @Override
        protected int getContentHeight() {
            return Math.max(content.size() * font.lineHeight, height - border);
        }

        @Override
        protected void drawPanel(PoseStack mStack, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            for (Component line : content) {
                GuiComponent.drawString(mStack, font, line, left + border, relativeY, 0xFFFFFF);
                relativeY += font.lineHeight;
            }
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
        }
    }
}
