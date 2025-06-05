package net.youshallnotpatrol.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;

@Mod(YouShallNotPatrol.MOD_ID)
public class YouShallNotPatrolModNeoForge {
    public YouShallNotPatrolModNeoForge(ModContainer container, IEventBus bus) {
        YouShallNotPatrol.init();
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
    }
}
