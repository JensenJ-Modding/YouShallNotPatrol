package net.youshallnotpatrol.fabric;

import net.minecraftforge.fml.config.ModConfig;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;

@SuppressWarnings("unused")
public class YouShallNotPatrolModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YouShallNotPatrol.init();
        ForgeConfigRegistry.INSTANCE.register(
                YouShallNotPatrol.MOD_ID, ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
