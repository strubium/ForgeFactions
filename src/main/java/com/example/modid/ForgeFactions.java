package com.example.modid;

import com.example.modid.commands.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class ForgeFactions {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    @Mod.Instance
    public static ForgeFactions instance;

    private FactionSavedData factionSavedData;
    private FactionChunkHandler factionChunkHandler;

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Check if the event is on the server side
        if (event.getSide().isServer()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            if (server == null) {
                System.err.println("MinecraftServer instance is null! Cannot proceed with initializing FactionSavedData.");
                return;
            }

            World world = server.getWorld(0); // Dimension ID 0 for the overworld

            if (world == null) {
                System.err.println("World instance is null! Cannot proceed with initializing FactionSavedData.");
                return;
            }

            // Initialize faction saved data
            factionSavedData = new FactionSavedData(world);

            // Create the FactionChunkHandler with the saved data
            factionChunkHandler = new FactionChunkHandler(world);

            // Register the handler
            MinecraftForge.EVENT_BUS.register(factionChunkHandler);
        } else {
            System.out.println("Running on client side. Faction data will not be initialized.");
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandCreateFaction());
        event.registerServerCommand(new CommandListFactions());
        event.registerServerCommand(new CommandClaimChunk());
        event.registerServerCommand(new CommandDeclareWar());
        event.registerServerCommand(new CommandEndWar());

    }


}
