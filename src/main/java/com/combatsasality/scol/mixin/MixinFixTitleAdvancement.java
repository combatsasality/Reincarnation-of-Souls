package com.combatsasality.scol.mixin;

import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AdvancementEntryGui.class)
public class MixinFixTitleAdvancement {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;substrByWidth(Lnet/minecraft/util/text/ITextProperties;I)Lnet/minecraft/util/text/ITextProperties;"))
    private int init(int i) {
        return i + 261;
    }
}
