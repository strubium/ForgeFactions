package com.strubium.forgefactions.commands;

import com.strubium.forgefactions.faction.Faction;
import com.strubium.forgefactions.faction.FactionManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
        if (sender instanceof EntityPlayerMP) {  // Use EntityPlayerMP for server-side commands
            EntityPlayerMP player = (EntityPlayerMP) sender;
            FactionManager factionManager = FactionManager.getInstance(server.getEntityWorld());

            // Use the factions directly from factionManager
            Faction playerFaction = getPlayerFaction(player, factionManager);
            Faction enemyFaction = factionManager.getFaction(args[0]);

            if (playerFaction != null && enemyFaction != null) {
                // Check if the player is the faction leader
                if (playerFaction.getLeader().getId().equals(player.getUniqueID())) {
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

    // Utility method to find the player's faction based on UUID comparison
    private Faction getPlayerFaction(EntityPlayerMP player, FactionManager factionManager) {
        for (Faction faction : factionManager.getFactions()) {  // Directly iterate over the Set
            for (GameProfile member : faction.getMembers()) {
                if (member.getId().equals(player.getUniqueID())) {
                    return faction;  // Return the faction if the player's UUID matches a member's UUID
                }
            }
        }
        return null;  // Player is not in any faction
    }
}
