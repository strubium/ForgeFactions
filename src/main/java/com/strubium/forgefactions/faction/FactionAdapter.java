package com.strubium.forgefactions.faction;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
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

        // Serialize leader as GameProfile (UUID + Name)
        GameProfile leader = faction.getLeader();
        if (leader != null) {
            out.name("leaderUUID").value(leader.getId().toString());
            out.name("leaderName").value(leader.getName());
        } else {
            out.name("leaderUUID").nullValue();
            out.name("leaderName").nullValue();
        }

        // Serialize members as GameProfiles (UUID + Name)
        out.name("members");
        out.beginArray();
        for (GameProfile profile : faction.getMembers()) {
            out.beginObject();
            out.name("uuid").value(profile.getId().toString());
            out.name("name").value(profile.getName());
            out.endObject();
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

        // Serialize enemies as a set of faction names
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
        Set<GameProfile> members = new HashSet<>();
        Set<ChunkPos> claimedChunks = new HashSet<>();
        Set<Faction> enemies = new HashSet<>();
        GameProfile leaderProfile = null;

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "name":
                    faction.setName(in.nextString());
                    break;
                case "leaderUUID":
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                    } else {
                        UUID leaderUUID = UUID.fromString(in.nextString());
                        // name will be read later
                        leaderProfile = new GameProfile(leaderUUID, null);
                    }
                    break;
                case "leaderName":
                    if (leaderProfile != null && in.peek() != JsonToken.NULL) {
                        leaderProfile = new GameProfile(leaderProfile.getId(), in.nextString());
                    } else {
                        in.skipValue();
                    }
                    break;
                case "members":
                    in.beginArray();
                    while (in.hasNext()) {
                        UUID uuid = null;
                        String name = null;
                        in.beginObject();
                        while (in.hasNext()) {
                            switch (in.nextName()) {
                                case "uuid":
                                    uuid = UUID.fromString(in.nextString());
                                    break;
                                case "name":
                                    name = in.nextString();
                                    break;
                                default:
                                    in.skipValue();
                            }
                        }
                        in.endObject();
                        if (uuid != null) {
                            members.add(new GameProfile(uuid, name));
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
                            switch (in.nextName()) {
                                case "x": x = in.nextInt(); break;
                                case "z": z = in.nextInt(); break;
                                default: in.skipValue();
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
                        String enemyName = in.nextString();
                        Faction enemy = resolveFactionByName(enemyName);
                        if (enemy != null) {
                            enemies.add(enemy);
                        }
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        // Set the leader using the resolved GameProfile
        faction.setLeader(leaderProfile);
        faction.setMembers(members);
        faction.setClaimedChunks(claimedChunks);
        faction.setEnemies(enemies);

        return faction;
    }

    /**
     * Helper method to resolve an EntityPlayer by UUID.
     * Resolves a player if they are online using the MinecraftServer.
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
        return FactionManager.getFaction(factionName);  // Adjust this method based on your FactionManager setup
    }
}