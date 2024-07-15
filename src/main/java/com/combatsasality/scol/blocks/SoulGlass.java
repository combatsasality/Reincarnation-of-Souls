package com.combatsasality.scol.blocks;

import com.combatsasality.scol.tiles.SoulGlassTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SoulGlass extends AbstractGlassBlock implements EntityBlock {
    public SoulGlass() {
        super(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(-1.0F, Float.MAX_VALUE));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoulGlassTile(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (level.isClientSide) return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SoulGlassTile soulGlassTile && entity instanceof Player player) {
            soulGlassTile.owner = player.getGameProfile().getName();
        }

        super.setPlacedBy(level, pos, state, entity, stack);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SoulGlassTile soulGlassTile) {
            if (player.getGameProfile().getName().equalsIgnoreCase(soulGlassTile.owner)) {
                level.destroyBlock(pos, true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
