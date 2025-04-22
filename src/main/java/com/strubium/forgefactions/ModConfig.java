package com.strubium.forgefactions;

import com.example.forgefactions.Tags;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID)
public class ModConfig {


    @Config.Name("Faction Command Prefix")
    @Config.Comment("The prefix using for this mods commands")
    @Config.RequiresMcRestart
    public static String factionCommandPrefix = "f";
}
