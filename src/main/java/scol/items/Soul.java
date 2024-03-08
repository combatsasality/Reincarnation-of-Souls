package scol.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import scol.Main;
import scol.items.generic.ISoulMaterial;

import javax.annotation.Nullable;
import java.util.List;

public class Soul extends Item implements ISoulMaterial {
    private final int burnTime;
    private final ISoulMaterial.SoulType soulType;
    public Soul(String registryName, int burnTime, ISoulMaterial.SoulType soulType) {
        super(new Properties().stacksTo(64).tab(Main.TAB));
        this.setRegistryName(registryName);
        this.burnTime = burnTime;
        this.soulType = soulType;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.item.soul.0"));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }


    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable IRecipeType<?> recipeType) {
        return this.burnTime;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.isDeadOrDying()) return;
            boolean random = world.getRandom().nextInt(120) == 0;
            if (random) {
                if (this.soulType == ISoulMaterial.SoulType.FRIENDLY) player.heal(1);
                else if (this.soulType == ISoulMaterial.SoulType.AGGRESSIVE) player.hurt(new DamageSource("soul").setMagic(), 2.0F);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public SoulType getSoulType() {
        return this.soulType;
    }
}
