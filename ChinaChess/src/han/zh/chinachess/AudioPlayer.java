package han.zh.chinachess;

import han.zh.chinachess.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer {
	private static final String TAG = AudioPlayer.class.getSimpleName();

	private MediaPlayer bgPlayer = null;
	private PLAYER_STATUS playerStatus = PLAYER_STATUS.STOP;
	private int curPosition = 0;

	private static AudioPlayer self = null;

	public enum PLAYER_STATUS {
		STOP, PLAYING, PAUSE,
	}

	private AudioPlayer(Activity activity) {
		bgPlayer = MediaPlayer.create(activity, R.raw.bg_music);
		if (bgPlayer != null)
			bgPlayer.setLooping(true);
		else
			Log.e(TAG, "create music player fail");
	}

	public static AudioPlayer getInstace(Activity activity) {
		if (self == null) {
			self = new AudioPlayer(activity);
		}
		return self;
	}

	public boolean playerReady() {
		return (bgPlayer == null) ? false : true;
	}

	public void Play() {
		if (bgPlayer == null) {
			// Log.e(TAG, "music player is null");
			return;
		}
		if (playerStatus == PLAYER_STATUS.STOP) {
			try {
				// bgPlayer.prepare();
				bgPlayer.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.toString());
			}
			playerStatus = PLAYER_STATUS.PLAYING;
		} else if (playerStatus == PLAYER_STATUS.PAUSE) {
			Resume();
		}
	}

	public void Pause() {
		if (bgPlayer == null) {
			// Log.e(TAG, "music player == null");
			return;
		}
		if (playerStatus == PLAYER_STATUS.PLAYING) {
			curPosition = bgPlayer.getCurrentPosition();
			bgPlayer.pause();
			playerStatus = PLAYER_STATUS.PAUSE;
		}
	}

	public void Resume() {
		if (bgPlayer == null) {
			// Log.e(TAG, "music player == null");
			return;
		}
		if (playerStatus == PLAYER_STATUS.PAUSE) {
			bgPlayer.seekTo(curPosition);
			bgPlayer.start();
			playerStatus = PLAYER_STATUS.PLAYING;
		}
	}

	public void Stop() {
		if (bgPlayer == null) {
			// Log.e(TAG, "music player == null");
			return;
		}
		if (playerStatus != PLAYER_STATUS.STOP) {
			try {
				bgPlayer.stop();
			} catch (IllegalStateException e) {
				Log.e(TAG, e.toString());
			}
			playerStatus = PLAYER_STATUS.STOP;
		}
	}

	public final PLAYER_STATUS getPlayerStatus() {
		return this.playerStatus;
	}

	public void release() {
		if (bgPlayer != null) {
			Stop();
			bgPlayer.release();
			bgPlayer = null;
		}
		self = null;
	}

}
