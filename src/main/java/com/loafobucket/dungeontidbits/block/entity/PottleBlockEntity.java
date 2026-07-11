package com.loafobucket.dungeontidbits.block.entity;

import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.block.custom.PottleBlock;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.misc.ModTags;
import com.loafobucket.dungeontidbits.recipe.ModRecipes;
import com.loafobucket.dungeontidbits.recipe.PottleNormalRecipe;
import com.loafobucket.dungeontidbits.recipe.PottleRecipe;
import com.loafobucket.dungeontidbits.recipe.PottleRecipeInput;
import com.loafobucket.dungeontidbits.screen.custom.PottleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PottleBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                if (isInputSlot(slot)) {
                    if (!isRecipeValid()) {
                        resetProgress();
                    }
                }
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot >= FIRST_INPUT_SLOT && slot <= LAST_INPUT_SLOT) {
                ItemStack existingStack = itemHandler.getStackInSlot(slot);
                if (!existingStack.isEmpty() && !ItemStack.isSameItem(existingStack, stack)) {
                    return stack;
                }
            }
            return super.insertItem(slot, stack, simulate);
        }
    };

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.POTTLE_BE.get(),
                (be, side) -> be.getItemHandler(side)
        );
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return itemHandler;
        }
        Direction facing = getBlockState().getValue(PottleBlock.FACING);
        if (side == Direction.UP) {
            return new InputItemHandler(itemHandler);
        } else if (side == Direction.DOWN){
            return new OutputItemHandler(itemHandler);
        } else if (side == facing.getClockWise()){
            return new EffectItemHandlerA(itemHandler);
        } else if (side == facing.getOpposite()){
            return new EffectItemHandlerB(itemHandler);
        } else if (side == facing.getCounterClockWise()){
            return new EffectItemHandlerC(itemHandler);
        } else {
            return new EffectItemHandler(itemHandler);
        }
    }
    private boolean isInputSlot(int slot) {
        return slot >= FIRST_INPUT_SLOT && slot <= LAST_INPUT_SLOT;
    }

    public static final int FIRST_INPUT_SLOT = 0;
    public static final int LAST_INPUT_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;
    public static final int EXTRA_SLOT = 5;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    private boolean isActivated(BlockState state) {
        return state.is(ModBlocks.POTTLE.get()) && state.getValue(PottleBlock.TRIGGERED);
    }
    private boolean canActivate = true;

    public PottleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.POTTLE_BE.get(), pPos, pBlockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> PottleBlockEntity.this.progress;
                    case 1 -> PottleBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: PottleBlockEntity.this.progress = value;
                    case 1: PottleBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }
//thank you alex's caves
    private static Vec3 rotateCenteredVec(Vec3 offset, Direction facing){
        Vec3 rotate = offset;
        switch (facing){
            case NORTH:
                rotate = offset;
                break;
            case SOUTH:
                rotate = offset.yRot((float) (Math.PI));
                break;
            case WEST:
                rotate = offset.yRot((float) (Math.PI / 2F));
                break;
            case EAST:
                rotate = offset.yRot(-(float) (Math.PI / 2F));
                break;
        }
        return rotate;
    }

    //@Override
    public Component getDisplayName() {
        return Component.translatable("block.dungeontidbits.pottle");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PottleMenu(i, inventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("pottleprogress", progress);
        pTag.putInt("pottlemaxprogress", maxProgress);
        pTag.putBoolean("canactivate", canActivate);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("pottleprogress");
        maxProgress = pTag.getInt("pottlemaxprogress");
        canActivate = pTag.getBoolean("canactivate");
    }
// thank you scorched guns
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, PottleBlockEntity blockEntity) {
        if (!level.isClientSide) {
            boolean hasValidRecipe = blockEntity.hasRecipe();
            boolean activated = blockEntity.isActivated(blockState);
            boolean canactivate = blockEntity.canActivate;
            if (!activated) {
                blockEntity.canActivate = true;
                if (hasValidRecipe) {
                    blockEntity.progress++;
                    setChanged(level, blockPos, blockState);
                    if (blockEntity.progress >= blockEntity.maxProgress) {
                        blockEntity.craftItem();
                        blockEntity.resetProgress();
                    }
                } else if (!hasValidRecipe) {
                    blockEntity.resetProgress();
                }
            } else {
                if(canactivate) {
                    blockEntity.canActivate = false;
                    level.playSound(null, blockPos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1, 0.7f);
                    List<MobEffectInstance> effectList = new ArrayList<>(List.of());
                    int cloudDuration = 0;
                    int tickDuration = 20;
                    if (blockEntity.itemHandler.getStackInSlot(0).is(ModTags.Items.SMOKY_ITEMS) && blockEntity.itemHandler.getStackInSlot(0).getCount()>=8) {
                        tickDuration = 30;
                    }
                    for (int i = 1; i < 4; i++) {
                        ItemStack itemStack = blockEntity.itemHandler.getStackInSlot(i);
                        int j = itemStack.getCount();
                        if (itemStack.is(ModItems.EFFECT_EXTRACT.get()) && !itemStack.get(DataComponents.POTION_CONTENTS).customEffects().isEmpty()) {
                            blockEntity.itemHandler.extractItem(i, Math.min(j, 16), false);
                            MobEffectInstance effect = new MobEffectInstance(itemStack.get(DataComponents.POTION_CONTENTS).customEffects().getFirst().getEffect(), Math.min(j, 16) * tickDuration);
                            effectList.add(effect);
                            cloudDuration = cloudDuration + Math.min(j, 16);
                        }
                    }
                    if (!effectList.isEmpty()) {
                        AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        if (tickDuration == 30 && ModList.get().isLoaded("nirvana")) {
                            blockEntity.itemHandler.extractItem(0, 8, false);
                            MobEffectInstance effect = new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse("nirvana:peace")).get(), 320, 4);
                            effectList.add(effect);
                            areaeffectcloud.setParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE);
                            areaeffectcloud.setRadius(0.3F);
                            areaeffectcloud.setRadiusPerTick(0.002F);
                            areaeffectcloud.setDuration(800);
                            level.playSound(null, blockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5f, 0.7f);
                        } else {
                            areaeffectcloud.setRadius(0.6F);
                            areaeffectcloud.setRadiusPerTick(0F);
                            areaeffectcloud.setDuration(cloudDuration * 5);
                            level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1, 0.7f);
                        }
                        PotionContents cloudEffect = new PotionContents(Optional.empty(), Optional.empty(), effectList);
                        Direction facing = blockState.getValue(PottleBlock.FACING);
                        Vec3 towards = rotateCenteredVec(new Vec3(0, -0.4F, -0.75F), facing);
                        areaeffectcloud.setPos(towards.add(blockPos.getCenter()));
                        areaeffectcloud.setRadiusOnUse(0f);
                        areaeffectcloud.setWaitTime(10);
                        areaeffectcloud.setPotionContents(cloudEffect);
                        level.addFreshEntity(areaeffectcloud);
                    }
                }
            }
        }
    }

    private void craftItem() {
        Optional<PottleRecipe> match = getRecipe();
        RandomSource random = RandomSource.create();
        if (match.isPresent()) {
            PottleRecipe recipe = match.get();
            ItemStack resultItem = recipe.assemble(createRecipeInput(), level.registryAccess());
            Float resultChance = recipe.getResultChance(level.registryAccess());
            ItemStack extraItem = recipe.getExtraItem(level.registryAccess());
            Float extraChance = recipe.getExtraChance(level.registryAccess());
            ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
            ItemStack extraStack = itemHandler.getStackInSlot(EXTRA_SLOT);

            if ((outputStack.isEmpty() || (ItemStack.isSameItemSameComponents(outputStack, resultItem) && outputStack.getCount() + resultItem.getCount() <= outputStack.getMaxStackSize())) &&
                    (extraStack.isEmpty() || (ItemStack.isSameItemSameComponents(extraStack, extraItem) && extraStack.getCount() + extraItem.getCount() <= extraStack.getMaxStackSize()))) {
                for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT; i++) {
                    itemHandler.extractItem(i, 1, false);
                }
                level.playSound(null, getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS);
                if (random.nextDouble() < resultChance) {
                    if (outputStack.isEmpty()) {
                        itemHandler.setStackInSlot(OUTPUT_SLOT, resultItem.copy());

                    } else {
                        outputStack.grow(resultItem.getCount());
                    }
                }
                if (random.nextDouble() < extraChance) {
                    if (extraStack.isEmpty()) {
                        itemHandler.setStackInSlot(EXTRA_SLOT, extraItem.copy());
                    } else {
                        extraStack.grow(extraItem.getCount());
                    }
                }
            }
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 100;
    }

    private boolean hasRecipe() {
        return getRecipe().isPresent();
    }

    private boolean isRecipeValid() {
        Optional<PottleNormalRecipe> currentRecipe = getCurrentRecipe();
        if (currentRecipe.isPresent()) {
            PottleRecipe recipe = currentRecipe.get();
            return recipe.matches(createRecipeInput(), level);
        }
        return false;
    }

    private Optional<PottleNormalRecipe> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.POTTLE_TYPE.get()).stream()
                .map(recipe -> recipe.value())
                .filter(recipe -> recipe.matches(createRecipeInput(), level))
                .findFirst();
    }

    private Optional<PottleRecipe> getRecipe() {
        if (level == null) return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.POTTLE_TYPE.get(),createRecipeInput(), level)
                .map(recipe -> recipe.value());
    }

    private PottleRecipeInput createRecipeInput() {
        List<ItemStack> inputs = new ArrayList<>(LAST_INPUT_SLOT - FIRST_INPUT_SLOT + 1);
        for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT; i++) {
            inputs.add(itemHandler.getStackInSlot(i));
        }
        return new PottleRecipeInput(inputs);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

//thank you scorched guns (2)
private class InputItemHandler implements IItemHandlerModifiable {
    private final ItemStackHandler itemHandler;
    public InputItemHandler(ItemStackHandler itemHandler) {this.itemHandler = itemHandler;}
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {itemHandler.setStackInSlot(slot, stack);}
    @Override
    public int getSlots() {return itemHandler.getSlots();}
    @Override
    public ItemStack getStackInSlot(int slot) {return itemHandler.getStackInSlot(slot);}
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot == FIRST_INPUT_SLOT && !stack.is(ModItems.EFFECT_EXTRACT)) {
            return itemHandler.insertItem(slot, stack, simulate);
        }
        return stack;
    }
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {return ItemStack.EMPTY;}
    @Override
    public int getSlotLimit(int slot) {return itemHandler.getSlotLimit(slot);}
    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
        }
}
    private class EffectItemHandler implements IItemHandlerModifiable {
        private final ItemStackHandler itemHandler;
        public EffectItemHandler(ItemStackHandler itemHandler) {this.itemHandler = itemHandler;}
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {itemHandler.setStackInSlot(slot, stack);}
        @Override
        public int getSlots() {return itemHandler.getSlots();}
        @Override
        public ItemStack getStackInSlot(int slot) {return itemHandler.getStackInSlot(slot);}
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot > FIRST_INPUT_SLOT && slot <= LAST_INPUT_SLOT && stack.is(ModItems.EFFECT_EXTRACT)) {
                return itemHandler.insertItem(slot, stack, simulate);
            }
            return stack;
        }
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {return ItemStack.EMPTY;}
        @Override
        public int getSlotLimit(int slot) {return itemHandler.getSlotLimit(slot);}
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
    private class EffectItemHandlerA implements IItemHandlerModifiable {
        private final ItemStackHandler itemHandler;
        public EffectItemHandlerA(ItemStackHandler itemHandler) {this.itemHandler = itemHandler;}
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {itemHandler.setStackInSlot(slot, stack);}
        @Override
        public int getSlots() {return itemHandler.getSlots();}
        @Override
        public ItemStack getStackInSlot(int slot) {return itemHandler.getStackInSlot(slot);}
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 1 && stack.is(ModItems.EFFECT_EXTRACT)) {
                return itemHandler.insertItem(slot, stack, simulate);
            }
            return stack;
        }
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {return ItemStack.EMPTY;}
        @Override
        public int getSlotLimit(int slot) {return itemHandler.getSlotLimit(slot);}
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
    private class EffectItemHandlerB implements IItemHandlerModifiable {
        private final ItemStackHandler itemHandler;
        public EffectItemHandlerB(ItemStackHandler itemHandler) {this.itemHandler = itemHandler;}
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {itemHandler.setStackInSlot(slot, stack);}
        @Override
        public int getSlots() {return itemHandler.getSlots();}
        @Override
        public ItemStack getStackInSlot(int slot) {return itemHandler.getStackInSlot(slot);}
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 2 && stack.is(ModItems.EFFECT_EXTRACT)) {
                return itemHandler.insertItem(slot, stack, simulate);
            }
            return stack;
        }
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {return ItemStack.EMPTY;}
        @Override
        public int getSlotLimit(int slot) {return itemHandler.getSlotLimit(slot);}
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
    private class EffectItemHandlerC implements IItemHandlerModifiable {
        private final ItemStackHandler itemHandler;
        public EffectItemHandlerC(ItemStackHandler itemHandler) {this.itemHandler = itemHandler;}
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {itemHandler.setStackInSlot(slot, stack);}
        @Override
        public int getSlots() {return itemHandler.getSlots();}
        @Override
        public ItemStack getStackInSlot(int slot) {return itemHandler.getStackInSlot(slot);}
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 3 && stack.is(ModItems.EFFECT_EXTRACT)) {
                return itemHandler.insertItem(slot, stack, simulate);
            }
            return stack;
        }
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {return ItemStack.EMPTY;}
        @Override
        public int getSlotLimit(int slot) {return itemHandler.getSlotLimit(slot);}
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
    private class OutputItemHandler implements IItemHandlerModifiable {
        private final ItemStackHandler itemHandler;
        public OutputItemHandler(ItemStackHandler itemHandler) {this.itemHandler = itemHandler;}
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {itemHandler.setStackInSlot(slot, stack);}
        @Override
        public int getSlots() {return itemHandler.getSlots();}
        @Override
        public @NotNull ItemStack getStackInSlot(int i) {return itemHandler.getStackInSlot(i);}
        @Override
        public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {return stack;}
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == OUTPUT_SLOT || slot == EXTRA_SLOT) {
                return itemHandler.extractItem(slot, amount, simulate);
            }return ItemStack.EMPTY;}
        @Override
        public int getSlotLimit(int slot) {return itemHandler.getSlotLimit(slot);}
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {return false;}
    }
}