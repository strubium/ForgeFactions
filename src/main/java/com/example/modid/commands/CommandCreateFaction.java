package com.example.modid.commands;

import com.example.modid.FactionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandCreateFaction extends CommandBase {
    @Override
    public String getName() {
        return "factioncreate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/factioncreate <name>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            // Check if the player provided a faction name
            if (args.length == 0) {
                player.sendMessage(new TextComponentString("Error: You must specify a faction name!"));
                return;
            }

            String factionName = args[0];

            // Check if the faction already exists
            if (FactionManager.getInstance(sender.getEntityWorld()).factionExists(factionName)) {
                player.sendMessage(new TextComponentString("Error: A faction with that name already exists."));
                return;
            }

            // Create the faction if the name is valid and doesn't exist
            FactionManager.getInstance(sender.getEntityWorld()).createFaction(factionName, player);
            player.sendMessage(new TextComponentString("Faction " + factionName + " created!"));
        } else {
            sender.sendMessage(new TextComponentString("This command can only be used by a player."));
        }
    }
}
