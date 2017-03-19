package edu.cornell.gdiac.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by Sal on 3/17/2017.
 * This class serves two functions.
 * (1) Loads JSON files into a WorldModel
 * (2) Writes contents of a WorldModel into a JSON file.
 */
public class FileIOController {
    protected WorldModel worldModel;
    protected JsonReader parser;
    protected FileWriter writer;
    protected Json json;

    public FileIOController(WorldModel wm) {
        worldModel = wm;
        parser = new JsonReader();
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
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
                String[] tags = f.get("tags").asStringArray();
                worldModel.addGameObject(new FurnitureModel(x, y, width, height, theta, tags));
            }

            // array of door objects
            JsonValue doors = level.get("doors");
            for (JsonValue d = doors.child(); d != null; d = d.next() ) {
                float x = d.get("x").asFloat();
                float y = d.get("y").asFloat();
                float width = d.get("width").asFloat();
                float height = d.get("height").asFloat();
                float theta = d.get("theta").asFloat() * (float)Math.PI / 180f;
                String[] tags = d.get("tags").asStringArray();
                worldModel.addGameObject(new DoorModel(x, y, width, height, theta, tags));
            }

            // array of food objects
            JsonValue food = level.get("food");
            for (JsonValue f = food.child(); f != null; f = f.next()) {
                float x = f.get("x").asFloat();
                float y = f.get("y").asFloat();
                float radius = f.get("radius").asFloat();
                float theta = f.get("theta").asFloat() * (float)Math.PI / 180f;
                boolean dessert = f.get("dessert").asBoolean();
                String[] tags = f.get("tags").asStringArray();
                worldModel.addGameObject(new FoodModel(x, y, radius, theta, dessert, tags));
            }

            // array of wall objects
            JsonValue walls = level.get("walls");
            for (JsonValue w = walls.child(); w != null; w = w.next()) {
                float[] coords = w.get("coords").asFloatArray();
                String[] tags = w.get("tags").asStringArray();
                worldModel.addGameObject(new WallModel(coords, tags));

            }
        } catch (Exception e) {}
    }
    /** Save the contents of the WorldModel into the specified file. */
    public void save(String filename) {
        try {
            writer = new FileWriter(filename);
            PooledList<Furniture> furniture = new PooledList<Furniture>();
            PooledList<Food> food = new PooledList<Food>();
            PooledList<Wall> walls = new PooledList<Wall>();
            PooledList<Door> doors = new PooledList<Door>();
            Player p = new Player(worldModel.getPlayer().getBody().getPosition().x,
                    worldModel.getPlayer().getBody().getPosition().y);
            for (GameObject go : worldModel.getGameObjects()) {
                if (go.getClass() == FurnitureModel.class) {
                    FurnitureModel f = (FurnitureModel)go;
                    Body b = f.getBody();
                    furniture.add(new Furniture(b.getPosition().x, b.getPosition().y,
                            f.getWidth(), f.getHeight(), b.getAngle()*180f/(float)Math.PI,
                            f.getTags()));
                }
//                 Adding Doors to the list of game object
                if (go.getClass() == DoorModel.class) {
                    DoorModel f = (DoorModel)go;
                    Body b = f.getBody();
                    doors.add(new Door(b.getPosition().x, b.getPosition().y,
                            f.getWidth(), f.getHeight(), b.getAngle()*180f/(float)Math.PI,
                            f.getTags()));
                }
                if (go.getClass() == FoodModel.class) {
                    FoodModel f = (FoodModel)go;
                    Body b = f.getBody();
                    food.add(new Food(b.getPosition().x, b.getPosition().y,
                            f.getRadius(), b.getAngle()*180f/(float)Math.PI,
                            f.isDessert(), f.getTags()));
                }
                if (go.getClass() == WallModel.class) {
                    WallModel wm = (WallModel)go;
                    walls.add(new Wall(wm.getCoords(), wm.getTags()));
                }
            }
            Level level = new Level(p, furniture.toArray(new Furniture[0]), food.toArray(new Food[0]), walls.toArray(new Wall[0]), doors.toArray( new Door[0]));
            writer.write(json.prettyPrint(level));

            writer.close();
        } catch (Exception e) {}
    }

    private class Furniture{
        float x, y, width, height, theta; String[] tags;
        public Furniture(float x, float y, float width, float height, float theta, String[] tags) {
            this.x=x; this.y=y; this.width=width; this.height = height; this.theta=theta; this.tags=tags;
        }
    }

    private class Door{
        float x, y, width, height, theta; String[] tags;
        public Door(float x, float y, float width, float height, float theta, String[] tags) {
            this.x=x; this.y=y; this.width=width; this.height = height; this.theta=theta; this.tags=tags;
        }
    }

    private class Food{
        float x, y, radius, theta; boolean dessert; String[] tags;
        public Food(float x, float y, float radius, float theta, boolean dessert, String[] tags) {
            this.x=x; this.y=y; this.radius=radius; this.theta=theta; this.dessert=dessert; this.tags=tags;
        }
    }
    private class Wall{
        float[] coords; String[] tags;
        public Wall(float[] coords, String[] tags) {
            this.coords=coords; this.tags=tags;
        }
    }
    private class Player{
        float x, y;
        public Player(float x, float y) {
            this.x=x; this.y=y;
        }
    }
    private class Level {
        private Furniture[] furniture;
        private Food[] food;
        private Wall[] walls;
        private Door[] doors;
        private Player player;
        public Level() {}
        public Level(Player p, Furniture[] fu, Food[] fo, Wall[] wa, Door[] dor) {
            player=p; furniture=fu; food=fo; walls=wa; doors = dor;
        }
    }
}