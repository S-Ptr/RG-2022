package com.example.rollingball;

import com.example.rollingball.arena.*;
import com.example.rollingball.player.ArenaCamera;
import com.example.rollingball.player.BallCamera;
import com.example.rollingball.player.Compass;
import com.example.rollingball.player.GameTimer;
import com.example.rollingball.timer.Timer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
	private static final double WINDOW_WIDTH  = 800;
	private static final double WINDOW_HEIGHT = 800;
	
	private static final double PODIUM_WIDTH  = 2000;
	private static final double PODIUM_HEIGHT = 10;
	private static final double PODIUM_DEPTH  = 2000;
	
	private static final double CAMERA_FAR_CLIP = 100000;
	private static final double CAMERA_Z        = -5000;
	private static final double CAMERA_X_ANGLE  = -45;
	
	private static final double BALL_RADIUS = 50;
	
	private static final double DAMP = 0.999;
	
	private static final double MAX_ANGLE_OFFSET = 30;
	private static final double MAX_ACCELERATION = 400;
	
	private static final int    NUMBER_OF_HOLES = 4;
	private static final double HOLE_RADIUS     = 2 * Main.BALL_RADIUS;
	private static final double HOLE_HEIGHT     = PODIUM_HEIGHT;
	private static final int LIVES = 5;

	private Stage stage;
	private Group root;

	private SubScene scene3d;
	private AnchorPane globalRoot;
	private Scene scene;

	private Ball  ball;
	private Arena arena;
	private Hole hole;

	private ArrayList<Hole> holes;
	private Floodlight floodlight;
	private ArenaCamera arenaCamera;
	private BallCamera ballCamera;
	private CopyOnWriteArrayList<Circle> livesDisplay;
	private int livesCount;
	private CopyOnWriteArrayList<Coin> coins;
	private ArrayList<Obstacle> obstacles;

	private ArrayList<Wall> walls;

	private Text score;
	private GameTimer gameTimer;

	private Compass compass;

	private int levelchoice;
	private int ballchoice;
	private double ballMaxSpeed;
	private PhongMaterial ballMaterial;
	private Translate startBallPosition;

	private double startBallX;
	private double startBallZ;

	private GridPane levelSelect;
	private GridPane ballSelect;



	private void generateHUD(){
		score = new Text();
		score.setTextAlignment(TextAlignment.LEFT);
		score.setX(WINDOW_WIDTH-50);
		score.setY(28);
		score.setFont(new Font(24));
		score.setText("0");
		score.setFill(Color.RED);
		globalRoot.getChildren().add(score);

		livesCount = LIVES;
		livesDisplay = new CopyOnWriteArrayList<>();
		for(int i = 0; i < LIVES; i++){
			livesDisplay.add(new Circle(10+i*20, 20, 7,Color.RED));
			globalRoot.getChildren().add(livesDisplay.get(i));
		}

		compass = new Compass(Main.WINDOW_WIDTH/5,new Translate(0, 4*Main.WINDOW_HEIGHT / 5), arena, Main.MAX_ANGLE_OFFSET);
		globalRoot.getChildren().add(compass);

		gameTimer = new GameTimer(180, new Translate(Main.WINDOW_WIDTH/3,2), Main.WINDOW_WIDTH/3);
		globalRoot.getChildren().add(gameTimer);


	}

	private void addHole(Color color, int points, Translate position){
		Hole newHole = new Hole(points, Main.HOLE_RADIUS, Main.HOLE_HEIGHT, new PhongMaterial(color), position );
		holes.add(newHole);
		this.arena.getChildren().add(newHole);
	}

	private void generateLevel1(Scene scene, Group group){
		startBallX = - ( Main.PODIUM_WIDTH / 2 - 2 * Main.BALL_RADIUS );
		startBallZ = Main.PODIUM_DEPTH / 2 - 2 * Main.BALL_RADIUS;
		startBallPosition = new Translate (
				startBallX,
				- ( Main.BALL_RADIUS + Main.PODIUM_HEIGHT / 2 ),
				startBallZ
		);
		
		Box podium = new Box (
				Main.PODIUM_WIDTH,
				Main.PODIUM_HEIGHT,
				Main.PODIUM_DEPTH
		);
		podium.setMaterial ( new PhongMaterial ( Color.BLUE ) );
		this.arena.getChildren().add(podium);

		coins.add(new Coin(BALL_RADIUS, new Translate(-50, 0, Main.PODIUM_DEPTH/4)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, -Main.PODIUM_WIDTH/4, 0)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, 0, -Main.PODIUM_DEPTH/4)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, Main.PODIUM_WIDTH/4, 0)));
		this.arena.getChildren().addAll(coins);

		obstacles = new ArrayList<>();
		obstacles.add(new Obstacle(new Translate(Main.PODIUM_WIDTH/4, -100, Main.PODIUM_DEPTH/4)));
		obstacles.add(new Obstacle(new Translate(-Main.PODIUM_WIDTH/4, -100, Main.PODIUM_DEPTH/4)));
		obstacles.add(new Obstacle(new Translate(Main.PODIUM_WIDTH/4, -100, -Main.PODIUM_DEPTH/4)));
		obstacles.add(new Obstacle(new Translate(-Main.PODIUM_WIDTH/4, -100, -Main.PODIUM_DEPTH/4)));
		obstacles.add(new SpecialObstacle(new Translate(0,-100,0), 5));
		this.arena.getChildren().addAll(obstacles);

		walls = new ArrayList<>();
		walls.add(new Wall(new Translate(-Main.PODIUM_WIDTH / 2 +10,-50, 0),Main.PODIUM_WIDTH/2));
		walls.add(new Wall(new Translate(Main.PODIUM_WIDTH / 2 -10,-50, 0),Main.PODIUM_WIDTH/2));
		walls.add(new Wall(new Translate(0,-50, Main.PODIUM_DEPTH/2 - 10),Main.PODIUM_DEPTH/2, true));
		walls.add(new Wall(new Translate(0,-50, -Main.PODIUM_DEPTH/2 + 10),Main.PODIUM_DEPTH/2, true));
		this.arena.getChildren().addAll(walls);

		double x = ( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		double z = - ( Main.PODIUM_DEPTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.YELLOW, 15, new Translate(x,-20,z));
		x = -( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		z = - ( Main.PODIUM_DEPTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.BLACK, -10, new Translate(x,-20,z));
		x = ( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		z = ( Main.PODIUM_DEPTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.BLACK, -10, new Translate(x,-20,z));

		this.root.getChildren ( ).add ( this.arena );

	}

	private void generateLevel2(){

		startBallX = 0;
		startBallZ = 0;
		startBallPosition = new Translate (
				startBallX,
				- ( Main.BALL_RADIUS + Main.PODIUM_HEIGHT / 2 ),
				startBallZ
		);

		Box podium = new Box (
				Main.PODIUM_WIDTH,
				Main.PODIUM_HEIGHT,
				Main.PODIUM_DEPTH
		);
		podium.setMaterial ( new PhongMaterial ( Color.DARKSEAGREEN ) );
		this.arena.getChildren().add(podium);

		coins.add(new Coin(BALL_RADIUS, new Translate(-50, -Main.PODIUM_DEPTH/3, 0.9*Main.PODIUM_WIDTH/2)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, -Main.PODIUM_WIDTH/3, 0)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, Main.PODIUM_WIDTH/3, -0.9*Main.PODIUM_DEPTH/2)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, Main.PODIUM_WIDTH/3, 0)));
		this.arena.getChildren().addAll(coins);

		obstacles = new ArrayList<>();
		obstacles.add(new Obstacle(new Translate(0.8*Main.PODIUM_WIDTH/2, -100, 0.8*Main.PODIUM_DEPTH/2)));
		obstacles.add(new Obstacle(new Translate(-0.8*Main.PODIUM_WIDTH/2, -100, 0.8*Main.PODIUM_DEPTH/2)));
		obstacles.add(new SpecialObstacle(new Translate(0.6*Main.PODIUM_WIDTH/2, -100, -0.6*Main.PODIUM_DEPTH/2),5));
		obstacles.add(new Obstacle(new Translate(-0.8*Main.PODIUM_WIDTH/2, -100, -0.8*Main.PODIUM_DEPTH/2)));
		this.arena.getChildren().addAll(obstacles);

		walls = new ArrayList<>();
		walls.add(new Wall(new Translate(-Main.PODIUM_WIDTH / 2 +10,-50, 0),0.6*Main.PODIUM_WIDTH));
		walls.add(new Wall(new Translate(Main.PODIUM_WIDTH / 2 -10,-50, 0),0.6*Main.PODIUM_WIDTH));
		walls.add(new Wall(new Translate(0,-50, Main.PODIUM_DEPTH/2 - 10),0.6*Main.PODIUM_DEPTH/2, true));
		walls.add(new Wall(new Translate(0,-50, -Main.PODIUM_DEPTH/2 + 10),0.6*Main.PODIUM_DEPTH/2, true));
		this.arena.getChildren().addAll(walls);

		double x = ( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		double z = - ( Main.PODIUM_DEPTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.YELLOW, 15, new Translate(x,-20,z));
		x = -( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.BLACK, -10, new Translate(x,-20,0));
		x = ( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.BLACK, -10, new Translate(x,-20,0));

		this.root.getChildren ( ).add ( this.arena );
	}

	private void generateLevel3(){
		Random random = new Random();
		startBallX = - ( Main.PODIUM_WIDTH / 2 - 2 * Main.BALL_RADIUS );
		startBallZ = 0;
		startBallPosition = new Translate (
				startBallX,
				- ( Main.BALL_RADIUS + Main.PODIUM_HEIGHT / 2 ),
				startBallZ
		);

		Box podium = new Box (
				Main.PODIUM_WIDTH,
				Main.PODIUM_HEIGHT,
				Main.PODIUM_DEPTH
		);
		podium.setMaterial ( new PhongMaterial ( Color.SALMON ) );
		this.arena.getChildren().add(podium);

		coins.add(new Coin(BALL_RADIUS, new Translate(-50, -Main.PODIUM_WIDTH/4, 0.6*Main.PODIUM_DEPTH/2)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, -Main.PODIUM_WIDTH/4, 0)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, -0.4*Main.PODIUM_WIDTH/4, -Main.PODIUM_DEPTH/4)));
		coins.add(new Coin(BALL_RADIUS, new Translate(-50, Main.PODIUM_WIDTH/4, -Main.PODIUM_DEPTH/4)));
		this.arena.getChildren().addAll(coins);

		obstacles = new ArrayList<>();
		obstacles.add(new Obstacle(new Translate(0.36*Main.PODIUM_WIDTH/2, -100, 0.72*Main.PODIUM_DEPTH/2)));
		obstacles.add(new Obstacle(new Translate(-0.22*Main.PODIUM_WIDTH/4, -100, 0.87*Main.PODIUM_DEPTH/4)));
		obstacles.add(new Obstacle(new Translate(0.5*Main.PODIUM_WIDTH/2, -100, -Main.PODIUM_DEPTH/4)));
		obstacles.add(new Obstacle(new Translate(-Main.PODIUM_WIDTH/8, -100, -Main.PODIUM_DEPTH/3)));
		obstacles.add(new SpecialObstacle(new Translate(-0.8*Main.PODIUM_WIDTH/2, -100,0.7*Main.PODIUM_DEPTH / 2), 5));
		obstacles.add(new SpecialObstacle(new Translate(-0.8*Main.PODIUM_WIDTH/2, -100,-0.7*Main.PODIUM_DEPTH / 2), 5));
		this.arena.getChildren().addAll(obstacles);

		walls = new ArrayList<>();
		walls.add(new Wall(new Translate(-Main.PODIUM_WIDTH / 2 +10,-50, 0),0.4*Main.PODIUM_WIDTH));
		walls.add(new Wall(new Translate(Main.PODIUM_WIDTH / 2 -10,-50, 0),0.2*Main.PODIUM_WIDTH));
		walls.add(new Wall(new Translate(0,-50, Main.PODIUM_DEPTH/2 - 10),0.8*Main.PODIUM_DEPTH, true));
		walls.add(new Wall(new Translate(0,-50, -Main.PODIUM_DEPTH/2 + 10),0.3*Main.PODIUM_DEPTH, true));
		this.arena.getChildren().addAll(walls);

		double x ;
		double z ;
		addHole(Color.BLACK, -10, new Translate(0,-20,0));
		x = ( Main.PODIUM_WIDTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.YELLOW, 15, new Translate(x,-20,0));
		z = ( Main.PODIUM_DEPTH / 2 - 2 * Main.HOLE_RADIUS );
		addHole(Color.BLACK, -10, new Translate(0,-20,z));
		addHole(Color.BLACK, -10, new Translate(0,-20,-z));

		this.root.getChildren ( ).add ( this.arena );
	}
	
	@Override
	public void start ( Stage stage ) throws IOException {
		this.arena = new Arena ( );
		this.stage = stage;
		this.root = new Group ( );
		this.globalRoot = new AnchorPane();
		scene = new Scene(globalRoot, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, true);

		coins = new CopyOnWriteArrayList<>();
		holes = new ArrayList<>();

		levelSelect = new GridPane();

		levelSelect.setHgap(10);
		levelSelect.setVgap(10);
		Scene levelScene = new Scene(levelSelect, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

		ballSelect = new GridPane();
		ballSelect.setHgap(10);
		ballSelect.setVgap(10);
		Scene cannonScene = new Scene(ballSelect, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

		Label levelLabel = new Label("Select your level...");
		levelLabel.setAlignment(Pos.CENTER);
		GridPane.setHalignment(levelLabel, HPos.CENTER);
		levelSelect.add(levelLabel,1,0);
		Button level1 = new Button("Level 1");
		GridPane.setHalignment(level1, HPos.CENTER);
		level1.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				generateLevel1(scene,root);
				stage.setScene(cannonScene);
			}
		});
		Button level2 = new Button("Level 2");
		GridPane.setHalignment(level2, HPos.CENTER);
		level2.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				generateLevel2();
				stage.setScene(cannonScene);
			}
		});
		Button level3 = new Button("Level 3");
		GridPane.setHalignment(level3, HPos.CENTER);
		level3.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				generateLevel3();
				stage.setScene(cannonScene);
			}
		});
		levelSelect.addRow(1,level1,level2,level3);

		Label cannonLabel = new Label("Select your ball...");
		ballSelect.addRow(0,cannonLabel);
		Button blueBall = new Button("Blue");
		blueBall.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				ballchoice = 1;
				ballMaxSpeed = 250;
				ballMaterial = new PhongMaterial(Color.BLUE);
				startGame();
				stage.setScene(scene);
			}
		});
		Button redBall = new Button("Red");
		redBall.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				ballchoice = 2;
				ballMaxSpeed = 400;
				ballMaterial = new PhongMaterial(Color.RED);
				startGame();
				stage.setScene(scene);

			}
		});
		Button greenBall = new Button("Green");
		greenBall.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				ballchoice = 3;
				ballMaxSpeed = 550;
				ballMaterial = new PhongMaterial(Color.GREEN);
				startGame();
				stage.setScene(scene);
			}
		});
		ballSelect.addRow(1,blueBall,redBall,greenBall);
		
		scene3d = new SubScene (
				this.root,
				Main.WINDOW_WIDTH,
				Main.WINDOW_HEIGHT,
				true,
				SceneAntialiasing.BALANCED
		);

		globalRoot.getChildren().add(scene3d);

		arenaCamera = new ArenaCamera( true, CAMERA_Z, CAMERA_X_ANGLE,CAMERA_FAR_CLIP);

		scene3d.setCamera ( arenaCamera );

		floodlight = new Floodlight();
		Translate lightPosition = new Translate(0,-800,0);
		floodlight.getTransforms().addAll(lightPosition);
		
		stage.setTitle ( "Rolling Ball" );
		stage.setScene ( levelScene );
		stage.show ( );
	}

	private void startGame() {
		generateHUD();
		Image bgImage = new Image("background.jpg");
		globalRoot.setBackground(new Background(new BackgroundImage(new Image("background.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(WINDOW_WIDTH,WINDOW_HEIGHT, false, false, false, false))));
		this.ball = new Ball ( Main.BALL_RADIUS, ballMaterial, startBallPosition, ballMaxSpeed);
		this.ballCamera = new BallCamera(this.ball);
		this.arena.getChildren ( ).add ( this.ball );
		this.root.getChildren().addAll(floodlight);

		Text gameOver = new Text("Kraj igre");
		gameOver.setX(WINDOW_WIDTH/2 - 2*32);
		gameOver.setY(WINDOW_HEIGHT/2 - 16);
		gameOver.setFill(Color.RED);
		gameOver.setFont(new Font(32));
		globalRoot.getChildren().add(gameOver);
		gameOver.setVisible(false);

		this.root.getChildren ( ).add ( arenaCamera );
		this.arena.getChildren().add( ballCamera );

		Timer timer = new Timer (
				deltaSeconds -> {
					AtomicInteger newScore = new AtomicInteger();
					if(gameTimer.isGameOver()) gameOver.setVisible(true);
					if ( this.ball != null) {
						boolean outOfArena = this.ball.update (
								deltaSeconds,
								Main.PODIUM_DEPTH / 2,
								-Main.PODIUM_DEPTH / 2,
								-Main.PODIUM_WIDTH / 2,
								Main.PODIUM_WIDTH / 2,
								this.arena.getXAngle ( ),
								this.arena.getZAngle ( ),
								Main.MAX_ANGLE_OFFSET,
								Main.MAX_ACCELERATION,
								Main.DAMP,
								obstacles,
								walls
						);

						for(Coin coin: coins){
							boolean coinCollected = coin.handleCollision(this.ball);
							if(coinCollected){
								this.arena.getChildren().remove(coin);
								coins.remove(coin);
								newScore.set(Integer.parseInt(score.getText()) +5);
								score.setText(String.valueOf(newScore));
							}
						}

						boolean isInHole= holes.stream().anyMatch ( hole -> {boolean status = hole.handleCollision (this.ball);
							if(status == true){
								newScore.set(Integer.parseInt(score.getText()) + (hole.getPoints()));
								score.setText(String.valueOf(newScore));
								System.out.println(newScore);
								System.out.println(hole.getPoints());
							}
							return status;
						} );

						if ( outOfArena || isInHole ) {
							this.arena.getChildren ( ).remove ( this.ball );
							this.ball = null;
							if(livesCount > 0){
								livesCount--;
								globalRoot.getChildren().remove(livesDisplay.get(livesCount));
								livesDisplay.remove(livesCount);
								Translate resetBallPosition = new Translate (
										startBallX,
										- ( Main.BALL_RADIUS + Main.PODIUM_HEIGHT / 2 ),
										startBallZ
								);
								this.ball = new Ball(Main.BALL_RADIUS, ballMaterial, resetBallPosition, ballMaxSpeed);
								this.arena.getChildren().add(this.ball);
								this.arena.resetTilt();
								ballCamera.setBall(ball);
							}else{
								gameTimer.setGameOver();
								gameOver.setVisible(true);
							}
						}
					}
				}, this.arena, this.ballCamera, this.compass
		);

		scene.addEventHandler ( KeyEvent.ANY, event -> this.arena.handleKeyEvent ( event, Main.MAX_ANGLE_OFFSET , gameTimer.isGameOver()) );
		scene.addEventHandler (KeyEvent.KEY_TYPED, event ->{
			switch ( event.getCharacter() ){
				case "0":
					floodlight.lightSwitch();
					break;
				case "1":
					scene3d.setCamera(arenaCamera);
					break;
				case "2":
					scene3d.setCamera(ballCamera);
					break;
				default:
					break;
			}
		});
		scene.addEventHandler (ScrollEvent.ANY, event -> arenaCamera.handleScrollEvent(event));
		scene.addEventHandler(MouseEvent.ANY, event -> arenaCamera.handleMouseEvent(event));


		this.arena.resetTilt();
		ballCamera.setBall(ball);
		this.stage.setScene(scene);

		timer.start ( );
		gameTimer.startTimer();
	}

	public static void main ( String[] args ) {
		launch ( );
	}

}