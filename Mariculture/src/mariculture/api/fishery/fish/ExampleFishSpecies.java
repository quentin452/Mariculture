package mariculture.api.fishery.fish;

import java.util.Random;

import mariculture.api.core.MaricultureRegistry;
import mariculture.api.fishery.EnumRodQuality;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ExampleFishSpecies extends FishSpecies {
	public ExampleFishSpecies(int id) {
		super(id);
	}

	Random rand = new Random();

	@Override
	public EnumFishGroup getGroup() {
		return EnumFishGroup.DOMESTICATED;
	}

	@Override
	public int getLifeSpan() {
		return 10;
	}

	@Override
	public int getFertility() {
		return 60;
	}

	@Override
	public boolean isDominant() {
		return false;
	}
	
	@Override
	public ItemStack getProduct(Random rand) {
		if(MaricultureRegistry.get("dropletElectric") == null) {
			return null;
		}
		
		return (rand.nextInt(74) == 0)? MaricultureRegistry.get("dropletElectric"): null;
	}

	@Override
	public boolean canCatch(Random rand, World world, int x, int y, int z, EnumRodQuality quality) {
		return false;
	}
	
	@Override
	public double getFishOilVolume() {
		return 0.450;
	}

	@Override
	public void affectWorld(World world, int x, int y, int z, int tankType) {
		if (rand.nextInt(30) == 0) {
			world.addWeatherEffect(new EntityLightningBolt(world, x + (rand.nextInt(5) - rand.nextInt(10)), y, z
					+ (rand.nextInt(5) - rand.nextInt(10))));
		}
		return;
	}

	@Override
	public int[] getChestGenChance() {
		return new int[] { 1, 1, 3 };
	}
	
	@Override
	public int getFishMealSize() {
		return 3;
	}
}
