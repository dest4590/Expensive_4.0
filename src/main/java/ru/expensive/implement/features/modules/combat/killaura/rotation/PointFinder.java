package ru.expensive.implement.features.modules.combat.killaura.rotation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.QuickImports;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class PointFinder implements QuickImports {
    private Random random = new SecureRandom();
    @NonFinal
    private Vec3d offset = Vec3d.ZERO;

    public Vec3d computeVector(LivingEntity entity, float maxDistance, Angle initialAngle, Vec3d velocity) {
        List<Vec3d> candidatePoints = generateCandidatePoints(entity, maxDistance);

        Vec3d bestVector = findBestVector(candidatePoints, initialAngle);

        updateOffset(velocity);

        return (bestVector == null ? entity.getEyePos() : bestVector).add(offset);
    }

    private List<Vec3d> generateCandidatePoints(LivingEntity entity, float maxDistance) {
        Box entityBox = entity.getBoundingBox().expand(-0.18F);
        double stepY = entityBox.getYLength() / 10.0F;

        return Stream.iterate(entityBox.minY, y -> y <= entityBox.maxY, y -> y + stepY)
                .map(y -> new Vec3d(entityBox.getCenter().x, y, entityBox.getCenter().z))
                .filter(point -> isWithinDistance(mc.player.getEyePos(), point, maxDistance))
                .collect(Collectors.toList());
    }

    private boolean isWithinDistance(Vec3d startPoint, Vec3d endPoint, float maxDistance) {
        return startPoint.distanceTo(endPoint) < maxDistance;
    }

    private Vec3d findBestVector(List<Vec3d> candidatePoints, Angle initialAngle) {
        Vec3d playerEyePos = mc.player.getEyePos();

        return candidatePoints.stream()
                .sorted(Comparator.comparing(point -> calculateRotationDifference(playerEyePos, point, initialAngle)))
                .findFirst()
                .orElse(null);
    }

    private double calculateRotationDifference(Vec3d startPoint, Vec3d endPoint, Angle initialAngle) {
        Angle targetAngle = AngleUtil.fromVec3d(endPoint.subtract(startPoint));
        Angle delta = AngleUtil.calculateDelta(initialAngle, targetAngle);
        return Math.hypot(delta.getYaw(), delta.getPitch());
    }

    private void updateOffset(Vec3d velocity) {
        offset = offset.add(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).multiply(velocity);
    }
}
