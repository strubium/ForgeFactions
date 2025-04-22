package com.example.modid.commands;

import com.example.modid.Faction;
import com.example.modid.FactionManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import static com.example.modid.FactionManager.getFactionByPlayer;

public class CommandEndWar extends CommandBase {

    @Override
    public String getName() {
        return "endwar";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/endwar <factionName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            GameProfile playerProfile = player.getGameProfile();
            FactionManager factionManager = FactionManager.getInstance(sender.getEntityWorld());

            Faction playerFaction = getFactionByPlayer(playerProfile).orElse(null);
            Faction enemyFaction = factionManager.getFaction(args[0]);

            if (playerFaction != null && enemyFaction != null) {
                if (playerFaction.getLeader().equals(player)) {
                    factionManager.endWar(playerFaction, enemyFaction);
                    player.sendMessage(new TextComponentString("Peace declared with " + enemyFaction.getName() + "."));
                } else {
                    player.sendMessage(new TextComponentString("Only the faction leader can declare peace!"));
                }
            } else {
                player.sendMessage(new TextComponentString("Faction not found!"));
            }
        }
    }
}

