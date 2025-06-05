package net.youshallnotpatrol.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static final String CATEGORY_DEBUG = "debug";
    public static final String CATEGORY_PILLAGER = "pillager";
    public static final String CATEGORY_TRADER = "trader";
    public static ModConfigSpec SERVER_CONFIG;

    // Debug Settings
    public static final ModConfigSpec.ConfigValue<Boolean> shouldLog;

    // Pillager Settings
    public static final ModConfigSpec.ConfigValue<Boolean> pillagerSpawnOnDifferentPlayer;
    public static final ModConfigSpec.ConfigValue<Integer> pillagerSpawnChance;

    // Trader Settings
    public static final ModConfigSpec.ConfigValue<Boolean> traderSpawnOnDifferentPlayer;
    public static final ModConfigSpec.ConfigValue<Integer> traderSpawnChance;

    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.comment("Debug/dev settings").push(CATEGORY_DEBUG);
        shouldLog = BUILDER.comment(
                        "Should all events be logged to the console. This includes things such as spawn chances per attempt and targeted players.")
                .define("shouldLog", false);
        BUILDER.pop();

        BUILDER.comment("Pillager Patrol Settings").push(CATEGORY_PILLAGER);
        pillagerSpawnOnDifferentPlayer = BUILDER.comment(
                        "If possible, should pillager patrols be unable to spawn on the same player twice in a row.")
                .define("pillagerSpawnOnDifferentPlayer", true);
        pillagerSpawnChance = BUILDER.comment(
                        "The spawn chance (%) for a pillager patrol to spawn on a selected player when an attempt is made.")
                .defineInRange("pillagerSpawnChance", 20, 0, 100);
        BUILDER.pop();

        BUILDER.comment("Wandering Trader Settings").push(CATEGORY_TRADER);
        traderSpawnOnDifferentPlayer = BUILDER.comment(
                        "If possible, should wandering traders be unable to spawn on the same player twice in a row.")
                .define("pillagerSpawnOnDifferentPlayer", true);
        traderSpawnChance = BUILDER.comment(
                        "The spawn chance (%) for a wandering trader to spawn on a selected player when an attempt is made.")
                .defineInRange("traderSpawnChance", 10, 0, 100);
        BUILDER.pop();
        SERVER_CONFIG = BUILDER.build();
    }
}
