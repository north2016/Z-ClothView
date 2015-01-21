package com.example.particle;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 
 * 模拟布料的物理结构
 * 
 * @author 白小康
 * @version 算法来自于http://html5gamedev.org/?p=1997
 */

// 继承自SurfaceView并实现SurfaceHolder.Callback接口的类
public class ParticleView extends SurfaceView implements SurfaceHolder.Callback {

	DrawThread drawThread; // 后台刷新屏幕线程
	ParticleSet particleSet; // ParticleSet对象引用

	String fps = "FPS:N/A"; // 声明帧速率字符串
	static int width = 8;// 网格宽度
	int height = 12;// 网格高度
	int window_Width;// Canvas宽度
	int window_Height;// Canvas高度

	// 边界
	static int boundsx, boundsy;// 宽度、 高度
	static int spacing = 50;// 粒子间的间隔
	public static float tear_distance = 90;// 扯断的距离
	static int gravity = 10;// 重力
	int TouchCricle = spacing / 2 - 10;// 触摸点大小

	// 构造器，初始化主要成员变量
	public ParticleView(Context context, int window_Width, int window_Height) {
		super(context); // 调用父类构造器
		this.window_Width = window_Width;
		this.window_Height = window_Height;

		boundsx = window_Width - 1;
		boundsy = window_Height - 1;

		this.getHolder().addCallback(this); // 添加Callback接口
		drawThread = new DrawThread(this, getHolder()); // 创建DrawThread对象

		particleSet = new ParticleSet(); // 创建ParticleSet对象
		particleSet.addPartical(width * height, window_Width, spacing);// 每次添加5粒
	}

	/**
	 * 鼠标状态
	 * 
	 * @author Administrator
	 * 
	 */
	public static class mouse {
		static boolean down = false;// 是否按下
		static float x = 0;// 当前x位置
		static float y = 0;// 当前y位置
		static float px = 0;// 之前x位置
		static float py = 0;// 之前y位置
	}

	// 方法：绘制屏幕
	public void doDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK); // 清屏
		ArrayList<Particle> particleSet = this.particleSet.particals; // 获得ParticleSet对象中的粒子集合对象
		Paint paint = new Paint(); // 创建画笔对象

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 手指按下时
					mouse.px = mouse.x;
					mouse.py = mouse.y;
					mouse.x = event.getX();
					mouse.y = event.getY();
					mouse.down = true;
					break;
				case MotionEvent.ACTION_MOVE:// 手指移动
					mouse.px = mouse.x;
					mouse.py = mouse.y;
					mouse.x = (int) event.getX();
					mouse.y = (int) event.getY();
					break;
				case MotionEvent.ACTION_UP:// 手指抬起
					mouse.down = false;
					break;
				default:
					break;

				}
				return true;
			}
		});

		for (int i = 0; i < particleSet.size(); i++) { // 遍历粒子集合，绘制每个粒子
			Particle partical = particleSet.get(i);
			if (mouse.down) {
				int diff_x = (int) (partical.x - mouse.x), // x位移
				diff_y = (int) (partical.y - mouse.y), // y位移
				dist = (int) Math.sqrt(diff_x * diff_x + diff_y * diff_y); // 绝对位移
				if (dist < TouchCricle) {// 大于滑动控制距离
					partical.startX = (int) (partical.x - (mouse.x - mouse.px) * 1.8);
					partical.startY = (int) (partical.y - (mouse.y - mouse.py) * 1.8);
				}
			}
			partical.resolve_constraints(canvas);// 分解每个粒子
			partical.add_force(0, gravity);// 添加重力
			partical.update((float) 0.5);// 添加自然抖动

		}

		paint.setColor(Color.WHITE); // 设置画笔颜色
		paint.setTextSize(18); // 设置文字大小
		paint.setAntiAlias(true); // 设置抗锯齿
		canvas.drawText(fps, 15, 15, paint);// 画出帧速率字符串
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {// 重写surfaceChanged方法
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {// 从写surfaceCreated方法
		if (!drawThread.isAlive()) { // 如果DrawThread没有启动，就启动这个线程
			drawThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {// 重写surfaceDestroyed方法
		drawThread.flag = false; // 停止线程的执行
		drawThread = null; // 将dt指向的对象声明为垃圾
	}

	/***
	 * 用于重绘屏幕和计算帧率
	 * 
	 * @author zhangjia
	 * 
	 */
	public class DrawThread extends Thread {
		ParticleView particalView;// 自定义View
		SurfaceHolder surfaceHolder;
		boolean flag = false;// 线程标识
		int sleepSpan = 15;// 线程休眠
		long start = System.nanoTime();// 其实时间，用于计算帧速率
		int count = 0;// 计算帧率

		public DrawThread(ParticleView particalView, SurfaceHolder surfaceHolder) {
			super();
			this.particalView = particalView;
			this.surfaceHolder = surfaceHolder;
			this.flag = true;
		}

		@Override
		public void run() {
			Canvas canvas = null;
			while (flag) {
				try {
					canvas = surfaceHolder.lockCanvas();// 获取canvas.
					synchronized (surfaceHolder) {
						particalView.doDraw(canvas);// 进行绘制ballView.

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);// 解锁
					}
				}
				this.count++;
				if (count == 20) { // 如果计满20帧
					count = 0; // 清空计数器
					long tempStamp = System.nanoTime();// 获取当前时间
					long span = tempStamp - start; // 获取时间间隔
					start = tempStamp; // 为start重新赋值
					double fps = Math.round(100000000000.0 / span * 20) / 100.0;// 计算帧速率
					particalView.fps = "FPS:" + fps;// 将计算出的帧速率设置到BallView的相应字符串对象中
				}
				try {
					Thread.sleep(sleepSpan); // 线程休眠一段时间
				} catch (Exception e) {
					e.printStackTrace(); // 捕获并打印异常
				}
			}
		}

	}
}