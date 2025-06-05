package net.youshallnotpatrol.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;

import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;
import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;

public class YouShallNotPatrolModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YouShallNotPatrol.init();
        NeoForgeConfigRegistry.INSTANCE.register(
                YouShallNotPatrol.MOD_ID, ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
