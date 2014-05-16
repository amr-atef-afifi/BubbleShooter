package com.example.bubbleshootergame;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.w3c.dom.Text;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class GamePlayDrawer extends View {
	public static final int TOP_MARGIN = 20;
	public static final int SCORE_MARGIN = 30;
	public static final int TEXT_SIZE = 25;
	int pitch_img;
	Bitmap pitch_bmp;
	int main_tile_img;
	int[][] game_map;
	Bitmap game_tile;
	GamePlay game_play;
	int map_starting_point;
	Paint[] paint;
	int fire_current_x;
	int fire_current_y;
	int fire_end_x;
	int fire_end_y;
	int fire_ball;
	boolean shoot;
	Point screen_size;
	double x0;
	double y0;
	int pitch_timer;
	ArrayList<Point> collision_list;
	Point fire_current_point;
	int max_y;
	int end_period;
	boolean game_ended;
	boolean kill_game;
	boolean lost;
	int score;
	Paint score_painter;
	Rect score_rect;
	Paint score_rect_painter;
	Context context;
	MediaPlayer fire_player;
	MediaPlayer win_player;
	MediaPlayer lose_player;
	MediaPlayer collision_player;
	AssetFileDescriptor fire_afd;
	AssetFileDescriptor win_afd;
	AssetFileDescriptor lose_afd;
	AssetFileDescriptor collision_afd;
	boolean is_grounded;
	boolean[][] explosion_map;
	boolean[][] visited;
	boolean play_win;
	int drop_count;
	ArrayList<Point> fallen_balls;
	Paint fallen_ball_painter;

	public GamePlayDrawer(Context context, int level_number) {
		super(context);
		this.context = context;
		drop_count = 0;
		fallen_balls = new ArrayList<Point>();
		win_player = new MediaPlayer();

		fallen_ball_painter = new Paint();

		screen_size = new Point();
		get_screen_size(screen_size, context);
		map_starting_point = TOP_MARGIN + SCORE_MARGIN;
		game_play = new GamePlay(level_number, screen_size.x, screen_size.y);
		int tile_size = game_play.get_tile_size();
		fire_current_x = game_play.get_fire_pos_x();
		fire_end_x = game_play.get_fire_pos_x();
		fire_current_y = game_play.get_fire_pos_y();
		fire_end_y = game_play.get_fire_pos_y();
		fire_ball = game_play.get_fire_balls()[0];
		fire_current_point = new Point();
		max_y = game_play.get_initial_height();
		game_ended = false;
		end_period = game_play.get_end_period();
		kill_game = false;
		lost = false;
		this.setClickable(true);
		score = 0;
		is_grounded = false;
		play_win = false;

		score_painter = new Paint(Paint.ANTI_ALIAS_FLAG);
		score_painter.setColor(Color.rgb(221, 255, 0));
		score_painter.setTextSize(TEXT_SIZE);
		score_painter.setAntiAlias(true);

		score_rect_painter = new Paint();
		score_rect_painter.setColor(Color.rgb(30, 30, 30));
		score_rect_painter.setAlpha(150);
		score_rect_painter.setTextSize(40);

		fire_player = new MediaPlayer();
		lose_player = new MediaPlayer();
		collision_player = new MediaPlayer();

		switch (level_number) {
		case 1:
			pitch_img = R.drawable.game_blue_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_port_small
					: R.drawable.ic_game_port_large;
			break;
		case 2:
			pitch_img = R.drawable.game_brown_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_christmas_small
					: R.drawable.ic_game_christmas_large;
			break;
		case 3:
			pitch_img = R.drawable.game_blue_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_sea_small
					: R.drawable.ic_game_sea_large;
			break;
		case 4:
			pitch_img = R.drawable.game_brown_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_gear_small
					: R.drawable.ic_game_gear_large;
			break;
		case 5:
			pitch_img = R.drawable.game_brown_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_street_small
					: R.drawable.ic_game_street_large;
			break;
		case 6:
			pitch_img = R.drawable.game_green_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_forest_small
					: R.drawable.ic_game_forest_large;
			break;

		default:
			pitch_img = R.drawable.game_blue_pitch;
			main_tile_img = (tile_size == GamePlay.SMALL_TILE_SIZE) ? R.drawable.ic_game_port_small
					: R.drawable.ic_game_port_large;
			break;
		}
		color_bitmap();
		pitch_bmp = BitmapFactory.decodeResource(getResources(), pitch_img);
		game_map = game_play.get_game_map();

		collision_list = new ArrayList<Point>();

		for (int i = 0; i < game_play.get_initial_height(); i++) {
			for (int j = 0; j < game_map[0].length; j++) {
				collision_list.add(get_center(j, i));
			}
		}

		init_game_tiles();
		score_rect = new Rect(0, 0, screen_size.x, SCORE_MARGIN);
		explosion_map = new boolean[game_map.length][game_map[0].length];
		visited = new boolean[game_map.length][game_map[0].length];
		for (int i = 0; i < explosion_map.length; i++) {
			for (int j = 0; j < explosion_map[0].length; j++) {
				explosion_map[i][j] = false;
				visited[i][j] = false;
			}
		}
		
	}

	private Point get_center(int i, int j) {
		Point p;
		if (j % 2 == 0) {
			p = new Point((1 + i) * game_play.get_tile_size(), TOP_MARGIN + j
					* game_play.get_tile_size() + game_play.get_tile_size() / 2);
		} else {
			p = new Point(i * game_play.get_tile_size()
					+ game_play.get_tile_size() / 2, TOP_MARGIN + j
					* game_play.get_tile_size() + game_play.get_tile_size() / 2);
		}
		return p;
	}

	private void init_game_tiles() {

		game_tile = BitmapFactory.decodeResource(getResources(), main_tile_img);
	}

	private void color_bitmap() {
		paint = new Paint[GamePlay.COLORED_TILES_NUMBER];

		paint[1] = new Paint(Color.GREEN);
		paint[2] = new Paint(Color.RED);

		paint[1].setColorFilter(new LightingColorFilter(Color.GREEN, 0));
		paint[2].setColorFilter(new LightingColorFilter(Color.RED, 0));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (collision_list.isEmpty()) {
			if (!play_win) {
				play_win();
				play_win = true;
			}
			end_game(true, canvas);
			end_period--;
			kill_game = (end_period <= 0);
		} else if (lost) {
			end_game(false, canvas);
			end_period--;
			kill_game = (end_period <= 0);
		}
		if (!game_ended) {
			draw_game_map(canvas);
			draw_fired_ball(canvas);
			draw_balls_bag(canvas);
			draw_score(canvas);
		}
		drop_balls(canvas);

	}

	private void draw_score(Canvas canvas) {
		canvas.drawRect(score_rect, score_rect_painter);
		canvas.drawText("Score : " + score, TEXT_SIZE, TEXT_SIZE, score_painter);
	}

	private void draw_fired_ball(Canvas canvas) {
		canvas.drawBitmap(game_tile, fire_current_x, fire_current_y,
				paint[fire_ball - 1]);
	}

	private void draw_balls_bag(Canvas canvas) {
		for (int i = 0; i < GamePlay.FIRE_BALLS_NUMBER; i++) {
			canvas.drawBitmap(game_tile, game_play.get_fire_pos_x() - i
					* (game_play.get_tile_size() + 10),
					game_play.get_fire_pos_y(),
					paint[game_play.get_fire_balls()[i] - 1]);
		}
	}

	private void draw_game_map(Canvas canvas) {
		int value = 0;
		canvas.drawBitmap(pitch_bmp, 0, map_starting_point - TOP_MARGIN, null);
		for (int i = 0; i < game_map.length; i++) {
			for (int j = 0; j < game_map[0].length; j++) {
				value = Math.abs(game_map[i][j]);
				if (value != 0) {
					if (i % 2 == 0) {
						canvas.drawBitmap(
								game_tile,
								j * game_play.get_tile_size()
										+ (game_play.get_tile_size() / 2), i
										* game_play.get_tile_size()
										+ map_starting_point, paint[value - 1]);
					} else {
						canvas.drawBitmap(game_tile,
								j * game_play.get_tile_size(),
								i * game_play.get_tile_size()
										+ map_starting_point, paint[value - 1]);
					}

					if (i * game_play.get_tile_size() + map_starting_point >= game_map.length
							* game_play.get_tile_size() + TOP_MARGIN) {
						lost = true;
						play_lose();
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void get_screen_size(Point size, Context context) {
		WindowManager window_manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = window_manager.getDefaultDisplay();
		try {
			Class<?> pointClass = Class.forName("android.graphics.Point");
			Method new_getSize = Display.class.getMethod("getSize",
					new Class[] { pointClass });
			new_getSize.invoke(display, size);
		} catch (Exception e) {
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
	}

	public void fire(int x, int y) {
		if (shoot == false && y <= game_play.get_fire_pos_y()) {
			playfire();
			fire_end_x = x;
			fire_end_y = y;
			shoot = true;
			fire_ball = game_play.get_fire_balls()[0];
			game_play.change_balls_bag();
			x0 = (double) game_play.get_fire_pos_x();
			y0 = (double) game_play.get_fire_pos_y();
			invalidate();
		}
	}

	public boolean update() {
		if (shoot) {
			fire_current_y -= 2;
			fire_current_x = (int) (x0 - (((x0 - (double) fire_end_x) * (y0 - (double) fire_current_y)) / (y0 - (double) fire_end_y)));

			if (fire_current_x + game_play.get_tile_size() >= screen_size.x) {
				fire_end_y = (2 * fire_current_y) - game_play.get_fire_pos_y();
				fire_end_x = game_play.get_fire_pos_x();
				x0 = screen_size.x - game_play.get_tile_size();
				y0 = fire_current_y;
				fire_current_x = (int) (x0 - (((x0 - (double) fire_end_x) * (y0 - (double) fire_current_y)) / (y0 - (double) fire_end_y)));
			} else if (fire_current_x <= 0) {
				fire_end_y = (2 * fire_current_y) - game_play.get_fire_pos_y();
				fire_end_x = game_play.get_fire_pos_x();
				x0 = 0;
				y0 = fire_current_y;
				fire_current_x = (int) (x0 - (((x0 - (double) fire_end_x) * (y0 - (double) fire_current_y)) / (y0 - (double) fire_end_y)));
			}

			for (int i = 0; i < collision_list.size(); i++) {
				fire_current_point.x = fire_current_x
						+ game_play.get_tile_size() / 2;
				fire_current_point.y = fire_current_y
						+ game_play.get_tile_size() / 2;
				if (is_collided(fire_current_point, collision_list.get(i))) {
					Point map_cell = get_map_cell(collision_list.get(i));

					if (map_cell.y >= game_map.length - 1) {
						lost = true;
						play_lose();
						break;
					}

					if (fire_current_point.x <= collision_list.get(i).x) // left
					{
						if (map_cell.y % 2 == 0) {
							if (game_map[map_cell.y + 1][map_cell.x] == 0) {
								game_map[map_cell.y + 1][map_cell.x] = fire_ball;
								collision_list.add(get_center(map_cell.x,
										map_cell.y + 1));
								if (check_around(map_cell.y + 1, map_cell.x,
										fire_ball)) {
									explode(map_cell.y + 1, map_cell.x, fire_ball);
								}
							} else {
								game_map[map_cell.y][map_cell.x - 1] = fire_ball;
								collision_list.add(get_center(map_cell.x-1,
										map_cell.y));
								if (check_around(map_cell.y, map_cell.x-1,
										fire_ball)) {
									explode(map_cell.y, map_cell.x-1, fire_ball);
								}

							}

						} else if (map_cell.x - 1 >= 0) {
							if (game_map[map_cell.y + 1][map_cell.x - 1] == 0) {
								game_map[map_cell.y + 1][map_cell.x - 1] = fire_ball;
								collision_list.add(get_center(map_cell.x - 1,
										map_cell.y + 1));
								if (check_around(map_cell.y + 1, map_cell.x - 1,
										fire_ball)) {
									explode(map_cell.y + 1, map_cell.x - 1,
											fire_ball);
								}
							} else {
								game_map[map_cell.y][map_cell.x - 1] = fire_ball;
								collision_list.add(get_center(map_cell.x - 1,
										map_cell.y));
								if (check_around(map_cell.y, map_cell.x - 1,
										fire_ball)) {
									explode(map_cell.y, map_cell.x - 1,
											fire_ball);
								}
							}
						}

					} else
					// right
					{
						if (map_cell.y % 2 == 0
								&& map_cell.x + 1 < game_map[0].length) {

							if (game_map[map_cell.y + 1][map_cell.x + 1] == 0) {
								game_map[map_cell.y + 1][map_cell.x + 1] = fire_ball;
								collision_list.add(get_center(map_cell.x + 1,
										map_cell.y + 1));
								if (check_around(map_cell.y + 1, map_cell.x + 1,
										fire_ball)) {
									
									explode(map_cell.y + 1, map_cell.x + 1,
											fire_ball);
								}
							} else {
								game_map[map_cell.y][map_cell.x + 1] = fire_ball;
								collision_list.add(get_center(map_cell.x + 1,
										map_cell.y));
								if (check_around(map_cell.y, map_cell.x + 1,
										fire_ball)) {
									
									explode(map_cell.y, map_cell.x + 1,
											fire_ball);
								}
							}
						} else {
							if (game_map[map_cell.y + 1][map_cell.x] == 0) {
								game_map[map_cell.y + 1][map_cell.x] = fire_ball;
								collision_list.add(get_center(map_cell.x,
										map_cell.y + 1));
								if (check_around(map_cell.y + 1, map_cell.x,
										fire_ball)) {
									explode(map_cell.y + 1, map_cell.x, fire_ball);
								}
							} else {
								game_map[map_cell.y][map_cell.x + 1] = fire_ball;
								collision_list.add(get_center(map_cell.x+1,
										map_cell.y));
								if (check_around(map_cell.y, map_cell.x+1,
										fire_ball)) {
									explode(map_cell.y, map_cell.x+1, fire_ball);
								}
							}
						}
					}
					shoot = false;
					fire_current_x = (int) game_play.get_fire_pos_x();
					fire_current_y = (int) game_play.get_fire_pos_y();
					break;
				}
			}

			if (fire_current_y - map_starting_point <= 0) {
				Point p = get_map_cell(new Point(fire_current_x
						+ game_play.get_tile_size() / 2, fire_current_y
						- map_starting_point + game_play.get_tile_size() / 2));
				game_map[p.y][p.x] = fire_ball;
				collision_list.add(get_center(p.x, p.y));
				if (check_around(p.y, p.x, fire_ball)) {

					explode(p.y, p.x, fire_ball);
				}
				shoot = false;
				fire_current_x = (int) game_play.get_fire_pos_x();
				fire_current_y = (int) game_play.get_fire_pos_y();
			}
		}
		pitch_timer++;
		if (pitch_timer >= game_play.get_falling_Rate()) {
			pitch_timer = 0;
			map_starting_point += game_play.get_tile_size();
		}	
		invalidate();
		return kill_game;
	}

	public GamePlay get_geme_play() {
		return game_play;
	}

	public boolean is_collided(Point p1, Point p2) {
		double distance = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x)
				+ (p1.y - p2.y - map_starting_point + TOP_MARGIN)
				* (p1.y - p2.y - map_starting_point + TOP_MARGIN));
		return (distance <= game_play.get_tile_size()
				- game_play.get_tile_size() / 6);
	}

	public Point get_map_cell(Point p) {
		int row_number = p.y / game_play.get_tile_size();
		int column_number;
		if (row_number % 2 == 0) {
			column_number = (p.x - game_play.get_tile_size() / 2)
					/ game_play.get_tile_size();
		} else {
			column_number = p.x / game_play.get_tile_size();
		}
		return new Point(column_number, row_number);

	}

	private boolean check_around(int y, int x, int type) {
		int adj_num = 0;
		Point adj_found = new Point();
		if (y % 2 == 0)// even_row
		{
			if ((y - 1) >= 0 && (x) >= 0 && game_map[y - 1][x] == type) {
				adj_num++;
				adj_found.x = x;
				adj_found.y = y - 1;
			}
			if ((y - 1) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y - 1][x + 1] == type) {
				adj_num++;
				adj_found.x = x + 1;
				adj_found.y = y - 1;
			}
			if ((y) >= 0 && (x - 1) >= 0 && game_map[y][x - 1] == type) {
				adj_num++;
				adj_found.x = x - 1;
				adj_found.y = y;
			}
			if ((y) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y][x + 1] == type) {
				adj_num++;
				adj_found.x = x + 1;
				adj_found.y = y;
			}
			if ((y + 1) >= 0 && (x) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x] == type) {
				adj_num++;
				adj_found.x = x;
				adj_found.y = y + 1;
			}
			if ((y + 1) >= 0 && (x + 1) >= 0 && (y + 1) < game_map.length
					&& (x + 1) < game_map[0].length
					&& game_map[y + 1][x + 1] == type) {
				adj_num++;
				adj_found.x = x + 1;
				adj_found.y = y + 1;
			}
		} else
		// odd_row
		{
			if ((y - 1) >= 0 && (x - 1) >= 0 && game_map[y - 1][x - 1] == type) {
				adj_num++;
				adj_found.x = x - 1;
				adj_found.y = y - 1;
			}
			if ((y - 1) >= 0 && (x) >= 0 && game_map[y - 1][x] == type) {
				adj_num++;
				adj_found.x = x;
				adj_found.y = y - 1;
			}
			if ((y) >= 0 && (x - 1) >= 0 && game_map[y][x - 1] == type) {
				adj_num++;
				adj_found.x = x - 1;
				adj_found.y = y;
			}
			if ((y) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y][x + 1] == type) {
				adj_num++;
				adj_found.x = x + 1;
				adj_found.y = y;
			}
			if ((y + 1) >= 0 && (x - 1) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x - 1] == type) {
				adj_num++;
				adj_found.x = x - 1;
				adj_found.y = y + 1;
			}
			if ((y + 1) >= 0 && (x) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x] == type) {
				adj_num++;
				adj_found.x = x;
				adj_found.y = y + 1;
			}
		}
		if (adj_num >= 2) {
			return true;
		} else if (adj_num == 1) {
			int x_found = adj_found.x;
			int y_found = adj_found.y;

			if (y_found % 2 == 0)// even_row
			{
				if ((y_found - 1) >= 0 && (x_found) >= 0
						&& game_map[y_found - 1][x_found] == type
						&& ((y_found - 1) != y || (x_found) != x)) {
					return true;
				}
				if ((y_found - 1) >= 0 && (x_found + 1) >= 0
						&& (x_found + 1) < game_map[0].length
						&& game_map[y_found - 1][x_found + 1] == type
						&& ((y_found - 1) != y || (x_found + 1) != x)) {
					return true;
				}
				if ((y_found) >= 0 && (x_found - 1) >= 0
						&& game_map[y_found][x_found - 1] == type
						&& ((y_found) != y || (x_found - 1) != x)) {
					return true;
				}
				if ((y_found) >= 0 && (x_found + 1) >= 0
						&& (x_found + 1) < game_map[0].length
						&& game_map[y_found][x_found + 1] == type
						&& ((y_found) != y || (x_found + 1) != x)) {
					return true;
				}
				if ((y_found + 1) >= 0 && (x_found) >= 0
						&& (y_found + 1) < game_map.length
						&& game_map[y_found + 1][x_found] == type
						&& ((y_found + 1) != y || (x_found) != x)) {
					return true;
				}
				if ((y_found + 1) >= 0 && (x_found + 1) >= 0
						&& (y_found + 1) < game_map.length
						&& (x_found + 1) < game_map[0].length
						&& game_map[y_found + 1][x_found + 1] == type
						&& ((y_found + 1) != y || (x_found + 1) != x)) {
					return true;
				}
			} else
			// odd_row
			{
				if ((y_found - 1) >= 0 && (x_found - 1) >= 0
						&& game_map[y_found - 1][x_found - 1] == type
						&& ((y_found - 1) != y || (x_found - 1) != x)) {
					return true;
				}
				if ((y_found - 1) >= 0 && (x_found) >= 0
						&& game_map[y_found - 1][x_found] == type
						&& ((y_found - 1) != y || (x_found) != x)) {
					return true;
				}
				if ((y_found) >= 0 && (x_found - 1) >= 0
						&& game_map[y_found][x_found - 1] == type
						&& ((y_found) != y || (x_found - 1) != x)) {
					return true;
				}
				if ((y_found) >= 0 && (x_found + 1) >= 0
						&& (x_found + 1) < game_map[0].length
						&& game_map[y_found][x_found + 1] == type
						&& ((y_found) != y || (x_found + 1) != x)) {
					return true;
				}
				if ((y_found + 1) >= 0 && (x_found - 1) >= 0
						&& (y_found + 1) < game_map.length
						&& game_map[y_found + 1][x_found - 1] == type
						&& ((y_found + 1) != y || (x_found - 1) != x)) {
					return true;
				}
				if ((y_found + 1) >= 0 && (x_found) >= 0
						&& game_map[y_found + 1][x_found] == type
						&& ((y_found + 1) != y || (x_found) != x)) {
					return true;
				}
			}
		}
		return false;
	}

	private void explode(int y, int x, int type) {
		game_map[y][x] = 0;
		explode_rec(y, x, type);
		follow_explosion();
		play_collabse();
	}

	private void explode_rec(int y, int x, int type) {
		remove_from_collision_list(y, x);

		if (y % 2 == 0)// even_row
		{
			if ((y - 1) >= 0 && (x) >= 0 && game_map[y - 1][x] == type) {
				game_map[y - 1][x] = 0;
				explode_rec(y - 1, x, type);
			}
			if ((y - 1) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y - 1][x + 1] == type) {
				game_map[y - 1][x + 1] = 0;
				explode_rec(y - 1, x + 1, type);
			}
			if ((y) >= 0 && (x - 1) >= 0 && game_map[y][x - 1] == type) {
				game_map[y][x - 1] = 0;
				explode_rec(y, x - 1, type);
			}
			if ((y) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y][x + 1] == type) {
				game_map[y][x + 1] = 0;
				explode_rec(y, x + 1, type);
			}
			if ((y + 1) >= 0 && (x) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x] == type) {
				game_map[y + 1][x] = 0;
				explode_rec(y + 1, x, type);
			}
			if ((y + 1) >= 0 && (x + 1) >= 0 && (y + 1) < game_map.length
					&& (x + 1) < game_map[0].length
					&& game_map[y + 1][x + 1] == type) {
				game_map[y + 1][x + 1] = 0;
				explode_rec(y + 1, x + 1, type);
			}
		} else
		// odd_row
		{
			if ((y - 1) >= 0 && (x - 1) >= 0 && game_map[y - 1][x - 1] == type) {
				game_map[y - 1][x - 1] = 0;
				explode_rec(y - 1, x - 1, type);
			}
			if ((y - 1) >= 0 && (x) >= 0 && game_map[y - 1][x] == type) {
				game_map[y - 1][x] = 0;
				explode_rec(y - 1, x, type);
			}
			if ((y) >= 0 && (x - 1) >= 0 && game_map[y][x - 1] == type) {
				game_map[y][x - 1] = 0;
				explode_rec(y, x - 1, type);
			}
			if ((y) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y][x + 1] == type) {
				game_map[y][x + 1] = 0;
				explode_rec(y, x + 1, type);
			}
			if ((y + 1) >= 0 && (x - 1) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x - 1] == type) {
				game_map[y + 1][x - 1] = 0;
				explode_rec(y + 1, x - 1, type);
			}
			if ((y + 1) >= 0 && (x) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x] == type) {
				game_map[y + 1][x] = 0;
				explode_rec(y + 1, x, type);
			}
		}
	}

	private void end_game(boolean winner, Canvas canvas) {
		game_ended = true;
		int end_type = (winner) ? R.drawable.won : R.drawable.lost;
		Bitmap end_message = BitmapFactory.decodeResource(getResources(),
				end_type);
		canvas.drawBitmap(end_message, (int) (screen_size.x / 2 - 150),
				(int) (screen_size.y / 2 - 125), null);

		if (winner) {
			Typeface font = Typeface.defaultFromStyle(Typeface.BOLD);
			score_painter.setTypeface(font);
			score_painter.setTextSize(40);
			
			canvas.drawText("Your Score: " + score,
					(int) (screen_size.x / 2 - 150),
					(int) (screen_size.y / 2 + 20), score_painter);
			
			score_painter.setTextSize(35);
			if (LevelChooser.high_score > score) {
				
				canvas.drawText("High Score: " + LevelChooser.high_score,
						(int) (screen_size.x / 2 - 130),
						(int) (screen_size.y / 2 + 60), score_painter);
			} else {
				canvas.drawText("High Score: " + score,
						(int) (screen_size.x / 2 - 130),
						(int) (screen_size.y / 2 + 60), score_painter);
			}
		}
	}

	public boolean get_winner_state() {
		return lost;
	}

	public int get_score() {
		return score;
	}

	public void playfire() {
		// set up MediaPlayer

		try {
			fire_player.reset();
			fire_afd = context.getAssets().openFd("collision_sound.mp3");
			fire_player.setDataSource(fire_afd.getFileDescriptor(),
					fire_afd.getStartOffset(), fire_afd.getLength());
			fire_player.prepare();
			fire_player.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play_win() {
		// set up MediaPlayer

		try {
			win_player.reset();
			win_afd = context.getAssets().openFd("winner.mp3");
			win_player.setDataSource(win_afd.getFileDescriptor(),
					win_afd.getStartOffset(), win_afd.getLength());
			win_player.prepare();
			win_player.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play_lose() {
		// set up MediaPlayer

		try {
			lose_player.reset();
			lose_afd = context.getAssets().openFd("loser.mp3");
			lose_player.setDataSource(lose_afd.getFileDescriptor(),
					lose_afd.getStartOffset(), lose_afd.getLength());
			lose_player.prepare();
			lose_player.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play_collabse() {
		// set up MediaPlayer

		try {
			collision_player.reset();
			collision_afd = context.getAssets().openFd("fire.mp3");
			collision_player.setDataSource(collision_afd.getFileDescriptor(),
					collision_afd.getStartOffset(), collision_afd.getLength());
			collision_player.prepare();
			collision_player.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drop_balls(Canvas canvas) {

		if (fire_ball == 1) {
			fallen_ball_painter.setColor(Color.WHITE);
			fallen_ball_painter.setColorFilter(new LightingColorFilter(
					Color.WHITE, 0));
			fallen_ball_painter.setAlpha(75);
		} else {
			fallen_ball_painter.setColor(paint[fire_ball - 1].getColor());
			fallen_ball_painter.setColorFilter(paint[fire_ball - 1]
					.getColorFilter());
			fallen_ball_painter.setAlpha(75);
		}

		for (int i = 0; i < fallen_balls.size(); i++) {
			canvas.drawBitmap(game_tile,
					fallen_balls.get(i).x - game_play.get_tile_size() / 2,
					fallen_balls.get(i).y - game_play.get_tile_size() / 2 + map_starting_point
							+ drop_count, fallen_ball_painter);
		}
		drop_count += 20;
		if (drop_count >= screen_size.y - map_starting_point) {
			drop_count = 0;
			fallen_balls.clear();
		}
	}

	private void follow_explosion() {
		for (int i = 0; i < game_map[0].length; i++) {
			if (game_map[0][i] != 0) {
				explosion_map[0][i]=true;
				follow_explosion_rec(0, i);
			}else
			{
				visited[0][i]=true;
			}
		}

		for (int i = 0; i < game_map.length; i++) {
			for (int j = 0; j < game_map[0].length; j++) {
				if (!explosion_map[i][j] && game_map[i][j] != 0) {
					fallen_balls.add(get_center(j, i));
					game_map[i][j] = 0;
					remove_from_collision_list(i, j);
				}
				explosion_map[i][j] = false;
				visited[i][j] = false;
			}
		}
	}

	private void remove_from_collision_list(int y, int x) {
		Point p = get_center(x, y);
		for (int i = 0; i < collision_list.size(); i++) {
			if (collision_list.get(i).x == p.x
					&& collision_list.get(i).y == p.y) {
				collision_list.remove(i);
				fallen_balls.add(p);
				score += (int) (10.0 * ((double) (screen_size.y
						- map_starting_point + TOP_MARGIN) / (double) screen_size.y));
				break;
			}
		}
	}

	private void follow_explosion_rec(int y, int x) {
		if (!visited[y][x]) {
			visited[y][x] = true;
		} else {
			return;
		} 
		if (y % 2 == 0)// even_row
		{
			if ((y - 1) >= 0 && (x) >= 0 && game_map[y - 1][x] != 0) {
				explosion_map[y - 1][x] = true;
				follow_explosion_rec(y - 1, x);
			}
			if ((y - 1) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y - 1][x + 1] != 0) {
				explosion_map[y - 1][x + 1] = true;
				follow_explosion_rec(y - 1, x + 1);
			}
			if ((y) >= 0 && (x - 1) >= 0 && game_map[y][x - 1] != 0) {
				explosion_map[y][x - 1] = true;
				follow_explosion_rec(y, x - 1);
			}
			if ((y) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y][x + 1] != 0) {
				explosion_map[y][x + 1] = true;
				follow_explosion_rec(y, x + 1);
			}
			if ((y + 1) >= 0 && (x) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x] != 0) {
				explosion_map[y + 1][x] = true;
				follow_explosion_rec(y + 1, x);
			}
			if ((y + 1) >= 0 && (x + 1) >= 0 && (y + 1) < game_map.length
					&& (x + 1) < game_map[0].length
					&& game_map[y + 1][x + 1] != 0) {
				explosion_map[y + 1][x + 1] = true;
				follow_explosion_rec(y + 1, x + 1);
			}
		} else
		// odd_row
		{
			if ((y - 1) >= 0 && (x - 1) >= 0 && game_map[y - 1][x - 1] != 0) {
				explosion_map[y - 1][x - 1] = true;
				follow_explosion_rec(y - 1, x - 1);
			}
			if ((y - 1) >= 0 && (x) >= 0 && game_map[y - 1][x] != 0) {
				explosion_map[y - 1][x] = true;
				follow_explosion_rec(y - 1, x);
			}
			if ((y) >= 0 && (x - 1) >= 0 && game_map[y][x - 1] != 0) {
				explosion_map[y][x - 1] = true;
				follow_explosion_rec(y, x - 1);
			}
			if ((y) >= 0 && (x + 1) >= 0 && (x + 1) < game_map[0].length
					&& game_map[y][x + 1] != 0) {
				explosion_map[y][x + 1] = true;
				follow_explosion_rec(y, x + 1);
			}
			if ((y + 1) >= 0 && (x - 1) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x - 1] != 0) {
				explosion_map[y + 1][x - 1] = true;
				follow_explosion_rec(y + 1, x - 1);
			}
			if ((y + 1) >= 0 && (x) >= 0 && (y + 1) < game_map.length
					&& game_map[y + 1][x] != 0) {
				explosion_map[y + 1][x] = true;
				follow_explosion_rec(y + 1, x);
			}
		}

	}

}
