package chinachess.mid;

public class NativeInterface {
	public static final String TAG = NativeInterface.class.getSimpleName();
	public static final int WINNER_BLACK = -1;
	public static final int WINNER_RED = 1;
	public static final int WINNER_NONE = 0;

	public static final int JNI_TRUE = 1;
	public static final int JNI_FALSE = 0;

	public native int initBoard();

	public native int moveChess(int fromCol, int fromRow, int toCol, int toRow);

	public native int getNextMove(int[] moves);

	public native int setChessIdByPosition(int x, int y, int chessID);

	public native int resetBoardDefault();

	public native int resetBoard(byte[] board);

	public native void clearBoard();

	public native void setGameLevel(int level);

	public native int getGameStatus();

	public native void setGameStatus(int status);

	public native String getAuthorName();
}
