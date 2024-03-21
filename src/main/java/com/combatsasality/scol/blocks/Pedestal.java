package com.combatsasality.scol.blocks;

import com.combatsasality.scol.blocks.generic.BaseItemStackBlock;
import com.combatsasality.scol.registries.ScolTiles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.stream.Stream;

public class Pedestal extends BaseItemStackBlock {
    private static final VoxelShape PEDESTAL_SHAPE = Stream.of(
            Block.box(2, 10, 2, 14, 11.3, 14),
            Block.box(3, 0, 3, 13, 0.3, 13),
            Block.box(6, 0, 6, 10, 10, 10)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public Pedestal() {
        super(Properties.of(Material.STONE));
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
