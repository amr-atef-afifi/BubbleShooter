package com.example.bubbleshootergame;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class LevelChooser extends Activity
{

	public static final String	LEVEL_NUMBER_MESSAGE	= "com.example.bubbleshooter.levelchooser.levelnumbermessage";
	public static int					open_levels;
	public static int					high_score;
	private ImageView			level_image;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		init_open_levels();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_level_chooser);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		for (int i = 0; i < open_levels; i++)
		{
			set_level_image_unlocked(i + 1);
		}
	}

	private void set_level_image_unlocked(int level)
	{
		switch (level)
		{
			case 1:
				level_image = (ImageView) findViewById(R.id.level_chooser_level1);
				level_image.setImageResource(R.drawable.ic_game_port_unlocked);
				break;
			case 2:
				level_image = (ImageView) findViewById(R.id.level_chooser_level2);
				level_image
						.setImageResource(R.drawable.ic_game_christmas_unlocked);
				break;
			case 3:
				level_image = (ImageView) findViewById(R.id.level_chooser_level3);
				level_image.setImageResource(R.drawable.ic_game_sea_unlocked);
				break;
			case 4:
				level_image = (ImageView) findViewById(R.id.level_chooser_level4);
				level_image
						.setImageResource(R.drawable.ic_game_factory_unlocked);
				break;
			case 5:
				level_image = (ImageView) findViewById(R.id.level_chooser_level5);
				level_image
						.setImageResource(R.drawable.ic_game_street_unlocked);
				break;
			case 6:
				level_image = (ImageView) findViewById(R.id.level_chooser_level6);
				level_image
						.setImageResource(R.drawable.ic_game_forest_unlocked);
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level_chooser, menu);
		return true;
	}

	public void to_level(View view)
	{
		int level_number;
		switch (view.getId())
		{
			case (R.id.level_chooser_level1):
				level_number = 1;
				break;

			case (R.id.level_chooser_level2):
				level_number = 2;
				break;

			case (R.id.level_chooser_level3):
				level_number = 3;
				break;

			case (R.id.level_chooser_level4):
				level_number = 4;
				break;

			case (R.id.level_chooser_level5):
				level_number = 5;
				break;

			case (R.id.level_chooser_level6):
				level_number = 6;
				break;

			default:
				level_number = 1;
				break;
		}

		if (level_number <= open_levels)
		{
			Intent game_play_intent = new Intent(this, GamePlayActivity.class);
			game_play_intent.putExtra(LEVEL_NUMBER_MESSAGE, level_number);
			startActivity(game_play_intent);
		}
	}
	
	private void init_open_levels()
	{

		try
		{
			BufferedReader inputReader;
			inputReader = new BufferedReader(new InputStreamReader(
					openFileInput("game_file.txt")));
			String inputString;
			if((inputString = inputReader.readLine()) == null)
			{
				open_levels = 1;
				high_score =  0;
			}
			else
			{
				open_levels = Integer.parseInt(inputString);
				inputString = inputReader.readLine();
				high_score = Integer.parseInt(inputString);
			}			
		} catch (Exception e)
		{
			open_levels=1;
			high_score=0;
			Log.d("main_activity", "can't read");
		}
	}

}
