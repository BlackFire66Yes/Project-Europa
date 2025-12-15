package com.europa.europamod;

import com.europa.europamod.block.ModBlocks;
import com.europa.europamod.item.ModCreativeModeTabs;
import com.europa.europamod.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(EuropaMod.MOD_ID)
public class EuropaMod {

    public static final String MOD_ID = "europamod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EuropaMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        // Register the com.europa.europamod.item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block com.europa.europamod.item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        /*if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            event.accept(ModItems.URANIUM);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            event.accept(ModBlocks.URANIUM_CRYSTAL);
        }*/

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = EuropaMod.MOD_ID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            // Здесь ваш код для настройки клиента
        }
    }

}
