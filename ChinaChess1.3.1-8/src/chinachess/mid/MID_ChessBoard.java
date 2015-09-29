package chinachess.mid;

import java.util.LinkedList;

import android.util.Log;
import chinachess.mid.MID_IPiece.PieceColor;
import chinachess.mid.MID_IPiece.PieceStatus;
import chinachess.mid.MID_IPiece.PieceType;

public class MID_ChessBoard {

	public static final String TAG = MID_ChessBoard.class.getSimpleName();

	public static final int MAX_COL = 9;
	public static final int MAX_ROW = 10;
	public static final int MAX_CHESS_NUM = 32;

	private static final int NO_CHESS = 0;
	private static final int BLACK_KING = 1;
	private static final int BLACK_CAR = 2;
	private static final int BLACK_HORSE = 3;
	private static final int BLACK_CANON = 4;
	private static final int BLACK_BISHOP = 5;
	private static final int BLACK_ELEPHANT = 6;
	private static final int BLACK_PAWN = 7;
	private static final int RED_KING = 8;
	private static final int RED_CAR = 9;
	private static final int RED_HORSE = 10;
	private static final int RED_CANON = 11;
	private static final int RED_BISHOP = 12;
	private static final int RED_ELEPHANT = 13;
	private static final int RED_PAWN = 14;

	private MID_Piece mBlackKing;
	private MID_Piece mBlackCar1;
	private MID_Piece mBlackCar2;
	private MID_Piece mBlackHorse1;
	private MID_Piece mBlackHorse2;
	private MID_Piece mBlackCanon1;
	private MID_Piece mBlackCanon2;
	private MID_Piece mBlackBishop1;
	private MID_Piece mBlackBishop2;
	private MID_Piece mBlackElephant1;
	private MID_Piece mBlackElephant2;
	private MID_Piece mBlackPawn1;
	private MID_Piece mBlackPawn2;
	private MID_Piece mBlackPawn3;
	private MID_Piece mBlackPawn4;
	private MID_Piece mBlackPawn5;
	private MID_Piece mRedKing;
	private MID_Piece mRedCar1;
	private MID_Piece mRedCar2;
	private MID_Piece mRedHorse1;
	private MID_Piece mRedHorse2;
	private MID_Piece mRedCanon1;
	private MID_Piece mRedCanon2;
	private MID_Piece mRedBishop1;
	private MID_Piece mRedBishop2;
	private MID_Piece mRedElephant1;
	private MID_Piece mRedElephant2;
	private MID_Piece mRedPawn1;
	private MID_Piece mRedPawn2;
	private MID_Piece mRedPawn3;
	private MID_Piece mRedPawn4;
	private MID_Piece mRedPawn5;

	private MID_Piece pieceMap[][];
	private SIDE curSide = SIDE.RED;
	private int[] moves;
	private byte[] board;

	private NativeInterface AI = null;
	private LinkedList<ActionRecord> actionList;

	private static MID_ChessBoard self = null;

	public enum SIDE {
		RED, BLACK,
	}

	public enum GAME_LEVEL {
		EASY, NORMAL, HARD,
	}

	public static class PiecePosition {
		public int x = -1;
		public int y = -1;
	}

	private class ActionRecord {
		public MID_Piece srcPiece = null;
		public int srcX;
		public int srcY;

		public MID_Piece dstPiece = null;
		public int dstX;
		public int dstY;

		public ActionRecord(MID_Piece src, int srcx, int srcy, int dstx, int dsty, MID_Piece dst) {
			srcPiece = src;
			srcX = srcx;
			srcY = srcy;

			dstPiece = dst;
			dstX = dstx;
			dstY = dsty;
		}
	}

	private MID_ChessBoard() {
		AI = new NativeInterface(); // Native interface接口对象
		Log.i(TAG, AI.getAuthorName());
		moves = new int[4];
		board = new byte[MAX_ROW * MAX_COL];
		actionList = new LinkedList<ActionRecord>(); // 走法记录队列，用于悔棋
		initChessBoard();
	}

	public static MID_ChessBoard getInstace() {
		if (self == null) {
			self = new MID_ChessBoard();
		}
		return self;
	}

	public void release() {
		self = null;
	}

	public boolean initChessBoard() {
		pieceMap = new MID_Piece[MAX_ROW][MAX_COL];

		createAllPieces();
		resetChessBoardDefault();
		return true;
	}

	private void createAllPieces() {
		mBlackKing = new MID_Piece(PieceColor.BLACK, PieceType.KING);
		mBlackCar1 = new MID_Piece(PieceColor.BLACK, PieceType.CAR);
		mBlackCar2 = new MID_Piece(PieceColor.BLACK, PieceType.CAR);
		mBlackHorse1 = new MID_Piece(PieceColor.BLACK, PieceType.HORSE);
		mBlackHorse2 = new MID_Piece(PieceColor.BLACK, PieceType.HORSE);
		mBlackCanon1 = new MID_Piece(PieceColor.BLACK, PieceType.CANON);
		mBlackCanon2 = new MID_Piece(PieceColor.BLACK, PieceType.CANON);
		mBlackBishop1 = new MID_Piece(PieceColor.BLACK, PieceType.BISHOP);
		mBlackBishop2 = new MID_Piece(PieceColor.BLACK, PieceType.BISHOP);
		mBlackElephant1 = new MID_Piece(PieceColor.BLACK, PieceType.ELEPHANT);
		mBlackElephant2 = new MID_Piece(PieceColor.BLACK, PieceType.ELEPHANT);
		mBlackPawn1 = new MID_Piece(PieceColor.BLACK, PieceType.PAWN);
		mBlackPawn2 = new MID_Piece(PieceColor.BLACK, PieceType.PAWN);
		mBlackPawn3 = new MID_Piece(PieceColor.BLACK, PieceType.PAWN);
		mBlackPawn4 = new MID_Piece(PieceColor.BLACK, PieceType.PAWN);
		mBlackPawn5 = new MID_Piece(PieceColor.BLACK, PieceType.PAWN);

		mRedKing = new MID_Piece(PieceColor.RED, PieceType.KING);
		mRedCar1 = new MID_Piece(PieceColor.RED, PieceType.CAR);
		mRedCar2 = new MID_Piece(PieceColor.RED, PieceType.CAR);
		mRedHorse1 = new MID_Piece(PieceColor.RED, PieceType.HORSE);
		mRedHorse2 = new MID_Piece(PieceColor.RED, PieceType.HORSE);
		mRedCanon1 = new MID_Piece(PieceColor.RED, PieceType.CANON);
		mRedCanon2 = new MID_Piece(PieceColor.RED, PieceType.CANON);
		mRedBishop1 = new MID_Piece(PieceColor.RED, PieceType.BISHOP);
		mRedBishop2 = new MID_Piece(PieceColor.RED, PieceType.BISHOP);
		mRedElephant1 = new MID_Piece(PieceColor.RED, PieceType.ELEPHANT);
		mRedElephant2 = new MID_Piece(PieceColor.RED, PieceType.ELEPHANT);
		mRedPawn1 = new MID_Piece(PieceColor.RED, PieceType.PAWN);
		mRedPawn2 = new MID_Piece(PieceColor.RED, PieceType.PAWN);
		mRedPawn3 = new MID_Piece(PieceColor.RED, PieceType.PAWN);
		mRedPawn4 = new MID_Piece(PieceColor.RED, PieceType.PAWN);
		mRedPawn5 = new MID_Piece(PieceColor.RED, PieceType.PAWN);
	}

	public boolean resetChessBoardDefault() {
		clearBoardPositionMap();
		actionList.clear();

		mBlackKing.setPieceStatus(PieceStatus.LIVE);
		mBlackKing.setPiecePosition(4, 0);
		pieceMap[0][4] = mBlackKing;

		mBlackBishop1.setPieceStatus(PieceStatus.LIVE);
		mBlackBishop1.setPiecePosition(5, 0);
		pieceMap[0][5] = mBlackBishop1;

		mBlackBishop2.setPieceStatus(PieceStatus.LIVE);
		mBlackBishop2.setPiecePosition(3, 0);
		pieceMap[0][3] = mBlackBishop2;

		mBlackElephant1.setPieceStatus(PieceStatus.LIVE);
		mBlackElephant1.setPiecePosition(6, 0);
		pieceMap[0][6] = mBlackElephant1;

		mBlackElephant2.setPieceStatus(PieceStatus.LIVE);
		mBlackElephant2.setPiecePosition(2, 0);
		pieceMap[0][2] = mBlackElephant2;

		mBlackHorse1.setPieceStatus(PieceStatus.LIVE);
		mBlackHorse1.setPiecePosition(7, 0);
		pieceMap[0][7] = mBlackHorse1;

		mBlackHorse2.setPieceStatus(PieceStatus.LIVE);
		mBlackHorse2.setPiecePosition(1, 0);
		pieceMap[0][1] = mBlackHorse2;

		mBlackCar1.setPieceStatus(PieceStatus.LIVE);
		mBlackCar1.setPiecePosition(8, 0);
		pieceMap[0][8] = mBlackCar1;

		mBlackCar2.setPieceStatus(PieceStatus.LIVE);
		mBlackCar2.setPiecePosition(0, 0);
		pieceMap[0][0] = mBlackCar2;

		mBlackCanon1.setPieceStatus(PieceStatus.LIVE);
		mBlackCanon1.setPiecePosition(7, 2);
		pieceMap[2][7] = mBlackCanon1;

		mBlackCanon2.setPieceStatus(PieceStatus.LIVE);
		mBlackCanon2.setPiecePosition(1, 2);
		pieceMap[2][1] = mBlackCanon2;

		mBlackPawn1.setPieceStatus(PieceStatus.LIVE);
		mBlackPawn1.setPiecePosition(8, 3);
		pieceMap[3][8] = mBlackPawn1;

		mBlackPawn2.setPieceStatus(PieceStatus.LIVE);
		mBlackPawn2.setPiecePosition(6, 3);
		pieceMap[3][6] = mBlackPawn2;

		mBlackPawn3.setPieceStatus(PieceStatus.LIVE);
		mBlackPawn3.setPiecePosition(4, 3);
		pieceMap[3][4] = mBlackPawn3;

		mBlackPawn4.setPieceStatus(PieceStatus.LIVE);
		mBlackPawn4.setPiecePosition(2, 3);
		pieceMap[3][2] = mBlackPawn4;

		mBlackPawn5.setPieceStatus(PieceStatus.LIVE);
		mBlackPawn5.setPiecePosition(0, 3);
		pieceMap[3][0] = mBlackPawn5;

		mRedKing.setPieceStatus(PieceStatus.LIVE);
		mRedKing.setPiecePosition(4, 9);
		pieceMap[9][4] = mRedKing;

		mRedBishop1.setPieceStatus(PieceStatus.LIVE);
		mRedBishop1.setPiecePosition(3, 9);
		pieceMap[9][3] = mRedBishop1;

		mRedBishop2.setPieceStatus(PieceStatus.LIVE);
		mRedBishop2.setPiecePosition(5, 9);
		pieceMap[9][5] = mRedBishop2;

		mRedElephant1.setPieceStatus(PieceStatus.LIVE);
		mRedElephant1.setPiecePosition(2, 9);
		pieceMap[9][2] = mRedElephant1;

		mRedElephant2.setPieceStatus(PieceStatus.LIVE);
		mRedElephant2.setPiecePosition(6, 9);
		pieceMap[9][6] = mRedElephant2;

		mRedHorse1.setPieceStatus(PieceStatus.LIVE);
		mRedHorse1.setPiecePosition(1, 9);
		pieceMap[9][1] = mRedHorse1;

		mRedHorse2.setPieceStatus(PieceStatus.LIVE);
		mRedHorse2.setPiecePosition(7, 9);
		pieceMap[9][7] = mRedHorse2;

		mRedCar1.setPieceStatus(PieceStatus.LIVE);
		mRedCar1.setPiecePosition(0, 9);
		pieceMap[9][0] = mRedCar1;

		mRedCar2.setPieceStatus(PieceStatus.LIVE);
		mRedCar2.setPiecePosition(8, 9);
		pieceMap[9][8] = mRedCar2;

		mRedCanon1.setPieceStatus(PieceStatus.LIVE);
		mRedCanon1.setPiecePosition(1, 7);
		pieceMap[7][1] = mRedCanon1;

		mRedCanon2.setPieceStatus(PieceStatus.LIVE);
		mRedCanon2.setPiecePosition(7, 7);
		pieceMap[7][7] = mRedCanon2;

		mRedPawn1.setPieceStatus(PieceStatus.LIVE);
		mRedPawn1.setPiecePosition(0, 6);
		pieceMap[6][0] = mRedPawn1;

		mRedPawn2.setPieceStatus(PieceStatus.LIVE);
		mRedPawn2.setPiecePosition(2, 6);
		pieceMap[6][2] = mRedPawn2;

		mRedPawn3.setPieceStatus(PieceStatus.LIVE);
		mRedPawn3.setPiecePosition(4, 6);
		pieceMap[6][4] = mRedPawn3;

		mRedPawn4.setPieceStatus(PieceStatus.LIVE);
		mRedPawn4.setPiecePosition(6, 6);
		pieceMap[6][6] = mRedPawn4;

		mRedPawn5.setPieceStatus(PieceStatus.LIVE);
		mRedPawn5.setPiecePosition(8, 6);
		pieceMap[6][8] = mRedPawn5;

		AI.initBoard();
		setCurSide(SIDE.RED);

		return true;
	}

	public void clearBoardPositionMap() {
		int row, col;
		for (row = 0; row < MAX_ROW; row++) {
			for (col = 0; col < MAX_COL; col++) {
				pieceMap[row][col] = null;
			}
		}
	}

	public void setBoardPosition(MID_IPiece piece) {
		pieceMap[piece.getPiecePosition().y][piece.getPiecePosition().x] = (MID_Piece) piece;
	}

	public void updateChessBoard() {
		MID_Piece piece;
		for (int row = 0; row < MAX_ROW; row++) {
			for (int col = 0; col < MAX_COL; col++) {
				piece = pieceMap[row][col];
				if (piece == null) {
					board[row * MAX_COL + col] = NO_CHESS;
					continue;
				} else {
					if (piece.getPieceStatus() == PieceStatus.DEAD) {
						board[row * MAX_COL + col] = NO_CHESS;
						continue;
					}
					switch (piece.getPieceType()) {
					case KING:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_KING;
						else
							board[row * MAX_COL + col] = RED_KING;
						break;
					case CAR:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_CAR;
						else
							board[row * MAX_COL + col] = RED_CAR;
						break;
					case HORSE:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_HORSE;
						else
							board[row * MAX_COL + col] = RED_HORSE;
						break;
					case CANON:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_CANON;
						else
							board[row * MAX_COL + col] = RED_CANON;
						break;
					case BISHOP:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_BISHOP;
						else
							board[row * MAX_COL + col] = RED_BISHOP;
						break;
					case ELEPHANT:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_ELEPHANT;
						else
							board[row * MAX_COL + col] = RED_ELEPHANT;
						break;
					case PAWN:
						if (piece.getPieceColor() == PieceColor.BLACK)
							board[row * MAX_COL + col] = BLACK_PAWN;
						else
							board[row * MAX_COL + col] = RED_PAWN;
						break;
					default:
						break;
					}
				}
			}
		}

		AI.resetBoard(board);
	}

	public MID_IPiece getMidPieceByPosition(int col, int row) {
		if ((col < MAX_COL) && (col >= 0) && (row < MAX_ROW) && (row >= 0)) {
			return pieceMap[row][col];
		}

		return null;
	}

	public boolean movePieceTo(PiecePosition fromPosition, PiecePosition toPosition) {
		MID_Piece srcPiece = (MID_Piece) pieceMap[fromPosition.y][fromPosition.x];
		MID_Piece dstPiece = (MID_Piece) pieceMap[toPosition.y][toPosition.x];
		if (srcPiece == null)
			return false;

		int fromY = fromPosition.y;
		int fromX = fromPosition.x;
		int toY = toPosition.y;
		int toX = toPosition.x;

		int res = AI.moveChess(fromX, fromY, toX, toY);
		if (res == 0) {
			Log.e(TAG, "Ai move chess fail.");
			return false;
		}

		actionList.add(new ActionRecord(srcPiece, fromX, fromY, toX, toY, dstPiece));

		if (dstPiece != null) {
			dstPiece.setPieceStatus(PieceStatus.DEAD);
			srcPiece.setPiecePosition(toPosition);
			pieceMap[toY][toX] = srcPiece;
			pieceMap[fromY][fromX] = null;
		} else {
			srcPiece.setPiecePosition(toPosition);
			pieceMap[fromY][fromX] = null;
			pieceMap[toY][toX] = srcPiece;
		}

		this.setCurSide(SIDE.BLACK);

		return true;
	}

	public boolean getNextStep(PiecePosition fromPosition, PiecePosition toPosition) {
		if (fromPosition == null || toPosition == null)
			return false;

		if (getGameStatus() != NativeInterface.WINNER_NONE) {
			return false;
		}

		int result = 0;
		result = AI.getNextMove(moves);
		if (result == 1) {
			fromPosition.x = moves[0];
			fromPosition.y = moves[1];
			toPosition.x = moves[2];
			toPosition.y = moves[3];
			return true;
		}
		return false;
	}

	public SIDE getCurSide() {
		return this.curSide;
	}

	public void setCurSide(SIDE side) {
		this.curSide = side;
	}

	public void setGameLevel(GAME_LEVEL level) {
		switch (level) {
		case EASY:
			AI.setGameLevel(1);
			break;
		case NORMAL:
			AI.setGameLevel(2);
			break;
		case HARD:
			AI.setGameLevel(3);
		}
	}

	public int getGameStatus() {
		return AI.getGameStatus();
	}

	public void setGameStatus(int status) {
		AI.setGameStatus(status);
	}

	private int getChessID(MID_Piece piece) {
		if (piece == null)
			return NO_CHESS;

		switch (piece.getPieceType()) {
		case KING:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_KING;
			else
				return RED_KING;
		case CAR:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_CAR;
			else
				return RED_CAR;
		case HORSE:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_HORSE;
			else
				return RED_HORSE;
		case CANON:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_CANON;
			else
				return RED_CANON;
		case BISHOP:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_BISHOP;
			else
				return RED_BISHOP;
		case ELEPHANT:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_ELEPHANT;
			else
				return RED_ELEPHANT;
		case PAWN:
			if (piece.getPieceColor() == PieceColor.BLACK)
				return BLACK_PAWN;
			else
				return RED_PAWN;
		default:
			break;
		}
		return NO_CHESS;
	}

	/**
	 * 悔棋一次，包括电脑和玩家的一对走法
	 */
	public boolean undoStep() {
		if (actionList.isEmpty() || actionList.size() % 2 != 0) {
			return false;
		}
		ActionRecord record1 = actionList.removeLast();
		AI.setChessIdByPosition(record1.srcX, record1.srcY, getChessID(record1.srcPiece));
		AI.setChessIdByPosition(record1.dstX, record1.dstY, getChessID(record1.dstPiece));
		record1.srcPiece.setPiecePosition(record1.srcX, record1.srcY);
		pieceMap[record1.srcY][record1.srcX] = record1.srcPiece;
		pieceMap[record1.dstY][record1.dstX] = record1.dstPiece;

		if (record1.dstPiece != null) {
			record1.dstPiece.setPieceStatus(PieceStatus.LIVE);
		}

		ActionRecord record2 = actionList.removeLast();
		record2.srcPiece.setPiecePosition(record2.srcX, record2.srcY);
		AI.setChessIdByPosition(record2.srcX, record2.srcY, getChessID(record2.srcPiece));
		AI.setChessIdByPosition(record2.dstX, record2.dstY, getChessID(record2.dstPiece));
		pieceMap[record2.srcY][record2.srcX] = record2.srcPiece;
		pieceMap[record2.dstY][record2.dstX] = record2.dstPiece;
		if (record2.dstPiece != null) {
			record2.dstPiece.setPieceStatus(PieceStatus.LIVE);
		}

		setGameStatus(NativeInterface.WINNER_NONE);
		record1 = null;
		record2 = null;

		return true;
	}
}