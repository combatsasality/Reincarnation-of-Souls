package scol.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class SoulBlock extends Block {
    public SoulBlock(String registryName) {
        super(AbstractBlock.Properties.of(Material.SAND, MaterialColor.SAND).harvestTool(ToolType.SHOVEL).strength(0.5F).speedFactor(0.4F));
        this.setRegistryName(registryName);
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        return false;
    }



}
