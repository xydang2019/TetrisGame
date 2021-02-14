package com.bzyd.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 俄罗斯方块
 */
public class TetrisGame extends JPanel {
    //背景图片
    private static BufferedImage background;
    //暂停图片
    private static BufferedImage pause;
    //游戏结束图片
    private static BufferedImage gameover;

    public static BufferedImage T;
    public static BufferedImage S;
    public static BufferedImage I;
    public static BufferedImage L;
    public static BufferedImage J;
    public static BufferedImage O;
    public static BufferedImage Z;

    private static int[] scoreTable = {0, 10, 50, 80, 200};//计分板

    public static final int ROWS = 20;//背景墙行数
    public static final int COLS = 10;//背景墙列数
    public static final int CELL_SIZE = 26;//格子大小
    public static final int FONT_COLOR = 0x667799;//字体颜色
    public static final int FONT_SIZE = 24;//字体大小
    public static final int RUNNING = 0;//运行
    public static final int PAUSE = 1;//暂停
    public static final int GAME_OVER = 2;//游戏结束

    static {
        try {
            background = ImageIO.read(TetrisGame.class.getResource("/tetris.png"));
            pause = ImageIO.read(TetrisGame.class.getResource("/pause.png"));
            gameover = ImageIO.read(TetrisGame.class.getResource("/gameover.png"));
            T = ImageIO.read(TetrisGame.class.getResource("/T.png"));
            S = ImageIO.read(TetrisGame.class.getResource("/S.png"));
            I = ImageIO.read(TetrisGame.class.getResource("/I.png"));
            L = ImageIO.read(TetrisGame.class.getResource("/L.png"));
            J = ImageIO.read(TetrisGame.class.getResource("/J.png"));
            O = ImageIO.read(TetrisGame.class.getResource("/O.png"));
            Z = ImageIO.read(TetrisGame.class.getResource("/Z.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int score;//分数
    private int lines;//销毁的行数
    private Cell[][] wall;//背景墙
    private Tetromino tetromino;//正在下落的四块方格
    private Tetromino nextOne;//下一个四格方块
    private Timer timer;//定时器
    private int intervel = 10;//时间间隔10毫秒
    private int state;//游戏状态
    private int speed;//下落速度
    private int level;//难道级别
    private int index;//下落计数器，当index%speed==0时候下落一次

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);//画背景
        g.translate(15, 15);//坐标系平移
        paintWall(g);//画墙
        paintTetromino(g);//画正在下落的四块方格
        paintNextOne(g);//画下一个要下落的四块方块
        paintScore(g);//绘制分数
        paintState(g);//画游戏状态
    }

    /**
     * 画墙
     *
     * @param g 画笔
     */
    private void paintWall(Graphics g) {
        for (int row = 0; row < wall.length; row++) {
            Cell[] line = wall[row];//每一行row
            for (int col = 0; col < line.length; col++) {
                Cell cell = line[col];//每行中的每个格子
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                if (cell == null) {
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);//空白部分绘制边框
                } else {
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    /**
     * 画正在下落的方块
     *
     * @param g 画笔
     */
    private void paintTetromino(Graphics g) {
        if (tetromino == null) {
            return;
        }
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            //i = 1,2,3,4
            Cell cell = cells[i];
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }

    }

    /**
     * 画下一个要下落的方块
     *
     * @param g 画笔
     */
    private void paintNextOne(Graphics g) {
        if (nextOne == null) {
            return;
        }
        Cell[] cells = nextOne.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            //即将下落的四块方格的位置在右上角
            int x = (cell.getCol() + 10) * CELL_SIZE;
            int y = (cell.getRow() + 1) * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    /**
     * 画分数
     *
     * @param g 画笔
     */
    private void paintScore(Graphics g) {
        int x = 290;
        int y = 160;
        g.setColor(new Color(FONT_COLOR));//给画笔设置字体颜色
        Font font = g.getFont();//取得画笔g当前字体
        font = new Font(font.getName(), font.getStyle(), FONT_SIZE);//重新设置字体大小
        g.setFont(font);//更改g的字体
        g.drawString("SCORE:" + score, x, y);
        y += 60;
        g.drawString("LINES:" + lines, x, y);
        y += 60;
        g.drawString("LEVEL:" + level, x, y);
    }

    /**
     * 画游戏状态
     *
     * @param g 画笔
     */
    private void paintState(Graphics g) {
        switch (state) {
            case PAUSE:
                g.drawImage(pause, -15, -15, null);
                break;
            case GAME_OVER:
                g.drawImage(gameover, -15, -15, null);
                break;
        }
    }

    /**
     * 判断是否出界
     */
    private boolean outOfBounds() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int col = cell.getCol();
            int row = cell.getRow();
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断正在下落的方块是否和墙上的方块重叠
     */
    private boolean coincide() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int col = cell.getCol();
            int row = cell.getRow();
            //如果墙上的(row,col)位置不是null，那就重叠了
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 右移
     */
    private void moveRightAction() {
        tetromino.moveRight();
        if (outOfBounds() || coincide()) {
            tetromino.moveLeft();
        }
    }

    /**
     * 左移
     */
    private void moveLeftAction() {
        tetromino.moveLeft();
        if (outOfBounds() || coincide()) {
            tetromino.moveRight();
        }
    }

    /**
     * 下落流程控制
     */
    private void softDropAction() {
        if (canDrop()) {
            tetromino.softDrop();
        } else {
            landIntoWall();
            int lines = destoryLines();
            this.lines += lines;
            this.score += scoreTable[lines];
            if (isGameOver()) {
                state = GAME_OVER;
            } else {
                tetromino = nextOne;
                nextOne = Tetromino.randomOne();//重新赋值
            }
        }
    }

    /**
     * 快速下落流程控制
     */
    private void hardDropAction() {
        while (canDrop()) {//一直循环执行下落动作
            tetromino.softDrop();
        }
        landIntoWall();
        int lines = destoryLines();
        this.lines += lines;
        this.score += scoreTable[lines];
        if (isGameOver()) {
            state = GAME_OVER;
        } else {
            tetromino = nextOne;
            nextOne = Tetromino.randomOne();//重新赋值
        }
    }

    /**
     * 右旋转
     */
    private void rotateRightAction() {
        tetromino.rotateRight();
        if (outOfBounds() || coincide()) {
            tetromino.rotateLeft();
        }
    }

    /**
     * 左旋转
     */
    private void rotateLeftAction() {
        tetromino.rotateLeft();
        if (outOfBounds() || coincide()) {
            tetromino.rotateRight();
        }
    }


    /**
     * 判断是否能下落
     */
    private boolean canDrop() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if (row == (ROWS - 1) || wall[row + 1][col] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 固定到墙上
     */
    private void landIntoWall() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    /**
     * 销毁满行
     *
     * @return lines 销毁行的数量
     */
    private int destoryLines() {
        int lines = 0;//销毁行的数量
        for (int row = 0; row < ROWS; row++) {//遍历行
            if (fullCells(row)) {
                deleteRow(row);
                lines++;
            }
        }
        return lines;
    }

    /**
     * 删除行
     */
    private void deleteRow(int row) {
        for (int i = row; i >= 1; i--) {//i从满的那行开始
            System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);//把i-1行复制到i行
        }
        Arrays.fill(wall[0], null);//把第一行全部填null值
    }


    /**
     * 判断是否满行
     */
    private boolean fullCells(int row) {
        Cell[] cells = wall[row];//行
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];//行中每格
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断游戏是否结束
     */
    private boolean isGameOver() {
        /*
         * 下一个出场方块没有位置放了，就是游戏结束
         * 即下一个出场方块每个格子行列对应的墙上如果有格子，就游戏结束
         */
        Cell[] cells = nextOne.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * RUNNING状态下中操作
     */
    private void processRunningKey(int key) {
        switch (key) {
            case KeyEvent.VK_Q:
                System.exit(0);
                break;//按q退出游戏
            case KeyEvent.VK_DOWN:
                softDropAction();
                break;//按↓下落
            case KeyEvent.VK_RIGHT:
                moveRightAction();
                break;//按→右移
            case KeyEvent.VK_LEFT:
                moveLeftAction();
                break;//按←左移
            case KeyEvent.VK_SPACE:
                hardDropAction();
                break;//按空格快速下落
            case KeyEvent.VK_UP:
                rotateRightAction();
                break;//按↑右旋转
            case KeyEvent.VK_Z:
                rotateLeftAction();
                break;//按z左旋转
            case KeyEvent.VK_P:
                state = PAUSE;
                break;//按p暂停
        }
    }

    /**
     * PAUSE状态下中操作
     */
    private void processPauseKey(int key) {
        switch (key) {
            case KeyEvent.VK_Q:
                System.exit(0);
                break;//按q退出游戏
            case KeyEvent.VK_C:
                index = 1;
                state = RUNNING;
                break;//按c继续游戏
        }
    }

    /**
     * GAME_OVER状态下中操作
     */
    private void processGameoverKey(int key) {
        switch (key) {
            case KeyEvent.VK_Q:
                System.exit(0);
                break;//按q退出游戏
            case KeyEvent.VK_S://按s重新开始游戏
                this.lines = 0;
                this.score = 0;
                this.wall = new Cell[ROWS][COLS];
                this.tetromino = Tetromino.randomOne();
                this.nextOne = Tetromino.randomOne();
                this.state = RUNNING;
                this.index = 0;
        }
    }


    /**
     * 游戏启动方法action：初始化
     */
    public void action() {
        wall = new Cell[ROWS][COLS];
        tetromino = Tetromino.randomOne();//tetromino实例化
        nextOne = Tetromino.randomOne();//nextOne
        state = RUNNING;


        /**
         * 键盘监听事件
         */
        KeyAdapter event = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {//键盘按下事件
                int key = e.getKeyCode();
                switch (state) {
                    case RUNNING:
                        processRunningKey(key);
                        break;
                    case PAUSE:
                        processPauseKey(key);
                        break;
                    case GAME_OVER:
                        processGameoverKey(key);
                        break;
                }
                repaint();

            }
        };
        this.requestFocus();//请求此 Component 获取输入焦点。
        this.addKeyListener(event);

        timer = new Timer();
        timer.schedule(new TimerTask() {//创建定时器任务
            @Override
            public void run() {
                //下落速度控制逻辑
                speed = 40 - (score / 1000);
                speed = speed <= 1 ? 1 : speed;
                level = 41 - speed;
                if (index % speed == 0) {
                    if (state == RUNNING) {
                        softDropAction();
                    }
                }
                index++;
                repaint();
            }
        }, intervel, intervel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("俄罗斯方块");
        TetrisGame tetrisGame = new TetrisGame();
        frame.add(tetrisGame);
        frame.setSize(550, 600);//设置窗口初始大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);//设置窗口初始位置
        frame.setVisible(true);//设置窗体是否显示
        tetrisGame.action();//游戏启动
    }
}
