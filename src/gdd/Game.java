package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame  {

    private TitleScene title;
    private Scene1 scene1;
    private Scene2 scene2;

    public Game() {
        initUI();
        loadTitleScene();
    }

    public void loadTitleScene() {
        getContentPane().removeAll();
        add(title);
        title.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        getContentPane().removeAll();
        add(scene1);
        scene1.start();
        revalidate();
        repaint();
    }

    public void loadScene2() {
        getContentPane().removeAll();
        add(scene2);
        scene2.start();
        revalidate();
        repaint();
    }

    private void initUI() {
        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        title = new TitleScene(this);
        scene1 = new Scene1(this);
        scene2 = new Scene2(this);
    }
}