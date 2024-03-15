package com.combatsasality.scol.integration.jei;

import com.combatsasality.scol.items.generic.ISoulMaterial;
import com.combatsasality.scol.registries.ScolEnchantments;
import com.combatsasality.scol.registries.ScolItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class JeiIntegration implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("scol", "jei_integration");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        IIngredientManager manager = registration.getIngredientManager();
        Collection<ItemStack> ingredients = manager.getAllIngredients(VanillaTypes.ITEM);
        Collection<ISoulMaterial> souls = new ArrayList<>(Arrays.asList(ScolItems.SOUL, ScolItems.AGGRESSIVE_SOUL, ScolItems.FRIENDLY_SOUL));
        Collection<Object> recipes = new ArrayList<>();
        for (ItemStack ingredient : ingredients) {
            if (ingredient.getItem() instanceof SwordItem) {
                List<ItemStack> result = new ArrayList<>();
                List<ItemStack> right = new ArrayList<>();
                for (ISoulMaterial soul : souls) {
                    ItemStack addResult = ingredient.copy();
                    switch (soul.getSoulType()) {
                        case AGGRESSIVE:
                            addResult.enchant(ScolEnchantments.SOUL_CATCHER, 3);
                            break;
                        case FRIENDLY:
                            addResult.enchant(ScolEnchantments.SOUL_CATCHER, 2);
                            break;
                        case NEGATIVE:
                            addResult.enchant(ScolEnchantments.SOUL_CATCHER, 1);
                            break;
                    }
                    right.add(new ItemStack((IItemProvider) soul));
                    result.add(addResult);
                }
                recipes.add(factory.createAnvilRecipe(ingredient, right, result));
            }
        }
        registration.addRecipes(recipes, VanillaRecipeCategoryUid.ANVIL);
    }

}
