package mariculture.core.handlers;

import java.util.Random;

import mariculture.api.core.MaricultureHandlers;
import mariculture.api.core.Environment.Salinity;
import mariculture.core.Core;
import mariculture.core.config.WorldGeneration.OreGen;
import mariculture.core.config.WorldGeneration.WorldGen;
import mariculture.core.helpers.BlockHelper;
import mariculture.core.lib.RockMeta;
import mariculture.core.tile.TileOyster;
import mariculture.core.world.WorldGenOre;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenHandler implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.dimensionId) {
            case -1:
                break;
            case 0:
                generateSurface(world, random, chunkX * 16, chunkZ * 16);
            case 1:
                break;
            default:
                generateSurface(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private void generateSurface(World world, Random random, int x, int z) {
        generateBauxite(world, random, x, z);
        generateCopper(world, random, x, z);
        generateOyster(world, random, x, z);
    }

    public static void generateBauxite(World world, Random random, int x, int z) {
        int posX, posY, posZ;
        // Layers 60-256 for Bauxite
        if (OreGen.BAUXITE_ON) {
            for (int i = 0; i < OreGen.BAUXITE_TOTAL; i++) {
                posX = x + random.nextInt(16);
                posY = OreGen.BAUXITE_MIN + random.nextInt(OreGen.BAUXITE_MAX - OreGen.BAUXITE_MIN);
                posZ = z + random.nextInt(16);
                new WorldGenOre(Core.rocks, RockMeta.BAUXITE, OreGen.BAUXITE_VEIN, Blocks.stone).generate(world, random, posX, posY, posZ);
            }
        }
    }

    public static void generateCopper(World world, Random random, int x, int z) {
        int posX, posY, posZ;
        // Layers 1-64 for Copper
        if (OreGen.COPPER_ON) {
            for (int i = 0; i < OreGen.COPPER_TOTAL; i++) {
                posX = x + random.nextInt(16);
                posY = OreGen.COPPER_MIN + random.nextInt(OreGen.COPPER_MAX - OreGen.COPPER_MIN);
                posZ = z + random.nextInt(16);
                new WorldGenOre(Core.rocks, RockMeta.COPPER, OreGen.COPPER_VEIN, Blocks.stone).generate(world, random, posX, posY, posZ);
            }
        }
    }

    // Generates Oysters in the Ocean
    public static void generateOyster(World world, Random random, int x, int z) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        if (!chunk.isChunkLoaded) {
            return;
        }

        if (WorldGen.OYSTER_ENABLED) {
            for (int j = 0; j < WorldGen.OYSTER_PER_CHUNK; j++) {
                int chance = WorldGen.OYSTER_CHANCE;
                if (random.nextInt(Math.max(1, chance)) == 0) {
                    int randMeta = random.nextInt(4);
                    int randX = x - 8 + random.nextInt(4);
                    int randZ = z - 8 + random.nextInt(4);

                    int blockY = world.getHeightValue(randX, randZ); 

                    if (blockY > 0 && world.blockExists(randX, blockY, randZ)) {
                        if (MaricultureHandlers.environment.getSalinity(world, randX, randZ) == Salinity.SALINE
                            && Core.water.canBlockStay(world, randX, blockY, randZ)
                            && BlockHelper.isWater(world, randX, blockY + 1, randZ)) {

                            world.setBlock(randX, blockY, randZ, Core.water);
                            TileOyster oyster = (TileOyster) world.getTileEntity(randX, blockY, randZ);
                            if (oyster != null) {
                                oyster.orientation = ForgeDirection.values()[2 + random.nextInt(4)];
                                if (random.nextInt(WorldGen.OYSTER_PEARL_CHANCE) == 0) {
                                    oyster.setInventorySlotContents(0, PearlGenHandler.getRandomPearl(random));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
