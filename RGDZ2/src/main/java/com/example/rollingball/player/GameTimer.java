package com.example.rollingball.player;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Duration;


public class GameTimer extends Group {
    Timeline timer;
    Timeline timerCount;
    Rectangle timeBar;
    Text timeLeft;
    boolean gameOver;
    Integer time;

    private final int startTime;
    private final double width;

    public GameTimer(int startTime, Translate position, double width){
        timeBar = new Rectangle(width, 15);
        timeBar.setFill(Color.RED);
        timeBar.setStroke(Color.RED);
        time = startTime;
        this.startTime = startTime;
        this.width = width;
        timeLeft = new Text( secondsToTimeString(time));
        timeLeft.setFill(Color.ALICEBLUE);
        timeLeft.setFont(new Font("Consolas", 15));
        timeLeft.getTransforms().add(new Translate(width/2 - 20, 14));
        timeLeft.toFront();
        this.getChildren().addAll(timeBar);
        this.getChildren().addAll(timeLeft);
        this.getTransforms().add(position);
        gameOver = false;


        timer = new Timeline(
                new KeyFrame(Duration.seconds(time), new KeyValue(timeBar.widthProperty(),0))
        );
        timerCount = new Timeline(
                new KeyFrame(Duration.seconds(1), tick -> updateTimer())
        );
        timerCount.setCycleCount(Timeline.INDEFINITE);

    }

    private String secondsToTimeString(Integer time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }
    private void updateTimer(){
        time--;
        timeLeft.setText(secondsToTimeString(time));
        if(time == 0){
            timer.stop();
            timerCount.stop();
            gameOver = true;
        }
    }

    public void bumpTime(int seconds){
        time+=seconds;
        if(time>startTime){
            timeBar.setWidth(width);
            timer = new Timeline(
                    new KeyFrame(Duration.seconds(time), new KeyValue(timeBar.widthProperty(),0))
            );
            timer.play();
        }else{
            timer.jumpTo(Duration.seconds(time-seconds));
        }
        timeLeft.setText(secondsToTimeString(time));
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public void startTimer(){
        timer.play();
        timerCount.play();
    }

    public void setGameOver(){
        timer.stop();
        timerCount.stop();
        gameOver = true;
    }
}

