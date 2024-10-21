package com.example.modid.commands;

import com.example.modid.Faction;
import com.example.modid.FactionChunkHandler;
import com.example.modid.FactionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import static com.example.modid.FactionManager.getFactionByPlayer;

public class CommandDeclareWar extends CommandBase {

    @Override
    public String getName() {
        return "declarewar";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/declarewar <factionName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            FactionManager factionManager = FactionManager.getInstance(server.getEntityWorld());

            Faction playerFaction = getFactionByPlayer(player).orElse(null);
            Faction enemyFaction = factionManager.getFaction(args[0]);

            if (playerFaction != null && enemyFaction != null) {
                if (playerFaction.getLeader().equals(player)) {
                    factionManager.declareWar(playerFaction, enemyFaction);
                    player.sendMessage(new TextComponentString("War declared on " + enemyFaction.getName() + "!"));
                } else {
                    player.sendMessage(new TextComponentString("Only the faction leader can declare war!"));
                }
            } else {
                player.sendMessage(new TextComponentString("Faction not found!"));
            }
        }
    }
}

