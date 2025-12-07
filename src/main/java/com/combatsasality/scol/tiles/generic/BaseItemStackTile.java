package com.combatsasality.scol.tiles.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseItemStackTile extends BaseTile {
    protected ItemStack item = ItemStack.EMPTY;

    public BaseItemStackTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!item.isEmpty()) {
            nbt.put("item", item.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        item = ItemStack.of(tag.getCompound("item"));
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        dispatchToNearbyPlayers();
        setChanged();
    }

    public void dispatchToNearbyPlayers() {
        var level = this.getLevel();
        if (level == null) return;

        var packet = this.getUpdatePacket();
        if (packet == null) return;

        var players = level.players();
        var pos = this.getBlockPos();

        for (var player : players) {
            if (player instanceof ServerPlayer mPlayer) {
                if (isPlayerNearby(mPlayer.getX(), mPlayer.getZ(), pos.getX() + 0.5, pos.getZ() + 0.5)) {
                    mPlayer.connection.send(packet);
                }
            }
        }
    }

    private static boolean isPlayerNearby(double x1, double z1, double x2, double z2) {
        return Math.hypot(x1 - x2, z1 - z2) < 64;
    }
}
