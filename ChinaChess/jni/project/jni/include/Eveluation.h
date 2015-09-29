
#ifndef __EVELUATION_H_
#define __EVELUATION_H_

#include "chess.h"

#define BASEVALUE_PAWN			100
#define BASEVALUE_BISHOP		250
#define BASEVALUE_ELEPHANT		250
#define BASEVALUE_CAR			500
#define BASEVALUE_HORSE			350
#define BASEVALUE_CANON			350
#define BASEVALUE_KING			10000

#define FLEXIBILITY_PAWN		15
#define FLEXIBILITY_BISHOP		1
#define FLEXIBILITY_ELEPHANT	1
#define FLEXIBILITY_CAR			6
#define FLEXIBILITY_HORSE		12
#define FLEXIBILITY_CANON		6
#define FLEXIBILITY_KING		0

/**
 * 初始化
 */
void EveluateInit(void);

/**
 * return compute count
 */
int GetComputeCount(void);

/**
 * 估值函数
 * position	待估值的棋盘
 * bIsRedTurn	TRUE为红方走棋，FALSE为黑方走棋子
 */
int Eveluate(BYTE position[MAX_ROW][MAX_COL], BOOL bIsRedTurn);

#endif
