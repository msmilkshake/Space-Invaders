package Main;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import Assets.*;
import Logic.EnemyShotTracking;
import Logic.EnemyTracking;
import Logic.Score;
import Utilities.PolygonMaker;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class SpaceInvaders extends Application {

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;
    private long lastFrameTime = 0L;


    public static void main(String[] args) {

        //I don't think these do anything ?
        System.setProperty("quantum.multithreading", "false");
        System.setProperty("javafx.animation.fullspeed", "false");
        System.setProperty("javafx.animation.pulse", "60");
        System.setProperty("javafx.animation.framerate", "60");


        launch(args);
    }

    public static boolean wait = false;

    @Override
    public void start(Stage stage) throws Exception {


        EnemyTracking enemyTracking = new EnemyTracking();
        EnemyShotTracking enemyShotTracking = new EnemyShotTracking(enemyTracking);

        ArrayList<Invader> invaders = enemyTracking.getInvaders();

        Sounds sounds = new Sounds();

        Player player = new Player(Globals.WIDTH / 2, Globals.HEIGHT - Globals.bottomPadding);
        Score score = new Score();
        GridPane pane = new GridPane();

        Text top = new Text("SCORE<1> HI-SCORE SCORE<2>");
        top.setFont(Globals.getFont());
        top.setTranslateX(56);
        top.setTranslateY(18);
        top.setFill(Color.WHITE);

        Text p1Score = new Text(score.p1GetScoreString());
        p1Score.setFont(Globals.getFont());
        p1Score.setTranslateX(102);
        p1Score.setTranslateY(70);
        p1Score.setFill(Color.WHITE);

        Text hiScore = new Text(score.getHighScoreString());
        hiScore.setFont(Globals.getFont());
        hiScore.setTranslateX(278);
        hiScore.setTranslateY(70);
        hiScore.setFill(Color.WHITE);

        Text lives = new Text("3");
        lives.setFont(Globals.getFont());
        lives.setTranslateX(40);
        lives.setTranslateY(740);
        lives.setFill(Color.WHITE);

        Text credit = new Text("CREDIT 00");
        credit.setFont(Globals.getFont());
        credit.setTranslateX(390);
        credit.setTranslateY(740);
        credit.setFill(Color.WHITE);

        pane.getChildren().add(top);
        pane.getChildren().add(p1Score);
        pane.getChildren().add(hiScore);
        pane.getChildren().add(lives);
        pane.getChildren().add(credit);


        pane.setPrefSize(Globals.WIDTH, Globals.HEIGHT);
        BackgroundFill background = new BackgroundFill(Color.BLACK, null, null);
        pane.setBackground(new Background(background));


        pane.getChildren().add(player.getAsset());
        pane.getChildren().add(player.getImageView());


        // experiment
        long t1 = System.currentTimeMillis();
        Bunker bunker1 = new Bunker(126, 575);
        Bunker bunker2 = new Bunker(246, 575);
        Bunker bunker3 = new Bunker(366, 575);
        Bunker bunker4 = new Bunker(486, 575);


        BunkerSegment[][] bunkerSegments1 = bunker1.getBunkerSegments();
        BunkerSegment[][] bunkerSegments2 = bunker2.getBunkerSegments();
        BunkerSegment[][] bunkerSegments3 = bunker3.getBunkerSegments();
        BunkerSegment[][] bunkerSegments4 = bunker4.getBunkerSegments();

        for (int i = 0; i < bunkerSegments1.length; ++i) {
            for (int j = 0; j < bunkerSegments1[0].length; ++j) {
                pane.getChildren().add(bunkerSegments1[i][j].getAsset());
                pane.getChildren().add(bunkerSegments2[i][j].getAsset());
                pane.getChildren().add(bunkerSegments3[i][j].getAsset());
                pane.getChildren().add(bunkerSegments4[i][j].getAsset());

            }
        }


        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);


        invaders.forEach(invader -> pane.getChildren().add(invader.getAsset()));
        invaders.forEach(invader -> pane.getChildren().add(invader.getCurrentImageView()));

        enemyShotTracking.getEnemyShots().forEach(shot -> pane.getChildren().add(shot.getAsset()));
        //invaders.forEach(invader -> pane.getChildren().add(invader.info()));
        //invaders.forEach(invader -> pane.getChildren().add(invader.displayLabel()));

        Scene scene = new Scene(pane);
        Map<KeyCode, Boolean> pressedKeys = new HashMap();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });

        PlayerShot[] playerShot = new PlayerShot[1];


        new AnimationTimer() {
            @Override
            public void handle(long now) {


                if (now > lastFrameTime + 8_333_333) { //8_333_333
                    lastFrameTime = now;


                    long oldFrameTime = frameTimes[frameTimeIndex];
                    frameTimes[frameTimeIndex] = now;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                    if (frameTimeIndex == 0) {
                        arrayFilled = true;
                    }
                    if (arrayFilled) {
                        long elapsedNanos = now - oldFrameTime;
                        long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                        double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                        System.out.println(String.format("Current frame rate: %.3f", frameRate));
                    }


                    p1Score.setText(score.p1GetScoreString());
                    if (wait) {
                        long t = System.currentTimeMillis();
                        while (true) {

                            if (System.currentTimeMillis() > t + 110) {
                                wait = false;
                                break;
                            }
                        }
                    }
/*
                    enemyShotTracking.getEnemyShots().stream()
                            .filter(shot -> !shot.getAddedToScreen())
                            .forEach(shot -> pane.getChildren().add(shot.getPoly()));
*/
                    //VERY BAD PERFORMANCE !!!
                    for (int i = 0; i < bunkerSegments1.length; ++i) {
                        for (int j = 0; j < bunkerSegments1[0].length; ++j) {
                            BunkerSegment b = bunkerSegments1[i][j];
                            enemyShotTracking.getEnemyShots().forEach(shot -> {

                                if (shot.detectCollision(b) && b.getAlive() && shot.getAlive()) {
                                    b.setAlive(false);
                                    shot.decrementPenetrance();
                                    pane.getChildren().remove(b.getAsset());
                                }
                            });
                            enemyTracking.getInvaders().stream().filter(invader -> !invader.getObstructed())
                                    .forEach(invader -> {
                                        if (invader.detectCollision(b)) {
                                            b.setAlive(false);
                                            pane.getChildren().remove(b.getAsset());
                                        }
                                    });
                        }
                    }
                    for (int i = 0; i < bunkerSegments2.length; ++i) {
                        for (int j = 0; j < bunkerSegments2[0].length; ++j) {
                            BunkerSegment b = bunkerSegments2[i][j];
                            enemyShotTracking.getEnemyShots().forEach(shot -> {
                                if (shot.detectCollision(b) && b.getAlive() && shot.getAlive()) {
                                    b.setAlive(false);
                                    shot.decrementPenetrance();
                                    pane.getChildren().remove(b.getAsset());
                                }
                            });
                            enemyTracking.getInvaders().stream().filter(invader -> !invader.getObstructed())
                                    .forEach(invader -> {
                                        if (invader.detectCollision(b)) {
                                            b.setAlive(false);
                                            pane.getChildren().remove(b.getAsset());
                                        }
                                    });
                        }
                    }
                    for (int i = 0; i < bunkerSegments3.length; ++i) {
                        for (int j = 0; j < bunkerSegments3[0].length; ++j) {
                            BunkerSegment b = bunkerSegments3[i][j];
                            enemyShotTracking.getEnemyShots().forEach(shot -> {
                                if (shot.detectCollision(b) && b.getAlive() && shot.getAlive()) {
                                    b.setAlive(false);
                                    shot.decrementPenetrance();
                                    pane.getChildren().remove(b.getAsset());
                                }
                            });
                            enemyTracking.getInvaders().stream().filter(invader -> !invader.getObstructed())
                                    .forEach(invader -> {
                                        if (invader.detectCollision(b)) {
                                            b.setAlive(false);
                                            pane.getChildren().remove(b.getAsset());
                                        }
                                    });
                        }
                    }
                    for (int i = 0; i < bunkerSegments4.length; ++i) {
                        for (int j = 0; j < bunkerSegments4[0].length; ++j) {
                            BunkerSegment b = bunkerSegments4[i][j];
                            enemyShotTracking.getEnemyShots().forEach(shot -> {
                                if (shot.detectCollision(b) && b.getAlive() && shot.getAlive()) {
                                    b.setAlive(false);
                                    shot.decrementPenetrance();
                                    pane.getChildren().remove(b.getAsset());
                                }
                            });
                            enemyTracking.getInvaders().stream().filter(invader -> !invader.getObstructed())
                                    .forEach(invader -> {
                                        if (invader.detectCollision(b)) {
                                            b.setAlive(false);
                                            pane.getChildren().remove(b.getAsset());
                                        }
                                    });
                        }
                    }


                    enemyShotTracking.shoot();

                    enemyShotTracking.getEnemyShots().forEach(shot -> shot.moveY(Globals.enemyProjectileSpeed));

                    //enemy projectile track and removal
                    enemyShotTracking.getEnemyShots().forEach(shot -> {
                        pane.getChildren().remove(shot.getAsset());
                        shot.updatePolygon();
                        pane.getChildren().add(shot.getAsset());
                        if (shot.detectCollision(player)) {
                            shot.setAlive(false);
                        }

                    });

                    enemyShotTracking.getEnemyShots().stream()
                            .forEach(shot -> {
                                if (shot.getY() > Globals.HEIGHT - 50) {
                                    shot.setAlive(false);
                                }
                                if (shot.getPenetrance() < 1) {
                                    shot.setAlive(false);
                                }
                                if (!shot.getAlive()) {
                                    pane.getChildren().remove(shot.getAsset());

                                }

                            });

                    enemyShotTracking.removeShot();


                    //remove dead invaders ***change this****
                    invaders.stream()
                            .filter(invader -> invader.getToRemove())
                            .forEach(invader -> pane.getChildren().remove(invader.getCurrentImageView()));
                    invaders.stream().filter(invader -> invader.getAlive()).forEach(invader -> pane.getChildren().remove(invader.getAsset()));


                    //for gods sake make this better
                    int pos = enemyTracking.getCurrent();
                    if (pos < invaders.size()) {
                        Invader invaderA = invaders.get(pos);
                        pane.getChildren().remove(invaderA.getCurrentImageView());
                        invaderA.setCurrentImageView();
                        pane.getChildren().add(invaderA.getCurrentImageView());
                    }

                    if (pressedKeys.getOrDefault(KeyCode.LEFT, Boolean.FALSE) || pressedKeys.getOrDefault(KeyCode.A, Boolean.FALSE)) {
                        if (player.boundaryLeft()) {
                            player.moveX(Globals.playerMoveSpeed * -1);
                        }
                    }
                    if (pressedKeys.getOrDefault(KeyCode.RIGHT, Boolean.FALSE) || pressedKeys.getOrDefault(KeyCode.D, Boolean.FALSE)) {
                        if (player.boundaryRight()) {
                            player.moveX(Globals.playerMoveSpeed);
                        }
                    }
                    if (pressedKeys.getOrDefault(KeyCode.SPACE, Boolean.FALSE) && player.getCanShoot() && player.getHasReleased()) {
                        player.setHasReleased(false);
                        PlayerShot shot = new PlayerShot(player.getAsset().getTranslateX() + 17, player.getAsset().getTranslateY());
                        player.setCanShoot(false);
                        playerShot[0] = shot;
                        pane.getChildren().add(shot.getAsset());
                        sounds.playShootSound();

                    }

                    //stops spam firing
                    if (!pressedKeys.getOrDefault(KeyCode.SPACE, Boolean.FALSE)) {
                        player.setHasReleased(true);
                    }


                    enemyTracking.moveNextInvader();


                    //Handle player projectiles
                    if (playerShot[0] != null) {
                        playerShot[0].moveY(Globals.playerShotSpeed);
                        if (playerShot[0].getY() < Globals.topPadding) {
                            sounds.stopShootSound();
                            player.setCanShoot(true);
                            pane.getChildren().remove(playerShot[0].getAsset());
                            playerShot[0].setAlive(false);
                        } else {

                            invaders.forEach(invader -> {
                                if (invader.detectCollision(playerShot[0]) && invader.getAlive()) {
                                    sounds.playInvaderKilledSound();
                                    sounds.stopShootSound();
                                    pane.getChildren().remove(playerShot[0].getAsset());
                                    pane.getChildren().remove(invader.getCurrentImageView());
                                    invader.setAlive(false);
                                    invader.setCurrentImageView();

                                    pane.getChildren().add(invader.getCurrentImageView());
                                    wait = true;

                                    invader.setToRemove();
                                    player.setCanShoot(true);
                                    playerShot[0].setAlive(false);
                                    score.p1addToScore(invader.getScore());
                                }
                            });
                        }
                        if (!playerShot[0].getAlive()) {

                            playerShot[0] = null;
                        }
                    }


                    //System.out.println("Left most: " + EnemyBoundLogic.leftColumnCalculate(invaders) + "  Right most: " + EnemyBoundLogic.rightColumnCalculate(invaders) + "  Bottom most: " + EnemyBoundLogic.bottomRowCalculate(invaders));

                }


            }
        }.start();

        stage.setTitle("Space Invaders!");
        stage.setScene(scene);

        stage.show();

    }

}
