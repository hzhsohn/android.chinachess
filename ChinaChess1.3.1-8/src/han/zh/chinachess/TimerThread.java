package han.zh.chinachess;

import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import chinachess.mid.MID_ChessBoard.SIDE;

public class TimerThread extends TimerTask {
	private static final String TAG = TimerThread.class.getSimpleName();

	private Timer timer;
	private int personTime = 0;
	private int computerTime = 0;
	private boolean pause = false;
	private ChessBoard board = null;

	private TimerListener listener;

	private static TimerThread self = null;

	public interface TimerListener {
		void updateTime(int computerTime, int personTime);
	}

	private TimerThread(ChessBoard board) {
		this.board = board;
		timer = new Timer();
		timer.schedule(this, 1000, 1000);
	}

	public static TimerThread getInstace(ChessBoard board) {
		if (self == null) {
			self = new TimerThread(board);
		}
		return self;
	}

	public void setListener(TimerListener listener) {
		this.listener = listener;
	}

	public int getComputerTime() {
		return this.computerTime;
	}

	public int getPersonTime() {
		return this.personTime;
	}

	public boolean isPause() {
		return pause;
	}

	public void pauseTimer() {
		pause = true;
	}

	public synchronized void resumeTimer() {
		if (pause == true) {
			pause = false;
			notify();
		}
	}

	public void resetTimer() {
		computerTime = 0;
		personTime = 0;
	}

	public void exit() {
		timer.cancel();
		timer = null;
		self = null;
	}

	@Override
	public void run() {
		while (pause) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
				}
			}
		}
		if (board.getCurSide() == SIDE.BLACK)
			computerTime++;
		else
			personTime++;
		listener.updateTime(computerTime, personTime);
	}
}
