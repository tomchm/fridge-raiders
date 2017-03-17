package edu.cornell.gdiac.game;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.game.model.DetectiveModel;
import edu.cornell.gdiac.game.model.FoodModel;
import edu.cornell.gdiac.game.model.FurnitureModel;
import edu.cornell.gdiac.game.model.WallModel;

import java.io.File;
import java.io.FileReader;

/**
 * Created by Sal on 3/17/2017.
 * This class serves two functions.
 * (1) Loads JSON files into a WorldModel
 * (2) Writes contents of a WorldModel into a JSON file.
 */
public class FileIOController {
    protected WorldModel worldModel;
    protected JsonReader parser;

    public FileIOController(WorldModel wm) {
        worldModel = wm;
        parser = new JsonReader();
    }
    /** Load the specified level into the WorldModel. */
    public void load(String filename) {
        try {
            JsonValue level = parser.parse(new FileReader(filename));
            JsonValue player = level.get("player");
            worldModel.setPlayer(new DetectiveModel(player.get("x").asFloat(), player.get("y").asFloat()));
            worldModel.addGameObject(worldModel.getPlayer());

            // array of furniture objects
            JsonValue furniture = level.get("furniture");
            for (JsonValue f = furniture.child(); f != null; f = f.next() ) {
                float x = f.get("x").asFloat();
                float y = f.get("y").asFloat();
                float width = f.get("width").asFloat();
                float height = f.get("height").asFloat();
                float theta = f.get("theta").asFloat() * (float)Math.PI / 180f;
                String tag = f.get("tag").asString();
                worldModel.addGameObject(new FurnitureModel(x, y, width, height, theta, tag));
            }

            // array of food objects
            JsonValue food = level.get("food");
            for (JsonValue f = food.child(); f != null; f = f.next()) {
                float x = f.get("x").asFloat();
                float y = f.get("y").asFloat();
                float radius = f.get("radius").asFloat();
                float theta = f.get("theta").asFloat() * (float)Math.PI / 180f;
                boolean dessert = f.get("dessert").asBoolean();
                String tag = f.get("tag").asString();
                worldModel.addGameObject(new FoodModel(x, y, radius, theta, dessert, tag));
            }

            // array of wall objects
            JsonValue walls = level.get("walls");
            for (JsonValue w = walls.child(); w != null; w = w.next()) {
                float[] coords = w.get("coords").asFloatArray();
                String tag = w.get("tag").asString();
                worldModel.addGameObject(new WallModel(coords, tag));
            }
        } catch (Exception e) {}
    }
    /** Save the contents of the WorldModel into the specified file. */
    public void save(String filename) {

    }
}
