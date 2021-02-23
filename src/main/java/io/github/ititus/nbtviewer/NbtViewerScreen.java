package io.github.ititus.nbtviewer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.List;
import java.util.Optional;

public class NbtViewerScreen extends Screen {

    private final List<ITextComponent> content;
    private NbtViewerPanel panel;

    public NbtViewerScreen(List<ITextComponent> content) {
        super(new TranslationTextComponent("gui.nbtviewer.title"));
        this.content = content;
    }

    public static void tryOpen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused() || mc.currentScreen != null) {
            return;
        }

        World world = mc.world;
        if (world == null) {
            return;
        }

        Optional<RayTraceResult> optResult = RayTraceHelper.rayTraceFromPlayerView();
        if (!optResult.isPresent()) {
            return;
        }

        RayTraceResult r = optResult.get();
        if (r.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        Optional<INBT> optNbt;
        if (r instanceof BlockRayTraceResult) {
            BlockPos pos = ((BlockRayTraceResult) r).getPos();
            if (!world.isBlockLoaded(pos)) {
                return;
            }

            BlockState state = world.getBlockState(pos);
            if (!state.hasTileEntity()) {
                return;
            }

            TileEntity tile = world.getChunk(pos).getTileEntity(pos);
            optNbt = NbtHelper.getNbt(tile);
        } else if (r instanceof EntityRayTraceResult) {
            Entity e = ((EntityRayTraceResult) r).getEntity();
            optNbt = NbtHelper.getNbt(e);
        } else {
            return;
        }

        optNbt
                .map(nbt -> nbt.toFormattedComponent(" ", 0))
                .map(TextComponentHelper::splitLines)
                .map(NbtViewerScreen::new)
                .ifPresent(mc::displayGuiScreen);
    }

    @Override
    protected void init() {
        super.init();

        panel = new NbtViewerPanel(width - 20, height - 30, 20, 10);
        children.add(panel);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width / 2, 10, 0xFFFFFF);
        panel.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private class NbtViewerPanel extends ScrollPanel {

        public NbtViewerPanel(int width, int height, int top, int left) {
            super(minecraft, width, height, top, left);
        }

        @Override
        protected int getContentHeight() {
            return Math.max(content.size() * font.FONT_HEIGHT, height - border);
        }

        @Override
        protected void drawPanel(MatrixStack mStack, int entryRight, int relativeY, Tessellator tess, int mouseX,
                                 int mouseY) {
            for (ITextComponent line : content) {
                drawString(mStack, font, line, left + border, relativeY, 0xFFFFFF);
                relativeY += font.FONT_HEIGHT;
            }
        }
    }
}
