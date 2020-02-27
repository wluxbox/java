package spaceinv.view;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import spaceinv.event.EventBus;
import spaceinv.event.EventHandler;
import spaceinv.event.ModelEvent;
import spaceinv.model.*;
import spaceinv.model.enemyships.BattleCruiser;
import spaceinv.model.enemyships.Bomber;
import spaceinv.model.enemyships.Frigate;

import static java.lang.System.out;
import static spaceinv.model.SI.*;


/*
 *  The GUI for the SpaceInv game (the view of the model).
 *  Run this to run the game
 *
 *  No application logic here just look/rendering, event handling
 *  and input to model
 */
public class SIGUI extends Application implements EventHandler {


    private SI spaceInv;                // Reference to the OO-model
    private boolean running = false;    // Is game running?

    // ------- Keyboard handling ----------------------------------

    private void keyPressed(KeyEvent event) {
        if (!running) {
            return;
        }
        KeyCode kc = event.getCode();
        switch (kc) {
            case LEFT:
               // TODO
                spaceInv.getPlayer().setXspeed(-GUN_MAX_DX);
                break;
            case RIGHT:
                spaceInv.getPlayer().setXspeed(GUN_MAX_DX);
               // TODO
                break;
            case SPACE:
                if(!spaceInv.getPlayer().projectileIsPresent()){
                    spaceInv.fireGun();
                }
                break;
            default:  // Nothing
        }
    }

    private void keyReleased(KeyEvent event) {
        if (!running) {
            return;
        }
        KeyCode kc = event.getCode();
        switch (kc) {
            case LEFT:
            case RIGHT:
                spaceInv.getPlayer().setXspeed(0);
                break;
            default: // Nothing
        }
    }

    // ---- Menu handling -----------------

    private void handleMenu(ActionEvent e) {
        MenuItem mi = (MenuItem) e.getSource();
        switch (mi.getText()) {
            case "New":
                newGame();
                break;
            case "Stop":
                stopGame();
                break;
            case "Exit":
                System.exit(0);
                break;
            case "Play":
                toggleMusic();
                break;
            default: // Nothing
        }
    }

    // ---------- Menu actions ---------------------

    private void newGame() {
        spaceInv = new SI();



        // TODO Build model

        // NOTE: Declared at top of class
        //spaceInv  =

        renderBackground();
        timer.start();
        running = true;
    }

    private void stopGame() {
        running = false;
        timer.stop();
    }

    private void toggleMusic() {
        // TODO Optional
        out.println("toggle");
       /* AudioClip music = FileService.getSound("music");
        if (music.isPlaying()) {
            music.stop();
            //setCheckMenuItemSelected("Music", "Play", false);
        } else {
            music.play(0.1);
            //setCheckMenuItemSelected("Music", "Play", true);
        }*/
    }

    // --- Handling events coming form the model -----
    // Implements EventHandler
    @Override
    public void onModelEvent(ModelEvent evt) {
        Positionable p;
        switch (evt.type) {
            case GUN_HIT_SHIP:
                p = (Positionable) evt.data;
                new Explosion(p.getX()+p.getWidth()/2, p.getY()+p.getHeight()/2, fg).start();
                Assets.INSTANCE.rocketHitShip.play();
                break;
            case GUN_SHOOT:
                // TODO Optional
                break;
            case BOMB_HIT_GROUND:
                // TODO Optional
                break;
            case BOMB_HIT_GUN:
                p = (Positionable) evt.data;
                new Explosion(p.getX()+p.getWidth()/2, p.getY()+p.getHeight()/2, fg).start();
                timer.stop();
                running = false;
                break;
            case HAS_WON:
                timer.stop();
                running = false;
                break;
            default:
        }
    }

    // ************* Rendering and JavaFX below (nothing to do)  *************

    private void render() {
        fg.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        for (Positionable d : spaceInv.getPositionables()) {
            Image i = Assets.INSTANCE.get(d.getClass());
            fg.drawImage(i, d.getX(), d.getY(), d.getWidth(), d.getHeight());
        }
        //out.println(spaceInv.getPoints());
        fg.setFont(Assets.INSTANCE.font);
        fg.setFill(Assets.INSTANCE.colorFgText);
        fg.fillText(String.valueOf(spaceInv.getPoints()),
                10 , GAME_HEIGHT - Assets.INSTANCE.font.getSize());

    }

    private void renderBackground() {
        bg.drawImage(Assets.INSTANCE.background, 0, 0, GAME_WIDTH, GAME_HEIGHT);
    }

    private AnimationTimer timer;
    private GraphicsContext fg;   // Foreground
    private GraphicsContext bg;   // Background
    private SIMenu menuBar;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        // Menu
        menuBar = new SIMenu(this::handleMenu);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        // Drawing areas
        Canvas backGround = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        bg = backGround.getGraphicsContext2D();
        Canvas foreGround = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        fg = foreGround.getGraphicsContext2D();

        Pane pane = new Pane(backGround, foreGround);
        root.setCenter(pane);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                spaceInv.update(now);
                render();
            }
        };

        bg.drawImage(Assets.INSTANCE.background, 0, 0, GAME_WIDTH, GAME_HEIGHT);
        EventBus.INSTANCE.register(this);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(this::keyPressed);
        scene.setOnKeyReleased(this::keyReleased);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Invaders");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
