
#include "../include/SearchEngineInterface.h"
#include <sys/time.h>

int g_i32SearchDepth = DEFAULT_SEARCH_DEPTH;	//最大搜索深度
int g_i32MaxDepth = 0;

BYTE g_CurPosition[MAX_ROW][MAX_COL] = {0};	//保存当前局面形势
ST_Move g_stBestMove = {0};	//当前局面最优的一步走法
BOOL g_IsFindBestMove = FALSE;	//是否找到最优走法

MoveGenerator_t g_MoveGenerator;		//走法产生器
Eveluator_t g_Eveluator;				//估值核心指针
SearchAGoodMove_t g_SearchEngine;

#if 0
typedef struct
{
	void (*InitSearchEngine)(void) = InitSearchEngine;
	void (*UnInitSearchEngine(void)) = UnInitSearchEngine;
	void (*SetSearchDepth)(int depth) = SetSearchDepth;
	void (*SetEveluator)(Eveluator_t eveluator) = SetEveluator;
	void (*SetMoveGenerator)(MoveGenerator_t generator) = SetMoveGenerator;
	BOOL (*SetSearchEngine)(SEARCH_ENGINE_TYPE type) = SetSearchEngine;
	int (*IsGameOver)(BYTE position[MAX_ROW][MAX_COL], int depth) = IsGameOver;
	SearchAGoodMove_t searchAGoodMove = NULL;
}SearchEngineInterface_t;
#endif

/**
 * init board
 */
void InitSearchEngine(void)
{
	//g_i32SearchDepth = DEFAULT_SEARCH_DEPTH;
	g_MoveGenerator = CreateAllPossibleMove;
	g_Eveluator = Eveluate;
	g_SearchEngine= AlphaBetaAndHH_SearchAGoodMove;
	memset(&g_stBestMove, 0x00, sizeof(ST_Move));
	memset(g_CurPosition, 0x00, MAX_ROW * MAX_COL);

	InitMoveGeneartor();
	EveluateInit();
}

/**
 * uninit board
 */
void UnInitSearchEngine(void)
{
	g_i32SearchDepth = 0;
	g_MoveGenerator = NULL;
	g_Eveluator = NULL;
	g_SearchEngine = NULL;
	memset(&g_stBestMove, 0x00, sizeof(ST_Move));
	memset(g_CurPosition, 0x00, MAX_ROW * MAX_COL);
}

/**
 * 设置最大搜索深度
 */
void SetSearchDepth(int depth)
{
	g_i32SearchDepth = depth;
}

/**
 * 设置估值引擎
 */
void SetEveluator(Eveluator_t eveluator)
{
	g_Eveluator = eveluator;
}

/**
 * 设置走法产生器
 */
void SetMoveGenerator(MoveGenerator_t generator)
{
	g_MoveGenerator = generator;
}

/**
 * 设置搜索引擎
 */
BOOL SetSearchEngine(EVELUATION_TYPE type)
{
	switch(type)
	{

	case NEGA_MAX_ENGINE:
		g_SearchEngine = NegaMax_SearchAGoodMove;
		break;

	case ALPHA_BETA_ENGINE:
		g_SearchEngine = AlphaBeta_SearchAGoodMove;
		break;

	case FALPHA_BETA_ENGINE:
		g_SearchEngine = FAlphaBeta_SearchAGoodMove;
		break;

	case PVS_ENGINE:
		g_SearchEngine = PVS_SearchAGoodMove;
		break;

	case ALPHA_BETA_HH_ENGINE:
		g_SearchEngine = AlphaBetaAndHH_SearchAGoodMove;
		break;

	default:
		break;
	}

	return TRUE;
}

//
int MakeMove(ST_Move *move)
{
	BYTE chessID = g_CurPosition[move->to.y][move->to.x];
	g_CurPosition[move->to.y][move->to.x] = g_CurPosition[move->from.y][move->from.x];	//将棋子移到目标位置
	g_CurPosition[move->from.y][move->from.x] = NO_CHESS;

	return chessID;
}

void UnMakeMove(ST_Move *move, BYTE chessID)
{
	g_CurPosition[move->from.y][move->from.x] = g_CurPosition[move->to.y][move->to.x];
	g_CurPosition[move->to.y][move->to.x] = chessID;
}

/**
 * 找出当前局面的下一步
 */
BOOL SearchAGoodMove(BYTE position[MAX_ROW][MAX_COL])
{
	struct timeval start_time, end_time;
	float use_time = 0.0f;
	BOOL result = FALSE;

	memset(&g_stBestMove, 0x00, sizeof(ST_Move));

	gettimeofday(&start_time, NULL);
	result = (*g_SearchEngine)(position);
	gettimeofday(&end_time, NULL);

	use_time = 1000000 * (end_time.tv_sec - start_time.tv_sec) + end_time.tv_usec - start_time.tv_usec;	//used time
	//__android_log_print(ANDROID_LOG_DEBUG,"AI"," cost %f time, %d Nodes were eveluated",use_time / 1000000, GetComputeCount());

	return result;
}

/**
 * 判断当前局面的胜负
 */
int IsGameOver(BYTE position[MAX_ROW][MAX_COL], int depth)
{
	int row, col;
	int flag;
	BOOL redLive = FALSE;
	BOOL blackLive = FALSE;

	//检查红方九宫是否有将帅
	for(row = 7; row < MAX_ROW; row++)
	{
		for(col = 3; col < 6; col++)
		{
			if(position[row][col] == B_KING)
				blackLive = TRUE;
			if(position[row][col] == R_KING)
				redLive = TRUE;
		}
	}

	//检查黑方九宫是否有将帅
	for(row = 0; row < 3; row++)
	{
		for(col = 3; col < 6; col++)
		{
			if(position[row][col] == B_KING)
				blackLive = TRUE;
			if(position[row][col] == R_KING)
				redLive = TRUE;
		}
	}

	flag = (g_i32SearchDepth - depth + 1) % 2;
	if(redLive == FALSE)
	{
		if(flag)
			return 19990 + depth;	//奇数层返回极大值
		else
			return -19990 - depth;	//偶数层返回极小值
	}

	if(blackLive == FALSE)
	{
		if(flag)
			return -19990 - depth;	//奇数层返回极小值
		else
			return 19990 + depth;	//偶数层返回极大值
	}

	return 0;	//将帅都在，返回0
}
