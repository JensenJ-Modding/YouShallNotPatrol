package net.youshallnotpatrol.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.PatrolSpawner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.youshallnotpatrol.YouShallNotPatrol;
import net.youshallnotpatrol.config.ServerConfig;

@Mixin(value = PatrolSpawner.class, priority = 100)
public class PatrolSpawnerMixin {

    // We need two different variables as if the pillagers fail to spawn for whatever reason, last targeted player would
    // be incorrect, and the same player may get targeted twice in a row.
    @Unique UUID youshallnotpatrol$lastTargetedPlayerUUID;

    @Unique UUID youshallnotpatrol$selectedPlayerUUID;

    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;getCurrentDifficultyAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/DifficultyInstance;"))
    private void youshallnotpatrol$setSelectedPlayer(
            ServerLevel serverLevel,
            boolean bl,
            boolean bl2,
            CallbackInfoReturnable<Integer> cir,
            @Local Player player) {
        youshallnotpatrol$selectedPlayerUUID = player.getUUID();
        if (ServerConfig.shouldLog.get()) {
            YouShallNotPatrol.LOGGER.info(
                    "Attempting patrol spawn on {}.",
                    YouShallNotPatrol.getPlayerNameFromUUID(
                            youshallnotpatrol$selectedPlayerUUID, serverLevel.players()));
        }
    }

    @Inject(
            method = "spawnPatrolMember",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/monster/PatrollingMonster;setPatrolLeader(Z)V"))
    private void youshallnotpatrol$setLastTargetedPlayer(
            ServerLevel serverLevel,
            BlockPos blockPos,
            RandomSource randomSource,
            boolean bl,
            CallbackInfoReturnable<Boolean> cir) {
        youshallnotpatrol$lastTargetedPlayerUUID = youshallnotpatrol$selectedPlayerUUID;
        if (ServerConfig.shouldLog.get()) {
            YouShallNotPatrol.LOGGER.info(
                    "Patrol spawned on {}.",
                    YouShallNotPatrol.getPlayerNameFromUUID(
                            youshallnotpatrol$lastTargetedPlayerUUID, serverLevel.players()));
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void youShallNotPatrol$checkForSpawnChance(
            ServerLevel serverLevel, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cir) {
        // Cancels the tick function if the spawn chance is 0.
        if (ServerConfig.pillagerSpawnChance.get() == 0) {
            cir.setReturnValue(0);
        }
    }

    // Note: this method overrides the original calculation used for pillager spawn rate.
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 1))
    private int youshallnotpatrol$shouldPatrolSpawn(
            int original, @Local RandomSource source, @Local(argsOnly = true) ServerLevel level) {
        // If the returned value is 0, a patrol will try to spawn
        if (level.players().isEmpty()) {
            return 1;
        }
        int randomChance = source.nextInt(100);
        int spawnChance = ServerConfig.pillagerSpawnChance.get();
        boolean willSpawn = randomChance < spawnChance;
        if (ServerConfig.shouldLog.get()) {
            YouShallNotPatrol.LOGGER.info(
                    "Rolling patrol spawn. Roll: {}. Spawn: {}. Success: {}.", randomChance, spawnChance, willSpawn);
        }
        return willSpawn ? 0 : 1;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;"))
    private List<ServerPlayer> youshallnotpatrol$modifyPlayerList(
            List<ServerPlayer> original, @Local(argsOnly = true) ServerLevel level) {
        if (level.players().size() == 1) {
            if (ServerConfig.shouldLog.get() && ServerConfig.pillagerSpawnOnDifferentPlayer.get()) {
                YouShallNotPatrol.LOGGER.info(
                        "Attempting patrol spawn with only 1 player. They will be chosen as there are no other candidates.");
            }
            return original;
        }

        // Create a copy of players so we don't modify the original server player list
        ArrayList<ServerPlayer> players = new ArrayList<>(original);
        if (ServerConfig.pillagerSpawnOnDifferentPlayer.get()) {
            if (youshallnotpatrol$lastTargetedPlayerUUID != null) {
                players.removeIf(player -> player.getUUID() == youshallnotpatrol$lastTargetedPlayerUUID);
                if (ServerConfig.shouldLog.get()) {
                    ArrayList<String> playerList = YouShallNotPatrol.formatPlayerList(players);
                    YouShallNotPatrol.LOGGER.info(
                            "Attempting patrol spawn. Will not spawn on {} as they were the last targeted player. Remaining candidates: {}",
                            YouShallNotPatrol.getPlayerNameFromUUID(
                                    youshallnotpatrol$lastTargetedPlayerUUID, level.players()),
                            playerList);
                }
            }
            return players;
        }
        return original;
    }
}
