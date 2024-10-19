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
            String factionName = args[0];
            FactionManager.getInstance().createFaction(factionName, player);
            player.sendMessage(new TextComponentString("Faction " + factionName + " created!"));
        }
    }
}
