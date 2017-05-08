package edu.cornell.gdiac.game.gui;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.WorldModel;
import edu.cornell.gdiac.game.asset.ImageAsset;
import edu.cornell.gdiac.game.model.FoodModel;
import edu.cornell.gdiac.game.model.GameObject;
import edu.cornell.gdiac.game.model.WallModel;

import java.util.Stack;


/**
 * Created by tomchm on 4/21/17.
 */
public class MinimapGUIModel extends GUIModel {

    private final static int MAX_SIZE = 200;
    private final static int BORDER_WIDTH = 5;
    private final static Color wallColor = new Color(0.8f,0.8f, 0.8f, 1f );
    private final static Color borderColor = new Color(0.7f, 0.7f, 0.7f, 1f);
    private final static Color playerColor = new Color(110f/255f, 76f/255f, 41f/255f, 1f);
    private final static int SCREEN_X = 400, SCREEN_Y = 120;
    private int adjX, adjY;
    private int counter = 0;
    private Pixmap map;
    private float minY, minX, scale;

    private WorldModel world;

    public MinimapGUIModel(WorldModel world){
        adjX = 0;
        adjY = 0;
        this.world = world;
        findConstants();
        map = new Pixmap(MAX_SIZE, MAX_SIZE, Pixmap.Format.RGBA8888);
        makeMap();
        guiTag = "MinimapGUI";
        tags = new String[]{"crumb"};

    }

    private void findConstants(){
        minX = 100000;
        minY = 100000;
        float maxX = 0;
        float maxY = 0;
        for(GameObject go : world.getGameObjects()){
            if(go instanceof WallModel){
                WallModel wm = (WallModel)go;
                float[] coords = wm.getCoords();
                for(int i=0; i< coords.length; i+=2){
                    if(coords[i] < minX){
                        minX = coords[i];
                    }
                    if(coords[i] > maxX){
                        maxX = coords[i];
                    }
                    if(coords[i+1] < minY){
                        minY = coords[i+1];
                    }
                    if(coords[i+1] > maxY){
                        maxY = coords[i+1];
                    }
                }
            }
        }
        maxX+=1;
        maxY+=1;
        minX-=1;
        minY-=1;
        if(maxX-minX > maxY-minY){
            scale = (maxX - minX)/MAX_SIZE;
            adjY = MAX_SIZE/2 - (int)((maxY - minY)/scale*0.5f);
        }
        else {
            scale = (maxY - minY)/MAX_SIZE;
            adjX = MAX_SIZE/2 - (int)((maxX - minX)/scale*0.5f);
        }
    }

    private void makeMap(){
        for(GameObject go : world.getGameObjects()) {
            if (go instanceof WallModel) {
                makeWall((WallModel)go);
            }
        }
        fillMap();
        addBorder();
    }

    private void makeWall(WallModel wall){
        float[] coords = wall.getCoords();
        int[] icoords = new int[8];
        for(int i=0; i<8; i+=2){
            icoords[i] = adjX + (int)((coords[i]-minX)/scale);
            icoords[i+1] = adjY + (int)((coords[i+1]-minY)/scale);
        }
        map.setColor(wallColor);
        map.fillTriangle(icoords[0], icoords[1], icoords[2], icoords[3], icoords[4], icoords[5]);
        map.fillTriangle(icoords[0], icoords[1], icoords[4], icoords[5], icoords[6], icoords[7]);
    }

    private void fillMap(){
        Stack<Pixel> stack = new Stack<Pixel>();
        stack.add(new Pixel(0,0));

        while(!stack.isEmpty()){
            Pixel p = stack.pop();
            if(isPixelEmpty(p.x, p.y)){
                map.drawPixel(p.x,p.y,Color.rgba8888(wallColor));
                if(isPixelEmpty(p.x-1,p.y)) stack.add(new Pixel(p.x-1, p.y));
                if(isPixelEmpty(p.x+1,p.y)) stack.add(new Pixel(p.x+1, p.y));
                if(isPixelEmpty(p.x,p.y-1)) stack.add(new Pixel(p.x, p.y-1));
                if(isPixelEmpty(p.x,p.y+1)) stack.add(new Pixel(p.x, p.y+1));
            }
        }
    }

    private void addBorder(){
        for(int x=0; x< MAX_SIZE; x++){
            for(int y=0; y<BORDER_WIDTH; y++){
                map.drawPixel(x,y,Color.rgba8888(borderColor));
                //System.out.println(x+" "+y);
            }

            for(int y=MAX_SIZE-BORDER_WIDTH; y<MAX_SIZE; y++){
                map.drawPixel(x,y,Color.rgba8888(borderColor));
                //System.out.println(x+"/"+y);
            }
        }

        for(int y=0; y< MAX_SIZE; y++){
            for(int x=0; x<BORDER_WIDTH; x++){
                map.drawPixel(x,y,Color.rgba8888(borderColor));
                //System.out.println(x+"-"+y);
            }

            for(int x=MAX_SIZE-BORDER_WIDTH; x<MAX_SIZE; x++){
                map.drawPixel(x,y,Color.rgba8888(borderColor));
                //System.out.println(x+"+"+y);
            }
        }
    }

    private boolean isPixelEmpty(int x, int y){
        if(x < 0 || x >= MAX_SIZE || y < 0 || y >= MAX_SIZE){
            return false;
        }
        if(map.getPixel(x,y) == Color.rgba8888(wallColor)){
            return false;
        }
        return true;
    }

    
    @Override
    public void draw(GameCanvas canvas) {
            TextureRegion texture = new TextureRegion(new Texture(map));
            canvas.draw(texture, new Color(1,1,1,0.5f), 0, 0, origin.x*GameObject.getDrawScale().x + SCREEN_X , origin.y*GameObject.getDrawScale().y + SCREEN_Y + MAX_SIZE, 0, 1f, -1f);
            ImageAsset ia = (ImageAsset)assetMap.get("crumb");
            if(ia != null){
                float px = origin.x*GameObject.getDrawScale().x + (world.getPlayer().getBody().getPosition().x-minX)/scale + SCREEN_X + adjX;
                float py = origin.y*GameObject.getDrawScale().y + (world.getPlayer().getBody().getPosition().y-minY)/scale + SCREEN_Y + adjY;
                canvas.draw(ia.getTexture(), Color.RED, ia.getOrigin().x , ia.getOrigin().y , px, py, 0, ia.getImageScale().x, ia.getImageScale().y);
                /*
                if(world.getPlayer().getAmountEaten() > world.getPlayer().getThreshold() && !world.getPlayer().isSecondStage()){
                    float px = origin.x*GameObject.getDrawScale().x + (world.getPlayer().getBody().getPosition().x-minX)/scale + SCREEN_X;
                    float py = origin.y*GameObject.getDrawScale().y + (world.getPlayer().getBody().getPosition().y-minY)/scale + SCREEN_Y;
                    canvas.draw(ia.getTexture(), Color.RED, ia.getOrigin().x , ia.getOrigin().y , px, py, 0, ia.getImageScale().x, ia.getImageScale().y);
                }
                */

                //Draw Dessert



                if(world.getPlayer().getAmountEaten() >= world.getPlayer().getThreshold() && (!world.getPlayer().isSecondStage()) && (counter <= 60)){
                    // Get Dessert
                    counter +=1;
                    float dessertX = 0f;
                    float dessertY = 0f;
                    for(GameObject gm: world.getGameObjects()){
                        if(gm instanceof FoodModel){
                            if(((FoodModel) gm).isDessert()){
                                dessertX = gm.getBody().getPosition().x;
                                dessertY = gm.getBody().getPosition().y;
                            }
                        }
                    }


                    float dx = origin.x*GameObject.getDrawScale().x + (dessertX-minX)/scale + SCREEN_X + adjX;
                    float dy = origin.y*GameObject.getDrawScale().y + (dessertY-minY)/scale + SCREEN_Y + adjY;
                    canvas.draw(ia.getTexture(), Color.YELLOW, ia.getOrigin().x , ia.getOrigin().y , dx, dy, 0, ia.getImageScale().x, ia.getImageScale().y);
                }

                if(counter >= 60 && counter < 90) {
                    counter += 1;
                }
                if(counter >= 90){
                    counter = 0;
                }

            }


    }

    private class Pixel{
        int x,y;

        Pixel(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
}
