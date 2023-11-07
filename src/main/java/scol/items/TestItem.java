package scol.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import scol.Main;
import scol.packets.client.PacketCapa;
import scol.scolCapability;

import javax.annotation.Nullable;
import java.util.List;

public class TestItem extends Item {
    public TestItem() {
        super(new Properties());
        this.setRegistryName("test_item");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.item.test_item.0"));
        if (Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(Minecraft.getInstance().player.getCapability(scolCapability.NeedVariables).map(capa -> capa.getNBT()).orElse(null).toString()));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            Main.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketCapa(entity.getCapability(scolCapability.NeedVariables).map(capa -> capa.getNBT()).orElse(null)));
            player.setHealth(player.getMaxHealth());
            player.addEffect(new EffectInstance(Effects.SATURATION, 1000, 1, true, false));
            player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 1000, 1, true, false));
        }
        super.inventoryTick(p_77663_1_, p_77663_2_, entity, p_77663_4_, p_77663_5_);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.getCapability(scolCapability.NeedVariables).ifPresent(capa -> capa.setCooldownBankai(0));
        player.getCapability(scolCapability.NeedVariables).ifPresent(capa -> capa.setActiveBankaiTime(0));
        return super.use(world, player, hand);
    }

}
