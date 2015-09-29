
#ifndef __MOVE_GENERATOR_H_
#define __MOVE_GENERATOR_H_

#include "define.h"
#include "chess.h"

#define MAX_DEPTH	8
#define MAX_STEP	80

extern ST_Move g_stMoveList[MAX_DEPTH][MAX_STEP];

/**
 * init move generator
 */
void InitMoveGeneartor(void);

/**
 * check move is valid
 */
BOOL IsValidMove(BYTE position[MAX_ROW][MAX_COL], int fromX, int fromY, int toX, int toY);

/**
 * generator all move
 */
int CreateAllPossibleMove(BYTE position[MAX_ROW][MAX_COL], int depth, int side);

#endif
