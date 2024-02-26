package com.him;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HimConfig 
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "him.json");
    
    // 1 - 8 minutes
    private int minSecondsStalk = 60;
    private int maxSecondsStalk = 480;
    
    // 1 - 10 minutes
    private int minSecondsHaunt = 60;
    private int maxSecondsHaunt = 600;
    
    // 1 - 10 minutes
    private int minSecondsGrief = 60;
    private int maxSecondsGrief = 600;
    
    // haunt bools
    public boolean enablePhantomAudio = true;
    public boolean enableManipulateBlocks = true;
    public boolean enableChatMessage = true;
    public boolean enableCreateParticle = true;
    public boolean enableSleepScare = true;
    
    // grief bools
    public boolean enableSpawnTunnel = true;
    public boolean enableSpawnShaft = true;
    public boolean enableSpawnDirtTower = true;
    public boolean enableSpawnSandPyramid = true;
    public boolean enablePlaceSign = true;
    public boolean enablePlaceTorch = true;
    public boolean enableTrimTrees = true;
    public boolean enableSetFire = true;

    public static HimConfig loadConfig() 
    {
        HimConfig config;
        try 
        {
            if (Files.exists(CONFIG_PATH)) 
            {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH))
                {
                    config = GSON.fromJson(reader, HimConfig.class);
                }
            }
            else
            {
                config = new HimConfig();
                Files.createDirectories(CONFIG_PATH.getParent());
                try (Writer writer = Files.newBufferedWriter(CONFIG_PATH))
                {
                    GSON.toJson(config, writer);
                }
            }
        } 
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load or create config", e);
        }
        return config;
    }

    public int getMinSecondsStalk() { return minSecondsStalk; }
    public int getMaxSecondsStalk() { return maxSecondsStalk; }
    public int getMinSecondsHaunt() { return minSecondsHaunt; }
    public int getMaxSecondsHaunt() { return maxSecondsHaunt; }
    public int getMinSecondsGrief() { return minSecondsGrief; }
    public int getMaxSecondsGrief() { return maxSecondsGrief; }
}