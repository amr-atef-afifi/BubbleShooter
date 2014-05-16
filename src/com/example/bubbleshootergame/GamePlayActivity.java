package com.example.bubbleshootergame;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GamePlayActivity extends Activity
{
	Drawable		background;
	int				level_number;
	ViewGroup		layout;
	GamePlayDrawer	game_play_drawer;
	int				x;
	int				y;
	Context			context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	private void init_game()
	{
		context = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_play);
		Intent intent = getIntent();
		level_number = intent.getIntExtra(LevelChooser.LEVEL_NUMBER_MESSAGE, 1);
		init_game(this, level_number);

		game_play_drawer = new GamePlayDrawer(this, level_number);
		game_play_drawer.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					x = (int) event.getX();
					y = (int) event.getY();

					game_play_drawer.fire(x
							- game_play_drawer.get_geme_play().get_tile_size()
							/ 2, y
							- game_play_drawer.get_geme_play().get_tile_size()
							/ 2);
				}
				return false;
			}
		});

		layout = (ViewGroup) findViewById(R.id.game_play_main_layout);
		layout.addView(game_play_drawer);

		update_game();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_play, menu);
		return true;
	}

	private void init_game(Context context, int level_number)
	{
		switch (level_number)
		{
			case 1:
				background = context.getResources().getDrawable(
						R.drawable.background_port);
				break;
			case 2:
				background = context.getResources().getDrawable(
						R.drawable.background_christmas);
				break;
			case 3:
				background = context.getResources().getDrawable(
						R.drawable.background_sea);
				break;
			case 4:
				background = context.getResources().getDrawable(
						R.drawable.background_factory);
				break;
			case 5:
				background = context.getResources().getDrawable(
						R.drawable.background_buildings);
				break;
			case 6:
				background = context.getResources().getDrawable(
						R.drawable.background_forest);
				break;
			default:
				background = context.getResources().getDrawable(
						R.drawable.background_port);
				break;
		}
		ImageView background_view = (ImageView) findViewById(R.id.gameplay_background);
		background_view.setImageDrawable(background);
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		init_game();
	}

	public void update_game()
	{
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask()
		{

			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						long start=System.currentTimeMillis();
						if (game_play_drawer.update())
						{
							if (!game_play_drawer.get_winner_state())
							{
								if (level_number + 1 > LevelChooser.open_levels && level_number<6)
								{
									if (game_play_drawer.get_score() > LevelChooser.high_score)
									{
										write(level_number + 1,
												game_play_drawer.get_score());
									} else
									{
										write(level_number + 1,
												LevelChooser.high_score);
									}
								}
							}
							System.exit(0);
						}
						Log.d("Time",System.currentTimeMillis()-start+"");
					}

				});
			}

		}, 0, 20);
	}

	public void write(int level_number, int high_score)
	{
		try
		{
			FileOutputStream fos = openFileOutput("game_file.txt",
					Context.MODE_PRIVATE);
			fos.write((level_number + "\n").getBytes());
			fos.write((high_score + "\n").getBytes());
			fos.close();
		} catch (Exception e)
		{
			Log.d("main_activity", "can't write");
		}
	}
}
