package com.combatsasality.scol.blocks;

import com.combatsasality.scol.blocks.generic.BaseItemStackBlock;
import com.combatsasality.scol.blocks.generic.ITickAbleTile;
import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tiles.AltarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class Altar extends BaseItemStackBlock implements ITickAbleTile {
    private static final VoxelShape ALTAR_SHAPE = Stream.of(
            Block.box(3, 0, 3, 13, 0.8, 13),
            Block.box(4, 0.8, 4, 12, 3.0999999999999996, 12),
            Block.box(5, 9.1, 5, 11, 15.100000000000001, 11),
            Block.box(5, 3.0999999999999996, 5, 11, 9.100000000000001, 11)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public Altar() {
        super(Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 6.0F).dynamicShape().requiresCorrectToolForDrops());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return ALTAR_SHAPE;
    }



    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AltarTile(blockPos, blockState);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AltarTile altarTile) {
            if (player.isCrouching()) {
                ItemStack stack = altarTile.getItem();
                if (altarTile.getPedestals().isEmpty() || stack.isEmpty() || !stack.isEnchantable() && !stack.isEnchanted() && !stack.getItem().equals(Items.ENCHANTED_BOOK)) {
                    return InteractionResult.FAIL;
                }
                if (altarTile.getEnchantType() == 0 && stack.getItem().equals(Items.ENCHANTED_BOOK)) {
                    return InteractionResult.FAIL;
                }
                altarTile.doActivate();
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, interactionHand, hitResult);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getServerTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ITickAbleTile.createTicker(type, ScolTiles.ALTAR, AltarTile::tick);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getClientTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ITickAbleTile.createTicker(type, ScolTiles.ALTAR, AltarTile::tick);
    }
}
