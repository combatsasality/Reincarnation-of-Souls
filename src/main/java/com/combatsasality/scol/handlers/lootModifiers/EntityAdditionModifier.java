package com.combatsasality.scol.handlers.lootModifiers;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityAdditionModifier extends LootModifier {
    private final Item addition;
    private float chance;

    private float lootingMultiplier;

    protected EntityAdditionModifier(ILootCondition[] conditionsIn, Item addition, float chance, float lootingMultiplier) {
        super(conditionsIn);
        this.addition = addition;
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() > this.chance - (context.getLootingModifier() * this.lootingMultiplier)) {
            generatedLoot.add(new ItemStack(addition, 1));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<EntityAdditionModifier> {
        private float chance;
        private float lootingMultiplier = 0.1F;

        public Serializer(float chance) {this.chance = chance;}

        public Serializer(float chance, float lootingMultiplier) {this.chance = chance; this.lootingMultiplier=lootingMultiplier;}

        @Override
        public EntityAdditionModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            Item addition = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation(JSONUtils.getAsString(object, "addition")));
            return new EntityAdditionModifier(conditionsIn, addition, this.chance, this.lootingMultiplier);
        }

        @Override
        public JsonObject write(EntityAdditionModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("addition", ForgeRegistries.ITEMS.getKey(instance.addition).toString());
            return json;
        }
    }
}