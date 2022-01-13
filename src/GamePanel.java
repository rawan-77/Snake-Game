import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int FRAME_WIDTH = 600; //600 pixels
    static final int FRAME_HEIGHT = 600;
    static final int OBJECT_SIZE = 20; //each player is going to have dimensions of 25 pixels for the width and height
    static final int GAME_UNITS = (FRAME_WIDTH * FRAME_HEIGHT)/ OBJECT_SIZE;
    static final int DELAY = 90; //the higher the number the slower the game
    final int x[] = new int[GAME_UNITS]; //holds all the x coordinates of the body parts, including head of snake
    final int y[] = new int[GAME_UNITS]; //holds all the y coordinates of the body parts, including head
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX; //x coordinate of where the apple is located, appears randomly each time snake eats apple
    int appleY; //apple y coordinates
    char direction = 'R'; //change to string (right, left, up, down)
    boolean gameRunning = false;
    Timer timer;
    Random random;

    public GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newApple();
        gameRunning = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void draw(Graphics graphics){
        /* The screen is 600 x 600 and each player is going to have dimensions of 25 pixels
         * for the width and height, so we will turn the screen into a grid to help us
         * visualize things, so the first for loop is optional
         * each item on the grid will take up one of the squares of the grid
         * if we increase the unit size, the squares on the grid increase*/

        if(gameRunning) {
            //draws grid lines
            for (int i = 0; i < FRAME_HEIGHT / OBJECT_SIZE; i++) {
                graphics.drawLine(i * OBJECT_SIZE, 0, i * OBJECT_SIZE, FRAME_HEIGHT);
                graphics.drawLine(0, i * OBJECT_SIZE, FRAME_WIDTH, i * OBJECT_SIZE);
            }

            graphics.setColor(Color.red);
            graphics.fillOval(appleX, appleY, OBJECT_SIZE, OBJECT_SIZE);

            //for loop iterates over body parts of snake to draw body and head
            for (int i = 0; i < bodyParts; i++) {
                if(i == 0) { //if we are dealing with the head of the snake
                    graphics.setColor(Color.cyan);
                }else { //if we are dealing with body parts
                    //makes the snake's body change color randomly
                    graphics.setColor(new Color(random.nextInt(255), random.nextInt(255),
                            random.nextInt(255)));
                }
                graphics.fillRect(x[i], y[i], OBJECT_SIZE, OBJECT_SIZE); //fillRect for rectangle
            }

            displayScore(graphics);

        }else{
            gameOver(graphics);
        }
    }

    /* generates the coordinates of a new apple whenever it is called
     * it gets called when we begin the game, or eat an apple*/
    public void newApple(){
        //we divide by unit size so that we get a x position in one of the horizontal grid squares, same for y, but vertical
        //we then multiply by unit size because we want the apple to be placed evenly within one of the squares of grid
        appleX = random.nextInt((int)(FRAME_WIDTH / OBJECT_SIZE)) * OBJECT_SIZE;
        appleY = random.nextInt((int)(FRAME_HEIGHT / OBJECT_SIZE)) * OBJECT_SIZE;

    }

    //Moves the snake
    public void move(){
        //for loop iterates over all the body parts of the snake to shift them around
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i -1]; //we are shifting all coordinates in array by one spot
            y[i] = y[i -1];
        }

        //switch to change the direction of where the snake is headed
        switch(direction){
            case 'U':
                y[0] = y[0] - OBJECT_SIZE; //goes up one position
                break;

            case 'D':
                y[0] = y[0] + OBJECT_SIZE; //goes down one position
                break;

            case 'L':
                x[0] = x[0] - OBJECT_SIZE; //goes left one position
                break;

            case 'R':
                x[0] = x[0] + OBJECT_SIZE; //goes right one position
                break;
        }
    }

    public void checkApple(){
        //check coordinates of snake and apple to see if they match
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        //check if the head of the snake collides with its body in any way
        for (int i = bodyParts; i > 0 ; i--) {
            //x[0], y[0] = head of snake
            if((x[0] == x[i]) && (y[0] == y[i])){
                gameRunning = false; //triggers a game over method
            }
        }

        //check if the head of the snake touches any of the screen borders
        if((x[0] < 0) || (x[0] > FRAME_WIDTH) || (y[0] < 0) || (y[0] > FRAME_HEIGHT)){
            gameRunning = false;
        }

        //if game is not running stop the timer
        if(!gameRunning){
            timer.stop();
        }
    }

    public void gameOver(Graphics graphics){
        //Game over text
        graphics.setColor(Color.red);
        graphics.setFont(new Font("Algerian", Font.BOLD, 100));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (FRAME_WIDTH - metrics.stringWidth("Game Over"))/2 , FRAME_HEIGHT /2);
        displayScore(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(gameRunning){//if game is running
            move(); //move the snake
            checkApple(); //check if we ran into the apple
            checkCollisions();
        }

        repaint();
    }

    public void displayScore(Graphics graphics){
        graphics.setColor(Color.ORANGE);
        graphics.setFont(new Font("Algerian", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + applesEaten, (FRAME_WIDTH - metrics.stringWidth("Score: " +
                        applesEaten))/2, graphics.getFont().getSize());
    }

    // MyKeyAdapter controls the snake
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            //switch examines the e KeyEvent
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT: //left arrow key
                    //don't want snake to turn 180 degrees in opposite direction, so must limit snake to 90 degree turns
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT: //right arrow key
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP: //top arrow key
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN: //bottom arrow key
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }

        }
    }
}
