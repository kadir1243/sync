package dev.kir.sync.entity;

import dev.kir.sync.mixin.DimensionTypeAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class PersistentCameraEntityGoal {
    public static final double MAX_DISTANCE = 25;
    public static final long PHASE_DELAY = 200;
    public static final double MAX_Y = DimensionTypeAccessor.getOverworld().getHeight() * 1.01;
    public static final long MIN_PHASE_DURATION = 400;
    public static final long MAX_PHASE_DURATION = 2500;

    public final Vec3d pos;
    public final float yaw;
    public final float pitch;
    public final long delay;
    public final long duration;
    private final Consumer<PersistentCameraEntity> onTransitionFinished;

    private PersistentCameraEntityGoal(Vec3d pos, float yaw, float pitch, long delay, long duration, Consumer<PersistentCameraEntity> onTransitionFinished) {
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.delay = delay;
        this.duration = duration;
        this.onTransitionFinished = onTransitionFinished;
    }

    public void finish(PersistentCameraEntity camera) {
        if (this.onTransitionFinished != null) {
            this.onTransitionFinished.accept(camera);
        }
    }

    public PersistentCameraEntityGoal then(PersistentCameraEntityGoal nextGoal) {
        return this.then(camera -> camera.setGoal(nextGoal));
    }

    public PersistentCameraEntityGoal then(Consumer<PersistentCameraEntity> callback) {
        Consumer<PersistentCameraEntity> combined = callback == null ? this.onTransitionFinished : this.onTransitionFinished == null ? callback : this.onTransitionFinished.andThen(callback);
        return new PersistentCameraEntityGoal(this.pos, this.yaw, this.pitch, this.delay, this.duration, combined);
    }


    public static PersistentCameraEntityGoal create(BlockPos pos, float yaw, float pitch, long duration) {
        return create(pos, yaw, pitch, 0, duration, null);
    }

    public static PersistentCameraEntityGoal create(BlockPos pos, float yaw, float pitch, long delay, long duration, Consumer<PersistentCameraEntity> onTransitionFinished) {
        Vec3d vecPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return create(vecPos, yaw, pitch, delay, duration, onTransitionFinished);
    }

    public static PersistentCameraEntityGoal create(Vec3d pos, float yaw, float pitch, long duration) {
        return create(pos, yaw, pitch, 0, duration, null);
    }

    public static PersistentCameraEntityGoal create(Vec3d pos, float yaw, float pitch, long delay, long duration, Consumer<PersistentCameraEntity> onTransitionFinished) {
        return new PersistentCameraEntityGoal(pos, yaw, pitch, delay, duration, onTransitionFinished);
    }


    public static PersistentCameraEntityGoal tp(BlockPos pos, float yaw, float pitch) {
        return create(pos, yaw, pitch, 0, 0, null);
    }

    public static PersistentCameraEntityGoal tp(BlockPos pos, float yaw, float pitch, Consumer<PersistentCameraEntity> onTransitionFinished) {
        return create(pos, yaw, pitch, 0, 0, onTransitionFinished);
    }

    public static PersistentCameraEntityGoal limbo(BlockPos start, Direction startFacing, BlockPos target, Consumer<PersistentCameraEntity> onTransitionFinished) {
        return limbo(start, startFacing, MAX_Y, target, MIN_PHASE_DURATION, MAX_PHASE_DURATION, PHASE_DELAY, MAX_DISTANCE, onTransitionFinished);
    }

    public static PersistentCameraEntityGoal limbo(BlockPos start, Direction startFacing, double y, BlockPos target, long firstPhaseDuration, long secondPhaseDuration, long phaseDelay, double maxDistance, Consumer<PersistentCameraEntity> onTransitionFinished) {
        double dX = target.getX() - start.getX();
        double dZ = target.getZ() - start.getZ();
        double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
        if (horizontalDistance > maxDistance) {
            double factor = maxDistance / horizontalDistance;
            target = new BlockPos(start.add(dX * factor, 0, dZ * factor));
        }

        float yaw = startFacing.asRotation();
        float pitch = 90;
        BlockPos pos0 = start.offset(Direction.UP);
        Vec3d pos1 = new Vec3d(start.getX() + target.getX(), y * 2, start.getZ() + target.getZ()).multiply(0.5);

        PersistentCameraEntityGoal goal0 = create(pos0, yaw, pitch, firstPhaseDuration);
        PersistentCameraEntityGoal goal1 = create(pos1, yaw, pitch, phaseDelay, secondPhaseDuration, onTransitionFinished);

        return goal0.then(goal1);
    }

    public static PersistentCameraEntityGoal stairwayToHeaven(BlockPos start, Direction startFacing, BlockPos target, Consumer<PersistentCameraEntity> onTransitionFinished) {
        return stairwayToHeaven(start, startFacing, MAX_Y, target, MIN_PHASE_DURATION, MAX_PHASE_DURATION, PHASE_DELAY, MAX_DISTANCE, onTransitionFinished);
    }

    public static PersistentCameraEntityGoal stairwayToHeaven(BlockPos start, Direction startFacing, double y, BlockPos target, long firstPhaseDuration, long secondPhaseDuration, long phaseDelay, double maxDistance, Consumer<PersistentCameraEntity> onTransitionFinished) {
        double dX = target.getX() - start.getX();
        double dZ = target.getZ() - start.getZ();
        double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
        if (horizontalDistance > maxDistance) {
            double factor = maxDistance / horizontalDistance;
            target = new BlockPos(start.add(dX * factor, 0, dZ * factor));
        }

        float yaw = startFacing.asRotation();
        float pitch = 90;
        BlockPos pos0 = start.offset(startFacing.getOpposite());
        Vec3d pos1 = new Vec3d(start.getX() + target.getX(), y * 2, start.getZ() + target.getZ()).multiply(0.5);

        PersistentCameraEntityGoal goal0 = create(pos0, yaw, 0, firstPhaseDuration);
        PersistentCameraEntityGoal goal1 = create(pos1, yaw, pitch, phaseDelay, secondPhaseDuration, onTransitionFinished);

        return goal0.then(goal1);
    }

    public static PersistentCameraEntityGoal highwayToHell(BlockPos start, Direction startFacing, BlockPos target, Direction targetFacing, Consumer<PersistentCameraEntity> onTransitionFinished) {
        return highwayToHell(start, startFacing, MAX_Y, target, targetFacing, MAX_PHASE_DURATION, MIN_PHASE_DURATION, PHASE_DELAY, MAX_DISTANCE, onTransitionFinished);
    }

    public static PersistentCameraEntityGoal highwayToHell(BlockPos start, Direction startFacing, double y, BlockPos target, Direction targetFacing, long firstPhaseDuration, long secondPhaseDuration, long phaseDelay, double maxDistance, Consumer<PersistentCameraEntity> onTransitionFinished) {
        BlockPos pos0 = target.offset(targetFacing.getOpposite());
        float yaw0 = targetFacing.asRotation();
        float yaw1 = targetFacing.getOpposite().asRotation();

        Vec3d centerPoint = new Vec3d(start.getX() + target.getX(), y * 2, start.getZ() + target.getZ()).multiply(0.5);
        PersistentCameraEntityGoal tpGoal = null;
        double dX = centerPoint.x - target.getX();
        double dZ = centerPoint.z - target.getZ();
        double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
        if (horizontalDistance > maxDistance) {
            double factor = maxDistance / horizontalDistance;
            BlockPos centerPointPos = new BlockPos(target.add(dX * factor, 0, dZ * factor)).withY((int)centerPoint.y);
            tpGoal = PersistentCameraEntityGoal.tp(centerPointPos, startFacing.asRotation(), 90);
        }

        PersistentCameraEntityGoal goal0 = create(pos0, yaw0, 0, firstPhaseDuration);
        PersistentCameraEntityGoal goal1 = create(target, yaw1, 0, phaseDelay, secondPhaseDuration, onTransitionFinished);

        return tpGoal == null ? goal0.then(goal1) : tpGoal.then(goal0.then(goal1));
    }
}