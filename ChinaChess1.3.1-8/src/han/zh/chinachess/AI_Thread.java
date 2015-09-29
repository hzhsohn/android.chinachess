package han.zh.chinachess;

import android.os.Message;
import android.util.Log;
import chinachess.mid.MID_ChessBoard;

public class AI_Thread extends Thread {
	private static final String TAG = AI_Thread.class.getSimpleName();

	private ChessBoard chessBoard = null;
	private GameView gameView = null;
	private static AI_Thread self = null;

	private AI_Thread(ChessBoard board, GameView view) {
		chessBoard = board;
		gameView = view;
	}

	public static AI_Thread getInstace(ChessBoard board, GameView view) {
		if (self == null) {
			self = new AI_Thread(board, view);
		}
		return self;
	}

	public synchronized void stopThread() {
		Thread.currentThread().interrupt();
		self = null;
	}

	public synchronized void resumeThread() {
		notifyAll();
	}

	@Override
	public void run() {
		boolean result;
		while (!Thread.currentThread().isInterrupted()) {
			result = false;
			synchronized (this) {
				if (chessBoard.checkGameOver(false) || (chessBoard.getCurSide() != MID_ChessBoard.SIDE.BLACK)) {
					try {
						wait();
					} catch (InterruptedException e) { // quit thread
						Log.e(TAG, e.toString());
						Thread.currentThread().interrupt(); // exit AI thread
					} catch (Exception e) { // quit thread
						Log.e(TAG, e.toString());
						Thread.currentThread().interrupt(); // exit AI thread
					}
					continue;
				}
			}

			result = chessBoard.doNextStep();
			if (result == true) {
				Message msg = new Message();
				msg.what = GameView.HANDLER_WHAT_COMPUTER_DONE;
				gameView.handler.sendMessage(msg);
				msg = null;
			}
		}
		chessBoard = null;
		gameView = null;
		self = null;
	}
}
