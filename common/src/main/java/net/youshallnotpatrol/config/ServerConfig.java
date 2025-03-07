package net.youshallnotpatrol.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final String CATEGORY_PILLAGER = "pillager";
    public static final String CATEGORY_TRADER = "trader";
    public static ForgeConfigSpec SERVER_CONFIG;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.comment("Pillager Patrol Settings").push(CATEGORY_PILLAGER);

        BUILDER.pop();

        BUILDER.comment("Wandering Trader Settings").push(CATEGORY_TRADER);

        BUILDER.pop();
        SERVER_CONFIG = BUILDER.build();
    }
}