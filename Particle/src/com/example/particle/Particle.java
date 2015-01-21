package com.example.particle;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

/***
 * 粒子对象
 * 
 * 
 */
public class Particle {

	double vertical_v;// 垂直速度
	double horizontal_v;// 水平速度
	int startX;
	int startY;
	int x;
	int y;
	int pin_x, pin_y;// 被钉死的位置
	

	List<Constraint> constraints;// 约束

	public Particle(double vertical_v, double horizontal_v, int x, int y) {
		super();

		this.vertical_v = vertical_v;
		this.horizontal_v = horizontal_v;
		this.startX = x;
		this.startY = y;
		this.x = x;
		this.y = y;
		this.constraints = new ArrayList<Constraint>();
	}

	public void remove_constraint(Constraint constraint) {
		this.constraints.remove(constraint);
	}

	public void attach(Particle particle) {
		this.constraints.add(new Constraint(this, particle));
	}

	public void pin(int x2, int y2) {
		this.pin_x = x2;
		this.pin_y = y2;
	}

	public void update(Float delta) {

		delta *= delta;
		int nx = (int) (this.x + ((this.x - this.startX) * .99) + ((this.horizontal_v / 2) * delta));
		int ny = (int) (this.y + ((this.y - this.startY) * .99) + ((this.vertical_v / 2) * delta));

		this.startX = this.x;
		this.startY = this.y;

		this.x = nx;
		this.y = ny;

		this.vertical_v = this.horizontal_v = 0;
	}

	void add_force(int i, int gravity) {
		this.horizontal_v += i;
		this.vertical_v += gravity;
	};

	public void resolve_constraints(Canvas canvas) {// 分解

		int i = this.constraints.size();
		while (i-- > 0) {
			this.constraints.get(i).resolve();
			this.constraints.get(i).draw(canvas);// 画图
		}
		if (this.x > ParticleView.boundsx) {
			this.x = 2 * ParticleView.boundsx - this.x;

		} else if (this.x < 1) {

			this.x = 2 - this.x;
		}

		if (this.y > ParticleView.boundsy) {

			this.y = ParticleView.boundsy;

		}
		if (this.pin_x != 0 && this.pin_y != 0) {
			this.x = this.pin_x;
			this.y = this.pin_y;
		}

	};

}
