package han.zh.chinachess;

import han.zh.chinachess.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import chinachess.mid.MID_ChessBoard;
import chinachess.mid.NativeInterface;
import chinachess.mid.MID_ChessBoard.PiecePosition;
import chinachess.mid.MID_ChessBoard.SIDE;
import chinachess.mid.MID_IPiece.PieceColor;
import chinachess.mid.MID_IPiece.PieceStatus;

public class ChessBoard /* implements MID_ChessBoard.BoardEventListener *//*
																		 * NativeInterface
																		 * .
																		 * NativeCallback
																		 */{
	private static final String TAG = ChessBoard.class.getSimpleName();

	/**
	 * chess bitmap
	 */
	// black chess
	private Bitmap bmpBlackKing;
	private Bitmap bmpBlackCar;
	private Bitmap bmpBlackHorse;
	private Bitmap bmpBlackElephant;
	private Bitmap bmpBlackBishop;
	private Bitmap bmpBlackCanon;
	private Bitmap bmpBlackPawn;
	// red chess
	private Bitmap bmpRedKing;
	private Bitmap bmpRedCar;
	private Bitmap bmpRedHorse;
	private Bitmap bmpRedElephant;
	private Bitmap bmpRedBishop;
	private Bitmap bmpRedCanon;
	private Bitmap bmpRedPawn;
	/**
	 * chess bitmap width & height
	 */
	private int chessImgWidth;
	private int chessImgHeight;

	private Rect chessSrcRec;
	private Rect chessDstBmp;

	private Point[][] POINT_MAP; // position map to position
	private Chess[] chessArray; // all chess object reference

	private Paint paint;
	private Point chessPoint;
	private PiecePosition chessPosition;
	private PiecePosition moveFrom;
	private PiecePosition moveTo;

	private Chess selectedChess = null;

	private MID_ChessBoard midBoard = null; // middleware chess board object
	private GameView parentView;
	private static ChessBoard self = null;

	public enum AP_GAME_LEVEL {
		EASY, NORMAL, HARD,
	}

	public static ChessBoard getInstace(GameView view) {
		if (self == null && view != null) {
			return new ChessBoard(view);
		}
		return self;
	}

	private ChessBoard(GameView view) {
		// Log.i(TAG, "construct ChessBoard");
		this.parentView = view;

		midBoard = MID_ChessBoard.getInstace();
		chessArray = new Chess[MID_ChessBoard.MAX_CHESS_NUM]; // 32 chess

		paint = new Paint();
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);

		chessPosition = new PiecePosition();
		moveFrom = new PiecePosition();
		moveTo = new PiecePosition();

		chessPoint = new Point();
		chessSrcRec = new Rect();
		chessDstBmp = new Rect();

		init(); // init game
		// this.midBoard.setEventListener(this); //register board event listener
		// NativeInterface.setNativaCallbackListener(this); //register native
		// callback object
	}

	/**
	 * init map,create all chess object and bitmap reset all piece position and
	 * point
	 */
	private void init() {
		initPointPositionMap(); // init map
		createAllChessBmp(); // create all chess bmp
		createAllChess(); // create all chess
		// resetChessBoardLocation(); //init all chess location
	}

	public void release() {
		midBoard.release();
		self = null;
	}

	/**
	 * reset chess board, put all chess to default position
	 * 
	 * @return
	 */
	public boolean resetChessBoard() {
		if (this.selectedChess != null) {
			this.selectedChess.setSelected(false);
			this.selectedChess = null;
		}
		return midBoard.resetChessBoardDefault();
	}

	/**
	 * init all chess bitmap
	 */
	private void createAllChessBmp() {
		// black chess
		this.bmpBlackBishop = parentView.getIconByID(R.drawable.black_bishop);
		this.bmpBlackCanon = parentView.getIconByID(R.drawable.black_canon);
		this.bmpBlackCar = parentView.getIconByID(R.drawable.black_car);
		this.bmpBlackElephant = parentView.getIconByID(R.drawable.black_elephant);
		this.bmpBlackHorse = parentView.getIconByID(R.drawable.black_horse);
		this.bmpBlackKing = parentView.getIconByID(R.drawable.black_king);
		this.bmpBlackPawn = parentView.getIconByID(R.drawable.black_pawn);
		// red chess
		this.bmpRedBishop = parentView.getIconByID(R.drawable.red_bishop);
		this.bmpRedCanon = parentView.getIconByID(R.drawable.red_canon);
		this.bmpRedCar = parentView.getIconByID(R.drawable.red_car);
		this.bmpRedElephant = parentView.getIconByID(R.drawable.red_elephant);
		this.bmpRedHorse = parentView.getIconByID(R.drawable.red_horse);
		this.bmpRedKing = parentView.getIconByID(R.drawable.red_king);
		this.bmpRedPawn = parentView.getIconByID(R.drawable.red_pawn);

		// get chess bitmap width & height
		chessImgWidth = bmpBlackBishop.getWidth();
		chessImgHeight = bmpBlackBishop.getHeight();
	}

	/**
	 * 创建所有棋子对象
	 */
	private void createAllChess() {
		// black chesses
		chessArray[0] = new Chess(midBoard.getMidPieceByPosition(4, 0), bmpBlackKing);
		chessArray[1] = new Chess(midBoard.getMidPieceByPosition(8, 0), bmpBlackCar);
		chessArray[2] = new Chess(midBoard.getMidPieceByPosition(0, 0), bmpBlackCar);
		chessArray[3] = new Chess(midBoard.getMidPieceByPosition(7, 0), bmpBlackHorse);
		chessArray[4] = new Chess(midBoard.getMidPieceByPosition(1, 0), bmpBlackHorse);
		chessArray[5] = new Chess(midBoard.getMidPieceByPosition(6, 0), bmpBlackElephant);
		chessArray[6] = new Chess(midBoard.getMidPieceByPosition(2, 0), bmpBlackElephant);
		chessArray[7] = new Chess(midBoard.getMidPieceByPosition(5, 0), bmpBlackBishop);
		chessArray[8] = new Chess(midBoard.getMidPieceByPosition(3, 0), bmpBlackBishop);
		chessArray[9] = new Chess(midBoard.getMidPieceByPosition(7, 2), bmpBlackCanon);
		chessArray[10] = new Chess(midBoard.getMidPieceByPosition(1, 2), bmpBlackCanon);
		chessArray[11] = new Chess(midBoard.getMidPieceByPosition(8, 3), bmpBlackPawn);
		chessArray[12] = new Chess(midBoard.getMidPieceByPosition(6, 3), bmpBlackPawn);
		chessArray[13] = new Chess(midBoard.getMidPieceByPosition(4, 3), bmpBlackPawn);
		chessArray[14] = new Chess(midBoard.getMidPieceByPosition(2, 3), bmpBlackPawn);
		chessArray[15] = new Chess(midBoard.getMidPieceByPosition(0, 3), bmpBlackPawn);

		// red chesses
		chessArray[16] = new Chess(midBoard.getMidPieceByPosition(4, 9), bmpRedKing);
		chessArray[17] = new Chess(midBoard.getMidPieceByPosition(0, 9), bmpRedCar);
		chessArray[18] = new Chess(midBoard.getMidPieceByPosition(8, 9), bmpRedCar);
		chessArray[19] = new Chess(midBoard.getMidPieceByPosition(1, 9), bmpRedHorse);
		chessArray[20] = new Chess(midBoard.getMidPieceByPosition(7, 9), bmpRedHorse);
		chessArray[21] = new Chess(midBoard.getMidPieceByPosition(2, 9), bmpRedElephant);
		chessArray[22] = new Chess(midBoard.getMidPieceByPosition(6, 9), bmpRedElephant);
		chessArray[23] = new Chess(midBoard.getMidPieceByPosition(3, 9), bmpRedBishop);
		chessArray[24] = new Chess(midBoard.getMidPieceByPosition(5, 9), bmpRedBishop);
		chessArray[25] = new Chess(midBoard.getMidPieceByPosition(1, 7), bmpRedCanon);
		chessArray[26] = new Chess(midBoard.getMidPieceByPosition(7, 7), bmpRedCanon);
		chessArray[27] = new Chess(midBoard.getMidPieceByPosition(0, 6), bmpRedPawn);
		chessArray[28] = new Chess(midBoard.getMidPieceByPosition(2, 6), bmpRedPawn);
		chessArray[29] = new Chess(midBoard.getMidPieceByPosition(4, 6), bmpRedPawn);
		chessArray[30] = new Chess(midBoard.getMidPieceByPosition(6, 6), bmpRedPawn);
		chessArray[31] = new Chess(midBoard.getMidPieceByPosition(8, 6), bmpRedPawn);
	}

	public final SIDE getCurSide() {
		return midBoard.getCurSide();
	}

	public final void setCurSide(SIDE side) {
		midBoard.setCurSide(side);
	}

	/**
	 * 悔棋一步
	 * 
	 * @return
	 */
	public boolean undoStep() {
		return midBoard.undoStep();
	}

	public int getGameStatus() {
		return midBoard.getGameStatus();
	}

	/**
	 * 检查游戏是否已结束，并显示对话框提示
	 * 
	 * @return 游戏结束返回true，否则返回false
	 */
	public boolean checkGameOver(boolean showDialog) {
		int result = midBoard.getGameStatus();
		if (showDialog) {
			if (result == NativeInterface.WINNER_BLACK) {
				parentView.showGameOver(R.string.game_black_winner);
			} else if (result == NativeInterface.WINNER_RED) {
				parentView.showGameOver(R.string.game_red_winner);
			}
		}
		if (result == NativeInterface.WINNER_NONE)
			return false; // game not over
		else
			return true; // game over
	}

	/*
	 * @Override public void onGameOver(SIDE winner) { // TODO Auto-generated
	 * method stub int result = midBoard.getGameStatus(); if(result ==
	 * NativeInterface.WINNER_BLACK) winner = SIDE.BLACK; else if(result ==
	 * NativeInterface.WINNER_RED) winner = SIDE.RED; else return;
	 * 
	 * if(winner == MID_ChessBoard.SIDE.BLACK){
	 * parentView.showGameOver(R.string.game_black_winner); }else if(winner ==
	 * MID_ChessBoard.SIDE.RED){
	 * parentView.showGameOver(R.string.game_red_winner); } }
	 * 
	 * @Override public void onWillKingKing(SIDE killer) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */

	public Chess[] getChessArray() {
		return chessArray;
	}

	public boolean updateChessBoard() {
		midBoard.clearBoardPositionMap();
		for (int index = 0; index < MID_ChessBoard.MAX_CHESS_NUM; index++) {
			if (chessArray[index].getStatus() != PieceStatus.DEAD)
				midBoard.setBoardPosition(chessArray[index].getMidPiece());
		}
		// Log.i(TAG, "now update mid board");
		midBoard.updateChessBoard();
		return true;
	}

	/**
	 * 初始化坐标映射表，将当前屏幕的UI坐标映射到棋盘坐标 UI坐标对应到棋盘坐标，如棋盘坐标(0,0)对应的UI坐标(4,9)
	 */
	private void initPointPositionMap() {
		int row, col;
		POINT_MAP = new Point[MID_ChessBoard.MAX_ROW][MID_ChessBoard.MAX_COL];
		for (row = 0; row < MID_ChessBoard.MAX_ROW; row++) {
			for (col = 0; col < MID_ChessBoard.MAX_COL; col++) {
				POINT_MAP[row][col] = new Point();
				POINT_MAP[row][col].x = parentView.chessStartX + col * parentView.boardGridW;
				POINT_MAP[row][col].y = parentView.chessStartY + row * parentView.boardGridH;
			}
		}
	}

	/**
	 * 根据棋盘坐标获取Chess object
	 * 
	 * @param position
	 * @return 如果指定坐标有棋子返回Chess Object，否则返回null
	 */
	private Chess getChessByPosition(PiecePosition position) {
		int index;
		for (index = 0; index < MID_ChessBoard.MAX_CHESS_NUM; index++) {
			if ((chessArray[index].getPosition().x == position.x) && (chessArray[index].getPosition().y == position.y) && (chessArray[index].getStatus() != PieceStatus.DEAD)) {
				return chessArray[index];
			}
		}
		return null;
	}

	/**
	 * 根据坐标映射表，将UI坐标转换到棋盘坐标
	 * 
	 * @param uiPoint
	 *            UI坐标
	 * @param piecePosition
	 *            返回棋盘坐标
	 * @return 换算成功返回true，否则返回false
	 */
	private boolean PointToPosition(Point uiPoint, MID_ChessBoard.PiecePosition piecePosition) {
		int row, col;

		int gridHalfH = parentView.boardGridHalfH;
		for (row = 0; row < MID_ChessBoard.MAX_ROW; row++) {
			if ((uiPoint.y > (POINT_MAP[row][0].y - gridHalfH)) && (uiPoint.y < (POINT_MAP[row][0].y + gridHalfH))) {
				break;
			}
		}
		if (row >= MID_ChessBoard.MAX_ROW)
			return false;

		int gridHalfW = parentView.boardGridHalfW;
		for (col = 0; col < MID_ChessBoard.MAX_COL; col++) {
			if ((uiPoint.x > (POINT_MAP[0][col].x - gridHalfW)) && (uiPoint.x < (POINT_MAP[0][col].x + gridHalfW))) {
				break;
			}
		}
		if (col >= MID_ChessBoard.MAX_COL)
			return false;

		piecePosition.x = col;
		piecePosition.y = row;

		return true;
	}

	/*
	 * private boolean PositionToPoint(MID_ChessBoard.PiecePosition
	 * piecePosition, Point uiPoint){ uiPoint.x =
	 * POINT_MAP[piecePosition.y][piecePosition.x].x; uiPoint.y =
	 * POINT_MAP[piecePosition.y][piecePosition.x].y; return true; }
	 */

	private boolean PositionToPoint(int col, int row, Point uiPoint) {
		uiPoint.x = POINT_MAP[row][col].x;
		uiPoint.y = POINT_MAP[row][col].y;
		return true;
	}

	/**
	 * check click point is in one of the chess
	 * 
	 * @param srcPoint
	 * @return ok return true,else return false
	 */
	public boolean ConfirmPiecePoint(Point srcPoint) {
		int row, col;

		int gridHalfH = parentView.boardGridHalfH;
		for (row = 0; row < MID_ChessBoard.MAX_ROW; row++) {
			if ((srcPoint.y > (POINT_MAP[row][0].y - gridHalfH)) && (srcPoint.y < (POINT_MAP[row][0].y + gridHalfH))) {
				break;
			}
		}
		if (row >= MID_ChessBoard.MAX_ROW)
			return false;

		int gridHalfW = parentView.boardGridHalfW;
		for (col = 0; col < MID_ChessBoard.MAX_COL; col++) {
			if ((srcPoint.x > (POINT_MAP[0][col].x - gridHalfW)) && (srcPoint.x < (POINT_MAP[0][col].x + gridHalfW))) {
				break;
			}
		}
		if (col >= MID_ChessBoard.MAX_COL)
			return false;

		// Log.i(TAG, "confirm point is row=" + row + "  col=" + col);
		srcPoint.x = POINT_MAP[row][col].x;
		srcPoint.y = POINT_MAP[row][col].y;

		return true;
	}

	/**
	 * 绘制一个棋子
	 * 
	 * @param canvas
	 * @param chess
	 */
	private void drawChess(Canvas canvas, Chess chess) {
		int left, top, bottom, right;

		Bitmap chessBmp = chess.getBmp();
		this.PositionToPoint(chess.getPosition().x, chess.getPosition().y, chessPoint);

		left = chessPoint.x - chessImgWidth / 2 + 5;
		top = chessPoint.y - chessImgHeight / 2 + 5;
		right = chessPoint.x + chessImgWidth / 2 - 5;
		bottom = chessPoint.y + chessImgHeight / 2 - 5;
		chessDstBmp.set(left, top, right, bottom);
		chessSrcRec.set(0, 0, chessImgWidth, chessImgHeight);

		canvas.drawBitmap(chessBmp, chessSrcRec, chessDstBmp, null); // draw
																		// chess
																		// bitmap

		if (chess.isSelected() == true) { // draw rect if chess is selected
			canvas.drawRect(left - 2, top - 2, right + 1, bottom + 1, paint);
		}
	}

	/**
	 * 绘制所有棋子
	 * 
	 * @param canvas
	 */
	public void drawAllChess(Canvas canvas) {
		for (int i = 0; i < 32; i++) {
			if (chessArray[i].getStatus() == PieceStatus.DEAD) {
				// Log.i(TAG, chessArray[i].toString());
				continue;
			}
			drawChess(canvas, chessArray[i]);
		}
	}

	/*
	 * move chess to special position
	 */
	/*
	 * public boolean moveChess(PiecePosition fromPosition, PiecePosition
	 * toPosition){ Chess targetChess = getChessByPosition(fromPosition);
	 * 
	 * if(targetChess == null) return false;
	 * 
	 * return targetChess.setPosition(toPosition); }
	 */

	/**
	 * 
	 * @param position
	 */
	public void setSelectedByPoint(Point point, boolean value) {
		boolean result = PointToPosition(point, this.chessPosition); // point to
																		// position
		if (result != true)
			return;

		Chess targetChess = getChessByPosition(chessPosition); // find chess by
																// position
		if (targetChess == null)
			return;

		targetChess.setSelected(value);
	}

	/**
	 * 电脑计算一步走法
	 * 
	 * @return
	 */
	public boolean doNextStep() {
		if (midBoard.getNextStep(moveFrom, moveTo) == true) {
			// Log.i(TAG, "get next move true");
			if (midBoard.movePieceTo(moveFrom, moveTo) == true) {
				// Log.i(TAG, "computer move piece ok");
				midBoard.setCurSide(SIDE.RED);
				this.selectedChess.setSelected(false);
				this.selectedChess = getChessByPosition(moveTo);
				this.selectedChess.setSelected(true);
				return true;
			} else {
				Log.i(TAG, "move piece error");
			}
		}
		// Log.i(TAG, "get next move false");
		return false;
	}

	/**
	 * 设置游戏难度
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		switch (level) {
		case SettingManager.LEVEL_EASY:
			this.midBoard.setGameLevel(MID_ChessBoard.GAME_LEVEL.EASY);
			break;
		case SettingManager.LEVEL_NORMAL:
			this.midBoard.setGameLevel(MID_ChessBoard.GAME_LEVEL.NORMAL);
			break;
		case SettingManager.LEVEL_HARD:
			this.midBoard.setGameLevel(MID_ChessBoard.GAME_LEVEL.HARD);
			break;
		default:
			break;
		}
	}

	/**
	 * click event on chess
	 * 
	 * @param point
	 * @return true:update screen,false:don't update screen
	 */
	public boolean eventOnChess(Point point) {
		// boolean moved = false;
		boolean result = PointToPosition(point, this.chessPosition); // 将UI坐标换算到棋盘坐标
		if (result == false) // nothing to do
			return result;
		if (this.midBoard.getCurSide() == SIDE.BLACK) {
			// Log.i(TAG, "computer do next step");
			return false;
		}

		Chess chess = getChessByPosition(this.chessPosition);
		if (chess == null) { // target position no chess
			if (selectedChess == null) {
				// Log.i(TAG, "not get chess,selected chess is null");
				result = false;
			} else if (selectedChess.getColor() == PieceColor.RED) {
				// Log.i(TAG,
				// "not get chess,selected chess is not null, result=" +
				// result);
				midBoard.movePieceTo(this.selectedChess.getPosition(), this.chessPosition);
				result = true;
			}
		} else { // target position have a chess
			// Log.i(TAG, "get chess ok, chess color is " + chess.getColor() +
			// " type is " + chess.getType());
			if (chess.getColor() == PieceColor.BLACK) {
				if ((selectedChess == null) || ((selectedChess != null) && (selectedChess.getColor() == PieceColor.BLACK)))
					return false;
			}
			if ((selectedChess != null) && (selectedChess.getColor() == PieceColor.BLACK)) {
				// Log.i(TAG, "selected chess is black, now set to null");
				selectedChess.setSelected(false);
				selectedChess = null;
			}
			if (selectedChess == null) {
				if (chess.getStatus() == PieceStatus.DEAD) { // chess is
																// dead,nothing
																// to do
					// Log.i(TAG, "selected chess is null, chess is dead");
					result = false;
				} else {
					// Log.i(TAG, "chess is not dead");
					selectedChess = chess;
					selectedChess.setSelected(true);
					result = true;
				}
			} else { // one of the chess had been selected
				if (chess.getStatus() == PieceStatus.DEAD) {
					// Log.i(TAG, "seleted chess not null, chess is dead");
					midBoard.movePieceTo(selectedChess.getPosition(), chessPosition);
					result = true;
				} else {
					// Log.i(TAG, "selected color=" + selectedChess.getColor() +
					// " chess color=" + chess.getColor());
					if (selectedChess == chess) {
						chess.setSelected(false);
						selectedChess = null;
						result = true;
					} else if (this.selectedChess.getColor() == chess.getColor()) {
						selectedChess.setSelected(false);
						selectedChess = chess;
						selectedChess.setSelected(true);
						result = true;
					} else {
						midBoard.movePieceTo(this.selectedChess.getPosition(),
								chessPosition);
						result = true;
					}
				}
			}
		}
		return result;
	}
}
