import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class MineSweeper implements ActionListener, MouseListener {

    private int difficulty = 0, mines = 10, numRows = 9, numCols = 9;
    private JMenuItem beginner, intermediate, expert, Default, Modern, Cool;
    private JToggleButton[][] grid;
    private JPanel gridPanel;
    private JFrame frame;
    private ImageIcon mineIcon, resetIcon, flagIcon;
    private Timer timer = new Timer();
    private JButton reset;
    private JTextField mineCount, time;
    private boolean firstClick = true;

    private MineSweeper() {
        frame = new JFrame();
        frame.setTitle("Sreekar's Awesome Minesweeper");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mineIcon = new ImageIcon("sprites/mine.png");
        mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        flagIcon = new ImageIcon("sprites/flagged.png");
        flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        resetIcon = new ImageIcon("sprites/smiley.png");
        resetIcon = new ImageIcon(resetIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        JMenuBar bar = new JMenuBar();
        frame.setJMenuBar(bar);

        JMenu game = new JMenu("Game");
        bar.add(game);

        beginner = new JMenuItem("Beginner");
        beginner.addActionListener(this);
        intermediate = new JMenuItem("Intermediate");
        intermediate.addActionListener(this);
        expert = new JMenuItem("Expert");
        expert.addActionListener(this);
        game.add(beginner);
        game.add(intermediate);
        game.add(expert);

        JMenu icons = new JMenu("Icons");
        bar.add(icons);

        Default = new JMenuItem("Default");
        Default.addActionListener(this);
        Modern = new JMenuItem("Modern");
        Modern.addActionListener(this);
        Cool = new JMenuItem("Cool");
        Cool.addActionListener(this);
        icons.add(Default);
        icons.add(Modern);
        icons.add(Cool);

        JMenu controls = new JMenu("Controls");
        JTextArea t = new JTextArea();
        t.setBackground(Color.BLACK);
        t.setForeground(Color.YELLOW);
        t.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        t.setText(" Left-click an empty square to reveal it ! \n Right click to flag !\n Press the smiley to reset !\n GOOD LUCK !");
        t.setEditable(false);
        controls.add(t);
        bar.add(controls);

        JPanel scoreboard = new JPanel();
        frame.add(scoreboard, BorderLayout.PAGE_START);
        scoreboard.setLayout(new GridLayout(1, 3));
        scoreboard.setPreferredSize(new Dimension(1000, 50));
        scoreboard.setBackground(Color.BLACK);

        reset = new JButton();
        reset.setFocusPainted(false);
        reset.setBackground(Color.BLACK);
        reset.setBorder(BorderFactory.createBevelBorder(0));
        reset.addActionListener(this);

        mineCount = new JTextField();
        mineCount.setBackground(Color.BLACK);
        mineCount.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        mineCount.setForeground(Color.RED);
        mineCount.setHorizontalAlignment(JTextField.CENTER);
        mineCount.setEditable(false);
        mineCount.setBorder(BorderFactory.createBevelBorder(0));

        time = new JTextField();
        time.setBackground(Color.BLACK);
        time.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        time.setForeground(Color.RED);
        time.setHorizontalAlignment(JTextField.CENTER);
        time.setEditable(false);
        time.setBorder(BorderFactory.createBevelBorder(0));

        scoreboard.add(mineCount);
        scoreboard.add(reset);
        scoreboard.add(time);

        createGrid(difficulty);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MineSweeper();
    }

    private void createGrid(int difficulty) {

        switch (difficulty) {
            case 0:
                numRows = 9;
                numCols = 9;
                mines = 10;
                break;
            case 1:
                numRows = 16;
                numCols = 16;
                mines = 40;
                break;
            case 2:
                numRows = 16;
                numCols = 30;
                mines = 99;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + difficulty);
        }

        if (gridPanel != null) frame.remove(gridPanel);
        grid = new JToggleButton[numRows][numCols];
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(numRows, numCols));
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                grid[row][col] = new JToggleButton();
                grid[row][col].addMouseListener(this);
                grid[row][col].putClientProperty("column", col);
                grid[row][col].putClientProperty("row", row);
                grid[row][col].putClientProperty("state", 0);
                grid[row][col].setFocusPainted(false);
                grid[row][col].setBackground(Color.LIGHT_GRAY);
                grid[row][col].setFont(new Font("Comic Sans MS", Font.BOLD, 24));
                grid[row][col].setBorder(BorderFactory.createBevelBorder(1));
                grid[row][col].setSelected(false);
                gridPanel.add(grid[row][col]);
            }
        }
        reset.setIcon(resetIcon);
        mineCount.setText("" + mines);
        time.setText("00:00");
        firstClick = true;
        timer.cancel();
        frame.setSize(40 * numCols, 40 * numRows);
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.revalidate();
    }


    private void dropMines(int cR, int cC) {
        int count = mines;

        while (count > 0) {
            int row = (int) (Math.random() * numRows);
            int col = (int) (Math.random() * numCols);
            int state = Integer.parseInt("" + grid[row][col].getClientProperty("state"));

            if (state == 0 && (Math.abs(row - cR) > 1 || Math.abs(col - cC) > 1)) {
                grid[row][col].putClientProperty("state", 9);
                count--;
            }

        }

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                count = 0;
                int currToggle = Integer.parseInt("" + grid[r][c].getClientProperty("state"));
                if (currToggle != 9) {
                    for (int x = r - 1; x <= r + 1; x++) {
                        for (int y = c - 1; y <= c + 1; y++) {
                            try {
                                int toggleState = Integer.parseInt("" + grid[x][y].getClientProperty("state"));
                                if (toggleState == 9 && !(x == r && y == c)) count++;
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                        }
                    }
                    grid[r][c].putClientProperty("state", count);
                }
            }
        }

    }

    public void expand(int row, int col) {
        if (!grid[row][col].isSelected()) grid[row][col].setSelected(true);
        grid[row][col].setIcon(null);

        int state = Integer.parseInt("" + grid[row][col].getClientProperty("state"));
        if (state > 0) writeText(row, col, state);
        else {

            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (!(r == row && c == col)) {
                        try {
                            if (!grid[r][c].isSelected()) expand(r, c);
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }

                }
            }

        }

    }

    public void checkWin() {
        int totalSpaces = numRows * numCols;
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                int state = Integer.parseInt("" + grid[r][c].getClientProperty("state"));
                if (grid[r][c].isSelected() && state != 9) count++;
            }
        }
        if (mines == totalSpaces - count) {
            timer.cancel();
            JOptionPane.showMessageDialog(null, "WINNER !!!");
            createGrid(difficulty);
        }

    }

    public void writeText(int r, int c, int state) {

        switch (state) {
            case 1:
                grid[r][c].setForeground(Color.BLUE);
                break;
            case 2:
                grid[r][c].setForeground(Color.GREEN);
                break;
            case 3:
                grid[r][c].setForeground(Color.RED);
                break;
            case 4:
                grid[r][c].setForeground(new Color(128, 0, 128));
                break;
            case 5:
                grid[r][c].setForeground(new Color(128, 0, 0));
                break;
            case 6:
                grid[r][c].setForeground(Color.CYAN);
                break;
            case 7:
                grid[r][c].setForeground(Color.BLACK);
                break;
            case 8:
                grid[r][c].setForeground(Color.GRAY);
                break;
            case 9:
                grid[r][c].setIcon(mineIcon);
                grid[r][c].setText("");
                break;
        }
        if (state != 9) grid[r][c].setText("" + state);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == beginner) difficulty = 0;
        else if (e.getSource() == intermediate) difficulty = 1;
        else if (e.getSource() == expert) difficulty = 2;
        else if (e.getSource() == Modern) {
            mineIcon = new ImageIcon("sprites/newmine.png");
            mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));

            flagIcon = new ImageIcon("sprites/newflag.png");
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));

            resetIcon = new ImageIcon("sprites/newsmiley.png");
            resetIcon = new ImageIcon(resetIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

            mineCount.setForeground(Color.MAGENTA);
            time.setForeground(Color.MAGENTA);

        } else if (e.getSource() == Default) {
            mineIcon = new ImageIcon("sprites/mine.png");
            mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

            flagIcon = new ImageIcon("sprites/flagged.png");
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

            resetIcon = new ImageIcon("sprites/smiley.png");
            resetIcon = new ImageIcon(resetIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

            mineCount.setForeground(Color.RED);
            time.setForeground(Color.RED);

        } else if (e.getSource() == Cool) {
            mineIcon = new ImageIcon("sprites/mine3.png");
            mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));

            flagIcon = new ImageIcon("sprites/flag3.png");
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));

            resetIcon = new ImageIcon("sprites/smiley3.png");
            resetIcon = new ImageIcon(resetIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

            mineCount.setForeground(Color.GREEN);
            time.setForeground(Color.GREEN);

        }
        createGrid(this.difficulty);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {


        int row = Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("row"));
        int col = Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("column"));

        if (e.getButton() == MouseEvent.BUTTON1 && grid[row][col].getIcon() == null) {

            if (firstClick) {
                dropMines(row, col);
                firstClick = false;
                timer = new Timer();
                Instant start = Instant.now();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Duration diff = Duration.between(start, Instant.now());
                        String t = String.format("%02d:%02d", diff.toMinutesPart(), diff.toSecondsPart());
                        time.setText(String.valueOf(t));
                    }
                }, 0, 500);

            }

            int state = Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("state"));

            if (state == 9) {
                grid[row][col].setSelected(true);
                grid[row][col].setIcon(mineIcon);
                timer.cancel();
                JOptionPane.showMessageDialog(null, "LOSER !!!");
                createGrid(difficulty);
            } else {
                expand(row, col);
                checkWin();
            }


        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (grid[row][col].getIcon() == null && grid[row][col].getText().isEmpty() && !grid[row][col].isSelected()) {
                if (Integer.parseInt(mineCount.getText()) > 0) {
                    grid[row][col].setIcon(flagIcon);
                    mineCount.setText(String.valueOf(Integer.parseInt(mineCount.getText()) - 1));
                }
            } else {
                grid[row][col].setIcon(null);
                if (!grid[row][col].isSelected())
                    mineCount.setText(String.valueOf(Integer.parseInt(mineCount.getText()) + 1));
            }
        }


    }


    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}