package com.example.modid;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiFactionManagement extends GuiScreen {
    private final EntityPlayer player;
    private List<String> factionList;        // To store the names of all created factions
    private List<String> claimedChunksList;  // To store claimed chunks
    private String message;                  // To display messages to the player

    public GuiFactionManagement(EntityPlayer player) {
        this.player = player;
        this.factionList = new ArrayList<>();
        this.claimedChunksList = new ArrayList<>();
        this.message = "";  // Initialize the message
    }

    @Override
    public void initGui() {
        System.out.println("INITing GUI");
        this.buttonList.clear();
        // Add buttons for various functionalities
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 50, "Claim Chunk"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 - 20, "View Factions"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 10, "View Claimed Chunks"));
        this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 2 + 40, "Close"));

        // Initialize the GUI lists to display the current state
        viewFactions(); // Load factions when the GUI opens
        viewClaimedChunks(); // Load claimed chunks
        System.out.println("Button List Size: " + this.buttonList.size()); // Check button list
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        System.out.println("Drawing GUI screen");
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Faction Management", this.width / 2, this.height / 2 - 100, 0xFFFFFF);
        // Draw the message if exists
        this.drawCenteredString(this.fontRenderer, message, this.width / 2, this.height / 2 - 80, 0xFFFFFF);
        // Draw the list of factions
        this.drawString(this.fontRenderer, "Factions:", this.width / 2 - 100, this.height / 2 - 40, 0xFFFFFF);
        int offsetY = 0;
        for (String factionName : factionList) {
            this.drawString(this.fontRenderer, factionName, this.width / 2 - 100, this.height / 2 - 30 + offsetY, 0xFFFFFF);
            offsetY += 10;
        }
        // Draw the claimed chunks list
        this.drawString(this.fontRenderer, "Claimed Chunks:", this.width / 2 - 100, this.height / 2 + 20, 0xFFFFFF);
        offsetY = 0;
        for (String chunkPos : claimedChunksList) {
            this.drawString(this.fontRenderer, chunkPos, this.width / 2 - 100, this.height / 2 + 30 + offsetY, 0xFFFFFF);
            offsetY += 10;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                claimChunk();
                break;
            case 1:
                viewFactions();
                break;
            case 2:
                viewClaimedChunks();
                break;
            case 3:
                mc.displayGuiScreen(null);  // Close the GUI
                break;
        }
    }

    private void claimChunk() {
        FactionManager factionManager = FactionManager.getInstance();
        Faction playerFaction = getPlayerFaction(player);

        if (playerFaction != null) {
            ChunkPos chunkPos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);
            if (!playerFaction.getClaimedChunks().contains(chunkPos)) {
                playerFaction.claimChunk(chunkPos);
                message = "You have claimed the chunk at " + chunkPos.toString() + ".";
            } else {
                message = "This chunk is already claimed by your faction.";
            }
        } else {
            message = "You must be in a faction to claim chunks.";
        }
        // Refresh the claimed chunks list after claiming
        viewClaimedChunks();
    }

    private void viewFactions() {
        factionList.clear();  // Clear previous entries
        for (Faction faction : FactionManager.getInstance().getFactions()) {
            factionList.add(faction.getName());
        }
        if (factionList.isEmpty()) {
            factionList.add("No factions created.");
        }
        message = "";  // Clear the message
    }

    private void viewClaimedChunks() {
        claimedChunksList.clear();  // Clear previous entries
        Faction playerFaction = getPlayerFaction(player);
        if (playerFaction != null) {
            for (ChunkPos chunkPos : playerFaction.getClaimedChunks()) {
                claimedChunksList.add(chunkPos.toString());
            }
            if (claimedChunksList.isEmpty()) {
                claimedChunksList.add("No claimed chunks.");
            }
        } else {
            claimedChunksList.add("You must be in a faction to view claimed chunks.");
        }
        message = "";  // Clear the message
    }

    private Faction getPlayerFaction(EntityPlayer player) {
        for (Faction faction : FactionManager.getInstance().getFactions()) {
            if (faction.getMembers().contains(player)) {
                return faction;
            }
        }
        return null;
    }
}
