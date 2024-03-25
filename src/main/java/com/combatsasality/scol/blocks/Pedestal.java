package com.combatsasality.scol.blocks;

import com.combatsasality.scol.blocks.generic.BaseItemStackBlock;
import com.combatsasality.scol.registries.ScolTiles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class Pedestal extends BaseItemStackBlock {
    private static final VoxelShape PEDESTAL_SHAPE = Stream.of(
            Block.box(1, 0, 1, 15, 1, 15),
            Block.box(2, 1, 2, 14, 3, 14),
            Block.box(4, 3, 4, 12, 16, 12),
            Block.box(1, 16, 1, 15, 18, 15)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public Pedestal() {
        super(Properties.of(Material.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE));
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ScolTiles.PEDESTAL.create();
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return PEDESTAL_SHAPE;
    }
}
