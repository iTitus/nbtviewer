package io.github.ititus.nbtviewer.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;

public final class RayTraceHelper {

    private RayTraceHelper() {
    }

    public static Optional<RayTraceResult> rayTraceFromPlayerView() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hitResult != null && mc.hitResult.getType() == RayTraceResult.Type.ENTITY) {
            return Optional.of(mc.hitResult);
        }

        Entity renderViewEntity = mc.getCameraEntity();
        if (renderViewEntity == null) {
            return Optional.empty();
        }

        PlayerController playerController = mc.gameMode;
        if (playerController == null) {
            return Optional.empty();
        }

        return Optional.of(rayTrace(renderViewEntity, playerController.getPickRange(), false, 0.0f));
    }

    private static RayTraceResult rayTrace(Entity start, float reach, boolean fluids, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        boolean blockHit = mc.hitResult != null && mc.hitResult.getType() == RayTraceResult.Type.BLOCK;

        Vector3d eyePos = start.getEyePosition(partialTicks);
        Vector3d lookVec = start.getViewVector(partialTicks);

        Vector3d traceEnd;
        if (blockHit) {
            traceEnd = mc.hitResult.getLocation();
        } else {
            traceEnd = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        }

        World world = start.getCommandSenderWorld();
        AxisAlignedBB bound = new AxisAlignedBB(eyePos, traceEnd);
        EntityRayTraceResult r = ProjectileHelper.getEntityHitResult(
                world,
                start,
                eyePos,
                traceEnd,
                bound,
                null
        );
        if (r != null) {
            return r;
        }

        if (blockHit) {
            traceEnd = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        }

        RayTraceContext ctx = new RayTraceContext(
                eyePos,
                traceEnd,
                RayTraceContext.BlockMode.OUTLINE,
                fluids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE,
                start
        );
        return world.clip(ctx);
    }
}
