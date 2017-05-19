package edu.cornell.gdiac.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by tomchm on 5/8/17.
 */
public class ScoreIOController {

    public static void saveDefaultScore(){
        LevelData[] levels = new LevelData[10];
        levels[0] = new LevelData("level0.json", true, "none", "none");
        for(int i=1; i<10; i++){
            levels[i] = new LevelData("level"+i+".json", false, "none", "none");
        }

        Json json  = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        try {
            FileWriter writer = new FileWriter("score.json");
            writer.write(json.prettyPrint(new LevelDataContainer(levels)));
            writer.close();
        } catch (IOException e) {
            System.out.println("Scores could not be saved.");
        }
    }

    public static boolean better(String oldone, String newone) {
        int oldval = 0;
        if (oldone.equals("silver")) oldval = 1;
        if (oldone.equals("gold")) oldval = 2;
        int newval = 0;
        if (newone.equals("silver")) newval = 1;
        if (newone.equals("gold")) newval = 2;
        return newval > oldval;
    }

    public static void updateLevel(int i, boolean unlocked, String foodMedal, String golfMedal){
        LevelData[] levels = getScores();
        levels[i].unlocked = unlocked;
        if (better(levels[i].foodMedal, foodMedal)) levels[i].foodMedal = foodMedal;
        if (better(levels[i].golfMedal, golfMedal)) levels[i].golfMedal = golfMedal;

        Json json  = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        try {
            FileWriter writer = new FileWriter("score.json");
            writer.write(json.prettyPrint(new LevelDataContainer(levels)));
            writer.close();
        } catch (IOException e) {
            System.out.println("Scores could not be updated.");
        }
    }

    public static LevelData[] getScores(){
        try {
            FileHandle file = Gdx.files.local("score.json");
            Json json = new Json();
            JsonReader parser = new JsonReader();
            String s = file.readString();
            JsonValue data = parser.parse(s);
            JsonValue levels = data.get("data");
            //System.out.println(json.toJson(levels));
            JsonValue level = levels.child();

            LevelData[] levelData = new LevelData[10];

            for(int i=0; i<10; i++){
                String filename = level.get("filename").asString();
                boolean unlocked = level.get("unlocked").asBoolean();
                String foodMedal = level.get("foodMedal").asString();
                String golfMedal = level.get("golfMedal").asString();
                levelData[i] = new LevelData(filename, unlocked, foodMedal, golfMedal);
                level = level.next();
            }


            return levelData;
        }
        catch(Exception e){
            System.out.println("Failed to read scores.json.");
        }
        return null;
    }


    public static class LevelData {
        String filename;
        boolean unlocked;
        String foodMedal;
        String golfMedal;

        LevelData(String filename, boolean unlocked, String foodMedal, String golfMedal) {
            this.filename = filename;
            this.unlocked = unlocked;
            this.foodMedal = foodMedal;
            this.golfMedal = golfMedal;
        }
    }

    private static class LevelDataContainer {
        LevelData[] data;

        LevelDataContainer(LevelData[] data){
            this.data = data;
        }
    }
}