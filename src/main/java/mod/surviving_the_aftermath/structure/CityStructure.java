package mod.surviving_the_aftermath.structure;

import mod.surviving_the_aftermath.Main;
import mod.surviving_the_aftermath.init.ModStructurePieceTypes;
import mod.surviving_the_aftermath.init.ModStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class CityStructure extends AbstractStructure {

	public CityStructure(StructureSettings settings) {
		super(settings);
	}

	@Override
	public StructureType<?> type() {
		return ModStructureTypes.CITY.get();
	}

	@Override
	public StructurePieceType pieceType() {
		return ModStructurePieceTypes.CITY.get();
	}

	@Override
	public ResourceLocation location() {
		return Main.asResource("city");
	}

	public static class Piece extends AbstractStructure.Piece {

		public Piece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(ModStructurePieceTypes.CITY.get(), context.structureTemplateManager(), tag);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
				RandomSource rand, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
			super.postProcess(level, structureManager, generator, rand, box, chunkPos, pos);
			BlockPos spawnPos = new BlockPos(rand.nextInt(box.minX(), box.maxX()), pos.getY(), rand.nextInt(box.minZ(), box.maxZ()));
			for (int y = spawnPos.getY(); y < box.maxY(); y++) {
				if (getBlock(level, spawnPos.getX(), y, spawnPos.getZ(), box).isAir()
						&& getBlock(level, spawnPos.getX(), y + 1, spawnPos.getZ(), box).isAir()) {
					Villager villager = EntityType.VILLAGER.create(level.getLevel());
					villager.moveTo(spawnPos.getX(), y, spawnPos.getZ());
					BuiltInRegistries.VILLAGER_TYPE.getRandom(rand).ifPresent((profession) ->
							villager.setVillagerData(villager.getVillagerData().setType(profession.value())));
					BuiltInRegistries.VILLAGER_PROFESSION.getRandom(rand).ifPresent((profession) ->
							villager.setVillagerData(villager.getVillagerData().setProfession(profession.value())));
					level.addFreshEntity(villager);
					break;
				}
			}
		}

	}
}