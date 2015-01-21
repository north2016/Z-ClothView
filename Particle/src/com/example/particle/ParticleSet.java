package com.example.particle;

import java.util.ArrayList;

/***
 * 存放粒子对象集合
 * 
 * @author zhangjia
 * 
 */
public class ParticleSet {
	ArrayList<Particle> particals;

	public ParticleSet() {
		super();
		particals = new ArrayList<Particle>();
	}


	/***
	 * 添加粒子
	 * 
	 * @param count
	 *            个数
	 * @param startTime
	 *            起始时间
	 * @param spacing
	 */

	public void addPartical(int count, int window_Width, int space) {
		int spacing = space;
		
		for (int i = 0; i < count; i++) {

			double vertical_v = 0, horizontal_v = 0;
			int Start = (window_Width - ParticleView.width * spacing) / 2;
			int w = i % ParticleView.width;// 列数
			int h = i / ParticleView.width;// 行数
			// Y坐标
			int startX = w * spacing + Start;
			// Y坐标
			int startY = h * spacing + Start;

			Particle partical = new Particle(vertical_v,
					horizontal_v, startX, startY);

			// 将每个粒子分别链接到其左侧和上测的粒子上（如果有的话）
			
			
			if (w != 0) {// 如果左边有的话，连接到左边的上面
				partical.attach(this.particals.get(this.particals.size() - 1));
			}

			if (h == 0)// 如果是第一排的话，钉死在这个位置上
			{
				partical.pin(partical.startX, partical.startY);
			} else {
				partical.pin(0, 0);
			}

			if (h != 0)// 如果上边有的话，连接到左边的上面
				partical.attach(this.particals.get(w + (h - 1)
						* ParticleView.width));

			particals.add(partical);
		}
	}
}
