
#include "../include/AlphaBetaEngine.h"

static int PrincipalVariation(int depth, int alpha, int beta)
{
	int score;
	int count,i;
	BYTE chessID;
	int best;

	i = IsGameOver(g_CurPosition, depth);
	if (i != 0)
		return i;

	if (depth <= 0)
		return (*g_Eveluator)(g_CurPosition, (g_i32MaxDepth - depth) % 2);	//返回估值

	count = (*g_MoveGenerator)(g_CurPosition, depth, (g_i32MaxDepth - depth) % 2);	//列出当前局面下一步所有可能走法

	chessID = MakeMove(&g_stMoveList[depth][0]);
	best = -PrincipalVariation( depth - 1, -beta, -alpha);
	UnMakeMove(&g_stMoveList[depth][0], chessID);
	if(depth == g_i32MaxDepth)
	{
		g_IsFindBestMove = TRUE;
		g_stBestMove = g_stMoveList[depth][0];
	}

	for (i = 1; i < count; i++)
	{
		if(best < beta)
		{
			if (best > alpha)
				alpha = best;
			chessID = MakeMove(&g_stMoveList[depth][i]);
			score = -PrincipalVariation(depth - 1, -alpha - 1, -alpha);
			if (score > alpha && score < beta)
			{
				best = -PrincipalVariation(depth-1, -beta, -score);
				if(depth == g_i32MaxDepth)
				{
					g_IsFindBestMove = TRUE;
					g_stBestMove = g_stMoveList[depth][i];
				}
			}else if (score > best)
			{
				best = score;
				if(depth == g_i32MaxDepth)
				{
					g_IsFindBestMove = TRUE;
					g_stBestMove = g_stMoveList[depth][i];
				}
			}
			UnMakeMove(&g_stMoveList[depth][i],chessID);
		}
	}

	return best;
}

BOOL PVS_SearchAGoodMove(BYTE position[MAX_ROW][MAX_COL])
{
	g_IsFindBestMove = FALSE;
	g_i32MaxDepth = g_i32SearchDepth;
	memcpy(g_CurPosition, position, MAX_ROW * MAX_COL);
//	for (m_nMaxDepth = 1; m_nMaxDepth <= m_nSearchDepth; m_nMaxDepth++)
	{
	   PrincipalVariation(g_i32MaxDepth, -20000, 20000);
	}

	//MakeMove(&m_cmBestMove);
	//memcpy(position, CurPosition, 90);
	if(g_IsFindBestMove == TRUE)
		return TRUE;
	else
		return FALSE;
}
