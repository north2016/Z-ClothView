package com.example.particle;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	LinearLayout main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		main = (LinearLayout) findViewById(R.id.main);
		
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		
		ParticleView mParticleView = new ParticleView(MainActivity.this,
				dm.widthPixels, dm.heightPixels);
		main.addView(mParticleView);

	}

}
