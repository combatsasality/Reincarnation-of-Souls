package com.combatsasality.scol.handlers.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EntityAdditionModifier extends LootModifier {
    public static final Supplier<Codec<EntityAdditionModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item))
                    .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                    .and(Codec.FLOAT.fieldOf("looting_multiplier").forGetter(m -> m.lootingMultiplier))
                    .apply(inst, EntityAdditionModifier::new)));

    private final Item item;
    private float chance;

    private float lootingMultiplier;


    protected EntityAdditionModifier(LootItemCondition[] conditionsIn, Item item, float chance, float lootingMultiplier) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() > this.chance - (context.getLootingModifier() * this.lootingMultiplier)) {
            generatedLoot.add(new ItemStack(item, 1));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
