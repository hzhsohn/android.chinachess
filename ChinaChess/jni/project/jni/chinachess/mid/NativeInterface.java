/**
 * 
 */
package chinachess.mid;


/**
 * @author Administrator
 *
 */
public class NativeInterface {
	public static final String TAG = NativeInterface.class.getSimpleName();
	/**
	 * @author Administrator
	 * game status
	 */
	public static final int WINNER_BLACK = -1;
	public static final int WINNER_RED = 1;
	public static final int WINNER_NONE = 0;
	
	public static final int JNI_TRUE = 1;
	public static final int JNI_FALSE = 0;
	
	/*
	public static NativeCallback callback = null;
	
	public interface NativeCallback {
		public void onGameOver(int winner_color);
		public void onWillKillKing(int action_color);
	}
	
	public static void setNativaCallbackListener(NativeCallback call_back){
		callback = call_back;
	}
	
	public static void gameOver(int winner_color){
		//Log.i(TAG, "gameOver called by native");
		if(callback != null){
			callback.onGameOver(winner_color);
		}
	}
	
	public static void killKing(int action_color){
		//Log.i(TAG, "killKing called by native");
		if(callback != null){
			callback.onWillKillKing(action_color);
		}
	}
	*/
	public native int initBoard();
	
	public native int moveChess(int fromCol, int fromRow, int toCol, int toRow);
	
	public native int getNextMove(int[] moves);
	
	public native int setChessIdByPosition(int x, int y, int chessID);
	
	public native int resetBoardDefault();
	
	public native int resetBoard(byte[] board);
	
	public native void clearBoard();
	
	public native void setGameLevel(int level);
	
	/**
	 * return 0, game not over
	 * return 1, winner color is red
	 * return -1, winner color is black
	 * @return
	 */
	public native int getGameStatus();
	public native void setGameStatus(int status);
	
	public native String getAuthorName();
}
