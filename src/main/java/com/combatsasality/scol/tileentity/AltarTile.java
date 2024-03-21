package com.combatsasality.scol.tileentity;

import com.combatsasality.scol.handlers.HelpHandler;
import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tileentity.generic.BaseItemStackTile;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class AltarTile extends BaseItemStackTile implements ITickableTileEntity {
    private int stage = 0;
    private int tick = 0;

    public AltarTile() {
        super(ScolTiles.ALTAR);
    }

    public List<BlockPos> getPedestalPositions() {
         List<BlockPos> positions = new ArrayList<>();
         BlockPos pos = this.getBlockPos().mutable();
         positions.add(pos.west(3));
         positions.add(pos.east(3));
         positions.add(pos.north(3));
         positions.add(pos.south(3));
         positions.add(pos.north(2).west(2));
         positions.add(pos.north(2).east(2));
         positions.add(pos.south(2).west(2));
         positions.add(pos.south(2).east(2));
         return positions;
    }
    public void doActivate() {
        this.stage = 1;
    }
    public List<PedestalTile> getPedestals() {
        List<PedestalTile> pedestals = new ArrayList<>();
        this.getPedestalPositions().forEach(blockPos -> {
            PedestalTile pedestalTile = (PedestalTile) this.getLevel().getBlockEntity(blockPos);
            if (pedestalTile != null) {
                pedestals.add(pedestalTile);
            }
        });
        return pedestals;
    }
    public List<ItemStack> getPedestalsItems(List<PedestalTile> posPedestal) {
        List<ItemStack> items = new ArrayList<>();
        posPedestal.forEach(pedestal -> {
            ItemStack stack = pedestal.getItem();
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        });
        return items;
    }

    private void activate() {
        List<PedestalTile> pedestals = this.getPedestals();
        if (pedestals.isEmpty()) {
            return;
        }

        List<ItemStack> items = this.getPedestalsItems(pedestals);
        if (items.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(this.getItem());
            Iterator<Map.Entry<Enchantment, Integer>> iterator = enchantments.entrySet().iterator();
            List<Enchantment> toDelete = new ArrayList<>();
            this.getPedestals().forEach(pedestal -> {
                if (iterator.hasNext()) {
                    Map.Entry<Enchantment, Integer> entry = iterator.next();
                    pedestal.setItem(Items.ENCHANTED_BOOK.getDefaultInstance());
                    EnchantmentHelper.setEnchantments(Collections.singletonMap(entry.getKey(), entry.getValue()), pedestal.getItem());
                    toDelete.add(entry.getKey());
                }
            });
            toDelete.forEach(enchantments::remove);
            EnchantmentHelper.setEnchantments(enchantments, this.getItem());
        } else {
            if (this.getItem().getItem().equals(Items.ENCHANTED_BOOK)) {
                return;
            }
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            this.getPedestals().forEach(pedestal -> {
                Map<Enchantment, Integer> pedestalEnchantments = EnchantmentHelper.getEnchantments(pedestal.getItem());
                if (!pedestalEnchantments.isEmpty()) {
                    enchantments.putAll(pedestalEnchantments);
                }
            });
            enchantments.putAll(EnchantmentHelper.getEnchantments(this.getItem()));
            EnchantmentHelper.setEnchantments(enchantments, this.getItem());
        }
    }

    public int getEnchantType(List<PedestalTile> list) {
        if (this.getPedestalsItems(list).isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }
    public int getEnchantType() {
        return this.getEnchantType(this.getPedestals());
    }


    @Override
    public void tick() {
        // The altar item swapping method is not a bug, it's a feature, and I know about him, made special for Pyding
        if (this.stage == 1) {
            if (this.tick % 20 == 0 && this.tick != 60) {
                List<PedestalTile> pedestals = this.getPedestals();
                if (this.getEnchantType(pedestals) == 0) {
                    HelpHandler.createVisualLighting(this.getBlockPos(), this.getLevel());
                } else {
                    pedestals.forEach(pedestal ->
                    {
                        if (!pedestal.getItem().isEmpty()) {
                            HelpHandler.createVisualLighting(pedestal.getBlockPos(), this.getLevel());
                        }
                    });
                }
            }
            this.tick++;
        }
        if (this.tick >= 60) {
            this.activate();
            List<PedestalTile> pedestals = this.getPedestals();
            if (this.getEnchantType(pedestals) == 0) {
                pedestals.forEach(pedestal ->
                {
                    if (!pedestal.getItem().isEmpty()) {
                        HelpHandler.createVisualLighting(pedestal.getBlockPos(), this.getLevel());
                    }
                });
            } else {
                HelpHandler.createVisualLighting(this.getBlockPos(), this.getLevel());
            }
            this.stage = 0;
            this.tick = 0;
        }
    }

}
