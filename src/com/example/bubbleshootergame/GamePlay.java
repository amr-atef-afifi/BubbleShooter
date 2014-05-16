package com.example.bubbleshootergame;

public class GamePlay
{
	public static final int	LARGE_TILE_SIZE			= 64;
	public static final int	SMALL_TILE_SIZE			= 48;
	public static final int	LARGE_SCREEN_THRESHOLD	= 512;
	public static final int	COLORED_TILES_NUMBER	= 3;
	public static final int	FIRE_BALLS_NUMBER		= 3;

	int[][]					game_matrix;
	int						tile_size;
	int						fire_position_x;
	int						fire_position_y;
	int[]					fire_balls;
	int						falling_rate;
	int						end_period;
	int						initial_height;

	public GamePlay(int level_number, int width, int height)
	{
		if (width < LARGE_SCREEN_THRESHOLD)
		{
			tile_size = SMALL_TILE_SIZE;
		} else
		{
			tile_size = SMALL_TILE_SIZE;
		}

		int matrix_width = (int) (width / tile_size);
		int matrix_height = (int) ((height-GamePlayDrawer.SCORE_MARGIN -GamePlayDrawer.TOP_MARGIN) / tile_size) - 1;

		if (width - (matrix_width * tile_size) < (tile_size / 2))
		{
			matrix_width--;
		}
		game_matrix = new int[matrix_height][matrix_width];
		init_matrix(level_number, matrix_width, matrix_height);

		fire_position_y = (int) (height - (1.5 * tile_size));
		fire_position_x = (int) ((width - tile_size) / 2);

		fire_balls = new int[FIRE_BALLS_NUMBER];
		end_period = 100;

		for (int i = 0; i < FIRE_BALLS_NUMBER; i++)
		{
			fire_balls[i] = i + 1;
		}

	}

	private void init_matrix(int level_number, int matrix_width,
			int matrix_height)
	{
		switch (level_number)
		{
			case 1:
				initial_height = 4;
				falling_rate   = 2000;
				break;
			case 2:
				initial_height = 4;
				falling_rate   = 1000;
				break;
			case 3:
				initial_height = 5;
				falling_rate   = 2000;
				break;
			case 4:
				initial_height = 5;
				falling_rate   = 1000;
				break;
			case 5:
				initial_height = 6;
				falling_rate   = 2000;
				break;
			case 6:
				initial_height = 6;
				falling_rate   = 1000;
				break;

			default:
				initial_height = 2;
				falling_rate   = 2000;
				break;
		}

		for (int i = 0; i < initial_height; i++)
		{
			for (int j = 0; j < matrix_width; j++)
			{
				int x = (int) (Math.random() * COLORED_TILES_NUMBER);
				game_matrix[i][j] = x + 1;
			}
		}

		for (int i = initial_height; i < matrix_height; i++)
		{
			for (int j = 0; j < matrix_width; j++)
			{
				game_matrix[i][j] = 0;
			}
		}

	}

	public int[][] get_game_map()
	{
		return game_matrix;
	}

	public int get_tile_size()
	{
		return tile_size;
	}

	public int[] get_fire_balls()
	{
		return fire_balls;
	}

	public int get_fire_pos_x()
	{
		return fire_position_x;
	}

	public int get_fire_pos_y()
	{
		return fire_position_y;
	}

	public void change_balls_bag()
	{
		for (int i = 0; i < FIRE_BALLS_NUMBER - 1; i++)
		{
			fire_balls[i] = fire_balls[i + 1];
		}
		fire_balls[FIRE_BALLS_NUMBER - 1] = (int) ((Math.random() * COLORED_TILES_NUMBER) + 1);
	}
	
	public int get_falling_Rate()
	{
		return falling_rate;
	}
	
	public int get_initial_height()
	{
		return initial_height;
	}
	
	public int get_end_period()
	{
		return end_period;
	}

}
