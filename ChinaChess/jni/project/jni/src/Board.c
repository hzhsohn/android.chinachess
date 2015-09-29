
#include "../include/Board.h"

static BYTE currPosition[MAX_ROW][MAX_COL] = {NO_CHESS};
/**
 * g_sWinner == 0, game not over
 * g_sWinner == 1, winner color is red
 * g_sWinner == -1, winner color is black
 */
static int gs_Winner = chinachess_mid_NativeInterface_WINNER_NONE;
static BOOL gs_KillKing = FALSE;

#if 0
static ST_Position gs_BKingPosition = {0,0};	//save black king position
static ST_Position gs_RKingPosition = {0,0};	//save red king position
#endif
/**
 * 重置所有棋子到棋局开始位置
 */
void resetAllChessPosition(void)
{
	memset(currPosition, NO_CHESS, MAX_ROW * MAX_COL);
	currPosition[0][0] = B_CAR;
	currPosition[0][1] = B_HORSE;
	currPosition[0][2] = B_ELEPHANT;
	currPosition[0][3] = B_BISHOP;
	currPosition[0][4] = B_KING;
	currPosition[0][5] = B_BISHOP;
	currPosition[0][6] = B_ELEPHANT;
	currPosition[0][7] = B_HORSE;
	currPosition[0][8] = B_CAR;
	currPosition[2][1] = B_CANON;
	currPosition[2][7] = B_CANON;
	currPosition[3][0] = B_PAWN;
	currPosition[3][2] = B_PAWN;
	currPosition[3][4] = B_PAWN;
	currPosition[3][6] = B_PAWN;
	currPosition[3][8] = B_PAWN;

	currPosition[6][0] = R_PAWN;
	currPosition[6][2] = R_PAWN;
	currPosition[6][4] = R_PAWN;
	currPosition[6][6] = R_PAWN;
	currPosition[6][8] = R_PAWN;
	currPosition[7][1] = R_CANON;
	currPosition[7][7] = R_CANON;
	currPosition[9][0] = R_CAR;
	currPosition[9][1] = R_HORSE;
	currPosition[9][2] = R_ELEPHANT;
	currPosition[9][3] = R_BISHOP;
	currPosition[9][4] = R_KING;
	currPosition[9][5] = R_BISHOP;
	currPosition[9][6] = R_ELEPHANT;
	currPosition[9][7] = R_HORSE;
	currPosition[9][8] = R_CAR;

	//save init king position
#if 0
	gs_BKingPosition.x = 4;
	gs_BKingPosition.y = 0;
	gs_RKingPosition.x = 4;
	gs_RKingPosition.y = 9;
#endif
}

/**
 * set game level
 */
void SetGameLevel(GAME_LEVEL level)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s, set search depth %d",__FUNCTION__,level);
	switch(level)
	{
	case EASY:
		SetSearchDepth(3);
		break;

	case NORMAL:
		SetSearchDepth(4);
		break;

	case HARD:
		SetSearchDepth(5);
		break;

	default:
		SetSearchDepth(4);
		break;
	}
}

/**
 * 初始化棋盘
 */
BOOL ResetBoardDefault(void)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","enter function %s",__FUNCTION__);
	gs_Winner = chinachess_mid_NativeInterface_WINNER_NONE;
	resetAllChessPosition();
	InitSearchEngine();
	SetGameLevel(NORMAL);
	SetSearchEngine(ALPHA_BETA_HH_ENGINE);	//set search engine

	return TRUE;
}

#if 0
void dump_position(BYTE position[])
{
	int x, y;
	for(y = 0; y < MAX_ROW; y++)
	{
		for(x = 0; x < MAX_COL; x++)
		{
			__android_log_print(ANDROID_LOG_DEBUG,"AI","%d ",position[y * MAX_ROW + x]);
		}
		__android_log_print(ANDROID_LOG_DEBUG,"AI","\n");
	}
}
#endif

/**
 * 使用给定数组初始化棋盘
 */
void ResetBoard(BYTE board[])
{
	int row;
	int col;
	gs_Winner = chinachess_mid_NativeInterface_WINNER_NONE;
	//dump_position(board);
	//clear black king and red king position
#if 0
	gs_BKingPosition.x = 0;
	gs_BKingPosition.y = 0;
	gs_RKingPosition.x = 0;
	gs_RKingPosition.y = 0;
#endif

	for(row = 0; row < MAX_ROW; row++)
	{
		for(col = 0; col < MAX_COL; col++)
		{
			currPosition[row][col] = board[row * MAX_COL + col];
#if 0
			if(currPosition[row][col] == B_KING)
			{
				gs_BKingPosition.x = col;
				gs_BKingPosition.y = row;

			}else if(currPosition[row][col] == R_KING)
			{
				gs_RKingPosition.x = col;
				gs_RKingPosition.y = row;
			}
#endif
		}
	}

	//dump_position();
}

/**
 * set chess ID by position
 */
void setChessIDByPosition(int x, int y, BYTE chessID)
{
	currPosition[y][x] = chessID;

	//save king position
#if 0
	if(currPosition[y][x] == B_KING)
	{
		gs_BKingPosition.x = x;
		gs_BKingPosition.y = y;

	}else if(currPosition[y][x] == R_KING)
	{
		gs_RKingPosition.x = x;
		gs_RKingPosition.y = y;
	}
#endif
}

/**
 * get AI next step
 */
BOOL GetNextStep(int moves[4])
{
	if(gs_Winner != chinachess_mid_NativeInterface_WINNER_NONE)	//game is over
		return FALSE;

	if(SearchAGoodMove(currPosition) == TRUE)
	{
		moves[0] = g_stBestMove.from.x;
		moves[1] = g_stBestMove.from.y;
		moves[2] = g_stBestMove.to.x;
		moves[3] = g_stBestMove.to.y;
		//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---return true---fromx=%d,fromy=%d,tox=%d,toy=%d\n",
		//		__FUNCTION__,moves[0],moves[1],moves[2],moves[3]);
		return TRUE;
	}
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---return false\n",__FUNCTION__);
	return FALSE;
}

/**
 * 将棋子移动到制定位置
 */
BOOL moveChess(int fromX, int fromY, int toX, int toY)
{
	if(IsValidMove(currPosition, fromX, fromY, toX, toY) == TRUE)
	{
		if(currPosition[toY][toX] == B_KING)	//winner color:red
		{
			gs_Winner = chinachess_mid_NativeInterface_WINNER_RED;
		}else if(currPosition[toY][toX] == R_KING)	//winner color:black
		{
			gs_Winner = chinachess_mid_NativeInterface_WINNER_BLACK;
		}else
		{
			gs_Winner = chinachess_mid_NativeInterface_WINNER_NONE;
		}
		currPosition[toY][toX] = currPosition[fromY][fromX];
		currPosition[fromY][fromX] = NO_CHESS;

		//save king position
#if 0
		if(currPosition[toY][toX] == B_KING)
		{
			gs_BKingPosition.x = toX;
			gs_BKingPosition.y = toY;

		}else if(currPosition[toY][toX] == R_KING)
		{
			gs_RKingPosition.x = toX;
			gs_RKingPosition.y = toY;
		}
#endif
		//__android_log_write(ANDROID_LOG_ERROR,"AI","it's valid move\n");
		return TRUE;
	}
	__android_log_write(ANDROID_LOG_ERROR,"AI","not valid move\n");
	return FALSE;
}

#if 0
BOOL WillKillKing(BYTE position[MAX_ROW][MAX_COL])
{
	return FALSE;
}
#endif

/**
 * 判断棋局是否已经结束
 * g_sWinner == 0, game not over
 * g_sWinner == 1, winner color is red
 * g_sWinner == -1, winner color is black
 */
int getGameStatus(void)
{
	return gs_Winner;
#if 0
	int y, x;
	BOOL redLive = FALSE;
	BOOL blackLive = FALSE;
	for (y = 7; y < MAX_ROW; y++)
		for (x = 3; x < 6; x++)
		{
			if (currPosition[y][x] == B_KING)
				blackLive = TRUE;
			if (currPosition[y][x] == R_KING)
				redLive  = TRUE;
		}

	for (y = 0; y < 3; y++)
		for (x = 3; x < 6; x++)
		{
			if (currPosition[y][x] == B_KING)
				blackLive = TRUE;
			if (currPosition[y][x] == R_KING)
				redLive  = TRUE;
		}
	if (redLive && blackLive)
		return FALSE;
	else
		return TRUE;
#endif
}

/**
 * 设置游戏状态
 */
void setGameStatus(int status)
{
	gs_Winner = status;
}
