import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 700;
    static final int SCREEN_HEIGHT = 700;
    static final int TOP_PANEL_HEIGHT = 100;
    static final int RIGHT_PANEL_WIDTH = 100;
    static final int UNIT_SIZE = 25; //size of items
    static final int GAME_UNITS = ((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) * (SCREEN_HEIGHT - TOP_PANEL_HEIGHT)) / UNIT_SIZE;
    static final int DELAY = 75; //the higher the number the slower the game
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;  // initial snake size
    int applesEaten = 0;
    int bestScore = 0;
    int appleX;
    int appleY;
    int mouseX;
    int mouseY;
    char direction = 'R'; // snake begin by going right
    char difficultyLevel = 'E';
    boolean running = false;
    Timer timer;
    Random random;
    JLabel scoreLabel;
    JLabel bestScoreLabel;
    JButton easyButton;
    JButton hardButton;
    JButton mediumButton;
    JButton restartButton;

    GamePanel() {
        long seed = System.currentTimeMillis();
        this.random = new Random(seed);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, TOP_PANEL_HEIGHT));
        topPanel.setBackground(Color.DARK_GRAY);

        scoreLabel = new JLabel("Score: " + applesEaten);
        scoreLabel.setFont(new Font("Ink Free", Font.BOLD, 20));
        scoreLabel.setForeground(Color.red);

        bestScoreLabel = new JLabel("Best score: " + bestScore);
        bestScoreLabel.setFont(new Font("Ink Free", Font.BOLD, 15));
        bestScoreLabel.setForeground(Color.black);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 5, 10);

        topPanel.add(scoreLabel, gbc);
        gbc.gridy = 1;
        topPanel.add(bestScoreLabel, gbc);

        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);

        //difficulty panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, (SCREEN_HEIGHT - TOP_PANEL_HEIGHT)));
        rightPanel.setBackground(Color.DARK_GRAY);
        this.easyButton = createButton("Easy");
        this.mediumButton = createButton("Medium");
        this.hardButton = createButton("Hard");
        this.restartButton = createButton("Restart");

        rightPanel.add(easyButton);
        rightPanel.add(mediumButton);
        rightPanel.add(hardButton);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(restartButton);

        this.add(rightPanel, BorderLayout.EAST);
        initializeButtons();
        startGame();
    }

    private void initializeButtons() {
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (difficultyLevel != 'E') {
                    difficultyLevel = 'E';
                    restartGame();
                }
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (difficultyLevel != 'H') {
                    difficultyLevel = 'H';
                    restartGame();
                }
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (difficultyLevel != 'M') {
                    difficultyLevel = 'M';
                    restartGame();
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
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
        timer = new Timer(DELAY, this);
        timer.start();

        x[0] = UNIT_SIZE;
        y[0] = TOP_PANEL_HEIGHT + SCREEN_HEIGHT / 2;
    }

    public void restartGame() {
        if (applesEaten > bestScore) {
            bestScore = applesEaten;
            bestScoreLabel.setText("Best score: " + bestScore);
        }
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        running = false;
        scoreLabel.setText("Score: " + applesEaten);
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = TOP_PANEL_HEIGHT + SCREEN_HEIGHT / 2;
        }

        if (timer != null) {
            timer.stop();

        }
        this.requestFocusInWindow();
        startGame();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        if (ifMouse()) {
            checkMouse();
        }
    }
    public void draw(Graphics g) {
        if (running) {
          /*  for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }*/
            //apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //mouse
            if (ifMouse()) {
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
            if (difficultyLevel == 'M' || difficultyLevel == 'H') {
                g.setColor(Color.GRAY);
                g.fillRect(UNIT_SIZE, TOP_PANEL_HEIGHT, SCREEN_WIDTH - UNIT_SIZE, UNIT_SIZE);
                g.fillRect(UNIT_SIZE, SCREEN_HEIGHT - UNIT_SIZE, SCREEN_WIDTH - 2 * UNIT_SIZE, UNIT_SIZE);
                g.fillRect(0, TOP_PANEL_HEIGHT, UNIT_SIZE, SCREEN_HEIGHT - 2 * UNIT_SIZE);
                g.fillRect(SCREEN_WIDTH - RIGHT_PANEL_WIDTH - UNIT_SIZE, TOP_PANEL_HEIGHT, UNIT_SIZE, SCREEN_HEIGHT - 3 * UNIT_SIZE);
                if (difficultyLevel == 'H') {
                    g.fillRect(UNIT_SIZE * 6, (SCREEN_HEIGHT + TOP_PANEL_HEIGHT) / 2, SCREEN_WIDTH - RIGHT_PANEL_WIDTH - (UNIT_SIZE * 11), UNIT_SIZE);
                    g.fillRect((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / 2, UNIT_SIZE * 11, UNIT_SIZE, SCREEN_HEIGHT - TOP_PANEL_HEIGHT - 12 * UNIT_SIZE);
                }
            }

        } else {
            gameOver(g);
        }
    }
    public void newApple() {
        switch (difficultyLevel) {
            case 'E':
                appleX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / UNIT_SIZE)) * UNIT_SIZE);
                appleY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT;
                break;
            case 'M':
                appleX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + UNIT_SIZE;
                appleY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT + UNIT_SIZE;
                break;
            case 'H':
                int tempAppleX;
                int tempAppleY;
                do {
                    tempAppleX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + UNIT_SIZE;
                 } while (isExcludedX(tempAppleX));
                 do{
                     tempAppleY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT + UNIT_SIZE;

                 }while(isExcludedY(tempAppleY));
                appleX = tempAppleX ;
                appleY = tempAppleY ;
                break;
        }

    }

    public void newMouse() {
        switch (difficultyLevel) {
            case 'E':
                mouseX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / UNIT_SIZE)) * UNIT_SIZE);
                mouseY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT;
                break;
            case 'M':
                mouseX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + UNIT_SIZE;
                mouseY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT + UNIT_SIZE;
                break;
            case 'H':
                int tempMouseX;
                int tempMouseY;
                do {
                    tempMouseX = ((random.nextInt((SCREEN_WIDTH - RIGHT_PANEL_WIDTH - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + UNIT_SIZE;
                } while (isExcludedX(tempMouseX));
                do {
                    tempMouseY = ((random.nextInt((SCREEN_HEIGHT - TOP_PANEL_HEIGHT - 2 * UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE) + TOP_PANEL_HEIGHT + UNIT_SIZE;

                } while (isExcludedY(tempMouseY));
                mouseX = tempMouseX;
                mouseY = tempMouseY;
                break;
        }
    }


    private boolean isExcludedX(int x) {
        return (x >= UNIT_SIZE * 6 && x <= SCREEN_WIDTH - RIGHT_PANEL_WIDTH - UNIT_SIZE * 11) ||
                (x >= (SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / 2 && x <= (SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / 2 + UNIT_SIZE);
    }

    private boolean isExcludedY(int y) {
        return (y >= (SCREEN_HEIGHT + TOP_PANEL_HEIGHT) / 2 && y <= (SCREEN_HEIGHT + TOP_PANEL_HEIGHT) / 2 + UNIT_SIZE) ||
                (y >= UNIT_SIZE * 11 && y <= SCREEN_HEIGHT - TOP_PANEL_HEIGHT - 12 * UNIT_SIZE);
    }

    public boolean ifMouse() {
        return bodyParts % 10 == 0;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
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
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkMouse() {
        if (ifMouse()) {
            if ((x[0] == mouseX) && (y[0] == mouseY)) {

                bodyParts++;
                applesEaten += 2;
                newApple();
            }
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        switch (difficultyLevel) {
            //checks if head touches left border
            case 'E':
                if (x[0] < 0) {
                    // running = false;
                    x[0] = SCREEN_WIDTH - RIGHT_PANEL_WIDTH;
                }
                //checks if head touches right border
                if (x[0] > SCREEN_WIDTH - RIGHT_PANEL_WIDTH) {
                    // running = false;
                    x[0] = 0;
                }
                //checks if head touches top border
                if (y[0] < TOP_PANEL_HEIGHT) {
                    //running = false;
                    y[0] = SCREEN_HEIGHT;
                }
                //checks if head touches bottom border
                if (y[0] > SCREEN_HEIGHT) {
                    //   running = false;
                    y[0] = TOP_PANEL_HEIGHT;
                }
                break;
            case 'M':
                if (x[0] < UNIT_SIZE) {
                     running = false;
                }
                //checks if head touches right border
                if (x[0] > SCREEN_WIDTH - RIGHT_PANEL_WIDTH - 2* UNIT_SIZE) {
                     running = false;
                }
                //checks if head touches top border
                if (y[0] < TOP_PANEL_HEIGHT + UNIT_SIZE) {
                    running = false;
                }
                //checks if head touches bottom border
                if (y[0] > SCREEN_HEIGHT - 2*UNIT_SIZE) {
                      running = false;
                }
                break;
            case 'H':
                if (x[0] < UNIT_SIZE) {
                    running = false;
                }
                //checks if head touches right border
                if (x[0] > SCREEN_WIDTH - RIGHT_PANEL_WIDTH - 2* UNIT_SIZE) {
                    running = false;
                }
                //checks if head touches top border
                if (y[0] < TOP_PANEL_HEIGHT + UNIT_SIZE) {
                    running = false;
                }
                //checks if head touches bottom border
                if (y[0] > SCREEN_HEIGHT - 2*UNIT_SIZE) {
                    running = false;
                }
                if (x[0] >= UNIT_SIZE * 6 && x[0] <= SCREEN_WIDTH - RIGHT_PANEL_WIDTH - UNIT_SIZE * 6 &&
                        y[0] >= (SCREEN_HEIGHT + TOP_PANEL_HEIGHT) / 2 && y[0] <= (SCREEN_HEIGHT + TOP_PANEL_HEIGHT) / 2 ) {
                    running = false;
                }
                if((x[0] >= (SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / 2  &&
                        x[0] <= (SCREEN_WIDTH - RIGHT_PANEL_WIDTH) / 2 ) &&
                        y[0] >= (TOP_PANEL_HEIGHT + UNIT_SIZE * 7) && y[0] <= (SCREEN_HEIGHT - 6* UNIT_SIZE)){
                    running = false;
                }
                break;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        if (applesEaten > bestScore) {
            bestScore = applesEaten;
            bestScoreLabel.setText("Best score: " + bestScore);
        }

        //game over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", ((SCREEN_WIDTH - RIGHT_PANEL_WIDTH) - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
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
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
