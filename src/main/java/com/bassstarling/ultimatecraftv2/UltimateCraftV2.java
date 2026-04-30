package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.datagen.ModItemTagProvider;
import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.init.ModCreativeTabs;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(UltimateCraftV2.MOD_ID)
public class UltimateCraftV2
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ultimatecraftv2";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public UltimateCraftV2(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus); // ★これ

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(this::gatherData);

        ModRecipes.register(modEventBus);

        ModFluids.register(modEventBus);

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ModCreativeTabs.register(modEventBus);

        ModMenuTypes.MENUS.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 1. まずブロックタグプロバイダーを作成（アイテムタグがこれを利用するため）
        BlockTagsProvider blockTags = new BlockTagsProvider(output, lookupProvider, "ultimatecraftv2", existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {
                // ブロックタグが必要なければ空でOK
            }
        };
        generator.addProvider(event.includeServer(), blockTags);

        // 2. アイテムタグプロバイダーを登録（blockTags.contentsGetter() を渡すのがコツ）
        generator.addProvider(event.includeServer(), new ModItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
}

//[汎用電解機][浸出器]
//[ポンプ]   [濾過機][分解層]←タンク1から搬入される
//[水源]     [分解層]←タンク2から搬入される
//
//[分解層]←タンク1から搬入される
//[空気]←分解層からアイテムがドロップされる
//
//上:入力スロット搬入
//横:燃料系、スパークストーン系搬入
//下:出力スロット搬出
//
//大半をかまど、ホッパーと同じ挙動に統一することに
//
//晶析機を作るための制御装置を作るためのレシピ
//プラチナ、銀、タングステン、真空管、
//プラチナ電極、銀半田
//＜＜プラチナ集め！！！＞＞
//
//#タングステンを作る
//灰重石
//粉砕
//濃塩酸+灰重石→タングステン酸
//タングステン酸+石炭粉→粗悪なタングステンの粉
//(電解炉(アーク炉)か高性能なかまど)
//↓レシピを通して
//粗悪なタングステンのフィラメント
//↓
//真空管
//↓
//制御装置
//↓
//晶析機
//
//灰重石を探すためにtier1ブラックライトを作る
//そのために水銀を手に入れる
//辰砂（Cinnabar）鉱石を掘る（赤色の石）
//↓
//かまどか電動精錬機で熱分解
//↓
//発生した水銀蒸気を冷却器で冷却して液体水銀として回収する(瓶入り)
//
//辰砂(しんしゃ)を追加する。別名「賢者の石」。実績名は「不老不死を求めて」
//中国の辰州（現在の湖南省近辺）で多く産出したことから、「辰砂」と呼ばれるようになった。
//
//辰砂の採掘のためには専用のガスマスクなどの防護服フル装備が必要
//フル装備のためにゴムや樹脂、プラスチックが必要
//ゴムの木からゴムを精製して防護服を作る
//
//ゴムを作る
//タールを蒸留してベンゼン等を得る
//ベンゼンとエチレンでスチレンを得る(触媒にリン酸やゼオライト)
//高純度な水と乳化剤(石鹸)のなかではげしく混ぜ熱を加えて重合(合成ゴムの塊)
//焼いて(ゴム)
//リン酸やゼオライト、高純度な水、乳化剤が欲しい
//
//蒸留器のために
//枝付きフラスコ
//三角フラスコ
//リービッヒ冷却管
//ガラス管
//土台(鉛)