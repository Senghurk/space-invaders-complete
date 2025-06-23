package gdd.sprite;

import static gdd.Global.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    
    // Increased movement speed from 2 to 4 for faster movement
    private static final int MOVEMENT_SPEED = 10;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
        
        // Set width for boundary checking
        this.width = ii.getIconWidth() * SCALE_FACTOR;
    }

    public void act() {
        x += dx;

        // Left boundary check
        if (x <= 2) {
            x = 2;
        }

        // Right boundary check - use actual image width
        if (x >= BOARD_WIDTH - width - 2) {
            x = BOARD_WIDTH - width - 2;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -MOVEMENT_SPEED; // Changed from -2 to -MOVEMENT_SPEED
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = MOVEMENT_SPEED; // Changed from 2 to MOVEMENT_SPEED
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            dx = 0; // Stop movement when key is released
        }
    }
    
    // Getter for width if needed elsewhere
    public int getWidth() {
        return width;
    }
}