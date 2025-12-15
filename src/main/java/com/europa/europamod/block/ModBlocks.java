package com.europa.europamod.block;

import com.europa.europamod.EuropaMod;
import com.europa.europamod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(EuropaMod.MOD_ID);

    public static final DeferredBlock<CrystalBlock> URANIUM_CRYSTAL = registerBlock("uranium_crystal", () ->
            new CrystalBlock(
                    Block.Properties.of()
                            .strength(5.0F, 20.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.GLASS)
                            .lightLevel(s-> 6)
                            .noOcclusion()
                            .isViewBlocking((state, level, pos) -> false)
                            .isSuffocating((state, level, pos) -> false),
                    createCrystalVariantBoxes()
            )
    );

    private static Map<Integer, double[][]> createCrystalVariantBoxes() {
        Map<Integer, double[][]> variantBoxes = new HashMap<>();

        // Вариант 0 (оригинальный)
        variantBoxes.put(0, new double[][]{
                {2, 0, 2, 14, 2, 14},    // Основание
                {2, 2, 9, 10, 4, 14},    // Верхний выступ
                {4, 2, 4, 12, 10, 12},   // Центральная часть
                {7, 2, 2, 14, 4, 8}      // Боковой выступ
        });

        // Вариант 1 (вторая модель)
        variantBoxes.put(1, new double[][]{
                {2, 0, 2, 14, 2, 14},    // Основание
                {3, 2, 3, 13, 5, 13},    // Средняя часть
                {8, 5, 6, 12, 13, 12},   // Главный кристалл
                {4, 5, 4, 9, 10, 11}     // Второстепенный кристалл
        });

        // Вариант 2 (третья модель)
        variantBoxes.put(2, new double[][]{
                {2, 0, 2, 14, 2, 14},    // Основание
                {2, 2, 7, 8, 5, 14},     // Боковой выступ
                {5, 2, 3, 13, 9, 11},    // Основной кристалл
                {8, 9, 4, 12, 13, 8}     // Верхний кристалл
        });

        return variantBoxes;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}




