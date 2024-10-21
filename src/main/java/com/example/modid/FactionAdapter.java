package com.example.modid;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FactionAdapter extends TypeAdapter<Faction> {

    @Override
    public void write(JsonWriter out, Faction faction) throws IOException {
        out.beginObject();

        // Serialize faction name
        out.name("name").value(faction.getName());

        // Serialize leader as UUID
        if (faction.getLeader() != null) {
            out.name("leaderUUID").value(faction.getLeader().getUniqueID().toString());
        } else {
            out.name("leaderUUID").nullValue();
        }

        // Serialize members as a set of UUIDs
        out.name("members");
        out.beginArray();
        for (EntityPlayer member : faction.getMembers()) {
            out.value(member.getUniqueID().toString());
        }
        out.endArray();

        // Serialize claimed chunks (x, z coordinates)
        out.name("claimedChunks");
        out.beginArray();
        for (ChunkPos chunk : faction.getClaimedChunks()) {
            out.beginObject();
            out.name("x").value(chunk.x);
            out.name("z").value(chunk.z);
            out.endObject();
        }
        out.endArray();

        // Serialize enemies as a set of faction names (or IDs if you prefer)
        out.name("enemies");
        out.beginArray();
        for (Faction enemy : faction.getEnemies()) {
            out.value(enemy.getName());  // Assuming Faction has a unique name
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public Faction read(JsonReader in) throws IOException {
        Faction faction = new Faction(); // Assume you have a default constructor
        Set<EntityPlayer> members = new HashSet<>();
        Set<ChunkPos> claimedChunks = new HashSet<>();
        Set<Faction> enemies = new HashSet<>();
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
                case "members":
                    in.beginArray();
                    while (in.hasNext()) {
                        UUID memberUUID = UUID.fromString(in.nextString());
                        EntityPlayer member = resolvePlayerByUUID(memberUUID);
                        if (member != null) {
                            members.add(member);
                        }
                    }
                    in.endArray();
                    break;
                case "claimedChunks":
                    in.beginArray();
                    while (in.hasNext()) {
                        int x = 0, z = 0;
                        in.beginObject();
                        while (in.hasNext()) {
                            String chunkField = in.nextName();
                            if (chunkField.equals("x")) {
                                x = in.nextInt();
                            } else if (chunkField.equals("z")) {
                                z = in.nextInt();
                            }
                        }
                        in.endObject();
                        claimedChunks.add(new ChunkPos(x, z));
                    }
                    in.endArray();
                    break;
                case "enemies":
                    in.beginArray();
                    while (in.hasNext()) {
                        String enemyFactionName = in.nextString();
                        Faction enemyFaction = resolveFactionByName(enemyFactionName);
                        if (enemyFaction != null) {
                            enemies.add(enemyFaction);
                        }
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();  // Skip unknown fields
                    break;
            }
        }
        in.endObject();

        // Resolve leader by UUID
        if (leaderUUID != null) {
            faction.setLeader(resolvePlayerByUUID(leaderUUID));
        }

        faction.setMembers(members);
        faction.setClaimedChunks(claimedChunks);
        faction.setEnemies(enemies);

        return faction;
    }

    /**
     * Helper method to resolve an EntityPlayer by UUID.
     */
    private EntityPlayer resolvePlayerByUUID(UUID uuid) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            return server.getPlayerList().getPlayerByUUID(uuid);  // Gets the player if they are online
        }
        return null;  // Player is offline or not found
    }

    /**
     * Helper method to resolve a Faction by its name (or ID).
     * This will depend on how you're managing factions in your mod.
     */
    private Faction resolveFactionByName(String factionName) {
        return FactionManager.getFaction(factionName);
    }
}
