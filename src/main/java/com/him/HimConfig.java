package com.him;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class HimConfig 
{
	private static final Gson GSON = new GsonBuilder()
	        .setPrettyPrinting()
	        .addSerializationExclusionStrategy(new FieldExclusionStrategy())
	        .addDeserializationExclusionStrategy(new FieldExclusionStrategy())
	        .create();
	private static final Path CONFIG_PATH = Paths.get("config", "him.json");
    
    // 1 - 8 minutes
    private int minSecondsStalk = 5;
    private int maxSecondsStalk = 480;
    
    // 1 - 10 minutes
    private int minSecondsHaunt = 60;
    private int maxSecondsHaunt = 600;
    
    // 1 - 10 minutes
    private int minSecondsGrief = 60;
    private int maxSecondsGrief = 600;
    
    // stalking distance
    private double minStalkDistance = 42;
    private double maxStalkDistance = 64;
    
    // stalking bool
    public boolean enableBlindingPlayer = true;
    
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
                    config = checkAndAddMissingValues(config);
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

    private static HimConfig checkAndAddMissingValues(HimConfig config) 
    {
        boolean updated = false;
        JsonObject jsonObject = (JsonObject) GSON.toJsonTree(config);

        Field[] fields = HimConfig.class.getDeclaredFields();

        for (Field field : fields) 
        {
            field.setAccessible(true);
            String name = field.getName();
            if (!jsonObject.has(name)) 
            {
                try 
                {
                    Object value = field.get(config);
                    if (value instanceof Number) 
                        jsonObject.addProperty(name, (Number) value);
                    else if (value instanceof Boolean) 
                        jsonObject.addProperty(name, (Boolean) value);
                    else if (value instanceof String) 
                        jsonObject.addProperty(name, (String) value);
                    else if (value instanceof Character) 
                        jsonObject.addProperty(name, (Character) value);
                    else 
                        jsonObject.add(name, GSON.toJsonTree(value));
                    updated = true;
                } 
                catch (IllegalAccessException e) 
                {
                    throw new RuntimeException("Failed to access field: " + name, e);
                }
            }
        }

        if (updated) 
        {
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) { GSON.toJson(jsonObject, writer); } 
            catch (IOException e) { throw new RuntimeException("Failed to update config file", e); }
            config = GSON.fromJson(jsonObject, HimConfig.class); // reload config to ensure all fields are set correctly
        }

        return config;
    }

    private static class FieldExclusionStrategy implements ExclusionStrategy 
    {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) { return fieldAttributes.getDeclaringClass() != HimConfig.class; }
        @Override
        public boolean shouldSkipClass(Class<?> clazz) { return false; }
    }

    public int getMinSecondsStalk() { return minSecondsStalk; }
    public int getMaxSecondsStalk() { return maxSecondsStalk; }
    public int getMinSecondsHaunt() { return minSecondsHaunt; }
    public int getMaxSecondsHaunt() { return maxSecondsHaunt; }
    public int getMinSecondsGrief() { return minSecondsGrief; }
    public int getMaxSecondsGrief() { return maxSecondsGrief; }
    public double getMinStalkDistance() { return minStalkDistance; }
    public double getMaxStalkDistance() { return maxStalkDistance; }
}