package net.youshallnotpatrol.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = WanderingTraderSpawner.class, priority = 100)
public class WanderingTraderSpawnerMixin {

    @Shadow @Final private RandomSource random;

    @Unique
    ServerPlayer youshallnotpatrol$lastTargetedPlayer;

    @Inject(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setWanderingTraderId(Ljava/util/UUID;)V"))
    private void youshallnotpatrol$setLastTargetedPlayer(ServerLevel serverLevel, CallbackInfoReturnable<Boolean> cir, @Local Player player){
        youshallnotpatrol$lastTargetedPlayer = (ServerPlayer) player;
        YouShallNotPatrol.LOGGER.warn("Trader Spawned. Set {} to the lastTargetedPlayer", youshallnotpatrol$lastTargetedPlayer);
    }

    //Note: this method overrides the original calculation used for pillager spawn rate.
    @ModifyExpressionValue(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int youshallnotpatrol$shouldPatrolSpawn(int original, @Local(argsOnly = true) ServerLevel level){
        //If the returned value is 0, a patrol will try to spawn
        int randomChance = random.nextInt(100);
        int spawnChance = ServerConfig.traderSpawnChance.get();
        YouShallNotPatrol.LOGGER.warn("Attempting trader spawn. Random roll was {}. Spawn chance is {}", randomChance, spawnChance);
        return randomChance < spawnChance ? 0 : 1;
    }

    @ModifyExpressionValue(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getRandomPlayer()Lnet/minecraft/server/level/ServerPlayer;"))
    private ServerPlayer youshallnotpatrol$modifyPlayerList(ServerPlayer original, @Local(argsOnly = true) ServerLevel level){
        if(original == null){
            return null;
        }

        if(level.players().size() == 1) {
            YouShallNotPatrol.LOGGER.warn("Only 1 player, attempting to spawn trader with normal player selection.");
            return original;
        }

        //If the original player was targeted last time. We will get a different player.
        if(ServerConfig.traderSpawnOnDifferentPlayer.get()) {
            if (original == youshallnotpatrol$lastTargetedPlayer) {
                List<ServerPlayer> players = level.players();
                players.remove(original);
                YouShallNotPatrol.LOGGER.warn("Spawning trader. Will not spawn on {} as they were the last player.", youshallnotpatrol$lastTargetedPlayer);
                return players.get(random.nextInt(players.size()));
            }
        }
        return original;
    }
}
