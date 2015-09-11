/**
 * @author Adhitya Vadivel
 * @date 26 May 2014
 * @version 1.0
 */

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.awt.image.*;
import java.net.*;
import javax.imageio.ImageIO;

public class MinesweeperAVadivel extends JFrame implements ActionListener, MouseListener {
    JMenuBar menu;
    JMenu gameOp, optionsOp, helpOp;
    JMenuItem howToPlay, aboutGame, exitGame, resetGame, mineCount;
    JTextPane howToPlayText, aboutGameText;
    JScrollPane howToPlayScroll, aboutScroll;
    javax.swing.Timer timer;
    JPanel window, gamePanel, minesBox, statBox, timerBox, bckGrnd;
    JLabel timerLabel, minesLabel;
    JButton resetButton;
    BufferedImage tile, blank, one, two, three, four, five, bomb, bombFlagged, bombQuestion;
    Container playArea;
    GridLayout canvasLayout;
    GridBox[][] grid = new GridBox[10][10];
    char[][] board = new char[10][10];
    int currentClick = 0, countCells = 0, time = 0, numMines = 10;
    boolean endGame = false;
    public static void main(String[] args) {
        MinesweeperAVadivel game = new MinesweeperAVadivel();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setSize(1000, 1000);
        game.setVisible(true);
    }
    public MinesweeperAVadivel(){
        super("Colorful Minesweeper");
        playArea = getContentPane();
        playArea.setLayout(new BorderLayout());
        window = new JPanel();
        window.setBackground(Color.RED);
        window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
        try {
            tile = ImageIO.read(new File("tile.png"));
            blank = ImageIO.read(new File("blank.png"));
            one = ImageIO.read(new File("one.png"));
            two = ImageIO.read(new File("two.png"));
            three = ImageIO.read(new File("three.png"));
            four = ImageIO.read(new File("four.png"));
            five = ImageIO.read(new File("five.png"));
            bomb = ImageIO.read(new File("bomb.png"));
            bombFlagged = ImageIO.read(new File("flag.png"));
            bombQuestion = ImageIO.read(new File("questionMark.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer = new javax.swing.Timer(1000, this);
        menu = new JMenuBar();
        gameOp = new JMenu("Game");
        menu.add(gameOp);
        optionsOp = new JMenu("Options");
        menu.add(optionsOp);
        helpOp = new JMenu("Help");
        menu.add(helpOp);
        resetGame = new JMenuItem("Reset");
        resetGame.addActionListener(this);
        gameOp.add(resetGame);
        exitGame = new JMenuItem("Exit");
        exitGame.addActionListener(this);
        gameOp.add(exitGame);
        mineCount = new JMenuItem("Mine Count");
        mineCount.addActionListener(this);
        optionsOp.add(mineCount);
        howToPlay = new JMenuItem("Instructions");
        howToPlay.addActionListener(this);
        helpOp.add(howToPlay);
        howToPlayText = new JTextPane();
        try {
            howToPlayText.setPage(new URL("file:instructions.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        howToPlayText.setEditable(false);
        howToPlayScroll = new JScrollPane(howToPlayText);
        howToPlayScroll.setMaximumSize(new Dimension(400, 400));
        howToPlayScroll.setMinimumSize(new Dimension(400, 400));
        howToPlayScroll.setPreferredSize(new Dimension(400, 400));
        aboutGame = new JMenuItem("About");
        aboutGame.addActionListener(this);
        helpOp.add(aboutGame);
        aboutGameText = new JTextPane();
        try {
            aboutGameText.setPage(new URL("file:aboutGame.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        aboutGameText.setEditable(false);
        aboutScroll = new JScrollPane(aboutGameText);
        aboutScroll.setMaximumSize(new Dimension(200, 200));
        aboutScroll.setMinimumSize(new Dimension(200, 200));
        aboutScroll.setPreferredSize(new Dimension(200, 200));
        playArea.add(menu, BorderLayout.NORTH);
        playArea.add(window, BorderLayout.CENTER);
        window.add(Box.createRigidArea(new Dimension(0, 20)));
        canvasLayout = new GridLayout(grid.length, grid[0].length);
        gamePanel = new JPanel();
        gamePanel.setBackground(Color.RED);
        gamePanel.setMaximumSize(new Dimension(400, 400));
        gamePanel.setMinimumSize(new Dimension(400, 400));
        gamePanel.setPreferredSize(new Dimension(400, 400));
        gamePanel.setLayout(canvasLayout);
        for(int row = 0; row < grid.length; row++){
            for(int col = 0; col < grid[0].length; col++){
                GridBox pWhite = new GridBox(row, col);
                pWhite.addMouseListener(this);
                grid[row][col] = pWhite;
                gamePanel.add(grid[row][col]);
            }
        }
        statBox = new JPanel();
        statBox.setBackground(Color.RED);
        statBox.setLayout(new GridLayout(1, 3));
        statBox.setMaximumSize(new Dimension(400, 75));
        statBox.setMinimumSize(new Dimension(400, 75));
        statBox.setPreferredSize(new Dimension(400, 75));
        timerBox = new JPanel();
        timerBox.setBackground(Color.YELLOW);
        timerBox.setLayout(new BorderLayout());
        timerBox.setBorder(new TitledBorder("Time"));
        timerLabel = new JLabel("" + time, JLabel.CENTER);
        timerBox.add(timerLabel, BorderLayout.CENTER);
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        minesBox = new JPanel();
        minesBox.setBackground(Color.YELLOW);
        minesBox.setLayout(new BorderLayout());
        minesBox.setBorder(new TitledBorder("Total Mines"));
        minesLabel = new JLabel("" + numMines, JLabel.CENTER);
        minesBox.add(minesLabel, BorderLayout.CENTER);
        statBox.add(resetButton);
        statBox.add(minesBox);
        statBox.add(timerBox);
        window.add(gamePanel);
        window.add(Box.createRigidArea(new Dimension(0,20)));
        window.add(statBox);
        window.add(Box.createRigidArea(new Dimension(0, 10)));
        boardGenerator();
    }
    public void boardGenerator(){
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board[0].length; c++){
                board[r][c] = '0';
            }
        }
        Random rand = new Random();
        for(int i = 0; i < numMines; i++){
            int randRow = rand.nextInt(board.length);
            int randCol = rand.nextInt(board[0].length);
            if(board[randRow][randCol] == '*'){
                i--;
            }else{
                board[randRow][randCol] = '*';
            }
        }
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board[0].length; c++){
                if(board[r][c] != '*'){
                    int num = getNumMines(r, c, board);
                    board[r][c] = Character.forDigit(num, 9);
                }
            }
        }
    }
    public int getNumMines(int row, int col, char[][] arr){
        int ans = 0;
        int[] cellSurroundingRow = {-1, -1, -1,  0, 0,  1, 1, 1};
        int[] cellSurroundingCol = {-1,  0,  1, -1, 1, -1, 0, 1};
        for(int i = 0; i < cellSurroundingRow.length; i++){
            if(isInBounds(row + cellSurroundingRow[i], col + cellSurroundingCol[i])){
                if(arr[row + cellSurroundingRow[i]][col + cellSurroundingCol[i]] == '*'){
                    ans++;
                }
            }
        }
        return ans;
    }
    public boolean isInBounds(int row, int col){
        if( (row < 10 && row >= 0) &&
                (col < 10 && col >= 0))
            return true;
        return false;
    }
    public void resetBoard(){
        addAllCellListeners();
        countCells = 0;
        timer.stop();
        time = 0;
        timerLabel.setText("" + time);
        endGame = false;
        currentClick = 0;
        for(int r = 0; r < grid.length; r++){
            for(int c = 0; c < grid[0].length; c++){
                grid[r][c].isClicked = false;
            }
        }
        repaint();
        boardGenerator();
        repaint();
    }
    public void endOfGame(){
        if(endGame == true){
            repaint();
            JOptionPane.showMessageDialog(null, "Game over!  Time: " + time, "Oh-no!", JOptionPane.PLAIN_MESSAGE);
            removeAllCellListeners();
        }
        countCells = 0;
    }
    public void click(int row, int col){
        if(grid[row][col].rightClickCount >= 1){
            return;
        }
        if(board[row][col] != '*'){
            currentClick = 1;
            grid[row][col].isClicked = true;
            if(board[row][col] == '0'){
                for(int i = row - 1; i <= row + 1; i++){
                    for(int j = col - 1; j <= col + 1; j++){
                        try{
                            if(grid[i][j].rightClickCount <= 1 && (i != row || j != col) && board[i][j] != '*' && grid[i][j].isClicked == false){
                                currentClick = 1;
                                grid[i][j].isClicked = true;
                                grid[i][j].repaint();
                                click(i, j);
                            }
                        }catch(ArrayIndexOutOfBoundsException e1){continue;}
                    }
                }
            }
            grid[row][col].repaint();
            grid[row][col].removeMouseListener(this);
        }else{
            endGame = true;
            endOfGame();
            return;
        }
    }
    public void removeAllCellListeners(){
        for(int r = 0; r < grid.length; r++){
            for(int c = 0; c < grid[0].length; c++){
                grid[r][c].removeMouseListener(this);
            }
        }
    }
    public void addAllCellListeners(){
        for(int r = 0; r < grid.length; r++){
            for(int c = 0; c < grid[0].length; c++){
                grid[r][c].addMouseListener(this);
            }
        }
    }
    public void gameWin(){
        if(countCells == numMines){
            timer.stop();
            JOptionPane.showMessageDialog(null, "Congratulations!  You won!  Your final time was: " + time + " seconds.  \nClick 'Reset' to start a new game!", "You Won", JOptionPane.PLAIN_MESSAGE);
            removeAllCellListeners();
            countCells = 0;
        }else{
            return;
        }
    }
    public void winCheck(int row, int col){
        if(grid[row][col].rightClickCount <= 1 && board[row][col] == '*' && grid[row][col].countedForWin == false){
            grid[row][col].countedForWin = true;
            countCells++;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer){
            time++;
            timerLabel.setText(time + "");
        }
        if(e.getSource() == exitGame){
            System.exit(0);
        }
        if(e.getSource() == resetButton || e.getSource() == resetGame){
            resetBoard();
        }
        if(e.getSource() == howToPlay){
            JOptionPane.showMessageDialog(null, howToPlayScroll, "How To Play", JOptionPane.PLAIN_MESSAGE);
        }
        if(e.getSource() == aboutGame){
            JOptionPane.showMessageDialog(null, aboutScroll, "About This Application", JOptionPane.PLAIN_MESSAGE);
        }
        if(e.getSource() == mineCount){
            String mines = JOptionPane.showInputDialog(null, "", "Set Total Number of Mines", JOptionPane.PLAIN_MESSAGE);
            if(mines != null || !mines.equals(""))
                numMines = Integer.parseInt(mines);
            minesLabel.setText("" + numMines);
            resetBoard();
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        GridBox tempPanel = (GridBox) e.getSource();
        if( (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK){
            currentClick = 1;
            click(tempPanel.row, tempPanel.col);
            winCheck(tempPanel.row, tempPanel.col);
            gameWin();
        } else if((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK){
            currentClick = 3;
            grid[tempPanel.row][tempPanel.col].repaint();
            winCheck(tempPanel.row, tempPanel.col);
            gameWin();
        }
    }
    private class GridBox extends JPanel {
        int row = 0, col = 0, rightClickCount = 0;
        boolean isClicked = false, countedForWin = false, isDisabled = false;
        public GridBox(int r, int c){
            row = r;
            col = c;
        }
        public void drawImages(Graphics2D g2){
            switch(board[row][col]){
                case '0':
                    g2.drawImage(blank, 0, 0, 40, 40, null);
                    break;
                case '1':
                    g2.drawImage(one, 0, 0, 40, 40, null);
                    break;
                case '2':
                    g2.drawImage(two, 0, 0, 40, 40, null);
                    break;
                case '3':
                    g2.drawImage(three, 0, 0, 40, 40, null);
                    break;
                case '4':
                    g2.drawImage(four, 0, 0, 40, 40, null);
                    break;
                case '5':
                    g2.drawImage(five, 0, 0, 40, 40, null);
                    break;
                case '*':
                    g2.drawImage(bomb, 0, 0, 40, 40, null);
                    break;
            }
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if(endGame == true){
                isClicked = false;
                timer.stop();
                drawImages(g2);
            }else{
                if(currentClick == 0){
                    g2.drawImage(tile, 0, 0, 40, 40, null);
                }
                else if(currentClick == 1){
                    isClicked = true;
                    if(!timer.isRunning()){
                        timer.start();
                    }
                    switch(board[row][col]){
                        case '0':
                            g2.drawImage(blank, 0, 0, 40, 40, null); isDisabled = true;
                            break;
                        case '1':
                            g2.drawImage(one, 0, 0, 40, 40, null); isDisabled = true;
                            break;
                        case '2':
                            g2.drawImage(two, 0, 0, 40, 40, null); isDisabled = true;
                            break;
                        case '3':
                            g2.drawImage(three, 0, 0, 40, 40, null); isDisabled = true;
                            break;
                        case '4':
                            g2.drawImage(four, 0, 0, 40, 40, null); isDisabled = true;
                            break;
                        case '5':
                            g2.drawImage(five, 0, 0, 40, 40, null); isDisabled = true;
                            break;
                        case '*':
                            g2.drawImage(bomb, 0, 0, 40, 40, null);
                            endGame = true;
                            endOfGame();
                            g2.drawImage(bomb, 0, 0, 40, 40, null);
                            break;
                    }
                }
                else if(currentClick == 3){
                    rightClickCount++;
                    if(rightClickCount > 2){
                        rightClickCount = 0;
                    }
                    isClicked = true;
                    if(rightClickCount == 0){
                        g2.drawImage(tile, 0, 0, 40, 40, null);
                        isClicked = false;
                        if(countedForWin == true){
                            countCells--;
                            countedForWin = false;
                        }
                    } else if(rightClickCount == 1){
                        g2.drawImage(bombFlagged, 0, 0, 40, 40, null);
                    } else if(rightClickCount == 2){
                        g2.drawImage(bombQuestion, 0, 0, 40, 40, null);
                    }
                }
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseReleased(MouseEvent e) { }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}