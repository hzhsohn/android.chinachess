package han.zh.chinachess;

import han.zh.chinachess.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

public class SettingManager implements DialogInterface.OnClickListener {

	private static final String KEY_BG_MUSIC = "bg_music";
	private static final String KEY_GAME_LEVEL = "game_level";

	private boolean musicOn = true;
	private int gameLevel = LEVEL_NORMAL;

	private Activity activity = null;
	private SharedPreferences setting;
	private View dialogView;

	private static SettingManager self = null;

	// game level
	public static final int LEVEL_EASY = 1;
	public static final int LEVEL_NORMAL = 2;
	public static final int LEVEL_HARD = 3;

	private SettingManager(Activity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		setting = this.activity.getPreferences(Activity.MODE_PRIVATE);
		this.musicOn = setting.getBoolean(KEY_BG_MUSIC, true);
		this.gameLevel = setting.getInt(KEY_GAME_LEVEL, LEVEL_NORMAL);
	}

	public static SettingManager getInstace(Activity activity) {
		if (self == null && activity != null) {
			self = new SettingManager(activity);
		}
		return self;
	}

	public void release() {
		self = null;
	}

	public boolean saveSetting() {
		SharedPreferences.Editor editor = setting.edit();
		editor.putBoolean(KEY_BG_MUSIC, musicOn);
		editor.putInt(KEY_GAME_LEVEL, gameLevel);
		editor.commit();
		return true;
	}

	public boolean getMusicOnOff() {
		return this.musicOn;
	}

	public int getGameLevel() {
		return this.gameLevel;
	}

	public void openSettingDialog() {
		LayoutInflater factory = LayoutInflater.from(activity);
		dialogView = factory.inflate(R.layout.game_setting, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.game_setting);
		builder.setPositiveButton(R.string.bt_str_ok, this);
		builder.setNegativeButton(R.string.bt_str_cancel, null);
		builder.setView(dialogView);
		AlertDialog dialog = builder.create();

		RadioGroup music = (RadioGroup) dialogView.findViewById(R.id.music_set);
		if (getMusicOnOff() == true)
			music.check(R.id.music_on);
		else
			music.check(R.id.music_off);

		RadioGroup level = (RadioGroup) dialogView.findViewById(R.id.level_setting);
		if (getGameLevel() == SettingManager.LEVEL_EASY)
			level.check(R.id.level_easy);
		else if (getGameLevel() == SettingManager.LEVEL_NORMAL)
			level.check(R.id.level_normal);
		else if (getGameLevel() == SettingManager.LEVEL_HARD)
			level.check(R.id.level_hard);
		else
			level.check(R.id.level_normal);

		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if (which == AlertDialog.BUTTON_POSITIVE) {
			// save music value
			RadioGroup music = (RadioGroup) dialogView.findViewById(R.id.music_set);
			if (music.getCheckedRadioButtonId() == R.id.music_on) {
				musicOn = true;
			} else {
				musicOn = false;
			}

			// save game level value
			RadioGroup level = (RadioGroup) dialogView.findViewById(R.id.level_setting);
			if (level.getCheckedRadioButtonId() == R.id.level_easy) {
				gameLevel = SettingManager.LEVEL_EASY;
			} else if (level.getCheckedRadioButtonId() == R.id.level_normal) {
				gameLevel = SettingManager.LEVEL_NORMAL;
			} else if (level.getCheckedRadioButtonId() == R.id.level_hard) {
				gameLevel = SettingManager.LEVEL_HARD;
			} else {
				gameLevel = SettingManager.LEVEL_NORMAL;
			}

			saveSetting();
		}
	}
}
