package com.strubium.forgefactions.commands;

import com.strubium.forgefactions.faction.Faction;
import com.strubium.forgefactions.faction.FactionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Set;

public class CommandListFactions extends CommandBase {
    @Override
    public String getName() {
        return "factionlist";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/factionlist";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            // Get the factions from the FactionManager
            Set<Faction> factions = FactionManager.getInstance(sender.getEntityWorld()).getFactions();

            if (factions.isEmpty()) {
                player.sendMessage(new TextComponentString("There are no factions currently."));
            } else {
                player.sendMessage(new TextComponentString("Factions:"));
                for (Faction faction : factions) {
                    player.sendMessage(new TextComponentString("- " + faction.getName()));
                }
            }
        }
    }
}