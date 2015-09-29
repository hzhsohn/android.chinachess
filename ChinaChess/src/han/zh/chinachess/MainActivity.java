package han.zh.chinachess;

import han.zh.chinachess.R;
import han.zh.chinachess.AbstractView.ViewType;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	public static final int REFERENCE_VALUE_WIDTH = 320;
	public static final int REFERENCE_VALUE_HEIGHT = 480;

	private static int screenWidth = 0;
	private static int screenHeight = 0;

	private static float scaleValueW = 1;
	private static float scaleValueH = 1;

	// menu item id
	private static final int MENU_ITEM_NEW_GAME = 0;
	private static final int MENU_ITEM_LOAD_GAME = 1;
	private static final int MENU_ITEM_SAVE_GAME = 2;
	private static final int MENU_ITEM_GAME_SETTING = 3;
	private static final int MENU_ITEM_GAME_EXIT = 4;
	private static final int MENU_ITEM_GAME_RETURN = 5;
	private static final int MENU_COMMENT = 6;

	private ControlView controlView = null;
	private SettingManager setting = null;
	private SaveManager saveManager = null;
	private AudioPlayer audioPlayer = null;

	private LinearLayout layout;

	AdView adView;

	static {
		try {
			System.loadLibrary("ChinaChess");
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		MainActivity.screenHeight = dm.heightPixels; // get screen height
		MainActivity.screenWidth = dm.widthPixels; // get screen width
		if (screenWidth != REFERENCE_VALUE_WIDTH)
			scaleValueW = (float) screenWidth / (float) REFERENCE_VALUE_WIDTH;
		if (screenHeight != REFERENCE_VALUE_HEIGHT)
			scaleValueH = (float) screenHeight / (float) REFERENCE_VALUE_HEIGHT;

		setting = SettingManager.getInstace(this);
		audioPlayer = AudioPlayer.getInstace(this);
		if (audioPlayer.playerReady() == false) {
			Log.e(TAG, "music player is null");
			Toast.makeText(this, R.string.player_null, Toast.LENGTH_LONG)
					.show();
		} else if (setting.getMusicOnOff() == true) {
			audioPlayer.Play();
		}

		controlView = ControlView.getInstace(this);
		saveManager = SaveManager.getInstace(this);

		// Create the AD adView
		adView = new AdView(this, AdSize.BANNER, "ca-app-pub-8018207524434638/9590449301");
		// 启动一般性请求并在其中加载广告
		adView.loadAd(new AdRequest());

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 30);
		params.bottomMargin = 0;
		params.gravity = Gravity.BOTTOM;
		layout = new LinearLayout(this);
		layout.setLayoutParams(params);
		layout.addView(adView);// 广告
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(controlView);
		setContentView(layout);

		// this.setContentView(controlView);
		openMainMenu();
	}

	/**
	 * get screen width
	 * 
	 * @return max screen width
	 */
	public static int getScreenWidth() {
		return MainActivity.screenWidth;
	}

	/**
	 * get screen height
	 * 
	 * @return max screen height
	 */
	public static int getScreenHeight() {
		return MainActivity.screenHeight;
	}

	public static float getScaleWidth() {
		return MainActivity.scaleValueW;
	}

	public static float getScaleHeight() {
		return MainActivity.scaleValueH;
	}

	/**
	 * show main menu
	 */
	public void openMainMenu() {
		controlView.switchContentView(ViewType.MAIN_MENU);
	}

	/**
	 * show help view
	 */
	public void openHelpView() {
		controlView.switchContentView(ViewType.HELP_ABOUT);
	}

	/**
	 * show chess board
	 */
	public void startGame() {
		controlView.switchContentView(ViewType.GAME);
	}

	public void updateBottomView() {
		this.layout.getChildAt(1).postInvalidate();
	}

	/**
	 * quit game, release resource
	 */
	public void exitGame() {
		if (controlView != null) {
			controlView.release();
			controlView = null;
		}

		if (setting != null) {
			setting.release();
			setting = null;
		}

		if (audioPlayer != null) {
			audioPlayer.release();
			audioPlayer = null;
		}

		if (saveManager != null) {
			saveManager.release();
			saveManager = null;
		}

		this.finish();
	}

	/**
	 * pause background music
	 */
	public void pauseMusic() {
		if (setting == null || audioPlayer == null)
			return;

		if (setting.getMusicOnOff() && audioPlayer.playerReady()) {
			audioPlayer.Pause();
		}
	}

	/**
	 * resume background music
	 */
	public void resumeMusic() {
		if (setting == null || audioPlayer == null)
			return;

		if (setting.getMusicOnOff() && audioPlayer.playerReady()) {
			audioPlayer.Play();
		}
	}

	/**
	 * show setting dialog
	 */
	public void openSettingDialog() {
		setting.openSettingDialog();
	}

	/**
	 * open game load dialog
	 */
	public void openLoadDialog() {
		saveManager.openLoadDialog();
	}

	/**
	 * open game save dialog
	 */
	public void openSaveDialog() {
		saveManager.openSaveDialog();
	}

	/**
	 * show game over dialog
	 */
	public void showGameOverDialog(int strID) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strID);
		builder.setPositiveButton(R.string.bt_str_ok,
				new GameOverDialogListener());
		builder.setNegativeButton(R.string.bt_str_cancel, null);
		builder.create().show();
	}

	/**
	 * game over dialog listener
	 */
	private class GameOverDialogListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {

			if (controlView.getCurrentViewType() == AbstractView.ViewType.GAME) {
				((GameView) controlView.getCurrentView()).newGame();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_NEW_GAME, Menu.NONE, R.string.new_game);
		menu.add(0, MENU_ITEM_LOAD_GAME, Menu.NONE, R.string.load_game);
		menu.add(0, MENU_ITEM_SAVE_GAME, Menu.NONE, R.string.save_game);
		menu.add(0, MENU_ITEM_GAME_SETTING, Menu.NONE, R.string.game_setting);
		menu.add(0, MENU_ITEM_GAME_EXIT, Menu.NONE, R.string.game_exit);
		menu.add(0, MENU_ITEM_GAME_RETURN, Menu.NONE, R.string.game_return);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		ViewType type = controlView.getCurrentViewType();
		if (type == AbstractView.ViewType.GAME) {
			menu.getItem(MENU_ITEM_NEW_GAME).setVisible(false);
			menu.getItem(MENU_ITEM_GAME_EXIT).setVisible(false);

			menu.getItem(MENU_ITEM_GAME_RETURN).setVisible(true);
			menu.getItem(MENU_ITEM_SAVE_GAME).setVisible(true);
			menu.getItem(MENU_ITEM_LOAD_GAME).setVisible(true);
			menu.getItem(MENU_ITEM_GAME_SETTING).setVisible(true);
		} else if (type == ViewType.HELP_ABOUT) {
			menu.getItem(MENU_ITEM_GAME_RETURN).setVisible(false);
			menu.getItem(MENU_ITEM_GAME_EXIT).setVisible(false);
			menu.getItem(MENU_ITEM_NEW_GAME).setVisible(false);
			menu.getItem(MENU_ITEM_SAVE_GAME).setVisible(false);
			menu.getItem(MENU_ITEM_LOAD_GAME).setVisible(false);
			menu.getItem(MENU_ITEM_GAME_SETTING).setVisible(false);
		} else if (type == ViewType.MAIN_MENU) {
			menu.getItem(MENU_ITEM_SAVE_GAME).setVisible(false);
			menu.getItem(MENU_ITEM_GAME_RETURN).setVisible(false);

			menu.getItem(MENU_ITEM_NEW_GAME).setVisible(true);
			menu.getItem(MENU_ITEM_GAME_EXIT).setVisible(true);
			menu.getItem(MENU_ITEM_LOAD_GAME).setVisible(true);
			menu.getItem(MENU_ITEM_GAME_SETTING).setVisible(true);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_NEW_GAME: // start game
			this.startGame();
			break;
		case MENU_ITEM_LOAD_GAME: // load
			this.openLoadDialog();
			break;
		case MENU_ITEM_SAVE_GAME: // save
			this.openSaveDialog();
			break;
		case MENU_ITEM_GAME_SETTING: // game setting dialog
			this.openSettingDialog();
			break;
		case MENU_ITEM_GAME_RETURN: // return to main menu view
			this.openMainMenu();
			break;
		case MENU_ITEM_GAME_EXIT: // exit game
			this.exitGame();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (controlView != null) {
			controlView.onKeyUp(keyCode, event);
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (controlView != null
				&& controlView.getCurrentViewType() == ViewType.GAME) {
			((GameView) controlView.getCurrentView()).pauseGame(); // Pause
																	// timer &
																	// music
		} else {
			pauseMusic(); // Pause music
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (controlView != null
				&& controlView.getCurrentViewType() == ViewType.GAME) {
			GameView gameView = (GameView) controlView.getCurrentView();
			if (gameView.isPauseKeyPressed() == false
					&& gameView.getGameStatus() == GameView.GameStatus.PAUSE) {
				gameView.resumeGame(); // Resume timer & music
			}
		} else {
			resumeMusic(); // Resume music
		}

	}

	@Override
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
		exitGame();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		if (hasFocus == true) {
			if (setting == null || audioPlayer == null)
				return;

			if (controlView != null
					&& controlView.getCurrentViewType() == ViewType.GAME) {
				GameView gameView = (GameView) controlView.getCurrentView();
				if (setting.getMusicOnOff()
						&& gameView.getGameStatus() == GameView.GameStatus.RUNNING) {
					audioPlayer.Play();
				} else {
					audioPlayer.Pause();
				}
			} else {
				if (setting.getMusicOnOff()) {
					audioPlayer.Play();
				} else {
					audioPlayer.Pause();
				}
			}
		}
	}

	// 处理键盘事件
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.alert);
			builder.setMessage(R.string.is_exit_app);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
							System.gc();
							android.os.Process.killProcess(android.os.Process
									.myPid());
						}
					});
			builder.setNeutralButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.show();
			return true;
		} else {

			return super.onKeyDown(keyCode, event);
		}
	}

}