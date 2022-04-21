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
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * 游戏主类
 */
public class Game {

    /**
     * JFrame对象
     */
    private final JFrame frame = new JFrame("弹球游戏");

    /**
     * 随机数生成器对象
     */
    private final Random random = new Random();

    /**
     * 一个[-0.5,0.5)之间的比率，用于控制小球的运行方向
     */
    private final double xyRate = random.nextDouble() - 0.5;

    /**
     * 小球纵向运行速度，只变符号，不变数值
     */
    private int ySpeed = 10;

    /**
     * 小球横向运行速度，可变
     */
    private int xSpeed = (int) (ySpeed * xyRate * 2);

    /**
     * 小球的纵坐标
     */
    private int ballY = random.nextInt(10) + 20;

    /**
     * 小球的横坐标
     */
    private int ballX = random.nextInt(200) + 20;

    /**
     * racketX代表球拍的水平位置
     */
    private int racketX = random.nextInt(200);

    /**
     * 游戏是否结束的旗标
     */
    private boolean isLose = false;

    /**
     * Consumer对象Lambda表达式
     */
    private final Consumer<Graphics> consumer = g -> {
        if (this.isLose) {
            // 寄
            g.setColor(new Color(255, 0, 0));
            g.setFont(new Font("Times", Font.BOLD, 30));
            g.drawString("游戏已结束", 50, 200);
        } else {
            // 设置颜色，并绘制小球
            g.setColor(new Color(255, 69, 0));
            g.fillOval(this.ballX, this.ballY, BALL_SIZE, BALL_SIZE);
            // 设置颜色，并绘制球拍
            g.setColor(new Color(25, 25, 112));
            g.fillRect(this.racketX, RACKET_Y, RACKET_WIDTH, RACKET_HEIGHT);
        }
    };

    /**
     * Canvas对象
     */
    private final MyCanvas canvas = new MyCanvas(consumer);

    /**
     * Timer定时器，一个后台线程计划执行指定任务
     */
    private Timer timer;

    /**
     * 初始化操作
     */
    private void init() {
        // 设置桌面区域的最佳大小
        this.canvas.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        // Canvas添加到JFrame
        this.frame.add(this.canvas);
        // 定义键盘监听器
        KeyAdapter keyProcessor = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                // 按下向左、向右键时，球拍水平坐标分别减少、增加
                if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (Game.this.racketX > 0) {
                        Game.this.racketX -= 10;
                    }
                } else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (Game.this.racketX < TABLE_WIDTH - RACKET_WIDTH) {
                        Game.this.racketX += 10;
                    }
                }
            }
        };
        // 为JFrame对象分别添加键盘事件监听器
        this.frame.addKeyListener(keyProcessor);
        // 为Canvas对象分别添加键盘事件监听器
        this.canvas.addKeyListener(keyProcessor);
        // 定义每0.1秒执行一次的事件监听器
        ActionListener taskPerformer = event -> {
            // 如果小球碰到左边边框
            if (this.ballX <= 0 || this.ballX >= TABLE_WIDTH - BALL_SIZE) {
                this.xSpeed = - this.xSpeed;
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
                this.ySpeed = - this.ySpeed;
            }
            // 小球纵坐标增加
            this.ballY += this.ySpeed;
            // 小球横坐标增加
            this.ballX += this.xSpeed;
            // 重新绘制
            this.canvas.repaint();
        };
        // 定时器初始化
        this.timer = new Timer(100, taskPerformer);
        // 开始计时
        this.timer.start();
        // 根据窗口里面的布局及组件的preferredSize来确定JFrame的最佳大小
        this.frame.pack();
        // 点击×即可退出程序
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // JFrame窗体居中
        this.frame.setLocationRelativeTo(null);
        // JFrame设置可见
        this.frame.setVisible(true);
    }

    public Game() {
        this.init();
    }

}
