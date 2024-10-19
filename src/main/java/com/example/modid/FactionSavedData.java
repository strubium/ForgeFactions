package com.example.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionSavedData {

    private static final String DATA_FILE = "faction_data.json"; // Name of the JSON file
    private Map<String, Faction> factions;
    private final Path savePath;

    private final Gson gson;

    public FactionSavedData(World world) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.savePath = Paths.get(world.getSaveHandler().getWorldDirectory().getPath(), DATA_FILE);
        load();
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(savePath)) {
            gson.toJson(factions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (Files.exists(savePath)) {
            try (BufferedReader reader = Files.newBufferedReader(savePath)) {
                Type factionMapType = new TypeToken<Map<String, Faction>>() {}.getType();
                factions = gson.fromJson(reader, factionMapType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            factions = new HashMap<>(); // Initialize factions if the file does not exist
        }
    }

    public void setFactions(Map<String, Faction> factions) {
        this.factions = factions;
        save();  // Save whenever factions are set
    }

    public Map<String, Faction> getFactions() {
        return factions;
    }

    private EntityPlayerMP resolvePlayerFromUUID(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);

        // Get the Minecraft server instance
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server != null) {
            // Use the server instance to get the player by UUID
            return server.getPlayerList().getPlayerByUUID(uuid);
        }

        // Return null if the player isn't found
        return null;
    }
}
