package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Map;
import java.util.UUID;

public class FactionSavedData extends WorldSavedData {

    private static final String DATA_NAME = "faction_data";
    private Map<String, Faction> factions;

    public FactionSavedData() {
        super(DATA_NAME);
    }

    public FactionSavedData(String name) {
        super(name);
    }

    public static FactionSavedData get(World world) {
        FactionSavedData data = (FactionSavedData) world.getPerWorldStorage().getOrLoadData(FactionSavedData.class, DATA_NAME);
        if (data == null) {
            data = new FactionSavedData();
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // Load factions from NBT
        NBTTagList factionList = nbt.getTagList("Factions", 10); // 10 is the tag type for compound
        for (int i = 0; i < factionList.tagCount(); i++) {
            NBTTagCompound factionNBT = factionList.getCompoundTagAt(i);

            // Extract faction name
            String factionName = factionNBT.getString("Name");

            // Load leader UUID and resolve the player
            String leaderUUID = factionNBT.getString("Leader");
            EntityPlayer leader = resolvePlayerFromUUID(leaderUUID);  // You'll need to implement this method

            if (leader != null) {
                // Create the faction with the loaded leader
                Faction faction = new Faction(factionName, leader);

                // Read the rest of the faction data
                faction.readFromNBT(factionNBT);

                // Add the faction to the manager's faction map
                factions.put(factionName, faction);
            } else {
                // Handle cases where the leader might not be found
                System.err.println("Leader not found for faction: " + factionName);
            }
        }
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        // Save factions to NBT
        NBTTagList factionList = new NBTTagList();
        for (Faction faction : factions.values()) {
            NBTTagCompound factionNBT = new NBTTagCompound();
            faction.writeToNBT(factionNBT);
            factionList.appendTag(factionNBT);
        }
        compound.setTag("Factions", factionList);
        return compound;
    }

    public void setFactions(Map<String, Faction> factions) {
        this.factions = factions;
        markDirty();  // Mark as needing to be saved
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

