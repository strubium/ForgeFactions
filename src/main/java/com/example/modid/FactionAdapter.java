package com.example.modid;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.UUID;

public class FactionAdapter extends TypeAdapter<Faction> {

    @Override
    public void write(JsonWriter out, Faction faction) throws IOException {
        out.beginObject();

        // Serialize faction name
        out.name("name").value(faction.getName());

        // Serialize leader as UUID string
        if (faction.getLeader() != null) {
            out.name("leaderUUID").value(faction.getLeader().getUniqueID().toString());
        } else {
            out.name("leaderUUID").nullValue();  // If there's no leader
        }

        // You can add other faction-related fields here
        out.endObject();
    }

    @Override
    public Faction read(JsonReader in) throws IOException {
        Faction faction = new Faction(); // Assume you have a default constructor
        UUID leaderUUID = null;

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "name":
                    faction.setName(in.nextString());
                    break;
                case "leaderUUID":
                    String uuidString = in.nextString();
                    if (uuidString != null && !uuidString.isEmpty()) {
                        leaderUUID = UUID.fromString(uuidString);
                    }
                    break;
                // Add cases for other fields if needed
                default:
                    in.skipValue();  // Skip unknown fields
                    break;
            }
        }
        in.endObject();

        // Resolve the leader by UUID once the JSON is fully read
        if (leaderUUID != null) {
            faction.setLeader(resolveLeaderByUUID(leaderUUID));
        }

        return faction;
    }

    /**
     * Helper method to resolve the leader by UUID.
     * This looks up the player on the Minecraft server by UUID.
     */
    private EntityPlayerMP resolveLeaderByUUID(UUID leaderUUID) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            return server.getPlayerList().getPlayerByUUID(leaderUUID); // Gets the player if they are online
        }
        return null; // Return null if the player is offline or not found
    }
}
