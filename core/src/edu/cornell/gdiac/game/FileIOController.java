package edu.cornell.gdiac.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.game.model.*;
import edu.cornell.gdiac.util.PooledList;

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
                String tag = f.get("tag").asString();
                worldModel.addGameObject(new FurnitureModel(x, y, width, height, theta, tag));
            }

            // array of door objects
            JsonValue door = level.get("door");
            for (JsonValue f = door.child(); f != null; f = f.next() ) {
                float x = f.get("x").asFloat();
                float y = f.get("y").asFloat();
                float width = f.get("width").asFloat();
                float height = f.get("height").asFloat();
                float theta = f.get("theta").asFloat() * (float)Math.PI / 180f;
                String tag = f.get("tag").asString();
                worldModel.addGameObject(new DoorModel(x, y, width, height, theta, tag));
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
                            f.getTags()[0]));
                }
//                 Adding Doors to the list of game object
                if (go.getClass() == DoorModel.class) {
                    DoorModel f = (DoorModel)go;
                    Body b = f.getBody();
                    doors.add(new Door(b.getPosition().x, b.getPosition().y,
                            f.getWidth(), f.getHeight(), b.getAngle()*180f/(float)Math.PI,
                            f.getTags()[0]));
                }
                if (go.getClass() == FoodModel.class) {
                    FoodModel f = (FoodModel)go;
                    Body b = f.getBody();
                    food.add(new Food(b.getPosition().x, b.getPosition().y,
                            f.getRadius(), b.getAngle()*180f/(float)Math.PI,
                            f.isDessert(), f.getTags()[0]));
                }
                if (go.getClass() == WallModel.class) {
                    WallModel wm = (WallModel)go;
                    walls.add(new Wall(wm.getCoords(), wm.getTags()[0]));
                }
            }
            Level level = new Level(p, furniture.toArray(new Furniture[0]), food.toArray(new Food[0]), walls.toArray(new Wall[0]), doors.toArray( new Door[0]));
            writer.write(json.prettyPrint(level));

            writer.close();
        } catch (Exception e) {}
    }

    private class Furniture{
        float x, y, width, height, theta; String tag;
        public Furniture(float x, float y, float width, float height, float theta, String tag) {
            this.x=x; this.y=y; this.width=width; this.height = height; this.theta=theta; this.tag=tag;
        }
    }

    private class Door{
        float x, y, width, height, theta; String tag;
        public Door(float x, float y, float width, float height, float theta, String tag) {
            this.x=x; this.y=y; this.width=width; this.height = height; this.theta=theta; this.tag=tag;
        }
    }

    private class Food{
        float x, y, radius, theta; boolean dessert; String tag;
        public Food(float x, float y, float radius, float theta, boolean dessert, String tag) {
            this.x=x; this.y=y; this.radius=radius; this.theta=theta; this.dessert=dessert; this.tag=tag;
        }
    }
    private class Wall{
        float[] coords; String tag;
        public Wall(float[] coords, String tag) {
            this.coords=coords; this.tag=tag;
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
        public Level(Player p, Furniture[] fu, Food[] fo, Wall[] wa, Door[] dor) {
            player=p; furniture=fu; food=fo; walls=wa; doors = dor;
        }
    }
}