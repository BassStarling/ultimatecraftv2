package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.datagen.ModItemTagProvider;
import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.init.ModCreativeTabs;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.registry.*;
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

        ModSounds.SOUND_EVENTS.register(modEventBus);

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

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
        event.enqueueWork(() -> {
            ModMessages.register();
        });

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

        BlockTagsProvider blockTags = new BlockTagsProvider(output, lookupProvider, "ultimatecraftv2", existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {
            }
        };
        generator.addProvider(event.includeServer(), blockTags);

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
// タングステンのフィラメント
//↓
//真空管
//↓
//制御装置
//↓
//晶析機
//
//辰砂(しんしゃ)を追加する。別名「賢者の石」。実績名は「不老不死を求めて」
//中国の辰州（現在の湖南省近辺）で多く産出したことから、「辰砂」と呼ばれるようになった。
//
//ゴムの木
//
//1. 【初級】撹拌槽型・回分式（バッチ）「撹拌槽型回分晶析機(agitated_tank_type_recrystallize)」
//まずは手動で少しずつ生産する、初期の工業化スタイルです。
//
//型: 撹拌槽型（シンプルなプロペラ）
//
//プロセス: 回分式（材料を入れて、終わるまで待つ）
//
//過飽和法: 冷却法のみ
//
//特徴:
//
//構造が単純。5x5クラフトでも安価な素材で済む。
//
//精密制御装置は不要（またはごく簡単な真空管1本）。
//
//欠点: 析出効率が悪く、一度の処理で種結晶が使い捨てになるか、成長が不十分。
//
//2. 【中級】DTB/DP型・半連続式（セミバッチ）
//ここから「精密制御装置」と「真空技術」が本領を発揮します。
//
//型: DTB（ドラフトチューブ・バッフル）型 または DP（ダブルプロペラ）型
//
//プロセス: 半連続式（溶液を流し込み続け、結晶が大きくなったら一部抜き出す）
//
//過飽和法: 冷却法 ＋ 反応晶析法（種結晶の循環）
//
//特徴:
//
//5x5クラフトで「ドラフトチューブ（内部の筒）」を配置。
//
//真空管による精密制御で、内部の流速を一定に保ち、結晶の破壊を防ぐ。
//
//ゲーム体験: 溶液（アルミン酸ナトリウム）をバケツやパイプで供給し続ける必要がある。
//
//3. 【上級】クリスタルオスロ型・連続式
//アルゴン置換真空技術の結晶。最高効率のプラントです。
//
//型: クリスタルオスロ型（分級脚付き循環型）
//
//プロセス: 連続式（供給と排出を止めることなく、純粋な結晶を自動生成）
//
//過飽和法: 冷却法 ＋ 蒸発法（真空減圧） ＋ 反応晶析法
//
//特徴:
//
//蒸発法を組み合わせるため、高度な真空制御が必要。
//
//大きな結晶だけが自重で下に沈み、分級脚から自動でアイテム化される。
//
//メリット: 純度が最高で、次の工程（アルミナへの熱分解）での燃料消費が激減する。
//
//以下でポンプの調節、電力の調整という概念が出てきますが、
//ポンプの調節は冷却効率に直結し、適切でなければ場所や大きさに偏りやばらつきが出て進行度の低下を招き、
//電力の調節は撹拌羽根の回転速度の調節に直結し、速度によっては撹拌羽根が結晶に接触することによる進行度の速度の低下を招くようにしたい
//
//表示させるもの:温度、冷却効率(1-100)、結晶化の進行状況(シンプルに矢印)
//いじるもの:冷却水のポンプの流水効率、電源(アイテムとしてレシピに含む、そのレシピに変圧器等含む)で電力の調整
//追加するもの:
//結晶の種を入れるスロット(入力スロット)
//出来た結晶を搬出するスロット(出力スロット)
//入力タンク(10,000mB)
//出力タンク(10,000mB)
//スパークタンク(コード上ではFE。スパーク搬入は上に投げ捨てられたものを勝手に吸い込む。36,000スパーク)
//ポンプの流水量を調節する欄(0-100で打ち込む)
//電力を調節する欄(0-100で打ち込む)
//その他:レシピタイプを作る。ものによってはスパーク(冷却やモーター)を使用しなくてもよい(単純な沈殿もこのブロックでできる(沈殿だけを行うブロックは他で追加する予定))