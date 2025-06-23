package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class ZigZagAlien extends Sprite {

    private Bomb bomb;
    private int zigzagDirection = 1; // 1 for right, -1 for left
    private int moveCounter = 0;
    private static final int ZIGZAG_CHANGE_FREQUENCY = 30; // Change direction every 30 frames
    private static final int HORIZONTAL_SPEED = 3;
    private static final int VERTICAL_SPEED = 1;

    public ZigZagAlien(int x, int y) {
        initZigZagAlien(x, y);
    }

    private void initZigZagAlien(int x, int y) {
        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        moveCounter++;

        // Change zigzag direction periodically
        if (moveCounter % ZIGZAG_CHANGE_FREQUENCY == 0) {
            zigzagDirection *= -1; // Reverse direction
        }

        // Move horizontally in zigzag pattern
        this.x += zigzagDirection * HORIZONTAL_SPEED;

        // Move vertically downward
        this.y += VERTICAL_SPEED;

        // Keep alien within screen bounds horizontally
        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
            zigzagDirection = 1; // Force right direction
        } else if (this.x >= BOARD_WIDTH - BORDER_RIGHT - ALIEN_WIDTH) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - ALIEN_WIDTH;
            zigzagDirection = -1; // Force left direction
        }

        // Update bomb position
        bomb.setX(this.x);
    }

    public Bomb getBomb() {
        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}