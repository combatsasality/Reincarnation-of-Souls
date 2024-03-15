package com.combatsasality.scol.registries;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolAttributes extends AbstractRegistry<Attribute> {
    @ObjectHolder(MODID + ":magical_damage")
    public static RangedAttribute MAGICAL_DAMAGE;
    public ScolAttributes() {
        super(ForgeRegistries.ATTRIBUTES);
        register("magical_damage", () -> new RangedAttribute("attribute.name.scol.magical_damage", 0.0D, 0.0d, 100000000).setSyncable(true));
    }
}
