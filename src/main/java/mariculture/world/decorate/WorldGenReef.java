package mariculture.world.decorate;

import hacker.TimeMeasurement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mariculture.core.Core;
import mariculture.core.config.WorldGeneration.WorldGen;
import mariculture.core.helpers.BlockHelper;
import mariculture.core.lib.CoralMeta;
import mariculture.lib.util.Library;
import mariculture.world.WorldPlus;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenReef extends WorldGenerator {

    public boolean coralCanReplace(Block block) {
        return block == Blocks.sand || block == Blocks.gravel || block == Blocks.dirt || block == Core.limestone;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        if (!chunk.isChunkLoaded) {
            return false;
        }

        if (Library.DEBUG_ON) {
            TimeMeasurement.start("CoralReef");
        }

        ArrayList<Integer> coralPositions = new ArrayList<>();

        y = world.getTopSolidOrLiquidBlock(x, z);

        if (WorldGen.CORAL_EXTRA) {
            generateExtraCorals(world, rand, x, y, z, coralPositions);
        }

        generateCoralPositions(world, rand, x, z, coralPositions);

        placeCorals(world, rand, coralPositions);

        if (Library.DEBUG_ON) {
            TimeMeasurement.finish("CoralReef");
        }

        return true;
    }

    private void generateExtraCorals(World world, Random rand, int x, int y, int z, List<Integer> coralPositions) {
        for (int i = -rand.nextInt(16) - rand.nextInt(4); i < rand.nextInt(16) + rand.nextInt(4); i++) {
            int xPos = x + i;
            for (int j = -rand.nextInt(12) - rand.nextInt(i != 3 ? 1 : 5); j < rand.nextInt(14) + rand.nextInt(1); j++) {
                int zPos = z + j;
                if (world.getChunkProvider().chunkExists(xPos >> 4, zPos >> 4)) {
                    if (canPlaceCoral(world, xPos, y, zPos)) {
                        coralPositions.add(xPos);
                        coralPositions.add(y - 1);
                        coralPositions.add(zPos);
                    }
                }
            }
        }
    }

    private void generateCoralPositions(World world, Random rand, int x, int z, List<Integer> coralPositions) {
        int l = rand.nextInt(5 - 2) + 2;
        for (int i1 = x - l; i1 <= x + l; ++i1) {
            for (int j1 = z - l; j1 <= z + l; ++j1) {
                int k1 = i1 - x;
                int l1 = j1 - z;

                if (k1 * k1 + l1 * l1 <= l * l) {
                    int yBlock = world.getTopSolidOrLiquidBlock(i1, j1);
                    if (canPlaceCoral(world, i1, yBlock, j1)) {
                        coralPositions.add(i1);
                        coralPositions.add(yBlock - 1);
                        coralPositions.add(j1);
                    }
                }
            }
        }
    }

    private void placeCorals(World world, Random rand, List<Integer> coralPositions) {
        for (int i = 0; i < coralPositions.size(); i += 3) {
            int x2 = coralPositions.get(i);
            int y2 = coralPositions.get(i + 1);
            int z2 = coralPositions.get(i + 2);

            if (BlockHelper.isWater(world, x2, y2 + 2, z2) && BlockHelper.isWater(world, x2, y2 + 1, z2)) {
                if (rand.nextInt(25) > 0) {
                    world.setBlock(x2, y2, z2, WorldPlus.plantStatic, rand.nextInt(CoralMeta.COUNT - 5) + 2, 2);
                } else {
                    world.setBlock(x2, y2, z2, Blocks.sponge, 0, 2);
                }
            }
        }
    }

    private boolean canPlaceCoral(World world, int x, int y, int z) {
        return coralCanReplace(world.getBlock(x, y - 1, z)) &&
            BlockHelper.isWater(world, x, y + 1, z) &&
            BlockHelper.isWater(world, x, y + 2, z);
    }
}
