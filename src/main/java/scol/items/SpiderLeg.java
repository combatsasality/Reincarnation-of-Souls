package scol.items;

import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import scol.Main;
public class SpiderLeg extends Item {

    public SpiderLeg(){
        super(new Item.Properties()
                .tab(Main.TAB)
                .food(new Food.Builder()
                .nutrition(3)
                .saturationMod(1.2f)
                .effect(new EffectInstance(Effects.SLOW_FALLING,300, 1), 1)
                .build()));
        this.setRegistryName("spider_leg");
    }


}



