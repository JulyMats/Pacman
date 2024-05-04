import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

public class Menu extends JFrame implements Serializable {

    private HighScoresPanel highScoresPanel;

    public Menu() {
        setTitle("Menu");
        setIconImage(new ImageIcon("startIcon.png").getImage());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);
        getContentPane().setBackground(Color.BLACK);
        createMenu();
        highScoresPanel = new HighScoresPanel();
        highScoresPanel.loadHighScores();
    }

    public void createMenu() {
        JLabel title = new JLabel("PACMAN", JLabel.CENTER);
        title.setFont(new Font(Font.SERIF, Font.BOLD, 75));
        title.setForeground(Color.YELLOW);
        title.setPreferredSize(new Dimension(title.getWidth(), 200));

        JPanel buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(buttons.getWidth(), 200));
        buttons.setBackground(Color.BLACK);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));


        Font fnt1 = new Font(Font.SERIF, Font.PLAIN, 35);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                e.getComponent().setForeground(Color.YELLOW);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);

                e.getComponent().setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };

        JButton newGame = new JButton("NEW GAME");
        newGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGame.setFont(fnt1);
         newGame.setForeground(Color.WHITE);
        newGame.setContentAreaFilled(false);
        newGame.setBorder(BorderFactory.createEmptyBorder());
        newGame.addMouseListener(mouseAdapter);
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askBoardSize();
            }

            public void askBoardSize() {
                JPanel boardSizePanel = new JPanel();
                JTextField rowInput = new JTextField(3);
                JTextField columnInput = new JTextField(3);

                boardSizePanel.add(new JLabel("Please enter size of the board, you want to have."));
                boardSizePanel.add(new JLabel("Rows: "));
                boardSizePanel.add(rowInput);
                boardSizePanel.add(new JLabel("Columns: "));
                boardSizePanel.add(columnInput);

                int rows;
                int columns;

                int result = JOptionPane.showConfirmDialog(null, boardSizePanel, "Board Size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(new ImageIcon("pacmanRun.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                if (result == JOptionPane.OK_OPTION) {
                    if ((Integer.parseInt(rowInput.getText()) < 101 && Integer.parseInt(rowInput.getText()) > 9) &&
                            (Integer.parseInt(columnInput.getText()) < 101 && Integer.parseInt(columnInput.getText()) > 9)) {
                        rows = Integer.parseInt(rowInput.getText());
                        columns = Integer.parseInt(columnInput.getText());
                        createBoardGame(rows, columns);
                    } else {
                        Icon icon = new ImageIcon(new ImageIcon("startIcon.png").getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH));
                        JOptionPane.showMessageDialog(boardSizePanel, "Please enter correct size. \nYou can choose values between 10 and 100", "Error", JOptionPane.ERROR_MESSAGE, icon);
                        askBoardSize();
                    }
                }
            }

            public void createBoardGame(int rows, int columns) {
                JFrame gameBoardFrame = new JFrame();
                gameBoardFrame.setSize(1000, 700);
                gameBoardFrame.setBackground(Color.BLACK);
                gameBoardFrame.setLocationRelativeTo(null);
                gameBoardFrame.setTitle("Pacman game");
                gameBoardFrame.setIconImage(new ImageIcon("startIcon.png").getImage());
                gameBoardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                GameBoard gameBoard = new GameBoard(rows, columns, highScoresPanel, gameBoardFrame);
                gameBoardFrame.add(gameBoard);
                gameBoardFrame.setVisible(true);
            }
        });

        JButton highScopes = new JButton("HIGH SCORES");
        highScopes.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScopes.setFont(fnt1);
        highScopes.setForeground(Color.WHITE);
        highScopes.setContentAreaFilled(false);
        highScopes.setBorder(BorderFactory.createEmptyBorder());
        highScopes.addMouseListener(mouseAdapter);
        highScopes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createHighScoresFrame();
            }

            public void createHighScoresFrame() {
                JFrame highScoresFrame = new JFrame();
                highScoresFrame.setSize(1000, 700);
                highScoresFrame.setBackground(Color.BLACK);
                highScoresFrame.setLocationRelativeTo(null);
                highScoresFrame.setTitle("High Scores");
                highScoresFrame.setIconImage(new ImageIcon("startIcon.png").getImage());
                highScoresFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                highScoresPanel.loadHighScores();
                highScoresFrame.add(highScoresPanel);
                highScoresFrame.setVisible(true);
            }
        });

        JButton exit = new JButton("EXIT");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.setFont(fnt1);
        exit.setForeground(Color.WHITE);
        exit.setContentAreaFilled(false);
        exit.setBorder(BorderFactory.createEmptyBorder());
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.addMouseListener(mouseAdapter);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttons.add(newGame);
        buttons.add(highScopes);
        buttons.add(exit);

        JPanel imagesPanel = new JPanel();
        imagesPanel.setBackground(Color.BLACK);
        imagesPanel.setLayout(new FlowLayout());
        Image image1 = new ImageIcon("orangeGhost.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        Image image2 = new ImageIcon("redGhost.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        Image image3 = new ImageIcon("greenGhost.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        Image image4 = new ImageIcon("pinkGhost.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel label1 = new JLabel(new ImageIcon(image1));
        JLabel label2 = new JLabel(new ImageIcon(image2));
        JLabel label3 = new JLabel(new ImageIcon(image3));
        JLabel label4 = new JLabel(new ImageIcon(image4));
        imagesPanel.add(label1);
        imagesPanel.add(label2);
        imagesPanel.add(label3);
        imagesPanel.add(label4);

        add(title, BorderLayout.NORTH);
        add(buttons, BorderLayout.SOUTH);
        add(imagesPanel, BorderLayout.CENTER);
    }
}
