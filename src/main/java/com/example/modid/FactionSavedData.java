package com.example.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
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
        // Enable pretty printing and serialization of null values
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().registerTypeAdapter(Faction.class, new FactionAdapter()).create();
        // Define the save path based on the world directory
        this.savePath = Paths.get(world.getSaveHandler().getWorldDirectory().getPath(), DATA_FILE);
        // Load existing data or initialize a new map
        load();
    }

    public void save() {
        Path tempFile = savePath.resolveSibling(DATA_FILE + ".tmp"); // Temporary file for safe writes
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            // Convert factions map to JSON
            String jsonOutput = gson.toJson(factions);
            System.out.println(jsonOutput); // Print the JSON for validation
            writer.write(jsonOutput);

            // Replace the original file with the temporary file atomically
            try {
                Files.move(tempFile, savePath);
            } catch (FileAlreadyExistsException e) {
                // If the file already exists, overwrite it
                ForgeFactions.LOGGER.warn("File already exists, overwriting: " + savePath);
                Files.delete(savePath); // Delete the existing file
                Files.move(tempFile, savePath); // Move the temp file to the original path
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (Files.exists(savePath)) {
            try (BufferedReader reader = Files.newBufferedReader(savePath)) {
                Type factionMapType = new TypeToken<Map<String, Faction>>() {}.getType();

                // Read the JSON as a string and print it for debugging
                String rawJson = new String(Files.readAllBytes(savePath));
                System.out.println("Raw JSON: " + rawJson);  // Debugging output

                // Attempt to parse the JSON
                factions = gson.fromJson(rawJson, factionMapType);

                // Handle the case where the loaded data is null
                if (factions == null) {
                    System.out.println("Loaded factions are null, initializing new map.");
                    factions = new HashMap<>();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                // Handle the case of malformed JSON
                System.err.println("Invalid JSON in " + savePath + ": " + e.getMessage());
                factions = new HashMap<>(); // Fallback to an empty map
            }
        } else {
            // Initialize factions if the file does not exist
            factions = new HashMap<>();
        }
    }

    public void setFactions(Map<String, Faction> factions) {
        this.factions = factions;
        save();  // Save the factions whenever they're updated
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
