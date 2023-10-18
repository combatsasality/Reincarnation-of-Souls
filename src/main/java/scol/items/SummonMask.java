package scol.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import scol.Main;
import scol.entity.IchigoVazard;

import javax.annotation.Nullable;
import java.util.List;

public class SummonMask extends Item {

    public SummonMask() {
        super(new Properties().tab(Main.TAB));
        this.setRegistryName("summon_mask");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag tooltip) {
        if (getFloatForAlfheimMask(stack) == 1) {
            list.add(new TranslationTextComponent("tooltip.scol.alfheim_mask.0"));
            list.add(new TranslationTextComponent("tooltip.scol.alfheim_mask.1"));
        }
        super.appendHoverText(stack, world, list, tooltip);
    }

    public static float getFloatForAlfheimMask(ItemStack stack) {
        if (stack.getHoverName().getString().equalsIgnoreCase("mysterious mask") || stack.getHoverName().getString().equalsIgnoreCase("\u0437\u0430\u0433\u0430\u0434\u043e\u0447\u043d\u0430\u044f \u043c\u0430\u0441\u043a\u0430")) {
            return 1;
        } else {
            return 0;
        }
    }
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        boolean summoned = context.getLevel().addFreshEntity(new IchigoVazard(context.getLevel(), context.getClickLocation()));
        if (summoned) {
            context.getItemInHand().setCount(context.getItemInHand().getCount()-1);
        }
        return super.useOn(context);
    }
}
