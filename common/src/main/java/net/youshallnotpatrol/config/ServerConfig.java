package net.youshallnotpatrol.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final String CATEGORY_PILLAGER = "pillager";
    public static final String CATEGORY_TRADER = "trader";
    public static ForgeConfigSpec SERVER_CONFIG;

    //Pillager Settings
    public static final ForgeConfigSpec.ConfigValue<Boolean> pillagerSpawnOnDifferentPlayer;
    public static final ForgeConfigSpec.ConfigValue<Integer> pillagerSpawnChance;

    //Trader Settings
    public static final ForgeConfigSpec.ConfigValue<Boolean> traderSpawnOnDifferentPlayer;
    public static final ForgeConfigSpec.ConfigValue<Integer> traderSpawnChance;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Pillager Patrol Settings").push(CATEGORY_PILLAGER);
        pillagerSpawnOnDifferentPlayer = BUILDER.comment("If possible, should pillager patrols be unable to spawn on the same player twice in a row.")
                .define("pillagerSpawnOnDifferentPlayer", true);
        pillagerSpawnChance = BUILDER.comment("The spawn chance (%) for a pillager patrol to spawn on a selected player when an attempt is made.")
                .defineInRange("pillagerSpawnOnDifferentPlayer", 20, 0, 100);
        BUILDER.pop();

        BUILDER.comment("Wandering Trader Settings").push(CATEGORY_TRADER);
        traderSpawnOnDifferentPlayer = BUILDER.comment("If possible, should wandering traders be unable to spawn on the same player twice in a row.")
                .define("pillagerSpawnOnDifferentPlayer", true);
        traderSpawnChance = BUILDER.comment("The spawn chance (%) for a wandering trader to spawn on a selected player when an attempt is made.")
                .defineInRange("pillagerSpawnOnDifferentPlayer", 10, 0, 100);
        BUILDER.pop();
        SERVER_CONFIG = BUILDER.build();
    }
}