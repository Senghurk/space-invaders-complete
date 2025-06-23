package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);

    private Timer timer;

    protected int frame = 0;

    private AudioPlayer audioPlayer;

    private Game game;

    public TitleScene(Game game) {
        this.game = game;
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/Opening.wav";
            audioPlayer = new AudioPlayer(filePath);
        } catch (Exception ex) {
            System.err.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);

        timer = new Timer(DELAY, new GameCycle());
        timer.start();

        initAudio();
        gameInit();
    }
    
    private void gameInit() {
        if (audioPlayer != null) {
            audioPlayer.play();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    /**
     * Creates a rainbow color based on the current frame for animated effect
     */
    private Color getRainbowColor(int offset) {
        // Create a smooth rainbow transition using HSB color space
        float hue = ((frame + offset) % 180) / 180.0f; // Cycle through hues
        return Color.getHSBColor(hue, 0.8f, 1.0f); // High saturation, full brightness
    }

    private void doDrawing(Graphics g) {
        // Enable antialiasing for smoother text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Draw the title image
        var ii = new ImageIcon("src/images/title.png");
        g.drawImage(ii.getImage(), 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);

        // Set font for the blinking text
        g.setFont(getFont().deriveFont(Font.BOLD, 42));

        // Blinking effect - show text for 40 frames, hide for 20 frames
        if (frame % 60 < 40) {
            String text = "Press SPACE to Start";
            int stringWidth = g.getFontMetrics().stringWidth(text);
            int x = (d.width - stringWidth) / 2;
            int y = 650; // Moved down from 600 to 650

            // Draw rainbow text by drawing each character in a different color
            char[] chars = text.toCharArray();
            int currentX = x;
            
            for (int i = 0; i < chars.length; i++) {
                // Get rainbow color for this character
                Color rainbowColor = getRainbowColor(i * 10); // Offset each character's color
                g.setColor(rainbowColor);
                
                // Draw the character
                String charStr = String.valueOf(chars[i]);
                g.drawString(charStr, currentX, y);
                
                // Move to next character position
                currentX += g.getFontMetrics().charWidth(chars[i]);
            }
            
            // Add a subtle glow effect by drawing the text slightly offset with lower opacity
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    if (offsetX == 0 && offsetY == 0) continue; // Skip the main text position
                    
                    currentX = x;
                    for (int i = 0; i < chars.length; i++) {
                        Color rainbowColor = getRainbowColor(i * 10);
                        // Create a darker, more transparent version for the glow
                        Color glowColor = new Color(
                            rainbowColor.getRed(), 
                            rainbowColor.getGreen(), 
                            rainbowColor.getBlue(), 
                            30 // Low alpha for glow effect
                        );
                        g.setColor(glowColor);
                        
                        String charStr = String.valueOf(chars[i]);
                        g.drawString(charStr, currentX + offsetX, y + offsetY);
                        currentX += g.getFontMetrics().charWidth(chars[i]);
                    }
                }
            }
        }

        getFocusCycleRootAncestor();
        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        // Nothing to update in title screen
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                System.out.println("Start Scene 2");
                try {
                    if (audioPlayer != null) {
                        audioPlayer.stop();
                    }
                } catch (Exception ex) {
                    System.err.println("Error stopping audio.");
                    ex.printStackTrace();
                }
                // Load Scene 2
                game.loadScene2();
            }
        }
    }
}