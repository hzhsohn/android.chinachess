package han.zh.chinachess;

import han.zh.chinachess.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GameView extends AbstractView implements TimerThread.TimerListener {
	private static final String TAG = GameView.class.getSimpleName();

	public enum GameStatus {
		RUNNING, PAUSE, STOP,
	}

	public static final int HANDLER_WHAT_GAME_OVER = 0x01;
	public static final int HANDLER_WHAT_COMPUTER_DONE = 0x02;
	public static final int HANDLER_WHAT_UPDATE_TIME = 0x03;

	private static final int REFERENCE_CHESS_START_X = 21;
	private static final int REFERENCE_CHESS_START_Y = 35;
	private static final int REFERENCE_BOARD_GRID_W = 35;
	private static final int REFERENCE_BOARD_GRID_H = 35;

	/**
	 * button point info reference
	 */
	private static final int REFERENCE_BUTTON_START_Y = 385;
	private static final int REFERENCE_NEW_GAME_START_X = 30;
	private static final int REFERENCE_PAUSE_START_X = 130;
	private static final int REFERENCE_UNDO_START_X = 230;
	/**
	 * Board,chess width and height
	 */
	public int chessStartX;
	public int chessStartY;
	public int boardGridW;
	public int boardGridH;
	public int boardGridHalfW;
	public int boardGridHalfH;

	// screen width & height
	private int screenWidth;
	private int screenHeight;

	// origin button width & height
	private int oriBtWidth;
	private int oriBtHeight;

	// background bitmap width & height
	private int bgBmpWidth;
	private int bgBmpHeight;

	// button start Y & End y
	private int buttonStartY;
	private int buttonEndY;
	private int buttonWidth;
	private int buttonHeight;

	// new game button point
	private int btNewGameStartX;
	private int btNewGameEndX;

	// undo button point
	private int btUndoStartX;
	private int btUndoEndX;

	// pause button point
	private int btPauseStartX;
	private int btPauseEndX;

	// computer and player time
	private int strTimeStartY;
	private int strComputerStartX;
	private int strPlayerStartX;

	// private Bitmap btBmpUp = null;
	private Bitmap btBmpUp_New = null;
	private Bitmap btBmpUP_PauseOrResume = null;
	private Bitmap btBmpUp_Pause = null;
	private Bitmap btBmpUp_Resume = null;
	private Bitmap btBmpUp_Undo = null;
	// private Bitmap btBmpDown = null;
	private Bitmap bgBmp; // chess board bitmap

	// button rect
	private Rect SrcRect;
	private Rect DstRect;

	private boolean mPauseKeyPressed = false;
	private GameStatus mGameStatus = GameStatus.STOP;
	private boolean isClickDown;
	private StringBuilder stringBuilder;
	private Paint paint;
	private Point eventPoint;
	private ChessBoard board;
	private MainActivity activity;
	private ControlView parentView;
	private AI_Thread mAiThread;
	private TimerThread timer;

	/**
	 * @param context
	 *            activity objece
	 * @param view
	 *            parent view
	 */
	public GameView(Context context, ControlView view) {
		super(context);
		// TODO Auto-generated constructor stub
		activity = (MainActivity) context;
		parentView = view;

		// get screen width & height scale
		float scaleValueW = MainActivity.getScaleWidth();
		float scaleValueH = MainActivity.getScaleHeight();

		// get screen width & height
		screenWidth = MainActivity.getScreenWidth();
		screenHeight = MainActivity.getScreenHeight();

		// btBmpDown = BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.button_down);
		btBmpUp_New = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_up_new);
		btBmpUp_Pause = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_up_pause);
		btBmpUp_Resume = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_up_resume);
		btBmpUp_Undo = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_up_undo);
		bgBmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);

		btBmpUP_PauseOrResume = btBmpUp_Pause;

		// background bitmap width & height
		bgBmpWidth = bgBmp.getWidth();
		bgBmpHeight = bgBmp.getHeight();

		// button origin width & height
		oriBtWidth = btBmpUp_New.getWidth();
		oriBtHeight = btBmpUp_New.getHeight();

		// chess board info
		chessStartX = (int) (REFERENCE_CHESS_START_X * scaleValueW);
		chessStartY = (int) (REFERENCE_CHESS_START_Y * scaleValueH);
		boardGridW = (int) (REFERENCE_BOARD_GRID_W * scaleValueW);
		boardGridH = (int) (REFERENCE_BOARD_GRID_H * scaleValueH);
		boardGridHalfW = boardGridW / 2;
		boardGridHalfH = boardGridH / 2;

		// button point
		buttonWidth = (int) (oriBtWidth * scaleValueW);
		if(buttonWidth>140){buttonWidth=140;}
		buttonHeight = (int) (oriBtHeight * scaleValueH);
		if(buttonHeight>90){buttonHeight=90;}
		buttonStartY = (int) (REFERENCE_BUTTON_START_Y * scaleValueH);
		buttonEndY = buttonStartY + buttonHeight;

		//新建,暂停,悔棋按钮的坐标
		btUndoStartX = (int) (REFERENCE_UNDO_START_X * scaleValueW);
		btUndoEndX = btUndoStartX + buttonWidth;
		btPauseStartX = (int) (REFERENCE_PAUSE_START_X * scaleValueW);
		btPauseEndX = btPauseStartX + buttonWidth;
		btNewGameStartX = (int) (REFERENCE_NEW_GAME_START_X * scaleValueW);
		btNewGameEndX = btNewGameStartX + buttonWidth;

		// computer and player time point
		strTimeStartY = buttonEndY +10;
		strComputerStartX = btNewGameStartX + 28;
		strPlayerStartX = btUndoStartX - 48;

		// button rect
		SrcRect = new Rect();
		DstRect = new Rect();

		eventPoint = new Point();
		paint = new Paint();

		paint.setColor(Color.WHITE);
		paint.setTypeface(Typeface.SERIF);

		board = ChessBoard.getInstace(this); // app chess board object
		mAiThread = AI_Thread.getInstace(board, this);
		mAiThread.start();

		setGameLevel(SettingManager.getInstace(null).getGameLevel());
		stringBuilder = new StringBuilder();
		timer = TimerThread.getInstace(board);
		timer.setListener(this);

		mGameStatus = GameStatus.RUNNING;
	}

	/**
	 * release game view resource
	 */
	public void release() {
		board.release();
		mAiThread.stopThread();
		try {
			mAiThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
		mAiThread = null;

		timer.exit();
		timer = null;
		mGameStatus = GameStatus.STOP;
	}

	public void showGameOver(int strID) {
		timer.pauseTimer();
		this.activity.showGameOverDialog(strID);
	}

	public GameStatus getGameStatus() {
		return mGameStatus;
	}

	public boolean isPauseKeyPressed() {
		return mPauseKeyPressed;
	}

	/**
	 * show chess board background
	 * 
	 * @param canvas
	 */
	private void drawBackground(Canvas canvas) {
		// draw chess board background
		SrcRect.set(0, 0, bgBmpWidth, bgBmpHeight);
		DstRect.set(0, 0, screenWidth, screenHeight);
		canvas.drawBitmap(bgBmp, SrcRect, DstRect, null);

		//悔棋按钮
		// draw undo button
		SrcRect.set(0, 0, oriBtWidth, oriBtHeight);
		DstRect.set(btUndoStartX, buttonStartY, btUndoEndX, buttonEndY);
		canvas.drawBitmap(btBmpUp_Undo, SrcRect, DstRect, null);

		//暂停按钮
		// draw pause button
		SrcRect.set(0, 0, oriBtWidth, oriBtHeight);
		DstRect.set(btPauseStartX, buttonStartY, btPauseEndX, buttonEndY);
		canvas.drawBitmap(btBmpUP_PauseOrResume, SrcRect, DstRect, null);

		//新建按钮
		// draw new game button
		SrcRect.set(0, 0, oriBtWidth, oriBtHeight);
		DstRect.set(btNewGameStartX, buttonStartY, btNewGameEndX, buttonEndY);
		canvas.drawBitmap(btBmpUp_New, SrcRect, DstRect, null);

		//显示时间
		int time;
		int data;
		// draw computer time
		time = timer.getComputerTime();
		stringBuilder.setLength(0);
		stringBuilder.append(getResources().getString(R.string.gameView_computer));
		stringBuilder.append(":");
		data = time / 3600;
		if (data <= 9) {
			stringBuilder.append(0);
		}
		stringBuilder.append(data);
		stringBuilder.append(":");
		data = time / 60 % 60;
		if (data <= 9) {
			stringBuilder.append(0);
		}
		stringBuilder.append(data);
		stringBuilder.append(":");
		data = time % 60;
		if (data <= 9) {
			stringBuilder.append(0);
		}
		stringBuilder.append(data);
		canvas.drawText(stringBuilder.toString(), strComputerStartX, strTimeStartY, paint);

		// draw player time
		time = timer.getPersonTime();
		stringBuilder.setLength(0);
		stringBuilder.append(getResources().getString(R.string.gameView_player));
		stringBuilder.append(":");
		data = time / 3600;
		if (data <= 9) {
			stringBuilder.append(0);
		}
		stringBuilder.append(data);
		stringBuilder.append(":");
		data = time / 60 % 60;
		if (data <= 9) {
			stringBuilder.append(0);
		}
		stringBuilder.append(data);
		stringBuilder.append(":");
		data = time % 60;
		if (data <= 9) {
			stringBuilder.append(0);
		}
		stringBuilder.append(data);
		canvas.drawText(stringBuilder.toString(), strPlayerStartX, strTimeStartY, paint);
	}

	/**
	 * get bitmap object by resource id
	 * 
	 * @param id
	 * @return
	 */
	public Bitmap getIconByID(int id) {
		return BitmapFactory.decodeResource(this.getResources(), id);
	}

	public void pauseGame() {
		if (timer != null) {
			timer.pauseTimer();
		}
		this.activity.pauseMusic();
		this.btBmpUP_PauseOrResume = this.btBmpUp_Resume;

		mGameStatus = GameStatus.PAUSE;
	}

	public void resumeGame() {
		if (timer != null) {
			timer.resumeTimer();
		}
		this.activity.resumeMusic();
		this.btBmpUP_PauseOrResume = this.btBmpUp_Pause;

		mGameStatus = GameStatus.RUNNING;
	}

	public boolean undoStep() {
		return this.board.undoStep();
	}

	/**
	 * 设置游戏等级：简单，一般，困难
	 * 
	 * @param level
	 */
	public void setGameLevel(int level) {
		switch (level) {
		case SettingManager.LEVEL_EASY:
			board.setLevel(SettingManager.LEVEL_EASY);
			break;
		case SettingManager.LEVEL_NORMAL:
			board.setLevel(SettingManager.LEVEL_NORMAL);
			break;
		case SettingManager.LEVEL_HARD:
			board.setLevel(SettingManager.LEVEL_HARD);
			break;
		default:
			break;
		}
	}

	/**
	 * 新建棋局
	 * 
	 * @return
	 */
	public boolean newGame() {
		boolean result = board.resetChessBoard();
		timer.resetTimer();
		timer.resumeTimer();
		this.activity.resumeMusic();
		mGameStatus = GameStatus.RUNNING;
		updateView(); // refresh screen
		return result;
	}

	public Chess[] getAllChess() {
		return board.getChessArray();
	}

	/**
	 * 将当前棋局存入指定文件
	 * 
	 * @param manager
	 * @param fileName
	 * @return
	 */
	public boolean saveChessBoard(SaveManager manager, String fileName) {
		try {
			manager.saveData(board.getChessArray(), fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 从指定文件载入棋局
	 * 
	 * @param manager
	 * @param fileName
	 * @return
	 */
	public boolean loadChessBoard(SaveManager manager, String fileName) {
		try {
			manager.loadData(board.getChessArray(), fileName);
			board.updateChessBoard();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
			return false;
		}
		timer.resetTimer();
		mGameStatus = GameStatus.RUNNING;
		updateView();
		return true;
	}

	/**
	 * 更新画面
	 */
	public void updateView() {
		parentView.updateCanvas();
	}

	/**
	 * 绘制所有棋子
	 * 
	 * @param canvas
	 */
	private void drawAllChess(Canvas canvas) {
		board.drawAllChess(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chinachess.app.AbstractView#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		drawBackground(canvas); // draw background board
		drawAllChess(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if ((event.getY() >= buttonStartY) && (event.getY() <= buttonEndY)) {
			if ((event.getX() >= btNewGameStartX) && (event.getX() <= btNewGameEndX)) { // touch
																						// on
																						// new
																						// game
																						// button
				this.newGame();
			} else if ((event.getX() >= btUndoStartX) && (event.getX() <= btUndoEndX)) { // touch
																							// on
																							// undo
																							// button
				if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					if (this.isClickDown == true)
						this.isClickDown = false;
					else
						this.isClickDown = true;
				}
				if (board.undoStep() == true) {
					this.timer.resumeTimer(); // resume timer
					// updateView();
				}
			} else if ((event.getX() >= btPauseStartX) && (event.getX() <= btPauseEndX)) { // touch
																							// on
																							// pause
																							// button
				if (mGameStatus == GameStatus.PAUSE) {
					mPauseKeyPressed = false;
					this.resumeGame();
				} else if (mGameStatus == GameStatus.RUNNING) {
					mPauseKeyPressed = true;
					this.pauseGame();
				}
			}
			this.updateView();
		}

		if ((event.getX() <= this.bgBmpWidth) && (event.getY() <= this.bgBmpHeight)) { // touch
																						// in
																						// the
																						// chess
																						// board
																						// region
			if (board.checkGameOver(false)) { // game is over
				this.showGameOver(R.string.game_over);
				return true;
			}
			eventPoint.x = (int) event.getX();
			eventPoint.y = (int) event.getY();
			if (board.ConfirmPiecePoint(eventPoint) == true) { // click point in
																// the chess
				if (mGameStatus != GameStatus.RUNNING) // Game paused or stoped
				{
					return false;
				}
				if (board.eventOnChess(eventPoint)) {
					// Log.d(TAG, "need to update ui, curent side " +
					// board.getCurSide());
					updateView(); // update screen
					if (board.checkGameOver(false) == true) { // game is over
																// and show
																// dialog
						return true;
					}
					mAiThread.resumeThread(); // resume AI thread
				}
			}
		}
		return true;
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GameView.HANDLER_WHAT_GAME_OVER:
				GameView.this.showGameOver(msg.arg1);
				break;
			case GameView.HANDLER_WHAT_COMPUTER_DONE:
				GameView.this.updateView();
				board.checkGameOver(true);
				break;
			case GameView.HANDLER_WHAT_UPDATE_TIME:
				break;
			default:
				break;
			}
			msg = null;
		}
	};

	@Override
	public void updateTime(int computerTime, int personTime) {
		// TODO Auto-generated method stub
		this.updateView();
	}
}
