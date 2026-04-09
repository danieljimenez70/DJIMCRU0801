import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int WIDTH = 600;
    static final int HEIGHT = 600;
    static final int UNIT = 25;
    static final int DELAY = 100;

    int x[] = new int[WIDTH * HEIGHT / UNIT];
    int y[] = new int[WIDTH * HEIGHT / UNIT];

    int bodyParts;
    int apples;
    int appleX, appleY;
    char direction;
    boolean running = false;

    Timer timer;
    Random random;

    JButton restartButton;

    public GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setLayout(null);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                    case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                    case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                    case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
                }
            }
        });

        // 🔘 BOTÓN REINICIAR
        restartButton = new JButton("Reiniciar");
        restartButton.setBounds(230, 320, 140, 40);
        restartButton.setFocusPainted(false);
        restartButton.setVisible(false);

        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
    }

    public void startGame() {
        bodyParts = 6;
        apples = 0;
        direction = 'R';

        for (int i = 0; i < bodyParts; i++) {
            x[i] = WIDTH / 2;
            y[i] = HEIGHT / 2;
        }

        newApple();
        running = true;

        restartButton.setVisible(false);

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void restartGame() {
        timer.stop();
        startGame();
        repaint();
        requestFocusInWindow();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 🎨 FONDO DEGRADADO
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(30, 30, 60),
                0, HEIGHT, new Color(10, 10, 20)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        if (running) {
            drawGame(g2);
        } else {
            drawGameOver(g2);
        }
    }

    public void drawGame(Graphics2D g) {
        // 🍎 comida con brillo
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT, UNIT);

        g.setColor(Color.WHITE);
        g.fillOval(appleX + 8, appleY + 5, 6, 6);

        // 🐍 serpiente con estilo
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(new Color(0, 255, 120));
            } else {
                g.setColor(new Color(0, 180, 90));
            }
            g.fillRoundRect(x[i], y[i], UNIT, UNIT, 10, 10);
        }

        // 🧾 puntuación
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + apples, 10, 25);
    }

    public void drawGameOver(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("GAME OVER", 150, 250);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Puntuación: " + apples, 210, 290);

        restartButton.setVisible(true);
    }

    public void newApple() {
        appleX = random.nextInt(WIDTH / UNIT) * UNIT;
        appleY = random.nextInt(HEIGHT / UNIT) * UNIT;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= UNIT; break;
            case 'D': y[0] += UNIT; break;
            case 'L': x[0] -= UNIT; break;
            case 'R': x[0] += UNIT; break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            apples++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i]) {
                if (y[0] == y[i]) running = false;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH ||
                y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
}