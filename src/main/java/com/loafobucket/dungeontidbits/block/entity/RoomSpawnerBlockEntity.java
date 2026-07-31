package com.loafobucket.dungeontidbits.block.entity;

import com.loafobucket.dungeontidbits.Config;
import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.block.custom.RoomSpawnerBlock;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

//a whole lot of copying from BaseSpawner
public class RoomSpawnerBlockEntity extends BlockEntity {
    public Random random = new Random();
    public static int mobCount = 4;
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean posSet = false;
    private int progress = 0;
    private int maxProgress = 60;
    private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
    @Nullable
    private SpawnData nextSpawnData;
    private List<Double> posListX = new ArrayList<>();
    private List<Double> posListY = new ArrayList<>();
    private List<Double> posListZ = new ArrayList<>();

    private void resetProgress() {
        progress = 0;
        maxProgress = 60;
        posListX.clear();
        posListY.clear();
        posListZ.clear();
    }

    public RoomSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ROOM_SPAWNER_BE.get(), pos, blockState);
    }

    private boolean isActivated(BlockState state) {
        return state.is(ModBlocks.ROOM_SPAWNER.get()) && state.getValue(RoomSpawnerBlock.TRIGGERED);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, RoomSpawnerBlockEntity blockEntity) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            boolean activated = blockEntity.isActivated(blockState);
            boolean posset = blockEntity.posSet;
            RandomSource randomsource = level.random;
            if (activated && !posset) {
                level.setBlock(blockPos, blockState.setValue(RoomSpawnerBlock.TRIGGERED, false), 2);
                blockEntity.resetProgress();
                if (blockState.getValue(RoomSpawnerBlock.LEVEL) != 4) {
                    level.setBlock(blockPos, blockState.setValue(RoomSpawnerBlock.LEVEL, blockState.getValue(RoomSpawnerBlock.LEVEL)+1), 2);
                } else {
                    level.destroyBlock(blockPos, true);
                    return;
                }
                level.playSound(null, blockPos,SoundEvents.CHAIN_BREAK,SoundSource.BLOCKS,1f,0.6f);
                if (blockEntity.getPosList(serverLevel, blockPos)) {
                    blockEntity.posSet = true;
                }
            } else if (activated && posset) {
                blockEntity.progress++;
                setChanged(level, blockPos, blockState);
                if (blockEntity.progress >= blockEntity.maxProgress) {
                    for (int i = 0; i < mobCount; i++) {
                        Vec3 pos = new Vec3(blockEntity.posListX.get(i),blockEntity.posListY.get(i),blockEntity.posListZ.get(i));
                        SpawnData spawndata = blockEntity.createNextSpawnData(serverLevel, randomsource, blockPos);
                        CompoundTag compoundtag = spawndata.getEntityToSpawn();
                        serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.3f, 1f);
                        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.x ,pos.y, pos.z, 20, 0, 0, 0, 0.3);
                        Entity entity = EntityType.loadEntityRecursive(compoundtag, serverLevel, (switcheroo) -> {
                            switcheroo.moveTo(pos.x, pos.y, pos.z, switcheroo.getYRot(), switcheroo.getXRot());
                            return switcheroo;
                        });
                        if (entity == null) {
                            level.destroyBlock(blockPos, false);
                            return;
                        }
                        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                        level.addFreshEntity(entity);
                    }
                    blockEntity.posSet = false;
                    level.setBlock(blockPos, blockState.setValue(RoomSpawnerBlock.TRIGGERED, false), 2);
                    blockEntity.resetProgress();
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        ListTag listTagX = new ListTag();
        ListTag listTagY = new ListTag();
        ListTag listTagZ = new ListTag();

        if (this.nextSpawnData != null) {
            pTag.put("SpawnData", (Tag)SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, this.nextSpawnData).getOrThrow((p_337966_) -> {
                return new IllegalStateException("Invalid SpawnData: " + p_337966_);
            }));
        }
        pTag.put("SpawnPotentials", (Tag)SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).getOrThrow());

        for (Double entry : this.posListX) {
            listTagX.add(DoubleTag.valueOf(entry));
        }
        for (Double entry : this.posListY) {
            listTagY.add(DoubleTag.valueOf(entry));
        }
        for (Double entry : this.posListZ) {
            listTagZ.add(DoubleTag.valueOf(entry));
        }
        pTag.put("PosListX", listTagX);
        pTag.put("PosListY", listTagY);
        pTag.put("PosListZ", listTagZ);
        pTag.putInt("SpawnProgress", progress);
        pTag.putInt("SpawnMaxProgress", maxProgress);
        pTag.putBoolean("PosSet", posSet);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.posListX.clear();
        if (pTag.contains("PosListX", Tag.TAG_LIST)) {
            ListTag listTag = pTag.getList("PosListX", Tag.TAG_DOUBLE);
            for (int i = 0; i < listTag.size(); i++) {
                this.posListX.add(listTag.getDouble(i));
            }
        }
        this.posListY.clear();
        if (pTag.contains("PosListY", Tag.TAG_LIST)) {
            ListTag listTag = pTag.getList("PosListY", Tag.TAG_DOUBLE);
            for (int i = 0; i < listTag.size(); i++) {
                this.posListY.add(listTag.getDouble(i));
            }
        }
        this.posListZ.clear();
        if (pTag.contains("PosListZ", Tag.TAG_LIST)) {
            ListTag listTag = pTag.getList("PosListZ", Tag.TAG_DOUBLE);
            for (int i = 0; i < listTag.size(); i++) {
                this.posListZ.add(listTag.getDouble(i));
            }
        }
        boolean flag = pTag.contains("SpawnData", 10);
        if (flag) {
            SpawnData spawndata = (SpawnData)SpawnData.CODEC.parse(NbtOps.INSTANCE, pTag.getCompound("SpawnData")).resultOrPartial((p_186391_) -> {
                LOGGER.warn("Invalid SpawnData: {}", p_186391_);
            }).orElseGet(SpawnData::new);
            this.setNextSpawnData(spawndata);
        }
        boolean flag1 = pTag.contains("SpawnPotentials", 9);
        if (flag1) {
            ListTag listtag = pTag.getList("SpawnPotentials", 10);
            this.spawnPotentials = (SimpleWeightedRandomList)SpawnData.LIST_CODEC.parse(NbtOps.INSTANCE, listtag).resultOrPartial((p_186388_) -> {
                LOGGER.warn("Invalid SpawnPotentials list: {}", p_186388_);
            }).orElseGet(SimpleWeightedRandomList::empty);
        } else {
            this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData());
        }
        progress = pTag.getInt("SpawnProgress");
        maxProgress = pTag.getInt("SpawnMaxProgress");
        posSet = pTag.getBoolean("PosSet");
    }

    public boolean getPosList(ServerLevel level, BlockPos blockPos) {
        int attempts = mobCount * 3;
        int spawnRange = 7;
        ArrayList<Vec3> posList = new ArrayList<>();
        for (int i = 0; i < attempts; ++i) {
            Vec3 spawnPos =  new Vec3(
                    blockPos.getX() + Math.floor((random.nextDouble(0.5) + ((double)random.nextInt(2) * 1.5 - 1.0)) * spawnRange) + 0.5,
                    blockPos.getY() + random.nextInt(7) - 1,
                    blockPos.getZ() + Math.floor((random.nextDouble(0.5) + ((double)random.nextInt(2) * 1.5 - 1.0)) * spawnRange) + 0.5);
            double x = spawnPos.x;
            double y = getSpawnY(level, spawnPos, blockPos);
            double z = spawnPos.z;

            if (level.noCollision(EntityType.ARMOR_STAND.getSpawnAABB(x, y, z))) {
                posList.add(new Vec3(x, y, z));
            }
        }
        if (posList.size() >= mobCount) {
            for (int i = 0; i < mobCount; i++) {
                Vec3 pos = posList.get(i);
                level.sendParticles(ParticleTypes.POOF, pos.x ,pos.y+1, pos.z, 20, 0.5, 0.5, 0.5, 0);
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 0.7f, 1f);
                posListX.add(i, pos.x);
                posListY.add(i, pos.y);
                posListZ.add(i, pos.z);
            }
            return true;
        } else {
            return false;
        }
    }

    //spreadplayers command
    public double getSpawnY(BlockGetter level, Vec3 pos, BlockPos blockPos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(pos.x(), (pos.y() + 1), pos.z());
        boolean flag = level.getBlockState(blockpos$mutableblockpos).isAir();
        blockpos$mutableblockpos.move(Direction.DOWN);
        boolean flag2;
        for(boolean flag1 = level.getBlockState(blockpos$mutableblockpos).isAir(); blockpos$mutableblockpos.getY() >= (blockPos.getY()-1); flag1 = flag2) {
            blockpos$mutableblockpos.move(Direction.DOWN);
            flag2 = level.getBlockState(blockpos$mutableblockpos).isAir();
            if (!flag2 && flag1 && flag) {
                return blockpos$mutableblockpos.getY() + 1;
            }
            flag = flag1;
        }
        return pos.y + 1;
    }

    private SpawnData createNextSpawnData(@Nullable Level level, RandomSource random, BlockPos pos) {
        this.setNextSpawnData((SpawnData)this.spawnPotentials.getRandom(random).map(WeightedEntry.Wrapper::data).orElseGet(SpawnData::new));
        return this.nextSpawnData;
    }

    protected void setNextSpawnData(SpawnData nextSpawnData) {
        this.nextSpawnData = nextSpawnData;
    }
}
