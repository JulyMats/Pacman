import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class HighScoresPanel extends JPanel implements Serializable{

    ArrayList<Score> scores;
    HighScoreListModel highScoreListModel;

    public HighScoresPanel() {

        setLayout(new BorderLayout());
        scores = new ArrayList<>();
        highScoreListModel = new HighScoreListModel(scores);

        JList highScoresList = new JList(highScoreListModel);

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.add(highScoresList);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        JButton removeButton = new JButton("remove");
        JButton menuButton = new JButton("menu");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = highScoresList.getSelectedIndex();
               /* if(index >=0) {
                }*/
            }
        });

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(menuButton).dispose();
            }
        });
        panel.add(removeButton);
        panel.add(menuButton);

        add(jScrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    public void addScore(Score score) {
        highScoreListModel.add(score);
        saveHighScore(score);
    }

    public void saveHighScore(Score score) {
        try {
            FileOutputStream fileOut = new FileOutputStream("highScores.ser", true);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(score);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadHighScores() {
        try {
            FileInputStream fileIn = new FileInputStream("highScores.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            highScoreListModel.add((Score) in.readObject());
            System.out.println(highScoreListModel.toString());
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class HighScoreListModel extends AbstractListModel implements Serializable{
    ArrayList<Score> scores;

    public HighScoreListModel(ArrayList<Score> scores) {
        this.scores = scores;
    }

    @Override
    public int getSize() {
        return scores.size();
    }

    @Override
    public Object getElementAt(int index) {
        return scores.get(index);
    }

    public void add(Score score) {
        scores.add(score);
    }

    /*public void clear() {
       for (Score score : scores) {
           scores.remove(score);
       }
    }*/

    @Override
    public String toString() {
        String str = " ";

        for (Score score : scores) {
            str += (score.toString()) + "," ;
        }
        return str;
    }
}

class Score implements Serializable {
    private int score;
    private String nickName;

    public Score(String nickName, int score) {
        this.nickName = nickName;
        this.score = score;
    }

    public Score(int score) {
        this.nickName = "Unknown";
        this.score = score;
    }

    @Override
    public String toString() {
        return score + " " + nickName;
    }
}
