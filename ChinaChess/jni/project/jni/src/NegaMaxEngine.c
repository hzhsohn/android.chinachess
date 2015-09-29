
#include "../include/NegaMaxEngine.h"

static BOOL findBestMove = FALSE;

static int NegaMax(int depth)
{
	int current = -20000 ;
	int score;
	int count;
	int i;
	int chessID;

	i = IsGameOver(g_CurPosition, depth);
	if (i != 0)
		return i;	//棋局结束，返回极大/极小值

	if (depth <= 0)	//叶子节点取估值
    	return (*g_Eveluator)(g_CurPosition, (g_i32MaxDepth - depth) % 2);	//返回估值

	count = (*g_MoveGenerator)(g_CurPosition, depth, (g_i32MaxDepth - depth) % 2);	//列出当前局面下一步所有可能走法

	for (i = 0; i < count; i++)
	{
		chessID = MakeMove(&g_stMoveList[depth][i]);	//根据所有可能走法产生新局面
		score = -NegaMax(depth - 1);		//递归调用负极大值搜索下一层节点
		UnMakeMove(&g_stMoveList[depth][i],chessID);	//恢复当前局面

		if (score > current)
		{
			current = score;
			if(depth == g_i32MaxDepth)
			{
				g_IsFindBestMove = TRUE;
				__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,find best move",__FUNCTION__);
				//memcpy(&g_stBestMove, &(g_stMoveList[depth][i]), sizeof(ST_Move));
				g_stBestMove = g_stMoveList[depth][i];	//靠近根部时保存最佳走法
			}
		}
	}

	return current;	//返回极大值
}

BOOL NegaMax_SearchAGoodMove(BYTE position[MAX_ROW][MAX_COL])
{
	g_IsFindBestMove = FALSE;
	g_i32MaxDepth = g_i32SearchDepth;
	memcpy(g_CurPosition, position, MAX_ROW * MAX_COL);
	NegaMax(g_i32MaxDepth);
	//MakeMove(&g_stBestMove);
	//memcpy(position, g_CurPosition, MAX_ROW * MAX_COL);
	if(g_IsFindBestMove == TRUE)
		return TRUE;
	else
		return FALSE;
}
