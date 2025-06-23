package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import gdd.sprite.ZigZagAlien;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {

    private List<Enemy> enemies;
    private List<ZigZagAlien> zigzagAliens;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private Game game;

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    protected int frame = 0;

    // Audio players for sound effects and background music
    private AudioPlayer shootSoundPlayer;
    private AudioPlayer explosionSoundPlayer;
    private AudioPlayer backgroundMusicPlayer;
    
    // *** ADJUST THIS VALUE TO CHANGE WIN CONDITION ***
    private static final int ENEMIES_TO_DESTROY = 5; // Change this number to make it easier/harder

    public Scene2(Game game) {
        this.game = game;

        // Fixed spawn pattern with more enemies spread throughout the level
        spawnMap.put(60, new SpawnDetails("Enemy", 100, 0));
        spawnMap.put(120, new SpawnDetails("ZigZag", 200, 0));
        spawnMap.put(180, new SpawnDetails("Enemy", 300, 0));
        spawnMap.put(240, new SpawnDetails("ZigZag", 400, 0));
        spawnMap.put(300, new SpawnDetails("Enemy", 150, 0));
        spawnMap.put(360, new SpawnDetails("ZigZag", 350, 0));
        spawnMap.put(420, new SpawnDetails("Enemy", 250, 0));
        spawnMap.put(480, new SpawnDetails("ZigZag", 450, 0));
        spawnMap.put(540, new SpawnDetails("Enemy", 50, 0));
        spawnMap.put(600, new SpawnDetails("ZigZag", 500, 0));
        spawnMap.put(660, new SpawnDetails("Enemy", 300, 0));
        spawnMap.put(720, new SpawnDetails("ZigZag", 100, 0));
        spawnMap.put(780, new SpawnDetails("Enemy", 450, 0));
        spawnMap.put(840, new SpawnDetails("ZigZag", 250, 0));
        spawnMap.put(900, new SpawnDetails("Enemy", 350, 0));
        
        System.out.println("Scene2 constructor - spawn map size: " + spawnMap.size());
        System.out.println("Win condition: Destroy " + ENEMIES_TO_DESTROY + " enemies");
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(DELAY, new GameCycle());
        timer.start();

        initAudio();
        gameInit();
    }

    private void initAudio() {
        try {
            // Initialize sound effect players
            System.out.println("Initializing audio...");
            shootSoundPlayer = new AudioPlayer("src/audio/Shoot.wav");
            explosionSoundPlayer = new AudioPlayer("src/audio/InvaderDown.wav");
            
            // Initialize background music
            backgroundMusicPlayer = new AudioPlayer("src/audio/MyScene2.wav");
            System.out.println("Audio initialized successfully");
        } catch (Exception ex) {
            System.err.println("Error initializing sound effects: " + ex.getMessage());
            ex.printStackTrace();
            // Create null players to avoid null pointer exceptions
            shootSoundPlayer = null;
            explosionSoundPlayer = null;
            backgroundMusicPlayer = null;
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        zigzagAliens = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        player = new Player();
        
        // Start background music with error handling
        try {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.play();
                System.out.println("Background music started successfully");
            } else {
                System.err.println("Background music player is null - audio file may not exist");
            }
        } catch (Exception ex) {
            System.err.println("Error starting background music: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("Victory!");
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }

        for (ZigZagAlien zigzag : zigzagAliens) {
            if (zigzag.isVisible()) {
                g.drawImage(zigzag.getImage(), zigzag.getX(), zigzag.getY(), this);
            }
            if (zigzag.isDying()) {
                zigzag.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Enemy e : enemies) {
            Enemy.Bomb b = e.getBomb();
            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }

        for (ZigZagAlien z : zigzagAliens) {
            ZigZagAlien.Bomb b = z.getBomb();
            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
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
        g.setFont(getFont().deriveFont(Font.BOLD, 14));
        g.drawString("Scene 2 - Frame: " + frame, 10, 10);
        // Updated to show the adjustable win condition
        g.drawString("Deaths: " + deaths + "/" + ENEMIES_TO_DESTROY, 10, 30);
        g.drawString("Enemies: " + enemies.size() + " ZigZag: " + zigzagAliens.size(), 10, 50);

        g.setColor(Color.green);

        if (inGame) {
            g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
            drawExplosions(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void playShootSound() {
        try {
            if (shootSoundPlayer != null) {
                shootSoundPlayer.restart();
            }
        } catch (Exception ex) {
            System.err.println("Error playing shoot sound: " + ex.getMessage());
        }
    }

    private void playExplosionSound() {
        try {
            if (explosionSoundPlayer != null) {
                explosionSoundPlayer.restart();
            }
        } catch (Exception ex) {
            System.err.println("Error playing explosion sound: " + ex.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        try {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                System.out.println("Background music stopped");
            }
        } catch (Exception ex) {
            System.err.println("Error stopping background music: " + ex.getMessage());
        }
    }

    private void update() {
        // Any new spawn?
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            System.out.printf("Scene2 Spawn Details: %s at %d,%d (frame %d)\n", sd.type, sd.x, sd.y, frame);
            if (sd.type.equals("Enemy")) {
                Enemy enemy = new Enemy(sd.x, sd.y);
                enemies.add(enemy);
                System.out.println("Added Enemy. Total enemies: " + enemies.size());
            } else if (sd.type.equals("ZigZag")) {
                ZigZagAlien zigzag = new ZigZagAlien(sd.x, sd.y);
                zigzagAliens.add(zigzag);
                System.out.println("Added ZigZag alien. Total zigzag: " + zigzagAliens.size());
            }
        }

        // *** WIN CONDITION - CHANGE ENEMIES_TO_DESTROY CONSTANT TO ADJUST ***
        if (deaths >= ENEMIES_TO_DESTROY) {
            inGame = false;
            timer.stop();
            stopBackgroundMusic();
            message = "Victory!";
            System.out.println("Player won! Destroyed " + deaths + " enemies.");
        }

        // player
        player.act();

        // shots
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                // Check collision with regular enemies
                for (Enemy enemy : enemies) {
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        playExplosionSound();
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                        System.out.println("Enemy destroyed! Deaths: " + deaths + "/" + ENEMIES_TO_DESTROY);
                    }
                }

                // Check collision with zigzag aliens
                for (ZigZagAlien zigzag : zigzagAliens) {
                    int zigzagX = zigzag.getX();
                    int zigzagY = zigzag.getY();

                    if (zigzag.isVisible() && shot.isVisible()
                            && shotX >= (zigzagX)
                            && shotX <= (zigzagX + ALIEN_WIDTH)
                            && shotY >= (zigzagY)
                            && shotY <= (zigzagY + ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        zigzag.setImage(ii.getImage());
                        zigzag.setDying(true);
                        explosions.add(new Explosion(zigzagX, zigzagY));
                        playExplosionSound();
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                        System.out.println("ZigZag alien destroyed! Deaths: " + deaths + "/" + ENEMIES_TO_DESTROY);
                    }
                }

                int y = shot.getY();
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // Update enemies (only move down, no horizontal movement)
        for (Enemy enemy : enemies) {
            enemy.act(); // This moves them down by 2 pixels only
        }

        // Update zigzag aliens (they handle their own zigzag movement)
        for (ZigZagAlien zigzag : zigzagAliens) {
            zigzag.act();
        }

        // Check if regular enemies reached ground
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                int y = enemy.getY();
                if (y > GROUND - ALIEN_HEIGHT) {
                    inGame = false;
                    stopBackgroundMusic();
                    message = "Invasion!";
                }
            }
        }

        // Check if zigzag aliens reached ground
        for (ZigZagAlien zigzag : zigzagAliens) {
            if (zigzag.isVisible()) {
                int y = zigzag.getY();
                if (y > GROUND - ALIEN_HEIGHT) {
                    inGame = false;
                    stopBackgroundMusic();
                    message = "Invasion!";
                }
            }
        }

        // Handle bombs from regular enemies
        for (Enemy enemy : enemies) {
            int chance = randomizer.nextInt(15);
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {
                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                playExplosionSound();
                stopBackgroundMusic();
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }

        // Handle bombs from zigzag aliens
        for (ZigZagAlien zigzag : zigzagAliens) {
            int chance = randomizer.nextInt(25); // Lower chance to drop bombs
            ZigZagAlien.Bomb bomb = zigzag.getBomb();

            if (chance == CHANCE && zigzag.isVisible() && bomb.isDestroyed()) {
                bomb.setDestroyed(false);
                bomb.setX(zigzag.getX());
                bomb.setY(zigzag.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                playExplosionSound();
                stopBackgroundMusic();
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
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
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Scene2 - Shots: " + shots.size());
                if (shots.size() < 4) {
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                    playShootSound();
                }
            }
        }
    }
}