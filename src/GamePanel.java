import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{
    
    static final int GAME_W = 1000;
    static final int GAME_H = (int)(GAME_W * (0.5555));
    static final Dimension SC_SIZE = new Dimension(GAME_W, GAME_H);
    static final int BALL_D = 20;
    static final int PADDLE_W = 25;
    static final int PADDLE_H = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;    
    Paddle paddle2;
    Ball ball;
    Score score;

    GamePanel() {
        newPaddles();
        newBall();
        score = new Score(GAME_W, GAME_H);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SC_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_W/2)-(BALL_D/2), random.nextInt(GAME_H-BALL_D), BALL_D, BALL_D);
    }

    public void newPaddles() {
		paddle1 = new Paddle(0, (GAME_H/2)-(PADDLE_H/2), PADDLE_W, PADDLE_H, 1);
		paddle2 = new Paddle(GAME_W-PADDLE_W, (GAME_H/2)-(PADDLE_H/2), PADDLE_W, PADDLE_H, 2);
	}

    public void paint(Graphics g) {
		image = createImage(getWidth(),getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image,0,0,this);
	}

	public void draw(Graphics g) {
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
        Toolkit.getDefaultToolkit().sync();
	}
	public void move() {
		paddle1.move();
		paddle2.move();
		ball.move();
	}
	public void checkCollision() {
		
		//bounce ball off top & bottom window edges
		if(ball.y <=0) {
			ball.setYDirection(-ball.yVelocity);
		}
		if(ball.y >= GAME_H-BALL_D) {
			ball.setYDirection(-ball.yVelocity);
		}
		//bounce ball off paddles
		if(ball.intersects(paddle1)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++; //optional for more difficulty
			if(ball.yVelocity>0)
				ball.yVelocity++; //optional for more difficulty
			else
				ball.yVelocity--;
			ball.setXDirection(ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		if(ball.intersects(paddle2)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++; //optional for more difficulty
			if(ball.yVelocity>0)
				ball.yVelocity++; //optional for more difficulty
			else
				ball.yVelocity--;
			ball.setXDirection(-ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		//stops paddles at window edges
		if(paddle1.y<=0)
			paddle1.y=0;
		if(paddle1.y >= (GAME_H-PADDLE_H))
			paddle1.y = GAME_H-PADDLE_H;
		if(paddle2.y<=0)
			paddle2.y=0;
		if(paddle2.y >= (GAME_H-PADDLE_H))
			paddle2.y = GAME_H-PADDLE_H;
		//give a player 1 point and creates new paddles & ball
		if(ball.x <=0) {
			score.player2++;
			newPaddles();
			newBall();
			System.out.println("Player 2: "+score.player2);
		}
		if(ball.x >= GAME_W-BALL_D) {
			score.player1++;
			newPaddles();
			newBall();
			System.out.println("Player 1: "+score.player1);
		}
	}
	public void run() {
		//game loop
		long lastTime = System.nanoTime();
		double amountOfTicks =60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while(true) {
			long now = System.nanoTime();
			delta += (now -lastTime)/ns;
			lastTime = now;
			if(delta >=1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}
	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle2.keyPressed(e);
		}
		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
	}
}
