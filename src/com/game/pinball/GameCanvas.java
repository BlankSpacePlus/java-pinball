package com.game.pinball;

import java.awt.Canvas;
import java.awt.Graphics;
import java.util.function.Consumer;

/**
 * Canvas组件实现
 */
public class GameCanvas extends Canvas {

    private static final long serialVersionUID = 1L;

    private final Consumer<Graphics> consumer;

    /**
     * 通过传入consumer参数匿名函数实现绘画功能
     *
     * @param consumer 匿名paint方法
     */
    public GameCanvas(Consumer<Graphics> consumer) {
        this.consumer = consumer;
    }

    /**
     * 重写Canvas的paint()方法以实现绘画
     *
     * @param g Graphics对象
     */
    @Override
    public void paint(Graphics g) {
        consumer.accept(g);
    }

}
