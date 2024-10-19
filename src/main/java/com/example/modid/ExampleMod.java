package com.example.modid;

import com.example.modid.Tags;
import com.example.modid.commands.CommandDeclareWar;
import com.example.modid.commands.CommandEndWar;
import com.example.modid.commands.CommandFactionGUI;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.modid.commands.CommandCreateFaction;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class ExampleMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    @Mod.Instance
    public static ExampleMod instance;
    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new FactionChunkHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        NetworkRegistry.INSTANCE.registerGuiHandler(ExampleMod.instance, new GuiHandler());
        ClientCommandHandler.instance.registerCommand(new CommandFactionGUI());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandDeclareWar());
        event.registerServerCommand(new CommandEndWar());

    }


}
