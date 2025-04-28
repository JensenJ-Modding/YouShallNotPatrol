package net.youshallnotpatrol;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouShallNotPatrol {
    public static final String MOD_ID = "youshallnotpatrol";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {}

    // These functions are only used when debug logging is turned on.

    public static ArrayList<String> formatPlayerList(List<ServerPlayer> players) {
        ArrayList<String> playerList = new ArrayList<>();
        for (Player player : players) {
            playerList.add(player.getDisplayName().getString());
        }
        return playerList;
    }

    public static String getPlayerNameFromUUID(UUID playerUUID, List<ServerPlayer> players) {
        for (Player player : players) {
            if (playerUUID == player.getUUID()) {
                return player.getDisplayName().getString();
            }
        }
        return playerUUID.toString();
    }
}
