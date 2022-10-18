package com.example.golfer;

import com.example.golfer.objects.*;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends Application implements EventHandler<MouseEvent> {
	private static final double WINDOW_WIDTH  = 600;
	private static final double WINDOW_HEIGHT = 800;
	
	private static final double PLAYER_WIDTH            = 10;
	private static final double PLAYER_HEIGHT           = 80;
	private static final double PLAYER_MAX_ANGLE_OFFSET = 60;
	private static final double PLAYER_MIN_ANGLE_OFFSET = -60;
	
	private static final double MS_IN_S            = 1e3;
	private static final double NS_IN_S            = 1e9;
	private static final double MAXIMUM_HOLD_IN_S  = 3;
	private static final double MAXIMUM_BALL_SPEED = 1500;
	private static final double BALL_RADIUS        = Main.PLAYER_WIDTH / 2;
	private static final double BALL_DAMP_FACTOR   = 0.995;
	private static final double MIN_BALL_SPEED     = 6;
	private static final double MAX_HOLE_SPEED 	   = 300;
	
	private static final double HOLE_RADIUS = 3 * BALL_RADIUS;

	private static final int LIVES = 5;
	
	private Group root;
	private Player player;
	private Ball ball;
	private long time;
	private ArrayList<Hole> holes;
	private ArrayList<Surface> surfaces;
	private ArrayList<Barrier> barriers;
	private ArrayList<Teleporter> teleporters;
	private CopyOnWriteArrayList<UFO> ufos;
	private CopyOnWriteArrayList<PowerUp> powerups;
	private CopyOnWriteArrayList<Circle> livesDisplay;
	private int livesCount;

	private GridPane levelSelect;
	private GridPane cannonSelect;
	private Stage stage;


	private Semaphore ballWait;
	private Cannon cannon;
	private Level level;

	Text score;
	Rectangle chargeBar;
	ScaleTransition charging;

	GameTimer gameTimer;

	private void addFences(){
		Image fenceImage = new Image("fence.jpg");
		ImagePattern fencePattern = new ImagePattern(fenceImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, false);

		Barrier fence0 = new Barrier(new Translate(0,0), 20,Main.WINDOW_HEIGHT);
		fence0.setFill(fencePattern);
		this.root.getChildren().add(fence0);
		barriers.add(fence0);

		Barrier fence1 = new Barrier(new Translate(20,0), Main.WINDOW_WIDTH-20, 20);
		fence1.setFill(fencePattern);
		this.root.getChildren().add(fence1);
		barriers.add(fence1);

		Barrier fence2 = new Barrier(new Translate(Main.WINDOW_WIDTH-20, 0),20, Main.WINDOW_HEIGHT);
		fence2.setFill(fencePattern);
		this.root.getChildren().add(fence2);
		barriers.add(fence2);

		Barrier fence3 = new Barrier(new Translate(20, Main.WINDOW_HEIGHT-20), Main.WINDOW_WIDTH-20, 20);
		fence3.setFill(fencePattern);
		this.root.getChildren().add(fence3);
		barriers.add(fence3);
	}

	private void addBarrier(Translate pos, double width, double height, Color color) {
		Barrier wall = new Barrier(pos, width, height);
		wall.setFill(color);
		this.root.getChildren().add(wall);
		barriers.add(wall);
	}

	private void addBarriers(Translate pos0, double w0, double h0, Translate pos1, double w1, double h1, Translate pos2, double w2, double h2){
		Barrier wall0 = new Barrier(pos0, w0, h0);
		wall0.setFill(Color.GREY);
		this.root.getChildren().add(wall0);
		barriers.add(wall0);

		Barrier wall1 = new Barrier(pos1, w1,h1);
		wall1.setFill(Color.GREY);
		this.root.getChildren().add(wall1);
		barriers.add(wall1);

		Barrier wall2 = new Barrier(pos2, w2,h2);
		wall2.setFill(Color.GREY);
		this.root.getChildren().add(wall2);
		barriers.add(wall2);
	}

	private void addSurface(Translate pos, double factor, double size, Color fill){
		Surface surface = new Surface(factor, pos, fill, size);
		this.root.getChildren().addAll(surface);
		surfaces.add(surface);
	}

	private void addSurfaces(Translate pos0, Translate pos1, Translate pos2, Translate pos3){

		Surface mud0 = new Surface(0.8, pos0, Color.SADDLEBROWN, 30);
		this.root.getChildren().addAll(mud0);
		surfaces.add(mud0);

		Surface mud1 = new Surface(0.8, pos1, Color.SADDLEBROWN, 30);
		this.root.getChildren().addAll(mud1);
		surfaces.add(mud1);

		Surface ice0 = new Surface(1.15, pos2, Color.LIGHTCYAN, 30);
		this.root.getChildren().addAll(ice0);
		surfaces.add(ice0);

		Surface ice1 = new Surface(1.15, pos3, Color.LIGHTCYAN, 30);
		this.root.getChildren().addAll(ice1);
		surfaces.add(ice1);
	}

	private void addTeleporter(Color fill, Translate pos1, Translate pos2){
		Teleporter teleporter = new Teleporter(fill, pos1, pos2);
		teleporters.add(teleporter);
		this.root.getChildren().add(teleporter);

	}

	private void addPowerUp(PowerUpType type, Color color, Translate position){
		PowerUp powerUp = new PowerUp(type, color, position);
		this.powerups.add(powerUp);
		this.root.getChildren().add(powerUp);
	}

	public void addLife(){
		this.livesDisplay.add(new Circle(WINDOW_WIDTH-15-this.livesCount*15, 10, BALL_RADIUS,Color.RED));
		this.root.getChildren().addAll(this.livesDisplay.get(livesCount));
		this.livesCount++;
	}

	public void addScore(int points){
		int newScore = Integer.parseInt(score.getText()) + points;
		score.setText(String.valueOf(newScore));
	}

	private void addHole(Translate pos, int points, Color color){
		Hole hole = new Hole ( Main.HOLE_RADIUS, pos, color, points );
		this.root.getChildren ( ).addAll ( hole );
		holes.add(hole);
	}
	private void addHoles (Translate pos0, Translate pos1, Translate pos2, Translate pos3) {
		Hole hole0 = new Hole ( Main.HOLE_RADIUS, pos0, Color.DARKSEAGREEN, 5 );
		this.root.getChildren ( ).addAll ( hole0 );
		holes.add(hole0);

		Hole hole1 = new Hole ( Main.HOLE_RADIUS, pos1, Color.YELLOW, 10);
		this.root.getChildren ( ).addAll ( hole1 );
		holes.add(hole1);

		Hole hole2 = new Hole ( Main.HOLE_RADIUS, pos2, Color.YELLOW, 10);
		this.root.getChildren ( ).addAll ( hole2 );
		holes.add(hole2);

		Hole hole3 = new Hole ( Main.HOLE_RADIUS, pos3, Color.PERU, 20 );
		this.root.getChildren ( ).addAll ( hole3 );
		holes.add(hole3);

	}

	private void addUFO(UFO ufo){
		ufos.add(ufo);
		this.root.getChildren().addAll(ufo);
	}

	private void initHUD(Scene scene){
		//************SCORE COUNTER***********
		score = new Text();
		score.setX(5);
		score.setY(28);
		score.setFont(new Font("Comic Sans MS",24));
		score.setText("0");
		score.setFill(Color.WHITE);
		this.root.getChildren().add(score);

		//***********CHARGE BAR***************
		chargeBar = new Rectangle(0, Main.WINDOW_HEIGHT, 15, 1);
		this.root.getChildren().add(chargeBar);
		chargeBar.setFill(Color.RED);
		charging = new ScaleTransition(Duration.seconds(3), chargeBar);
		charging.setFromY(1);
		charging.setToY(WINDOW_HEIGHT*2);
		charging.setInterpolator(Interpolator.LINEAR);
		//***************LIVES****************

		livesCount = LIVES;
		livesDisplay = new CopyOnWriteArrayList<>();
		for(int i = 0; i < LIVES; i++){
			livesDisplay.add(new Circle(WINDOW_WIDTH-15-i*15, 10, BALL_RADIUS,Color.RED));
			this.root.getChildren().add(livesDisplay.get(i));
		}
	}

	public void formGrassLevel(Scene scene){

		//***********BG IMAGE*****************
		Image backgroundImage = new Image("grass.jpg");
		ImagePattern backgroundPattern = new ImagePattern(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, false);
		scene.setFill(backgroundPattern);

		//*************HOLES*****************

		Translate hole0Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.4
		);
		Translate hole1Position = new Translate (
				Main.WINDOW_WIDTH / 3,
				Main.WINDOW_HEIGHT * 0.25
		);

		Translate hole2Position = new Translate (
				Main.WINDOW_WIDTH * 2 / 3,
				Main.WINDOW_HEIGHT * 0.25
		);
		Translate hole3Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.1
		);
		this.addHoles (hole0Position, hole1Position, hole2Position, hole3Position );

		//**********SURFACES****************
		Translate mud0Position = new Translate(100,100);
		Translate mud1Position = new Translate(Main.WINDOW_WIDTH-120,3*Main.WINDOW_HEIGHT/5);
		Translate ice0Position = new Translate(Main.WINDOW_WIDTH-120,100);
		Translate ice1Position = new Translate(100,3*Main.WINDOW_HEIGHT/5);

		this.addSurfaces(mud0Position, mud1Position, ice0Position, ice1Position);

		//**********TELEPORTER*************

		Translate tele1Position = new Translate(100,2*Main.WINDOW_HEIGHT/5);
		Translate tele2Position = new Translate(WINDOW_WIDTH - 100,2*Main.WINDOW_HEIGHT/5);
		addTeleporter(Color.MAGENTA, tele1Position, tele2Position);


		//*************BARRIERS*************
		addFences();
		Translate barr0Position = new Translate((Main.WINDOW_WIDTH-17)/2, Main.WINDOW_HEIGHT/6);
		Translate barr1Position = new Translate(Main.WINDOW_WIDTH/5, Main.WINDOW_HEIGHT/2);
		Translate barr2Position = new Translate(3*Main.WINDOW_WIDTH/5, Main.WINDOW_HEIGHT/2);
		double w0 = 17;
		double h0 = 125;
		double w1 = 125;
		double h1 = 17;
		double w2 = 125;
		double h2 = 17;
		this.addBarriers(barr0Position,w0, h0,barr1Position,w1,h1,barr2Position,w2,h2);

		/*UFO ufo = new UFO();
		ufo.getTransforms().add(new Translate(Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT*0.8));
		this.root.getChildren().add(ufo);
		System.out.println(ufo.getBoundsInParent());*/

		/*Translate poweruptest = new Translate(Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT*0.8);

		this.addPowerUp(PowerUpType.TIME,Color.DARKSEAGREEN, poweruptest);*/


	}

	public void formAridLevel(Scene scene){
		//***********BG IMAGE*****************
		Image backgroundImage = new Image("arid.jpg");
		ImagePattern backgroundPattern = new ImagePattern(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, false);
		scene.setFill(backgroundPattern);

		//*************HOLES*****************
		Translate hole0Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.1
		);
		Translate hole1Position = new Translate (
				Main.WINDOW_WIDTH / 3,
				Main.WINDOW_HEIGHT * 0.25
		);

		Translate hole2Position = new Translate (
				Main.WINDOW_WIDTH * 2 / 3,
				Main.WINDOW_HEIGHT * 0.25
		);
		Translate hole3Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.4
		);
		this.addHoles (hole0Position, hole1Position, hole2Position, hole3Position );

		//**********SURFACES****************
		Translate mud0Position = new Translate(Main.WINDOW_WIDTH-120,3*Main.WINDOW_HEIGHT/5);
		Translate mud1Position = new Translate(100,3*Main.WINDOW_HEIGHT/5);
		Translate ice0Position = new Translate(Main.WINDOW_WIDTH-120,100);
		Translate ice1Position = new Translate(100,100);

		this.addSurfaces(mud0Position, mud1Position, ice0Position, ice1Position);

		//**********TELEPORTER*************

		Translate tele1Position = new Translate(Main.WINDOW_WIDTH/2,0.25*Main.WINDOW_HEIGHT);
		Translate tele2Position = new Translate(Main.WINDOW_WIDTH/2,0.6*Main.WINDOW_HEIGHT);
		addTeleporter(Color.rgb(92,229,120), tele1Position, tele2Position);

		//*************BARRIERS*************
		double w0 = 17;
		double h0 = 150;
		double w1 = 220;
		double h1 = 17;
		double w2 = 17;
		double h2 = 150;
		Translate barr0Position = new Translate(2*(Main.WINDOW_WIDTH)/3 + 2*w0, (Main.WINDOW_HEIGHT-h0)/6);
		Translate barr1Position = new Translate((Main.WINDOW_WIDTH-w1)/2, Main.WINDOW_HEIGHT/2);
		Translate barr2Position = new Translate((Main.WINDOW_WIDTH)/3 - 3*w2, (Main.WINDOW_HEIGHT-h2)/6);

		addFences();
		this.addBarriers(barr0Position,w0, h0,barr1Position,w1,h1,barr2Position,w2,h2);


	}

	public void formStoneLevel(Scene scene){
		//***********BG IMAGE*****************
		Image backgroundImage = new Image("stone.jpg");
		ImagePattern backgroundPattern = new ImagePattern(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, false);
		scene.setFill(backgroundPattern);

		//*************HOLES*****************

		Translate hole0Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.1
		);
		Translate hole1Position = new Translate (
				Main.WINDOW_WIDTH / 3,
				Main.WINDOW_HEIGHT * 0.25
		);
		Translate hole2Position = new Translate (
				Main.WINDOW_WIDTH * 2 / 3,
				Main.WINDOW_HEIGHT * 0.25
		);
		Translate hole3Position = new Translate (
				Main.WINDOW_WIDTH / 2,
				Main.WINDOW_HEIGHT * 0.25
		);
		this.addHoles (hole0Position, hole1Position, hole2Position, hole3Position );

		//*************BARRIERS*************
		double w0 = 50;
		double h0 = 125;
		double w1 = 50;
		double h1 = 175;
		double w2 = 50;
		double h2 = 125;
		Translate barr0Position = new Translate(2*(Main.WINDOW_WIDTH)/3 + w0, (Main.WINDOW_HEIGHT)/2);
		Translate barr1Position = new Translate((Main.WINDOW_WIDTH-w1)/2, Main.WINDOW_HEIGHT/2 - (h1-h0)/2);
		Translate barr2Position = new Translate((Main.WINDOW_WIDTH)/3 - 2*w2, (Main.WINDOW_HEIGHT)/2);
		addFences();
		this.addBarriers(barr0Position,w0, h0,barr1Position,w1,h1,barr2Position,w2,h2);
		double w3 = 25;
		double h3 = 100;
		Translate barr3Position = new Translate((Main.WINDOW_WIDTH)/5 , (Main.WINDOW_HEIGHT-h3)/5);
		Translate barr4Position = new Translate(4*(Main.WINDOW_WIDTH)/5 -w3 , (Main.WINDOW_HEIGHT-h3)/5);
		addBarrier(barr3Position,w3,h3,Color.GRAY);
		addBarrier(barr4Position,w3,h3,Color.GRAY);

		//**********SURFACES****************
		Translate mud0Position = new Translate(Main.WINDOW_WIDTH - 60,Main.WINDOW_HEIGHT/2 );
		Translate mud1Position = new Translate((Main.WINDOW_WIDTH)/2 -2*w1 -30 ,Main.WINDOW_HEIGHT/2 );
		Translate ice0Position = new Translate(Main.WINDOW_WIDTH / 2 +2*w1  ,Main.WINDOW_HEIGHT/2 );
		Translate ice1Position = new Translate(30,Main.WINDOW_HEIGHT/2);

		this.addSurfaces(mud0Position, mud1Position, ice0Position, ice1Position);

		//**********TELEPORTER*************

		Translate tele1Position = new Translate(Main.WINDOW_WIDTH*0.9,0.2*Main.WINDOW_HEIGHT);
		Translate tele2Position = new Translate(Main.WINDOW_WIDTH*0.1,0.2*Main.WINDOW_HEIGHT);
		addTeleporter(Color.BLUEVIOLET, tele1Position, tele2Position);


	}

	private void addPlayer(Scene scene, Cannon type){
		//*********PLAYER********************

		Translate playerPosition = new Translate (
				Main.WINDOW_WIDTH / 2 - Main.PLAYER_WIDTH / 2,
				Main.WINDOW_HEIGHT - Main.PLAYER_HEIGHT- Main.PLAYER_WIDTH
		);

		this.player = new Player (
				Main.PLAYER_WIDTH,
				Main.PLAYER_HEIGHT,
				playerPosition,
				type
		);

		this.root.getChildren ( ).addAll ( this.player );


		scene.addEventHandler (
				MouseEvent.MOUSE_MOVED,
				mouseEvent -> this.player.handleMouseMoved (
						mouseEvent,
						Main.PLAYER_MIN_ANGLE_OFFSET,
						Main.PLAYER_MAX_ANGLE_OFFSET
				)
		);
		scene.addEventHandler ( MouseEvent.ANY, this );

		//************REMOVE BALL ON SPACE************

		scene.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
			if(keyEvent.getCode() == KeyCode.SPACE && ball != null) {
				this.root.getChildren ( ).remove ( this.ball );
				this.ball = null;
			}
		});
	}

	private void initializeGame(){
		AtomicBoolean baitinb = new AtomicBoolean(false); //baitinb - behave as if there is no ball
		AtomicInteger newScore = new AtomicInteger();

		ScaleTransition holeEnter = new ScaleTransition(Duration.seconds(2));
		holeEnter.setToX(0.1); holeEnter.setToY(0.1);
		holeEnter.setInterpolator(Interpolator.EASE_OUT);
		holeEnter.setOnFinished(finish->{
			this.root.getChildren ( ).remove ( this.ball );
			this.ball = null;
			baitinb.set(false);
			score.setText(String.valueOf(newScore));
		});
		TranslateTransition holeEnter2 = new TranslateTransition(Duration.seconds(2));
		holeEnter2.setInterpolator(Interpolator.EASE_OUT);
		ParallelTransition holeEnterAnim = new ParallelTransition(holeEnter,holeEnter2);

		gameTimer = new GameTimer(90, new Translate(Main.WINDOW_WIDTH/3,2), Main.WINDOW_WIDTH/3);
		this.root.getChildren().add(gameTimer);

		Random random = new Random();
		AtomicReference<Double> powerUpSpawn = new AtomicReference<>();
		powerUpSpawn.set(random.nextGaussian()*7+21);

		AtomicReference<Double> UFOSpawn = new AtomicReference<>();
		UFOSpawn.set(random.nextGaussian()*3+10);
		System.out.println(UFOSpawn);

		Timer timer = new Timer (
				deltaNanoseconds -> {
					double deltaSeconds = ( double ) deltaNanoseconds / Main.NS_IN_S;

					if(UFOSpawn.get() <= 0){
						while (UFOSpawn.get() <=0){
							UFOSpawn.set(random.nextGaussian()*3+10);
							System.out.println(UFOSpawn);
						}
						UFO ufo = new UFO();
						addUFO(ufo);
						if(random.nextBoolean()) {
							ufo.patrol(random.nextDouble() * (Main.WINDOW_HEIGHT - PLAYER_HEIGHT), Main.WINDOW_WIDTH+30, -30, random.nextDouble()*5+4);
						}else{
							ufo.patrol(random.nextDouble() * (Main.WINDOW_HEIGHT - PLAYER_HEIGHT), -30, Main.WINDOW_WIDTH+30, random.nextDouble()*5+4);
						}
					}else{
						double temp = UFOSpawn.get();
						UFOSpawn.set(temp-deltaSeconds);
					}

					ufos.forEach(ufo->{
						ufo.time-=deltaSeconds;
						if(ufo.time<=0){
							this.root.getChildren().remove(ufo);
							this.ufos.remove(ufo);
							return;
						}
					});

					if(powerUpSpawn.get() <=0){
						while (powerUpSpawn.get() <=0){
							powerUpSpawn.set(random.nextGaussian()*10+30);
						}
						boolean goodPosition = false;
						double x = random.nextDouble()*WINDOW_WIDTH;
						double y = random.nextDouble()*WINDOW_HEIGHT;
						double size = PowerUp.getSize();
						while(!goodPosition) {
							goodPosition = true;
							for (Barrier barrier : barriers) {
								if (barrier.getBoundsInParent().intersects(x - size / 2, y - size / 2, size, size)){
									goodPosition = false;
									break;
								}
							}
							if(!goodPosition){
								x = random.nextDouble()*WINDOW_WIDTH;
								y = random.nextDouble()*WINDOW_HEIGHT;
								continue;
							}
							for (Hole hole : holes) {
								if (hole.getBoundsInParent().intersects(x - size / 2, y - size / 2, size, size)){
									goodPosition = false;
									break;
								}
							}
							if(!goodPosition){
								x = random.nextDouble()*WINDOW_WIDTH;
								y = random.nextDouble()*WINDOW_HEIGHT;
								continue;
							}
							for(Surface surface : surfaces){
								if(surface.getBoundsInParent().intersects(x - size / 2, y - size / 2, size, size)){
									goodPosition = false;
									break;
								}
							}
							if(!goodPosition){
								x = random.nextDouble()*WINDOW_WIDTH;
								y = random.nextDouble()*WINDOW_HEIGHT;
								continue;
							}
							for(Teleporter teleporter : teleporters){
								goodPosition = !(teleporter.isOnPad(x - size / 2, y - size / 2, size, size));
								if(!goodPosition) break;
							}
							if(!goodPosition){
								x = random.nextDouble()*WINDOW_WIDTH;
								y = random.nextDouble()*WINDOW_HEIGHT;
								continue;
							}
							int type = random.nextInt(3);
							switch(type){
								case 0:
									addPowerUp(PowerUpType.LIFE, Color.RED, new Translate(x,y));
									break;
								case 1:
									addPowerUp(PowerUpType.TIME, Color.SKYBLUE, new Translate(x,y));
									break;
								case 2:
									addPowerUp(PowerUpType.SCORE, Color.GOLD, new Translate(x,y));
									break;
							}
						}

					}else{
						double temp = powerUpSpawn.get();
						powerUpSpawn.set(temp-deltaSeconds);
					}
					powerups.forEach(powerUp->{
						powerUp.timeLeft-=deltaSeconds;
						if(powerUp.timeLeft<=0){
							this.root.getChildren().remove(powerUp);
							this.powerups.remove(powerUp);
							return;
						}
					});

					if ( this.ball != null && !baitinb.get()) {
						AtomicReference<Double> dampFactor = new AtomicReference<>(Main.BALL_DAMP_FACTOR);
						boolean isOnSurface = surfaces.stream().anyMatch ( surface -> {boolean status = surface.handleOverlap (this.ball);
							if(status == true){
								dampFactor.set(surface.getDampFactor());
							}
							return status;
						} );
						boolean stopped = this.ball.update (
								deltaSeconds,
								0,
								Main.WINDOW_WIDTH,
								0,
								Main.WINDOW_HEIGHT,
								dampFactor.get(),
								Main.MIN_BALL_SPEED,
								barriers
						);
						teleporters.forEach(teleporter -> teleporter.handleTeleport(this.ball));
						powerups.forEach(powerUp -> {
							if(this.ball.getBoundsInParent().intersects(powerUp.getBounds())){
								switch (powerUp.powerUpType){
									case LIFE:
										addLife();
										break;
									case TIME:
										gameTimer.addTime(10);
										break;
									case SCORE:
										addScore(15);
										break;
								}
								this.root.getChildren().remove(powerUp);
								this.powerups.remove(powerUp);
							}
						});
						ufos.forEach(ufo->{
							if(this.ball.getBoundsInParent().intersects(ufo.getBoundsInParent())){
								this.root.getChildren ( ).remove ( this.ball );
								this.ball = null;
								return;
							}
						});
						if(this.ball == null){
							return;
						}

						boolean isInHole= holes.stream().anyMatch ( hole -> {boolean status = hole.handleCollision ( this.ball, Main.MAX_HOLE_SPEED );
							if(status == true){
								newScore.set(Integer.parseInt(score.getText()) + hole.getPoints());
							}
							return status;
						} );
						if(isInHole){
							baitinb.set(true);
							holeEnter.setNode(ball);
							holeEnter2.setNode(ball);
							holeEnter2.setToX(this.ball.getBoundsInParent().getCenterX()*0.9); holeEnter2.setToY(this.ball.getBoundsInParent().getCenterY()*0.9);

							ball.dontMove();
							holeEnterAnim.playFromStart();

						}
						if (stopped && !isInHole) {
							this.root.getChildren ( ).remove ( this.ball );
							this.ball = null;
						}
					}else if(livesCount == 0){
						gameTimer.setGameOver();
					}
					if((livesCount == 0  || gameTimer.isGameOver()) && this.ball == null){
						Text gameOver = new Text("GAME OVER!");
						gameOver.setFill(Color.RED);
						gameOver.setFont(new Font("Tahoma",36));
						gameOver.getTransforms().add(new Translate(Main.WINDOW_WIDTH/2-100, Main.WINDOW_HEIGHT/2-20));
						this.root.getChildren ( ).add(gameOver);
					}
				}
		);
		timer.start ( );
		gameTimer.startTimer();
	}

	private void generatePowerUp(){

	}


	@Override
	public void start ( Stage stage ) throws IOException {
		this.root  = new Group ( );
		ballWait = new Semaphore(0);
		this.stage = stage;
		Scene scene = new Scene ( this.root, Main.WINDOW_WIDTH, WINDOW_HEIGHT);
		this.barriers = new ArrayList<>();
		this.holes = new ArrayList<>();
		this.surfaces = new ArrayList<>();
		this.teleporters = new ArrayList<>();
		this.ufos = new CopyOnWriteArrayList<>();
		this.powerups = new CopyOnWriteArrayList<>();



		levelSelect = new GridPane();

		levelSelect.setHgap(10);
		levelSelect.setVgap(10);
		Scene levelScene = new Scene(levelSelect, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

		cannonSelect = new GridPane();
		cannonSelect.setHgap(10);
		cannonSelect.setVgap(10);
		Scene cannonScene = new Scene(cannonSelect, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

		Label levelLabel = new Label("Select your level...");
		levelLabel.setAlignment(Pos.CENTER);
		GridPane.setHalignment(levelLabel, HPos.CENTER);
		levelSelect.add(levelLabel,1,0);
		Button grassyLevel = new Button("Grass plains");
		GridPane.setHalignment(grassyLevel, HPos.CENTER);
		grassyLevel.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				level = Level.GRASS;
				stage.setScene(cannonScene);
			}
		});
		Button aridLevel = new Button("Arid plateau");
		GridPane.setHalignment(aridLevel, HPos.CENTER);
		aridLevel.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				level = Level.ARID;
				stage.setScene(cannonScene);
			}
		});
		Button stoneLevel = new Button("Stone path");
		GridPane.setHalignment(stoneLevel, HPos.CENTER);
		stoneLevel.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				level = Level.STONE;
				stage.setScene(cannonScene);
			}
		});
		levelSelect.addRow(1,grassyLevel,aridLevel,stoneLevel);

		Label cannonLabel = new Label("Select your cannon...");
		cannonSelect.addRow(0,cannonLabel);
		Button blueCannon = new Button("Blue Blaster");
		blueCannon.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				cannon = Cannon.BLUE;
				startGame(scene);
				stage.setScene(scene);
			}
		});
		Button redCannon = new Button("Crimson Catapult");
		redCannon.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				cannon = Cannon.RED;
				startGame(scene);
				stage.setScene(scene);

			}
		});
		Button blackCannon = new Button("Midnight Mortar");
		blackCannon.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				cannon = Cannon.BLACK;
				startGame(scene);
				stage.setScene(scene);
			}
		});
		Button whiteCannon = new Button("Radiant Railgun");
		whiteCannon.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				cannon = Cannon.WHITE;
				startGame(scene);
				stage.setScene(scene);
			}
		});
		cannonSelect.addRow(1,blueCannon,redCannon,blackCannon, whiteCannon);
		
		stage.setTitle ( "Golfer" );
		stage.setResizable ( false );
		stage.setScene ( levelScene );
		stage.show ( );
	}

	private void startGame(Scene scene) {
		switch(level){
			case GRASS:
				formGrassLevel(scene);
				break;
			case ARID:
				formAridLevel(scene);
				break;
			case STONE:
				formStoneLevel(scene);
				break;
		}
		addPlayer(scene,cannon);
		initHUD(scene);
		initializeGame();

		scene.setCursor ( Cursor.NONE );
	}

	public static void main ( String[] args ) {
		launch ( );
	}
	
	@Override public void handle ( MouseEvent mouseEvent ) {

		if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_PRESSED ) && mouseEvent.isPrimaryButtonDown ( ) && this.ball == null &&livesCount!=0 && !gameTimer.isGameOver()) {
			this.time = System.currentTimeMillis ( );
			charging.playFromStart();
		} else if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_RELEASED ) && livesCount!=0) {
			if ( this.time != - 1 && this.ball == null ) {
				charging.playFromStart();
				charging.jumpTo(Duration.ZERO);
				charging.stop();
				livesCount--;
				this.root.getChildren().remove(livesDisplay.get(livesCount));
				livesDisplay.remove(livesCount);
				double value        = ( System.currentTimeMillis ( ) - this.time ) / Main.MS_IN_S;
				double deltaSeconds = Utilities.clamp ( value, 0, Main.MAXIMUM_HOLD_IN_S );
				
				double ballSpeedFactor = deltaSeconds / Main.MAXIMUM_HOLD_IN_S * player.getMaxSpeed();
				
				Translate ballPosition = this.player.getBallPosition ( );
				Point2D   ballSpeed    = this.player.getSpeed ( ).multiply ( ballSpeedFactor );
				
				this.ball = new Ball ( Main.BALL_RADIUS, ballPosition, ballSpeed );
				this.root.getChildren ( ).addAll ( this.ball );
			}
			this.time = -1;
		}
	}
}