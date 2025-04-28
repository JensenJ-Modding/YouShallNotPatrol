package net.youshallnotpatrol.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import dev.architectury.platform.forge.EventBuses;
import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;

@Mod(YouShallNotPatrol.MOD_ID)
@SuppressWarnings("unused")
public class YouShallNotPatrolModForge {
    public YouShallNotPatrolModForge() {
        EventBuses.registerModEventBus(
                YouShallNotPatrol.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        YouShallNotPatrol.init();

        MinecraftForge.EVENT_BUS.register(YouShallNotPatrolModForge.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
