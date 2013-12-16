package mariculture.world.terrain;

import mariculture.api.core.EnumBiomeType;
import mariculture.api.core.MaricultureHandlers;
import mariculture.core.lib.WorldGeneration;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineWaterBOP extends MapGenRavine
{
    protected void recursiveGenerate(World world, int par2, int par3, int chunkX, int chunkZ, byte[] data) {
    	if(MaricultureHandlers.biomeType.getBiomeType(world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16)) == EnumBiomeType.OCEAN) {
    		if (this.rand.nextInt(WorldGeneration.RAVINE_CHANCE) == 0) {
                double d0 = (double)(par2 * 16 + this.rand.nextInt(16));
                double d1 = (double)(this.rand.nextInt(this.rand.nextInt(80) + 16) + 40);
                double d2 = (double)(par3 * 16 + this.rand.nextInt(16));
                byte b0 = 1;

                for (int i1 = 0; i1 < b0; ++i1) {
                    float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                    float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                    float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                    try {
                    	this.generateRavine(this.rand.nextLong(), chunkX, chunkZ, data, d0, d1, d2, f2, f, f1, 0, 0, 15.0D);
                    } catch (Exception e) {
                    	try {
                    		this.generateRavine(this.rand.nextLong(), chunkX, chunkZ, data, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
                    	} catch (Exception ex) {
                    		ex.printStackTrace();
                    	}
                    }
                }
            }
    	} else {
    		super.recursiveGenerate(world, par2, par3, chunkX, chunkZ, data);
    	}
    }

    @Override
    protected boolean isOceanBlock(byte[] data, int index, int x, int y, int z, int chunkX, int chunkZ) {
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
    	if(MaricultureHandlers.biomeType.getBiomeType(biome) == EnumBiomeType.OCEAN && y < 63) {
    		return false;
    	}
    	
    	return super.isOceanBlock(data, index, x, y, z, chunkX, chunkZ);
    }
    
    private boolean isTopBlock(byte[] data, int index, int x, int y, int z, int chunkX, int chunkZ)  {
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        return (isExceptionBiome(biome) ? data[index] == Block.grass.blockID : data[index] == biome.topBlock);
    }

    private boolean isExceptionBiome(BiomeGenBase biome)  {
        if (biome == BiomeGenBase.mushroomIsland) return true;
        if (biome == BiomeGenBase.beach) return true;
        if (biome == BiomeGenBase.desert) return true;
        return false;
    }

    @Override
    protected void digBlock(byte[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
    	BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
    	if(MaricultureHandlers.biomeType.getBiomeType(biome) == EnumBiomeType.OCEAN && y < 63) {
	        int top    = (isExceptionBiome(biome) ? Block.grass.blockID : biome.topBlock);
	        int filler = (isExceptionBiome(biome) ? Block.dirt.blockID  : biome.fillerBlock);
	        int block  = data[index];
	
	        if (block == Block.stone.blockID || block == filler || block == top) {
	            data[index] = (byte)Block.waterMoving.blockID;
	            if (foundTop && data[index - 1] == filler) {
	            	data[index - 1] = (byte)top;
	            }
	        }
    	} else {
    		super.digBlock(data, index, x, y, z, chunkX, chunkZ, foundTop);
    	}
    }
}
