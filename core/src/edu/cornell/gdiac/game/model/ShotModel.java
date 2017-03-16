//package edu.cornell.gdiac.game.model;
//
//import edu.cornell.gdiac.game.GameCanvas;
//
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//
///**
// * Created by Sean on 3/15/17.
// */
//public class ShotModel {
//
//
//    /**
//     * Created by tomchm on 3/6/17.
//     */
//    public class Dot extends WheelObstacle {
//        private boolean visible = false;
//
//
//        public Dot(float x, float y) {
//            super(x, y, 1f);
//            this.setSensor(true);
//            this.setAwake(false);
//        }
//
//        public boolean isVisisble() {
//            return visible;
//        }
//
//        public void setVisible(boolean v) {
//            visible = v;
//        }
//
//        public float getZ() {
//            return -10000;
//        }
//
//
//        public void draw(GameCanvas canvas) {
//            if (texture != null && visible) {
//                System.out.println("Drawing");
//                canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.5f, 0.5f);
//            }
//        }
//
//    }
//
//}