package com.manas.anurag.virtualattack;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;


public class MainActivity extends Activity implements SensorEventListener, OnLoadCompleteListener{

	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	private static SoundPool sp;
	static String audio = "audio/whip.mp3";
	private ImageView imview;
	private int soundId;
	float volume;
	boolean loaded = true;
	private float[] last_acc = new float[]{5,5,5};
	private long last_loaded_time = System.currentTimeMillis();
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imview = (ImageView)findViewById(R.id.imageview);
		mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		sp.setOnLoadCompleteListener(this);
		textView = (TextView) findViewById(R.id.content);
		textView.setMovementMethod(new ScrollingMovementMethod());
		textView.setText("Expression here");
	}

	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	public final void onSensorChanged(SensorEvent event) {
		float[] acc = event.values;
		if(isJerk(acc) && loaded){
			loaded = false;;
			try {
				AssetFileDescriptor descriptor = getAssets().openFd(audio);
				soundId = sp.load(descriptor, 1);
				descriptor.close();
				textView.setText(selectRandomText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String selectRandomText(){
		int number_of_strings=10;
		String[] display_strings= new String[number_of_strings];
		display_strings[0]="Hit harder!";
		display_strings[1]="That surely hit!";
		display_strings[2]="Smack!";
		display_strings[3]="Swoosh";
		display_strings[4]="Dont be gentle!";
		display_strings[5]="Harshness!";
		display_strings[6]="Aah!";
		display_strings[7]="Bloody shot!";
		display_strings[8]="Show who is the daddy";
		display_strings[9]="You are the boss!";
		
		Random generator = new Random();
		int random = generator.nextInt(number_of_strings);
		return display_strings[random];
		
	}
	
	private boolean isJerk(float[] acc){
		float magnitude = 0;
		for(int i=0;i<3;i++){
			magnitude += (Math.abs(acc[i]) - last_acc[i]) * (Math.abs(acc[i]) - last_acc[i]);
			last_acc[i] = Math.abs(acc[i]);
		}
		long current_time = System.currentTimeMillis();
		if(magnitude>100 && (current_time-last_loaded_time) > 170){
			Log.d("data",Float.toString(magnitude));
			last_loaded_time = current_time;
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mySensorManager.unregisterListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.sword:		//degree radian implementation
			audio = "audio/sword.mp3";
			imview.setBackgroundResource(R.drawable.sword_back);
			break;

		case R.id.whip:		// fraction decimal view implementation
			audio = "audio/whip.mp3";
			imview.setBackgroundResource(R.drawable.whip_back);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		sp.play(soundId, volume, volume, 1, 0, 1f);
		loaded = true;
	}

}
