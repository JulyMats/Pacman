import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class GameBoard extends JPanel implements ActionListener, Serializable{
    private int cellSize;
    private int rowsNum;
    private int columnsNum;
    private HighScoresPanel highScoresPanel;
    private JFrame gameBoardFrame;
    private short[][] defaultBoard;
    private short[][] board;
    private Image pacman;
    private Image pauseIcon = new ImageIcon("startIcon.png").getImage();
    private Image wall = new ImageIcon("wall.png").getImage();
    private Image bigDot = new ImageIcon("bigDot.png").getImage();
    private Image dot = new ImageIcon("dot.png").getImage();
    private Image line = new ImageIcon("line1.png").getImage();
    private Image live = new ImageIcon("live.png").getImage();
    private Image speed = new ImageIcon("speed.png").getImage();
    private Image stop = new ImageIcon("pause.png").getImage();
    private Image scorePlus = new ImageIcon("scorePlus.png").getImage();
    private Image redGhost = new ImageIcon("redGhost.png").getImage();
    private Image greenGhost = new ImageIcon("greenGhost.png").getImage();
    private Image orangeGhost = new ImageIcon("orangeGhost.png").getImage();
    private Image pinkGhost = new ImageIcon("pinkGhost.png").getImage();
    private Image pacmanRight = new ImageIcon("pacmanRight.png").getImage();
    private Image pacmanLeft = new ImageIcon("pacmanLeft.png").getImage();
    private Image pacmanUp = new ImageIcon("pacmanUp.png").getImage();
    private Image pacmanDown = new ImageIcon("pacmanDown.png").getImage();
    private Image pacmanFull = new ImageIcon("pacman.png").getImage();
    private Image pacmanStart = pacmanFull;
    private int score;
    private int lives;
    private int[] highScores;
    private int cookiesCounter;
    private int numOfCookies;
    private boolean isGameStarted;
    private int[] ghostCurX, ghostCurY, ghostNextX, ghostNextY, ghostPrevX, ghostPrevY;
    private int ghostSpeed;
    private int nextX, nextY, currentX, currentY, startX, startY;
    private int pacmanDirection;
    private int[] ghostDirection;
    private short[] previousValues;
    private final int leftDirection = 1, rightDirection = 2, upDirection = 3, downDirection = 4;
    private final int NUMBER_OF_GHOSTS = 4;
    Thread moveGhostsThread;
    Thread awardsThread;
    Thread animationThread;



    public GameBoard(int rows, int columns, HighScoresPanel highScoresPanel, JFrame gameBoardFrame) {

        this.highScoresPanel = highScoresPanel;
        this.gameBoardFrame = gameBoardFrame;
        this.rowsNum = rows;
        this.columnsNum = columns;

        initBoard();
        initVariables();
        startGame();

        this.moveGhostsThread = new Thread(() -> {
            while(true) {
                System.out.println(isGameStarted);
                while (isGameStarted) {
                    System.out.println(isGameStarted);
                    try {
                        Thread.sleep(ghostSpeed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ghostsMove();
                }
            }
        });
        moveGhostsThread.start();
        this.awardsThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createAwards();
            }
        });
        awardsThread.start();
        this.animationThread = new Thread(() -> {
            while(true) {
                try {
                    pacman = pacmanFull;
                    Thread.sleep(450);
                    pacmanMove();
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        animationThread.start();
    }
    public void createAwards() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(4);

        if (randomNumber < 1) {
            int ghost = rand.nextInt(4);
            int award = rand.nextInt(3);

            switch (award) {
                case 1 -> board[ghostPrevY[ghost]][ghostPrevX[ghost]] = 'S';
                case 2 -> board[ghostPrevY[ghost]][ghostPrevX[ghost]] = 'L';
                case 3 -> board[ghostPrevY[ghost]][ghostPrevX[ghost]] = 'K';
                case 4 -> board[ghostPrevY[ghost]][ghostPrevX[ghost]] = 'W';

            }
        }
    }

    public void initBoard() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //super.keyTyped(e);
                int key = e.getKeyCode();

            }
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        //nextX = currentX - 1;
                        //nextY = currentY;
                        pacmanDirection = leftDirection;
                        isGameStarted = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        //nextX = currentX + 1;
                        //nextY = currentY;
                        pacmanDirection = rightDirection;
                        isGameStarted = true;
                        break;
                    case KeyEvent.VK_UP:
                        //nextX = currentX;
                        //nextY = currentY - 1;
                        pacmanDirection = upDirection;
                        isGameStarted = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        //nextX = currentX;
                        //nextY = currentY + 1;
                        pacmanDirection = downDirection;
                        isGameStarted = true;
                        break;
                    case KeyEvent.VK_Q:
                        if(e.isControlDown() && e.isShiftDown())
                            gameBoardFrame.dispose();
                        break;
                    default:
                        nextX = currentX;
                        nextY = currentY;
                        break;
                }
                play();
                repaint();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == Event.LEFT || key == Event.RIGHT
                        || key == Event.UP || key == Event.DOWN) {
                    nextX = 0;
                    nextY = 0;
                }
            }
        });
        setFocusable(true);
    }

    public void initVariables() {
        isGameStarted = false;
        numOfCookies = 0;
        score = 0;
        lives = 3;
        highScores = new int[5];
        defaultBoard = readBoard();
        ghostCurY = new int[NUMBER_OF_GHOSTS];
        ghostCurX = new int[NUMBER_OF_GHOSTS];
        ghostNextY = new int[NUMBER_OF_GHOSTS];
        ghostNextX = new int[NUMBER_OF_GHOSTS];
        ghostPrevX = new int[NUMBER_OF_GHOSTS];
        ghostPrevY = new int[NUMBER_OF_GHOSTS];
        ghostDirection = new int[NUMBER_OF_GHOSTS];
        previousValues = new short[NUMBER_OF_GHOSTS];
    }

    public void startGame() {
        generateBoard();
        pacman = pacmanStart;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j] == 'M') {
                    startX = j;
                    startY = i;
                }
                if(board[i][j] == '.' || board[i][j] == 'O') {
                    numOfCookies++;
                }
                if(board[i][j] == 'G') {
                    ghostCurX[0] = j;
                    ghostCurY[0] = i;
                    ghostNextX[0] = ghostCurX[0];
                    ghostNextY[0] = ghostCurY[0];
                    ghostPrevY[0] = ghostCurY[0];
                    ghostPrevX[0] = ghostCurX[0];
                }
                if(board[i][j] == 'R') {
                    ghostCurX[1] = j;
                    ghostCurY[1] = i;
                    ghostNextX[1] = ghostCurX[1];
                    ghostNextY[1] = ghostCurY[1];
                    ghostPrevY[1] = ghostCurY[1];
                    ghostPrevX[1] = ghostCurX[1];
                }
                if(board[i][j] == 'B') {
                    ghostCurX[2] = j;
                    ghostCurY[2] = i;
                    ghostNextX[2] = ghostCurX[2];
                    ghostNextY[2] = ghostCurY[2];
                    ghostPrevY[2] = ghostCurY[2];
                    ghostPrevX[2] = ghostCurX[2];
                }
                if(board[i][j] == 'P') {
                    ghostCurX[3] = j;
                    ghostCurY[3] = i;
                    ghostNextX[3] = ghostCurX[3];
                    ghostNextY[3] = ghostCurY[3];
                    ghostPrevY[3] = ghostCurY[3];
                    ghostPrevX[3] = ghostCurX[3];
                }
            }
        }
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            ghostDirection[i] = upDirection;
        }
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            previousValues[i] = ' ';
        }
        startNewLive();
    }

    public void startNewLive() {
        ghostSpeed = 700;
        pacman = pacmanStart;
        nextX = 0;
        nextY = 0;
        currentX = startX;
        currentY = startY;
        board[currentY][currentX] = 'M';
        repaint();

        if(lives <= 0) {
            isGameStarted = false;
            JOptionPane.showMessageDialog(this, "Game over" +
                    "\nIf you want, you could try again", "Game over message", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    public void drawGameBoard() {
        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(Color.BLACK);
        JTable table = new JTable(new GameBoardModel());
        table.setOpaque(false);
        if(rowsNum > columnsNum) {
            cellSize = (getHeight() - 100) / rowsNum;

        } else if (columnsNum > rowsNum) {
            cellSize = getWidth() / columnsNum;
        } else {
            cellSize = (getHeight() - 100) / rowsNum;
        }
        table.setRowHeight(cellSize);
        for (int i = 0; i < columnsNum; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        }
        for (int i = 0; i < rowsNum; i++) {
            for (int j = 0; j < columnsNum; j++) {
                table.getColumnModel().getColumn(j).setCellRenderer(new GameBoardCellRenderer());
            }
        }
        tablePanel.add(table);
        add(tablePanel, BorderLayout.CENTER);
    }

    public static short[][] readBoard() {
        short[][] board = new short[100][100];
        try {
            File file = new File("pacmanMap.txt");
            Scanner scanner = new Scanner(file);
            for (int i = 0; i < board.length; i++) {
                String line = scanner.nextLine();
                for (int j = 0, k = 0; j < board[0].length; j++, k+=2) {
                    char val = line.charAt(k);
                    board[i][j] = (short) val;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return board;
    }

    public void generateBoard() {
        board = new short[rowsNum][columnsNum];

        int rowStart = (defaultBoard.length - board.length) / 2 + 1;
        int columnStart = (defaultBoard[0].length - board[0].length) / 2 + 1;

        for (int i = 1, rowCopy = rowStart; i < board.length - 1; i++, rowCopy++) {
            for (int j = 1, columnCopy = columnStart; j < board[0].length - 1; j++, columnCopy++) {
                board[i][j] = defaultBoard[rowCopy][columnCopy];
            }
        }
        for (int i = 0; i < board.length; i++) {
            board[i][0] = 'x';
            board[i][board[0].length-1] = 'x';
        }
        for (int i = 0; i < board[0].length; i++) {
            board[0][i] = 'x';
            board[board.length - 1][i] = 'x';
        }
    }

    class GameBoardModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return board.length;
        }

        @Override
        public int getColumnCount() {
            return board[0].length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return board[rowIndex][columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }
    }

    class GameBoardCellRenderer extends JLabel implements TableCellRenderer {

        private short val;
        private Image img;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, cellSize, cellSize, this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            val = (short) value;
            setOpaque(false);
            img = chooseImage(val);
            return this;
        }

        public Image chooseImage(short val) {
            switch (val) {
                case 120:
                    return wall;
                case 46:
                    return dot;
                case 32:
                    return null;
                case 45:
                    return line;
                case 66:
                    return orangeGhost;
                case 82:
                    return redGhost;
                case 79:
                    return bigDot;
                case 80:
                    return pinkGhost;
                case 71:
                    return greenGhost;
                case 77:
                    return pacman;
                case 'L':
                    return live;
                case 'S':
                    return speed;
                case 'K':
                    return stop;
                case 'W':
                    return scorePlus;
                default:
                    return null;
            }
        }
    }

    public void drawScore(Graphics g) {
        JLabel scoreLabel = new JLabel();
        scoreLabel.setPreferredSize(new Dimension(scoreLabel.getWidth(), 50));
        add(scoreLabel, BorderLayout.NORTH);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        g.setColor(Color.GRAY);
        g.drawString("Score: " + score,  50, 30);
        int highestScore = highScores[0] = 1320;
        for (int i = 0; i < highScores.length; i++) {
            if(highScores[i] > highestScore)
                highestScore = highScores[i];
        }
        g.drawString("Highest score: " + highestScore, getWidth() / 2 + 10, 30);
    }

    public void drawLives(Graphics g) {
        JLabel livesLabel = new JLabel();
        for (int i = 0; i < lives; i++) {
            g.drawImage(pacmanRight, i * 30 + 30, getHeight() - 20 - 30, 30, 30, livesLabel);
        }
        livesLabel.setPreferredSize(new Dimension(getWidth(), 50));
        add(livesLabel, BorderLayout.SOUTH);
    }

    public void pacmanMove() {
            switch (pacmanDirection) {
                case leftDirection:
                    nextX = currentX - 1;
                    nextY = currentY;
                    break;
                case rightDirection:
                    nextX = currentX + 1;
                    nextY = currentY;
                    break;
                case upDirection:
                    nextX = currentX;
                    nextY = currentY - 1;
                    break;
                case downDirection:
                    nextX = currentX;
                    nextY = currentY + 1;
                    break;
            }

            if (nextX != currentX || nextY != currentY) {
                if (board[nextY][nextX] == 'x') {
                    nextX = currentX;
                    nextY = currentY;
                    pacman = pacmanFull;
                } else if (board[nextY][nextX] == 'R' || board[nextY][nextX] == 'B' || board[nextY][nextX] == 'G' || board[nextY][nextX] == 'P') {
                    board[currentY][currentX] = ' ';
                    lives--;
                    startNewLive();
                } else {
                    if (board[nextY][nextX] == '.') {
                        score += 10;
                        cookiesCounter++;
                    }
                    if (board[nextY][nextX] == 'O') {
                        score += 100;
                        cookiesCounter++;
                    }
                    if (board[nextY][nextX] == 'S') {
                        ghostSpeed /= 2;
                    }
                    if (board[nextY][nextX] == 'L') {
                        lives++;
                    }
                    if (board[nextY][nextX] == 'K') {
                        try {
                            moveGhostsThread.wait(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (board[nextY][nextX] == 'W') {
                        score += 2000;
                    }
                    changePacmanDirection();
                    board[currentY][currentX] = ' ';
                    currentX = nextX;
                    currentY = nextY;
                    board[currentY][currentX] = 'M';
                }
            }

    }

    public void changePacmanDirection(){
        switch (pacmanDirection){
            case leftDirection -> pacman = pacmanLeft;
            case rightDirection -> pacman = pacmanRight;
            case upDirection -> pacman = pacmanUp;
            case downDirection -> pacman = pacmanDown;
        }
    }

   public void ghostsMove() {
       for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
           short typeOfGhost = board[ghostCurY[i]][ghostCurX[i]];
           System.out.println((char) typeOfGhost);
           switch (ghostDirection[i]) {
               case leftDirection -> {
                   System.out.println("left");
                   ghostNextX[i] = ghostCurX[i] - 1;
                   ghostNextY[i] = ghostCurY[i];
               }
               case rightDirection -> {
                   System.out.println("right");
                   ghostNextX[i] = ghostCurX[i] + 1;
                   ghostNextY[i] = ghostCurY[i];
               }
               case downDirection -> {
                   System.out.println("down");
                   ghostNextX[i] = ghostCurX[i];
                   ghostNextY[i] = ghostCurY[i] + 1;
               }
               case upDirection -> {
                   System.out.println("Up");
                   ghostNextX[i] = ghostCurX[i];
                   ghostNextY[i] = ghostCurY[i] - 1;
               }
           }
           if (board[ghostNextY[i]][ghostNextX[i]] != 'x' && board[ghostNextY[i]][ghostNextX[i]] != 'R'
                   && board[ghostNextY[i]][ghostNextX[i]] != 'G' && board[ghostNextY[i]][ghostNextX[i]] != 'B' && board[ghostNextY[i]][ghostNextX[i]] != 'P') {
           } else {
               int[][] choices = new int[4][4];
               if (ghostDirection[i] != leftDirection) {
                   ghostNextX[i] = ghostCurX[i] - 1;
                   ghostNextY[i] = ghostCurY[i];
                   if (board[ghostNextY[i]][ghostNextX[i]] != 'x' && board[ghostNextY[i]][ghostNextX[i]] != 'R'
                           && board[ghostNextY[i]][ghostNextX[i]] != 'G' && board[ghostNextY[i]][ghostNextX[i]] != 'B' && board[ghostNextY[i]][ghostNextX[i]] != 'P') {
                       choices[0][0] = ghostNextY[i];
                       choices[0][1] = ghostNextX[i];
                   }
               }
               if (ghostDirection[i] != rightDirection) {
                   ghostNextX[i] = ghostCurX[i] + 1;
                   ghostNextY[i] = ghostCurY[i];
                   if (board[ghostNextY[i]][ghostNextX[i]] != 'x' && board[ghostNextY[i]][ghostNextX[i]] != 'R'
                           && board[ghostNextY[i]][ghostNextX[i]] != 'G' && board[ghostNextY[i]][ghostNextX[i]] != 'B' && board[ghostNextY[i]][ghostNextX[i]] != 'P') {
                       choices[1][0] = ghostNextY[i];
                       choices[1][1] = ghostNextX[i];
                   }
               }
               if (ghostDirection[i] != upDirection) {
                   ghostNextX[i] = ghostCurX[i];
                   ghostNextY[i] = ghostCurY[i] - 1;
                   if (board[ghostNextY[i]][ghostNextX[i]] != 'x' && board[ghostNextY[i]][ghostNextX[i]] != 'R'
                           && board[ghostNextY[i]][ghostNextX[i]] != 'G' && board[ghostNextY[i]][ghostNextX[i]] != 'B' && board[ghostNextY[i]][ghostNextX[i]] != 'P') {
                       choices[2][0] = ghostNextY[i];
                       choices[2][1] = ghostNextX[i];
                   }
               }
               if (ghostDirection[i] != downDirection) {
                   ghostNextX[i] = ghostCurX[i];
                   ghostNextY[i] = ghostCurY[i] + 1;
                   if (board[ghostNextY[i]][ghostNextX[i]] != 'x' && board[ghostNextY[i]][ghostNextX[i]] != 'R'
                           && board[ghostNextY[i]][ghostNextX[i]] != 'G' && board[ghostNextY[i]][ghostNextX[i]] != 'B' && board[ghostNextY[i]][ghostNextX[i]] != 'P') {
                       choices[3][0] = ghostNextY[i];
                       choices[3][1] = ghostNextX[i];
                   }
               }
               boolean isAnOption = false;
               outerLoop:
               for (int j = 0; j < choices.length; j++) {
                   for (int k = 0; k < choices[0].length; k++) {
                       if(choices[j][k] != 0) {
                           isAnOption = true;
                           break outerLoop;
                       }
                   }
               }
               if (!isAnOption) {
                   ghostNextX[i] = ghostCurX[i];
                   ghostNextY[i] = ghostCurX[i];
               }
               while (isAnOption) {
                   int rand = (int) (Math.random() * 4);
                   if (choices[rand][0] != 0 || choices[rand][1] != 0) {
                       isAnOption = false;
                       switch (rand) {
                           case 0:
                               ghostNextY[i] = choices[0][0];
                               ghostNextX[i] = choices[0][1];
                               ghostDirection[i] = leftDirection;
                               break;
                           case 1:
                               ghostNextY[i] = choices[1][0];
                               ghostNextX[i] = choices[1][1];
                               ghostDirection[i] = rightDirection;
                               break;
                           case 2:
                               ghostNextY[i] = choices[2][0];
                               ghostNextX[i] = choices[2][1];
                               ghostDirection[i] = upDirection;
                               break;
                           case 3:
                               ghostNextY[i] = choices[3][0];
                               ghostNextX[i] = choices[3][1];
                               ghostDirection[i] = downDirection;
                               break;
                       }
                   }
               }
           }
           if(board[ghostNextY[i]][ghostNextX[i]] == 'M') {
               lives--;
               startNewLive();
               board[ghostCurY[i]][ghostCurX[i]] = previousValues[i];
               previousValues[i] = ' ';
               ghostPrevX[i] = ghostCurX[i];
               ghostPrevY[i] = ghostCurY[i];
               ghostCurX[i] = ghostNextX[i];
               ghostCurY[i] = ghostNextY[i];
               board[ghostCurY[i]][ghostCurX[i]] = typeOfGhost;
           } else {
               board[ghostCurY[i]][ghostCurX[i]] = previousValues[i];
               previousValues[i] = board[ghostNextY[i]][ghostNextX[i]];
               ghostPrevX[i] = ghostCurX[i];
               ghostPrevY[i] = ghostCurY[i];
               ghostCurX[i] = ghostNextX[i];
               ghostCurY[i] = ghostNextY[i];
               board[ghostCurY[i]][ghostCurX[i]] = typeOfGhost;
           }
       }
       repaint();
   }

    public void play() {
        checkGameFinished();
    }

    public void checkGameFinished() {
        if(cookiesCounter >= numOfCookies) {
            isGameStarted = false;
            JPanel nameInputPanel = new JPanel();
            JTextField nameInput = new JTextField(15);
            nameInputPanel.add(new JLabel("You Won! \nPlease enter your name"));
            nameInputPanel.add(new JLabel("NickName: "));
            nameInputPanel.add(nameInput);

            int result = JOptionPane.showConfirmDialog(this, nameInputPanel, "Congratulation message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(pauseIcon.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            if(result == JOptionPane.OK_OPTION) {
                highScoresPanel.addScore(new Score(nameInput.getText(), score));
                SwingUtilities.getWindowAncestor(this).dispose();
            } else if(result == JOptionPane.OK_CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                highScoresPanel.addScore(new Score(score));
                SwingUtilities.getWindowAncestor(this).dispose();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawScore(g);
        drawLives(g);
        drawGameBoard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
