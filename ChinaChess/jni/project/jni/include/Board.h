
#ifndef __BOARD_H_
#define __BOARD_H_

#include "define.h"
#include "chess.h"
#include "MoveGenerator.h"
#include "Eveluation.h"
#include "SearchEngineInterface.h"

typedef enum
{
	EASY = 1,
	NORMAL,
	HARD,
}GAME_LEVEL;

/**
 * 初始化棋盘
 */
BOOL ResetBoardDefault(void);

/**
 * 使用给定数组初始化棋盘
 */
void ResetBoard(BYTE board[]);

/**
 * 重置所有棋子到棋局开始位置
 */
void resetAllChessPosition(void);

/**
 * 判断棋局是否已经结束
 * game over return 1,else return 0
 */
int getGameStatus(void);

/**
 * 设置游戏状态
 *
 */
void setGameStatus(int status);

/**
 * 设置指定位置的棋子类型
 */
void setChessIDByPosition(int x, int y, BYTE chessID);

/**
 * 将棋子移动到制定位置
 */
BOOL moveChess(int fromX, int fromY, int toX, int toY);

/**
 * get AI next step
 */
BOOL GetNextStep(int moves[4]);

#endif
