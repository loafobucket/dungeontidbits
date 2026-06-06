package com.loafobucket.dungeontidbits.block.entity;

import com.loafobucket.dungeontidbits.recipe.PottleRecipe;
import com.loafobucket.dungeontidbits.screen.custom.PottleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
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

    private boolean isInputSlot(int slot) {
        return slot >= FIRST_INPUT_SLOT && slot <= LAST_INPUT_SLOT;
    }

    public static final int FIRST_INPUT_SLOT = 0;
    public static final int LAST_INPUT_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;
    public static final int EXTRA_SLOT = 5;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

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

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("pottleprogress");
        maxProgress = pTag.getInt("pottlemaxprogress");
    }
// thank you scorched guns
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, PottleBlockEntity blockEntity) {
        if (!level.isClientSide) {
            boolean hasValidRecipe = blockEntity.hasRecipe();
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
        }
    }

    private void craftItem() {
        Optional<PottleRecipe> match = getRecipe();
        if (match.isPresent()) {
            PottleRecipe recipe = match.get();
            ItemStack resultItem = recipe.getResultItem(level.registryAccess());
            ItemStack extraItem = recipe.getExtraItem(level.registryAccess());
            ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
            ItemStack extraStack = itemHandler.getStackInSlot(EXTRA_SLOT);

            if ((outputStack.isEmpty() || (outputStack.getItem() == resultItem.getItem() && outputStack.getCount() + resultItem.getCount() <= outputStack.getMaxStackSize()))
                    && (extraStack.isEmpty() || (extraStack.getItem() == extraItem.getItem() && extraStack.getCount() + extraItem.getCount() <= extraStack.getMaxStackSize()))) {
                for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT; i++) {
                    itemHandler.extractItem(i, 1, false);
                }
                if (outputStack.isEmpty()) {
                    itemHandler.setStackInSlot(OUTPUT_SLOT, resultItem.copy());
                } else {
                    outputStack.grow(resultItem.getCount());
                }
                if (extraStack.isEmpty()) {
                    itemHandler.setStackInSlot(EXTRA_SLOT, extraItem.copy());
                } else {
                    extraStack.grow(extraItem.getCount());
                }
            }
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasRecipe() {
        return getRecipe().isPresent();
    }

    private boolean isRecipeValid() {
        Optional<PottleRecipe> currentRecipe = getCurrentRecipe();
        if (currentRecipe.isPresent()) {
            PottleRecipe recipe = currentRecipe.get();
            return recipe.matches(createRecipeInput(), level);
        }
        return false;
    }

    private Optional<PottleRecipe> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        PottleRecipe.Input input = createRecipeInput();
        return level.getRecipeManager().getAllRecipesFor(PottleRecipe.Type.INSTANCE).stream()
                .map(recipe -> recipe.value())
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();
    }

    private Optional<PottleRecipe> getRecipe() {
        if (level == null) return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(PottleRecipe.Type.INSTANCE, createRecipeInput(), level)
                .map(recipe -> recipe.value());
    }

    private PottleRecipe.Input createRecipeInput() {
        List<ItemStack> inputs = new ArrayList<>(LAST_INPUT_SLOT - FIRST_INPUT_SLOT + 1);
        for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT; i++) {
            inputs.add(itemHandler.getStackInSlot(i));
        }
        return new PottleRecipe.Input(inputs);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
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
}