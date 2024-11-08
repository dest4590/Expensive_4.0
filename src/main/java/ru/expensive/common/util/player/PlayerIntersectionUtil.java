package ru.expensive.common.util.player;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import ru.expensive.common.QuickImports;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlayerIntersectionUtil implements QuickImports {
    public boolean isPlayerInWeb() {
        Box playerBox = mc.player.getBoundingBox();
        BlockPos playerPosition = BlockPos.ofFloored(mc.player.getPos());

        return getNearbyBlockPositions(playerPosition).stream()
                .anyMatch(pos -> isBlockCobweb(playerBox, pos));
    }

    private List<BlockPos> getNearbyBlockPositions(BlockPos center) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = center.getX() - 2; x <= center.getX() + 2; x++) {
            for (int y = center.getY() - 1; y <= center.getY() + 4; y++) {
                for (int z = center.getZ() - 2; z <= center.getZ() + 2; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    private boolean isBlockCobweb(Box playerBox, BlockPos blockPos) {
        return playerBox.intersects(new Box(blockPos)) && mc.world.getBlockState(blockPos).getBlock() == Blocks.COBWEB;
    }
}
