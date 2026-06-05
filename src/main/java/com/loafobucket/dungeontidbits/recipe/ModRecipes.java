package com.loafobucket.dungeontidbits.recipe;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, DungeonTidbits.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, DungeonTidbits.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PottleRecipe>> POTTLE_SERIALIZER =
            SERIALIZERS.register("pottle",PottleRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PottleRecipe>> POTTLE_TYPE =
            TYPES.register("pottle", () -> new RecipeType<PottleRecipe>() {
                @Override
                public String toString() {
                    return "pottle";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
