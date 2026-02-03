package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.init.ModTiers;
import com.bassstarling.ultimatecraftv2.item.PipeItem;
import com.bassstarling.ultimatecraftv2.item.SoBolt;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UltimateCraftV2.MOD_ID);

    public enum MoldType {
        PLATE("plate");

        private final String name;
        MoldType(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public static final RegistryObject<Item> ASSEMBLED_CYLINDER_BLOCK =
            ITEMS.register("assembled_cylinder_block",
                    () -> new Item(new Item.Properties()
                    ));

    public static final RegistryObject<Item> BOLT =
            ITEMS.register("bolt",
                    () -> new SoBolt(new Item.Properties().stacksTo(64)
                    ));
    public static final RegistryObject<Item> SPARK_STONE =
            ITEMS.register("spark_stone",
                    () -> new SparkStone(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WIRE =
            ITEMS.register("wire",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WEAK_MAGNET =
            ITEMS.register("weak_magnet",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SPARK_GENERATOR =
            ITEMS.register("spark_generator",
                    () -> new BlockItem(
                            ModBlocks.SPARK_GENERATOR.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> SPARK_COMPRESSOR =
            ITEMS.register("spark_compressor",
                    () -> new BlockItem(
                            ModBlocks.SPARK_COMPRESSOR.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> HEATE_GENERATOR =
            ITEMS.register("heat_generator",
                    () -> new BlockItem(
                            ModBlocks.HEAT_GENERATOR.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> IRON_PLATE =
            ITEMS.register("iron_plate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COIL =
            ITEMS.register("coil",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PROPELLER =
            ITEMS.register("propeller",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> RAW_BAUXITE =
            ITEMS.register("raw_bauxite",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> BAUXITE_ORE =
            ITEMS.register("bauxite_ore",
                    () -> new BlockItem(
                            ModBlocks.BAUXITE_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> COARSE_BAUXITE_POWDER =
            ITEMS.register("coarse_bauxite_powder",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRUSHER_BLOCK =
            ITEMS.register("crusher_block",
                    () -> new BlockItem(
                            ModBlocks.CRUSHER_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> WASHED_BAUXITE_POWDER =
            ITEMS.register("washed_bauxite_powder",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WASHING_MACHINE =
            ITEMS.register("washing_machine",
                    () -> new BlockItem(
                            ModBlocks.WASHING_MACHINE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> ALUMINA =
            ITEMS.register("alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELETRICCALCINER =
            ITEMS.register("eletriccalciner",
                    () -> new BlockItem(
                            ModBlocks.ELETRICCALCINER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> IRON_WIRE =
            ITEMS.register("iron_wire",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> HEATER =
            ITEMS.register("heater",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRYOLITE_ORE =
            ITEMS.register("cryolite_ore",
                    () -> new BlockItem(
                            ModBlocks.CRYOLITE_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRYOLITE =
            ITEMS.register("cryolite",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELECTROLYTICFURNACE =
            ITEMS.register("electrolyticfurnace",
                    () -> new BlockItem(
                            ModBlocks.ELECTROLYTICFURNACE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> MOLTEN_CRYOLITE =
            ITEMS.register("molten_cryolite",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> MOLTEN_CRYOLITE_WITH_ALUMINA =
            ITEMS.register("molten_cryolite_with_alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> USED_ELECTROLYTICFURNACE =
            ITEMS.register("used_electrolyticfurnace",
                    () -> new BlockItem(
                            ModBlocks.USED_ELECTROLYTICFURNACE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> ALUMINIUM_INGOT =
            ITEMS.register("aluminium_ingot",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> MOLTEN_ALUMINIUM_IN_BUCKET =
            ITEMS.register("molten_aluminium_in_bucket",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PIPE = ITEMS.register("pipe",
            () -> new PipeItem(
                    ModTiers.PIPE,  // 上で作った素材 (耐久500)
                    4,              // 追加攻撃力: 4 (合計: 1+2+5 = 7ダメージ = ハート3.5個分)
                    -1.6f,          // 攻撃速度補正: -1.6 (合計: 4.0 - 1.6 = 2.4)
                    new Item.Properties()
            ));
    public static final RegistryObject<Item> INDUSTRIAL_WORKBENCH =
            ITEMS.register("industrial_workbench",
                    () -> new BlockItem(
                            ModBlocks.INDUSTRIAL_WORKBENCH.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> TAR_BUCKET =
            ITEMS.register("tar_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
                    ));
    public static final RegistryObject<Item> IRON_PIPE =
            ITEMS.register("iron_pipe",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKE =
            ITEMS.register("coke",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKEOVEN =
            ITEMS.register("cokeoven",
                    () -> new BlockItem(
                            ModBlocks.COKEOVEN.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKE_DUST =
            ITEMS.register("coke_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> UNFIRED_ELECTRODE =
            ITEMS.register("unfired_electrode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKE_ELECTRODE =
            ITEMS.register("coke_electrode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> GRAPHITE_ELECTRODE =
            ITEMS.register("graphite_electrode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ARC_FURNACE =
            ITEMS.register("arc_furnace",
                    () -> new BlockItem(
                            ModBlocks.ARC_FURNACE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> PIG_IRON =
            ITEMS.register("pig_iron",
                    () -> new Item(new Item.Properties()
                    ));

    public static final RegistryObject<Item> UNFIRED_PIG_IRON =
            ITEMS.register("unfired_pig_iron",
                    () -> new Item(new Item.Properties()
                    ));

    public static final RegistryObject<Item> STEEL_INGOT =
            ITEMS.register("steel_ingot",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELECTROLYZER =
            ITEMS.register("electrolyzer",
                    () -> new BlockItem(
                            ModBlocks.ELECTROLYZER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> OXYGEN_BOTTLE =
            ITEMS.register("oxygen_bottle",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> OXYGEN_CONVERTER =
            ITEMS.register("oxygen_converter",
                    () -> new BlockItem(
                            ModBlocks.OXYGEN_CONVERTER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> CASTING_MACHINE =
            ITEMS.register("casting_machine",
                    () -> new BlockItem(
                            ModBlocks.CASTING_MACHINE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> STEEL_PLATE =
            ITEMS.register("steel_plate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ALUMINIUM_PLATE =
            ITEMS.register("aluminium_plate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> HIGH_PURITY_ALUMINA =
            ITEMS.register("high_purity_alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SINTERED_CERAMIC =
            ITEMS.register("sintered_ceramic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> POWER_SUPPLY =
            ITEMS.register("power_supply",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WELDING_MACHINE =
            ITEMS.register("welding_machine",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> HAMMER =
            ITEMS.register("hammer",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> FORMED_CERAMIC =
            ITEMS.register("formed_ceramic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ADDITIVE_PROCESSED_SINTERED_CERAMIC =
            ITEMS.register("additive_processed_sintered_ceramic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELECTROLYTIC_LAYER =
            ITEMS.register("electrolytic_layer",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CATHODE =
            ITEMS.register("cathode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ANODE =
            ITEMS.register("anode",
                    () -> new Item(new Item.Properties()
                    ));

    // --- 型（Mold）の自動登録システム ---
    // 生成されたアイテムを保存しておくMap（後で他からアクセスするため）
    public static final Map<MoldType, RegistryObject<Item>> MOLDS = new HashMap<>();

    static {
        for (MoldType type : MoldType.values()) {
            String id = "mold_" + type.getName(); // 例: mold_plate, mold_gear

            RegistryObject<Item> moldItem = ITEMS.register(id,
                    () -> new Item(new Item.Properties().stacksTo(1))); // 型はスタックしない設定など

            MOLDS.put(type, moldItem);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
//石灰岩(limestone)は比較的風化されにくいので、山脈中の高いピークや大きな山となっている場合が多い。ヒマラヤ山脈のエベレストの頂上や、アルプス山脈のアイガー等は石灰岩でできている。日本では伊吹山や藤原岳や武甲山が全山、石灰岩である。
//（quick lime）生石灰
//（slaked lime）消石灰。酸化カルシウムに加水すると生成する。
//水酸化ナトリウム（sodium hydroxide）は消石灰と炭酸ナトリウムの複分解反応（2つの水溶液を混ぜる)。
//炭酸ナトリウム（sodium carbonate）炭酸水素ナトリウムを熱すると得られる。
//炭酸水素ナトリウム(sodium bicarbonate）塩化ナトリウム溶液の電気分解で得られた水酸化ナトリウム溶液に、二酸化炭素を反応させて製造する。
//塩化ナトリウム=塩(salt)

//高純度アルミナ
//+ 炭酸ナトリウム
//→ 発泡アルミナ(Foamed alumina)

//発泡アルミナ
//+ 高温焼成
//→ 多孔質断熱ブロック(Porous Insulation Block)

//電解機の材料に必要とする電極でチタンやプラチナを使用するらしい。
//↑工場とかでの話で完全にオーバースペックらしい
//プラチナのレア度はダイヤの4万倍ほど。
//結局電極にはより一般的なニッケルを使用する
//ニッケル(nickel)は抽出冶金によって得られます。鉱石から従来の焙焼および還元プロセスによって抽出され、純度75%以上の金属が得られます。
//ニッケル鉱石(nickel_ore)を還元焙焼(使用するのはコークオーブンとする)すると不純ながら(約75%)ニッケルを得ることができる。十分運用可能である。