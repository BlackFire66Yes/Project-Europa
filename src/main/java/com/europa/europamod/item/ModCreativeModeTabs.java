package com.europa.europamod.item;

import com.europa.europamod.EuropaMod;
import com.europa.europamod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EuropaMod.MOD_ID);

    public static final Supplier<CreativeModeTab> ITEMS_TAB = CREATIVE_MODE_TAB.register("items_tab",
            () -> CreativeModeTab.builder().icon(()->new ItemStack(ModItems.URANIUM.get()))
                    .title(Component.translatable("creativetab.europamod.items"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.URANIUM);
                    }))
                    .build());

    public static final Supplier<CreativeModeTab> BLOCK_TAB = CREATIVE_MODE_TAB.register("blocks_tab",
            () -> CreativeModeTab.builder().icon(()->new ItemStack(ModBlocks.URANIUM_CRYSTAL))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(EuropaMod.MOD_ID, "items_tab"))
                    .title(Component.translatable("creativetab.europamod.blocks"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.URANIUM_CRYSTAL);
                    }))
                    .build());



    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
