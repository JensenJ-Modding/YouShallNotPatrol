package net.youshallnotpatrol.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;

@Mixin(value = WanderingTraderSpawner.class, priority = 100)
public class WanderingTraderSpawnerMixin {

    @Shadow
    @Final
    private RandomSource random;

    @Unique UUID youshallnotpatrol$lastTargetedPlayerUUID;

    @Inject(
            method = "spawn",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/storage/ServerLevelData;setWanderingTraderId(Ljava/util/UUID;)V"))
    private void youshallnotpatrol$setLastTargetedPlayer(
            ServerLevel serverLevel, CallbackInfoReturnable<Boolean> cir, @Local Player player) {
        youshallnotpatrol$lastTargetedPlayerUUID = player.getUUID();
        if (ServerConfig.shouldLog.get()) {
            YouShallNotPatrol.LOGGER.info(
                    "Wandering trader spawned on {}.",
                    YouShallNotPatrol.getPlayerNameFromUUID(
                            youshallnotpatrol$lastTargetedPlayerUUID, serverLevel.players()));
        }
    }

    // Note: this method overrides the original calculation used for pillager spawn rate.
    @ModifyExpressionValue(
            method = "spawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int youshallnotpatrol$shouldTraderSpawn(int original, @Local(argsOnly = true) ServerLevel level) {
        // If the returned value is 0, a trader will try to spawn
        if (level.players().isEmpty()) {
            return 1;
        }
        int randomChance = random.nextInt(100);
        int spawnChance = ServerConfig.traderSpawnChance.get();
        boolean willSpawn = randomChance < spawnChance;
        if (ServerConfig.shouldLog.get()) {
            YouShallNotPatrol.LOGGER.info(
                    "Rolling wandering trader spawn. Roll: {}. Spawn: {}. Success: {}.",
                    randomChance,
                    spawnChance,
                    willSpawn);
        }
        return willSpawn ? 0 : 1;
    }

    @ModifyExpressionValue(
            method = "spawn",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;getRandomPlayer()Lnet/minecraft/server/level/ServerPlayer;"))
    private ServerPlayer youshallnotpatrol$modifyPlayerList(
            ServerPlayer original, @Local(argsOnly = true) ServerLevel level) {
        if (original == null) {
            return null;
        }

        if (level.players().size() == 1) {
            if ((ServerConfig.shouldLog.get() && ServerConfig.traderSpawnOnDifferentPlayer.get())) {
                YouShallNotPatrol.LOGGER.info(
                        "Attempting wandering trader spawn with only 1 player. They will be chosen as there are no other candidates.");
            }
            return original;
        }

        // If the original player was targeted last time. We will get a different player.
        if (ServerConfig.traderSpawnOnDifferentPlayer.get()) {
            if (original.getUUID() == youshallnotpatrol$lastTargetedPlayerUUID) {
                // We create a new arraylist to prevent modification to the original.
                List<ServerPlayer> players = new ArrayList<>(level.players());
                players.remove(original);
                if (ServerConfig.shouldLog.get()) {
                    ArrayList<String> playerList = YouShallNotPatrol.formatPlayerList(players);
                    YouShallNotPatrol.LOGGER.info(
                            "Attemting wandering trader spawn. Will not spawn on {} as they were the last targeted player. Remaining candidates: {}",
                            YouShallNotPatrol.getPlayerNameFromUUID(
                                    youshallnotpatrol$lastTargetedPlayerUUID, level.players()),
                            playerList);
                }
                return players.get(random.nextInt(players.size()));
            }
        }
        return original;
    }
}
