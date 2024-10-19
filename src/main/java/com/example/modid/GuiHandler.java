package com.example.modid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static int FactionManageId = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ExampleMod.LOGGER.warn("Server GUI Element requested with ID: " + ID);
        if (ID == FactionManageId) {
            ExampleMod.LOGGER.warn("Opening GUI ID: " + ID);
            return new GuiFactionManagement(player);
        }
        ExampleMod.LOGGER.warn("Incorrect GUI ID: " + ID);
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ExampleMod.LOGGER.info("Client GUI Element requested with ID: " + ID);
        if (ID == FactionManageId) {
            ExampleMod.LOGGER.warn("Opening GUI ID: " + ID);
            return new GuiFactionManagement(player);
        }
        ExampleMod.LOGGER.warn("Incorrect GUI ID: " + ID);
        return null; // Return null for other IDs
    }
}

