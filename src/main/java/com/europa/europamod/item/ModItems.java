package com.europa.europamod.item;

import com.europa.europamod.EuropaMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems { //Класс предметов

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EuropaMod.MOD_ID);

    //public static final сочетание модификаторов доступа, статического и final, которые определяют область видимости, статичность и неизменяемость элементов класса.сочетание модификаторов доступа, статического и final, которые определяют область видимости, статичность и неизменяемость элементов класса.
    //public — модификатор доступа, static — модификатор статического члена класса, final — модификатор, указывающий на неизменяемость.
    //DeferredRegister способ является оберткой для RegisterEvent

    public static final DeferredItem<Item> URANIUM = ITEMS.register("uranium",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {//шина событий

        ITEMS.register(eventBus);

    }

}
