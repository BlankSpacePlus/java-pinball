package com.game.pinball;

import static com.game.pinball.Constant.BALL_SIZE;
import static com.game.pinball.Constant.RACKET_HEIGHT;
import static com.game.pinball.Constant.RACKET_WIDTH;
import static com.game.pinball.Constant.RACKET_Y;
import static com.game.pinball.Constant.TABLE_HEIGHT;
import static com.game.pinball.Constant.TABLE_WIDTH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * 游戏主类
 */
public class PinballFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * 随机数生成器对象
     */
    private final Random random;

    /**
     * 一个[-0.5,0.5)之间的比率，用于控制小球的运行方向
     */
    private double xyRate;

    /**
     * 小球运行速度
     */
    private int ySpeed, xSpeed;

    /**
     * 小球的坐标
     */
    private int ballY, ballX;

    /**
     * 球拍的横坐标
     */
    private int racketX;

    /**
     * 标记游戏是否结束
     */
    private boolean isLose;

    /**
     * Canvas对象
     */
    private final GameCanvas canvas;

    /**
     * Timer定时器，一个后台线程计划执行指定任务
     */
    private Timer timer;

    public PinballFrame() {
        super("弹球游戏");
        this.random = new Random();
        this.canvas = new GameCanvas(g -> {
            if (this.isLose) {
                g.setColor(new Color(255, 0, 0));
                g.setFont(new Font("Times", Font.BOLD, 30));
                int option = JOptionPane.showConfirmDialog(this, "是否重新开始？", "游戏结束", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    this.startGame();
                }
            } else {
                // 设置颜色，并绘制小球
                g.setColor(new Color(255, 70, 0));
                g.fillOval(this.ballX, this.ballY, BALL_SIZE, BALL_SIZE);
                // 设置颜色，并绘制球拍
                g.setColor(new Color(25, 25, 110));
                g.fillRect(this.racketX, RACKET_Y, RACKET_WIDTH, RACKET_HEIGHT);
            }
        });
        this.init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        // 设置图标
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("../../../pinball.jpg")));
        this.setIconImage(imageIcon.getImage());
        // 设置桌面区域的最佳大小
        this.canvas.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        // Canvas添加到JFrame
        this.add(this.canvas);
        // 定义键盘监听器
        KeyAdapter keyProcessor = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                // 按下向左、向右键时，球拍水平坐标分别减少、增加
                if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (PinballFrame.this.racketX > 0) {
                        PinballFrame.this.racketX -= 10;
                    }
                } else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (PinballFrame.this.racketX < TABLE_WIDTH - RACKET_WIDTH) {
                        PinballFrame.this.racketX += 10;
                    }
                }
            }
        };
        // 为JFrame对象分别添加键盘事件监听器
        this.addKeyListener(keyProcessor);
        // 为Canvas对象分别添加键盘事件监听器
        this.canvas.addKeyListener(keyProcessor);
        // 根据窗口里面的布局及组件的preferredSize来确定JFrame的最佳大小
        this.pack();
        // JFrame窗体居中
        this.setLocationRelativeTo(null);
        // 设置窗体关闭无反应
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // 设置关闭事件监听器
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(PinballFrame.this, "确定退出游戏？", "退出游戏", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION && e.getWindow() == PinballFrame.this) {
                    PinballFrame.this.dispose();
                    System.exit(0);
                }
            }
        });
        // JFrame设置可见
        this.setVisible(true);
        this.startGame();
    }

    /**
     * 游戏启动
     */
    private void startGame() {
        this.xyRate = random.nextDouble() - 0.5;
        this.ySpeed = 12;
        this.xSpeed = (int) (ySpeed * xyRate * 2);
        this.ballY = random.nextInt(10) + 20;
        this.ballX = random.nextInt(200) + 20;
        this.racketX = random.nextInt(200);
        this.isLose = false;
        // 创建每0.1秒执行一次的事件监听器
        this.timer = new Timer(100, e -> {
            // 如果小球碰到左边边框
            if (this.ballX <= 0 || this.ballX >= TABLE_WIDTH - BALL_SIZE) {
                this.xSpeed = -this.xSpeed;
            }
            // 如果小球高度超越了球拍位置，且横向不在球拍范围之内，游戏结束
            if (this.ballY >= RACKET_Y - BALL_SIZE &&
                    (this.ballX < this.racketX || this.ballX > this.racketX + RACKET_WIDTH)) {
                // 计时停止
                this.timer.stop();
                // 设置游戏是否结束的旗标为true
                this.isLose = true;
                // 重新绘制
                this.canvas.repaint();
            } else if (this.ballY <= 0 || (this.ballY > RACKET_Y - BALL_SIZE &&
                    this.ballX > racketX && this.ballX <= racketX + RACKET_WIDTH)) {
                // 如果小球位于球拍之内，且到达球拍位置，球反弹
                this.ySpeed = -this.ySpeed;
            }
            // 小球纵坐标增加
            this.ballY += this.ySpeed;
            // 小球横坐标增加
            this.ballX += this.xSpeed;
            // 重新绘制
            this.canvas.repaint();
        });
        // 开始计时
        this.timer.start();
    }

}
