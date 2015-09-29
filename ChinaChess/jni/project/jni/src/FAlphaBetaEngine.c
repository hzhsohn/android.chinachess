
#include "../include/AlphaBetaEngine.h"

static int FAlphaBeta(int depth, int alpha, int beta)
{
	int current = -20000 ;
	int score;
	int count, i;
	BYTE chessID;

	i = IsGameOver(g_CurPosition, depth);
	if (i != 0)
		return i;

	if (depth <= 0)
		return (*g_Eveluator)(g_CurPosition, (g_i32MaxDepth - depth) % 2);	//返回估值

	count = (*g_MoveGenerator)(g_CurPosition, depth, (g_i32MaxDepth - depth) % 2);	//列出当前局面下一步所有可能走法

	for (i = 0; i < count; i++)
	{
		chessID = MakeMove(&g_stMoveList[depth][i]);
		score = -FAlphaBeta(depth - 1, -beta, -alpha);
		UnMakeMove(&g_stMoveList[depth][i], chessID);

		if (score > current)
		{
			current = score;
			if(depth == g_i32MaxDepth)
			{
				g_IsFindBestMove = TRUE;
				g_stBestMove = g_stMoveList[depth][i];
			}
			if(score > alpha)
				alpha = score;
			if(score >= beta)	//beta剪枝
				break;
		}
	}

	return current;
}

BOOL FAlphaBeta_SearchAGoodMove(BYTE position[MAX_ROW][MAX_COL])
{
	g_IsFindBestMove = FALSE;
	g_i32MaxDepth = g_i32SearchDepth;
	memcpy(g_CurPosition, position, MAX_ROW * MAX_COL);
	FAlphaBeta(g_i32MaxDepth, -20000, 20000);
	//MakeMove(&m_cmBestMove);
	//memcpy(position, CurPosition, 90);
	if(g_IsFindBestMove == TRUE)
		return TRUE;
	else
		return FALSE;
}
