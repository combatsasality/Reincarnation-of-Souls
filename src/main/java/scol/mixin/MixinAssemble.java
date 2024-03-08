package scol.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingRecipe.class)
public class MixinAssemble {

    @Shadow @Final private ResourceLocation id;
    @Inject(method = "assemble", at = @At("RETURN"))
    public void onAssemble(CallbackInfoReturnable<ItemStack> cir) {
        if (this.id.equals(new ResourceLocation("scol:trident_improve"))) {
           cir.getReturnValue().getOrCreateTag().putBoolean("scol.IsImproved", true);
        }
    }
}
