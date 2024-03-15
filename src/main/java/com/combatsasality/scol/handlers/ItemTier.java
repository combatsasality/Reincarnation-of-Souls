package com.combatsasality.scol.handlers;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public enum ItemTier implements IItemTier {

    FOR_ALL(0,0, 0.0F, 0, 22);

    public int durability = 0;
    public float speed = 0;
    public float attack = 0;
    public int harvestlevel = 0;
    public int enchantemnt = 0;

    public Ingredient repair = null;


    ItemTier(int durability, float speed, float attack, int harvestlevel, int enchantemnt) {
        this.durability = durability;
        this.speed = speed;
        this.attack = attack;
        this.harvestlevel = harvestlevel;
        this.enchantemnt = enchantemnt;
    }
    ItemTier(int durability, float speed, float attack, int harvestlevel, int enchantemnt, Ingredient repair) {
        this.durability = durability;
        this.speed = speed;
        this.attack = attack;
        this.harvestlevel = harvestlevel;
        this.enchantemnt = enchantemnt;
        this.repair = repair;
    }

    @Override
    public int getUses() {
        return this.durability;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attack;
    }

    @Override
    public int getLevel() {
        return this.harvestlevel;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantemnt;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repair;
    }
}

