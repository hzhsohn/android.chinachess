
#include "../include/AlphaBetaAndHH.h"

static int alphabeta(int depth, int alpha, int beta)
{
	int score;
	int count,i;
	BYTE chessID;

	i = IsGameOver(g_CurPosition, depth);
	if (i != 0)
		return i;

	if (depth <= 0)
	{
		//return (*g_Eveluator)(g_CurPosition, (g_i32MaxDepth - depth) % 2);	//返回估值
		return Eveluate(g_CurPosition, (g_i32MaxDepth - depth) % 2);
	}

	//count = (*g_MoveGenerator)(g_CurPosition, depth, (g_i32MaxDepth - depth) % 2);	//列出当前局面下一步所有可能走法
	count = CreateAllPossibleMove(g_CurPosition, depth, (g_i32MaxDepth - depth) % 2);

	for (i = 0; i < count; i++)
	{
		g_stMoveList[depth][i].score = GetHistoryScore(&g_stMoveList[depth][i]);
	}

	MergeSort(g_stMoveList[depth], count, 0);
	int bestmove = -1;
	for (i = 0; i < count; i++)
	{
		chessID = MakeMove(&g_stMoveList[depth][i]);
		score = -alphabeta(depth - 1, -beta, -alpha);
		UnMakeMove(&g_stMoveList[depth][i],chessID);

		if (score > alpha)
		{
			alpha = score;
			if(depth == g_i32MaxDepth)
			{
				g_IsFindBestMove = TRUE;
				g_stBestMove = g_stMoveList[depth][i];
			}
			bestmove = i;
		}
        if (alpha >= beta)
		{
			bestmove = i;
			break;
		}

	}
	if (bestmove != -1)
	EnterHistoryScore(&g_stMoveList[depth][bestmove], depth);
	return alpha;
}

BOOL AlphaBetaAndHH_SearchAGoodMove(BYTE position[MAX_ROW][MAX_COL])
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s, max search depth %d",__FUNCTION__, g_i32SearchDepth);
	g_IsFindBestMove = FALSE;
	g_i32MaxDepth = g_i32SearchDepth;
	memcpy(g_CurPosition, position, MAX_ROW * MAX_COL);
	ResetHistoryTable();
	alphabeta(g_i32MaxDepth, -20000, 20000);

	if(g_IsFindBestMove == TRUE)
		return TRUE;
	else
		return FALSE;
}
