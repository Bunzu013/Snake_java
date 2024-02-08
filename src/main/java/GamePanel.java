import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 750;
    static final int SCREEN_HEIGHT = 650;
    static final int TOP_PANEL_HEIGHT = 50;
    static  final int RIGHT_PANEL_WIDTH = 150;
    static final int UNIT_SIZE = 25; //size of items
    static final int GAME_UNITS = ((SCREEN_WIDTH- RIGHT_PANEL_WIDTH)*(SCREEN_HEIGHT-TOP_PANEL_HEIGHT))/UNIT_SIZE;
   static final int DELAY = 75; //the higher the number the slower the game
   final int x[] = new int[GAME_UNITS];
   final int y[] = new int[GAME_UNITS];
   int bodyParts = 6;  // initial snake size
    int applesEaten;
    int appleX;
    int appleY;
    int mouseX;
    int mouseY;
    char direction = 'R'; // snake begin by going right
    boolean running = false;
    Timer timer;
    Random random;
    JLabel scoreLabel;

    GamePanel() {
        long seed = System.currentTimeMillis();
        this.random = new Random(seed);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        //score panel
        scoreLabel = new JLabel("Score: " + applesEaten);
        scoreLabel.setFont(new Font("Ink Free", Font.BOLD, 20));
        scoreLabel.setForeground(Color.red);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, TOP_PANEL_HEIGHT));
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.add(scoreLabel);

        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);

        //difficulty panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, (SCREEN_HEIGHT-TOP_PANEL_HEIGHT)));
        rightPanel.setBackground(Color.DARK_GRAY);
        JButton easyButton = createButton("Easy");
        JButton hardButton = createButton("Hard");
        JButton surpriseButton = createButton("Surprise me");
        JButton restartButton = createButton("Restart");
        //restartButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        rightPanel.add(easyButton);
        rightPanel.add(hardButton);
        rightPanel.add(surpriseButton);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(restartButton);
        this.add(rightPanel, BorderLayout.EAST);

        startGame();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 30));
        button.setMinimumSize(new Dimension(RIGHT_PANEL_WIDTH, 30));
        return button;
    }
    public void startGame() {
        newApple();
        newMouse();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();

        x[0] = 0;
        y[0] = TOP_PANEL_HEIGHT + SCREEN_HEIGHT /2;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        if (ifMouse()) {
            checkMouse();
        }
    }

    public void draw(Graphics g) {
        if(running) {
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }*/
            //apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //mouse
            if(ifMouse()) {
                g.setColor(Color.GRAY);
                g.fillRect(mouseX, mouseY, UNIT_SIZE, UNIT_SIZE);
            }
            //sake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 30);
        }
        else{
            gameOver(g);
        }

    }
    public void newApple(){
        appleX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / UNIT_SIZE)) * UNIT_SIZE);
        appleY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT;
    }

    public void newMouse(){
        mouseX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / UNIT_SIZE)) * UNIT_SIZE);
        mouseY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT;
    }


    public boolean ifMouse(){
        return bodyParts % 10 == 0;
    }
    public void move() {
        for(int i=bodyParts; i>0;i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            default:
                break;
        }
    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkMouse() {
        if(ifMouse()) {
            if ((x[0] == mouseX) && (y[0] == mouseY)) {

                bodyParts++;
                applesEaten += 2;
                newApple();
            }
        }
    }
    public void checkCollisions() {
        //checks if head collides with body
        for(int i = bodyParts; i>0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        //checks if head touches left border
        if(x[0] < 0){
            running = false;
        }
        //checks if head touches right border
        if(x[0] > SCREEN_WIDTH - RIGHT_PANEL_WIDTH){
            running = false;
        }
        //checks if head touches top border
        if(y[0] < TOP_PANEL_HEIGHT){
            running = false;
        }
        //checks if head touches bottom border
        if(y[0] > SCREEN_HEIGHT){
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, ((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) - metrics1.stringWidth("Score: " + applesEaten))/2,g.getFont().getSize());
        //game over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", ((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) - metrics2.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkMouse();
            checkCollisions();
            scoreLabel.setText("Score: " + applesEaten); // Score label actualization

        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }

}
