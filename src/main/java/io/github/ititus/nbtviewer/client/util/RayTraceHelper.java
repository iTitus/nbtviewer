package io.github.ititus.nbtviewer.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class RayTraceHelper {

    private RayTraceHelper() {
    }

    public static Optional<HitResult> rayTraceFromPlayerView() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
            return Optional.of(mc.hitResult);
        }

        Entity renderViewEntity = mc.getCameraEntity();
        if (renderViewEntity == null) {
            return Optional.empty();
        }

        MultiPlayerGameMode playerController = mc.gameMode;
        if (playerController == null) {
            return Optional.empty();
        }

        return Optional.of(rayTrace(renderViewEntity, playerController.getPickRange(), false, 0.0f));
    }

    private static HitResult rayTrace(Entity start, float reach, boolean fluids, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        boolean blockHit = mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK;

        Vec3 eyePos = start.getEyePosition(partialTicks);
        Vec3 lookVec = start.getViewVector(partialTicks);

        Vec3 traceEnd;
        if (blockHit) {
            traceEnd = mc.hitResult.getLocation();
        } else {
            traceEnd = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        }

        Level world = start.getCommandSenderWorld();
        AABB bound = new AABB(eyePos, traceEnd);
        EntityHitResult r = ProjectileUtil.getEntityHitResult(
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

        ClipContext ctx = new ClipContext(
                eyePos,
                traceEnd,
                ClipContext.Block.OUTLINE,
                fluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                start
        );
        return world.clip(ctx);
    }
}
