
#ifndef __SEARCH_ENGINE_INTERFACE_H_
#define __SEARCH_ENGINE_INTERFACE_H_

#include "define.h"
#include "chess.h"
#include "MoveGenerator.h"
#include "Eveluation.h"
#include "FAlphaBetaEngine.h"
#include "PVSEngine.h"
#include "NegaMaxEngine.h"
#include "AlphaBetaEngine.h"
#include "AlphaBetaAndHH.h"

#define DEFAULT_SEARCH_DEPTH	4

typedef int (*MoveGenerator_t)(BYTE position[MAX_ROW][MAX_COL], int depth, int side);
typedef int (*Eveluator_t)(BYTE position[MAX_ROW][MAX_COL], BOOL bIsRedTurn);
typedef BOOL (*SearchAGoodMove_t)(BYTE position[MAX_ROW][MAX_COL]);

typedef enum
{
	NEGA_MAX_ENGINE,
	ALPHA_BETA_ENGINE,
	FALPHA_BETA_ENGINE,
	PVS_ENGINE,
	ALPHA_BETA_HH_ENGINE,
}SEARCH_ENGINE_TYPE;

typedef enum
{
	EVELUATION,
}EVELUATION_TYPE;

//var declare
extern int g_i32SearchDepth;
extern int g_i32MaxDepth;
extern BYTE g_CurPosition[MAX_ROW][MAX_COL];
extern ST_Move g_stBestMove;
extern BOOL g_IsFindBestMove;
extern MoveGenerator_t g_MoveGenerator;
extern Eveluator_t g_Eveluator;

//extern function declare
int MakeMove(ST_Move *move);
void UnMakeMove(ST_Move *move, BYTE chessID);

/**
 * init board
 */
void InitSearchEngine(void);

/**
 * uninit board
 */
void UnInitSearchEngine(void);

/**
 * 设置最大搜索深度
 */
void SetSearchDepth(int depth);

/**
 * 设置估值引擎
 */
void SetEveluator(Eveluator_t eveluator);

/**
 * 设置走法产生器
 */
void setMoveGenerator(MoveGenerator_t generator);

/**
 * 判断当前局面的胜负
 */
int IsGameOver(BYTE position[MAX_ROW][MAX_COL], int depth);

#endif
