package scol.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import scol.Main;
import scol.handlers.ItemTier;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Properties;

public class ThunderBlade extends SwordItem {
    public ThunderBlade() {
        super(ItemTier.FOR_THUNDER, 1, 1, new Properties().tab(Main.TAB).rarity(Rarity.EPIC));
        this.setRegistryName("thunder_blade");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> list, ITooltipFlag p_77624_4_) {
        list.add(1, new TranslationTextComponent("tooltip.item.thunder_blade", stack.getOrCreateTag().getInt("scol.thunder")));
        super.appendHoverText(stack, p_77624_2_, list, p_77624_4_);
    }

    public void Lightning(LivingEntity entity) {
        World world = entity.level;
        LightningBoltEntity lightningBolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
        lightningBolt.setPos(entity.getX(), entity.getY(), entity.getZ());
        world.addFreshEntity(lightningBolt);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Lightning(attacker);
        return super.hurtEnemy(stack, target, attacker);
    }
}
