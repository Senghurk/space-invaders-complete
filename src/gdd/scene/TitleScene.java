package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath);

            // audioPlayer.play();
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
        audioPlayer.play();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);

        var ii = new ImageIcon("src/images/title.png");
        g.drawImage(ii.getImage(), 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        g.setFont(getFont().deriveFont(Font.BOLD, 40));

        if (frame % 60 < 30) {
            String text = "Press SPACE to Start";
            int stringWidth = g.getFontMetrics().stringWidth(text);
            int x = (d.width - stringWidth) / 2;
            g.drawString(text, x, 600);
        }

        getFocusCycleRootAncestor();

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {

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
                System.out.println("Start the game");
                try {
                    audioPlayer.stop();
                } catch (Exception ex) {
                    System.err.println("Error stopping audio.");
                    ex.printStackTrace();
                }
                game.loadScene1();

            }

        }
    }
}
