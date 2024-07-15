package com.combatsasality.scol.tiles;

import com.combatsasality.scol.blocks.Altar;
import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tiles.generic.BaseItemStackTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class AltarTile extends BaseItemStackTile  {
    private int stage = 0;
    private int tick = 0;


    public AltarTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public AltarTile(BlockPos pos, BlockState state) {
        this(ScolTiles.ALTAR, pos, state);
    }

    public List<BlockPos> getPedestalPositions() {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos pos = this.getBlockPos().mutable();
        positions.add(pos.west(3));
        positions.add(pos.north(2).west(2));
        positions.add(pos.north(3));
        positions.add(pos.north(2).east(2));
        positions.add(pos.east(3));
        positions.add(pos.south(2).east(2));
        positions.add(pos.south(3));
        positions.add(pos.south(2).west(2));
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

    private void activate() {
        List<PedestalTile> pedestals = this.getPedestals();
        if (pedestals.isEmpty()) {
            return;
        }
        List<ItemStack> items = this.getPedestalsItems(pedestals);
        if (items.isEmpty()) {
            Map<Enchantment, Integer> enchantments = this.getItem().getAllEnchantments();
            Iterator<Map.Entry<Enchantment, Integer>> iterator = enchantments.entrySet().iterator();
            List<Enchantment> toDelete = new ArrayList<>();
            pedestals.forEach(pedestal -> {
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
            if (this.getItem().getItem().equals(Items.ENCHANTED_BOOK)) return;
            Map<Enchantment, Integer> enchantments = this.getItem().getAllEnchantments();
            pedestals.forEach(pedestal -> {
                enchantments.putAll(EnchantmentHelper.getEnchantments(pedestal.getItem()));
            });
            EnchantmentHelper.setEnchantments(enchantments, this.getItem());
        }
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


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, AltarTile altarTile) {
        if (altarTile.stage == 1) {
            if (altarTile.tick % 20 == 0 && altarTile.tick != 60) {
                List<PedestalTile> pedestals = altarTile.getPedestals();
                if (altarTile.getEnchantType(pedestals) == 0) {
                    altarTile.createVisualLighting(altarTile.getBlockPos());
                } else {
                    pedestals.forEach(pedestal -> {
                        if (!pedestal.getItem().isEmpty()) {
                            altarTile.createVisualLighting(pedestal.getBlockPos());
                        }
                    });
                }
            }
            altarTile.tick++;
        }

        if (altarTile.tick >= 60) {
            List<PedestalTile> pedestals = altarTile.getPedestals();
            int type = altarTile.getEnchantType(pedestals);
            altarTile.activate();
            if (type == 0) {
                pedestals = altarTile.getPedestals();
                pedestals.forEach(pedestal -> {
                    if (!pedestal.getItem().isEmpty()) {
                        altarTile.createVisualLighting(pedestal.getBlockPos());
                    }
                });
            } else {
                altarTile.createVisualLighting(altarTile.getBlockPos());
            }
            altarTile.stage = 0;
            altarTile.tick = 0;
        }
    }


    public void createVisualLighting(BlockPos pos) {
        LightningBolt ent = EntityType.LIGHTNING_BOLT.create(this.level);
        ent.setPos(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
        ent.setVisualOnly(true);
        this.level.addFreshEntity(ent);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

}
