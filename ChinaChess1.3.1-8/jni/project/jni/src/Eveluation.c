
#include "../include/Eveluation.h"

static int g_BaseValue[15] = {0};	//存放每个棋子的基本价值
static int g_FlexValue[15] = {0};	//存放每个棋子灵活性分数

static short g_AttackPos[MAX_ROW][MAX_COL] = {0};	//存放每一个位置被威胁的值
static BYTE g_GuardPos[MAX_ROW][MAX_COL] = {0};		//存放每一个位置被保护的值
static BYTE g_FlexibilityPos[MAX_ROW][MAX_COL] = {0};	//存放每一个位置上棋子的灵活性值
static int g_ChessValue[MAX_ROW][MAX_COL] = {0};		//存放每一个位置上的棋子的总价值

static int g_PosCount = 0;		//记录一棋子的相关位置个数
static ST_Position g_stRelatePos[20] = {0};	//记录一个棋子相关位置的数组

static int g_count = 0;	//统计调用了估值函数的叶子节点次数

/**
 * 下面两个常量数组存放了兵在不同位置的附加值，基本上是过河之后越靠近将帅值越高
 */
//红卒的附加值矩阵
static const int BA0[MAX_ROW][MAX_COL]=
{
	{0,		0,		0,		0,		0,  	0,  	0,  	0,  	0},
	{90,	90,		110,	120,	120,	120,	110,	90,		90},
	{90,	90,		110,	120,	120,	120,	110,	90,		90},
	{70,	90,		110,	110,	110,	110,	110,	90,		70},
	{70,	70,		70, 	70, 	70,  	70, 	70,		70,		70},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
};

//黑兵的附加值矩阵
static const int BA1[MAX_ROW][MAX_COL]=
{
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{0,  	0,  	0,  	0,  	0,  	0,  	0,  	0,  	0},
	{70,	70,		70, 	70, 	70,		70, 	70,		70, 	70},
	{70,	90,		110,	110,	110,	110,	110,	90,		70},
	{90,	90,		110,	120,	120,	120,	110,	90,		90},
	{90,	90,		110,	120,	120,	120,	110,	90,		90},
	{0,  	0, 		0,  	0,  	0,  	0,  	0,  	0,  	0},
};

/**
 * 初始化
 */
void EveluateInit(void)
{
	//base value
	g_BaseValue[B_KING] = BASEVALUE_KING;
	g_BaseValue[B_CAR] = BASEVALUE_CAR;
	g_BaseValue[B_HORSE] = BASEVALUE_HORSE;
	g_BaseValue[B_BISHOP] = BASEVALUE_BISHOP;
	g_BaseValue[B_ELEPHANT] = BASEVALUE_ELEPHANT;
	g_BaseValue[B_CANON] = BASEVALUE_CANON;
	g_BaseValue[B_PAWN] = BASEVALUE_PAWN;

	g_BaseValue[R_KING] = BASEVALUE_KING;
	g_BaseValue[R_CAR] = BASEVALUE_CAR;
	g_BaseValue[R_HORSE] = BASEVALUE_HORSE;
	g_BaseValue[R_BISHOP] = BASEVALUE_BISHOP;
	g_BaseValue[R_ELEPHANT] = BASEVALUE_ELEPHANT;
	g_BaseValue[R_CANON] = BASEVALUE_CANON;
	g_BaseValue[R_PAWN] = BASEVALUE_PAWN;

	//flex value
	g_FlexValue[B_KING] = FLEXIBILITY_KING;
	g_FlexValue[B_CAR] = FLEXIBILITY_CAR;
	g_FlexValue[B_HORSE] = FLEXIBILITY_HORSE;
	g_FlexValue[B_BISHOP] = FLEXIBILITY_BISHOP;
	g_FlexValue[B_ELEPHANT] = FLEXIBILITY_ELEPHANT;
	g_FlexValue[B_CANON] = FLEXIBILITY_CANON;
	g_FlexValue[B_PAWN] = FLEXIBILITY_PAWN;

	g_FlexValue[R_KING] = FLEXIBILITY_KING;
	g_FlexValue[R_CAR] = FLEXIBILITY_CAR;
	g_FlexValue[R_HORSE] = FLEXIBILITY_HORSE;
	g_FlexValue[R_BISHOP] = FLEXIBILITY_BISHOP;
	g_FlexValue[R_ELEPHANT] = FLEXIBILITY_ELEPHANT;
	g_FlexValue[R_CANON] = FLEXIBILITY_CANON;
	g_FlexValue[R_PAWN] = FLEXIBILITY_PAWN;

	g_PosCount = 0;
	g_count = 0;
	memset(g_stRelatePos, 0x00, sizeof(ST_Position) * 20);
}

/**
 * 为每一个兵返回附加值
 * x	横坐标
 * y	纵坐标
 * 棋子不是兵则返回0
 */
static int GetPawnValue(int x, int y, BYTE CurSituation[MAX_ROW][MAX_COL])
{
	if (CurSituation[y][x] == R_PAWN)
		return BA0[y][x];

	if (CurSituation[y][x] == B_PAWN)
		return BA1[y][x];

	return 0;
}

/**
 * return compute count
 */
int GetComputeCount(void)
{
	return g_count;
}

/**
 *
 */
static void AddPoint(int x, int y)
{
	g_stRelatePos[g_PosCount].x = x;
	g_stRelatePos[g_PosCount].y = y;
	g_PosCount++;
}

/**
 *
 */
static BOOL CanTouch(BYTE position[MAX_ROW][MAX_COL], int nFromX, int nFromY, int nToX, int nToY)
{
	int row, col;
	int nMoveChessID, nTargetID;

	if (nFromY ==  nToY && nFromX == nToX)
		return FALSE;//目的与源相同

	nMoveChessID = position[nFromY][nFromX];
	nTargetID = position[nToY][nToX];

	switch(nMoveChessID)
	{
	case B_KING:
		if (nTargetID == R_KING)//帅将见面?
		{
			if (nFromX != nToX)
				return FALSE;
			for (row = nFromY + 1; row < nToY; row++)
				if (position[row][nFromX] != NO_CHESS)
					return FALSE;
		}
		else
		{
			if (nToY > 2 || nToX > 5 || nToX < 3)
				return FALSE;//目标点在九宫之外
			if(abs(nFromY - nToY) + abs(nToX - nFromX) > 1)
				return FALSE;//将帅只走一步直线:
		}
		break;

	case R_BISHOP:
		if (nToY < 7 || nToX > 5 || nToX < 3)
			return FALSE;//士出九宫

		if (abs(nFromY - nToY) != 1 || abs(nToX - nFromX) != 1)
			return FALSE;	//士走斜线
		break;

	case B_BISHOP:   //黑士
		if (nToY > 2 || nToX > 5 || nToX < 3)
			return FALSE;//士出九宫

		if (abs(nFromY - nToY) != 1 || abs(nToX - nFromX) != 1)
			return FALSE;	//士走斜线
		break;

	case R_ELEPHANT://红象
		if(nToY < 5)
			return FALSE;//相不能过河

		if(abs(nFromX-nToX) != 2 || abs(nFromY-nToY) != 2)
			return FALSE;//相走田字

		if(position[(nFromY + nToY) / 2][(nFromX + nToX) / 2] != NO_CHESS)
			return FALSE;//相眼被塞住了
		break;

	case B_ELEPHANT://黑象
		if(nToY > 4)
			return FALSE;//相不能过河

		if(abs(nFromX-nToX) != 2 || abs(nFromY-nToY) != 2)
			return FALSE;//相走田字

		if(position[(nFromY + nToY) / 2][(nFromX + nToX) / 2] != NO_CHESS)
			return FALSE;//相眼被塞住了
		break;

	case B_PAWN:     //黑兵
		if(nToY < nFromY)
			return FALSE;//兵不回头

		if( nFromY < 5 && nFromY == nToY)
			return FALSE;//兵过河前只能直走

		if(nToY - nFromY + abs(nToX - nFromX) > 1)
			return FALSE;//兵只走一步直线:
		break;

	case R_PAWN:    //红兵
		if(nToY > nFromY)
			return FALSE;//兵不回头

		if( nFromY > 4 && nFromY == nToY)
			return FALSE;//兵过河前只能直走

		if(nFromY - nToY + abs(nToX - nFromX) > 1)
			return FALSE;//兵只走一步直线:
		break;

	case R_KING:
		if(nTargetID == B_KING)//老将见面?
		{
			if(nFromX != nToX)
				return FALSE;//两个将不在同一列
			for(row = nFromY - 1; row > nToY; row--)
			{
				if(position[row][nFromX] != NO_CHESS)
					return FALSE;//中间有别的子
			}
		}else
		{
			if(nToY < 7 || nToX > 5 || nToX < 3)
				return FALSE;//目标点在九宫之外
			if(abs(nFromY - nToY) + abs(nToX - nFromX) > 1)
				return FALSE;//将帅只走一步直线:
		}
		break;

	case B_CAR:
	case R_CAR:
		if((nFromY != nToY) && (nFromX != nToX))
			return FALSE;	//车走直线:

		if(nFromY == nToY)
		{
			if(nFromX < nToX)
			{
				for(col = nFromX + 1; col < nToX; col++)
				{
					if(position[nFromY][col] != NO_CHESS)
						return FALSE;
				}
			}else
			{
				for(col = nToX + 1; col < nFromX; col++)
				{
					if(position[nFromY][col] != NO_CHESS)
						return FALSE;
				}
			}
		}else
		{
			if(nFromY < nToY)
			{
				for(row = nFromY + 1; row < nToY; row++)
				{
					if(position[row][nFromX] != NO_CHESS)
						return FALSE;
				}
			}else
			{
				for(row= nToY + 1; row < nFromY; row++)
				{
					if(position[row][nFromX] != NO_CHESS)
						return FALSE;
				}
			}
		}
		break;

	case B_HORSE:
	case R_HORSE:
		if(!((abs(nToX - nFromX) == 1 && abs(nToY - nFromY) == 2)
				||(abs(nToX - nFromX) == 2 && abs(nToY - nFromY) == 1)))
			return FALSE;//马走日字

		if(nToX - nFromX == 2)
		{
			col = nFromX + 1;
			row = nFromY;
		}else if(nFromX - nToX == 2)
		{
			col = nFromX - 1;
			row = nFromY;
		}else if(nToY - nFromY == 2)
		{
			col = nFromX;
			row = nFromY + 1;
		}else if(nFromY - nToY == 2)
		{
			col = nFromX;
			row = nFromY - 1;
		}

		if(position[row][col] != (BYTE)NO_CHESS)
			return FALSE;//绊马腿
		break;

	case B_CANON:
	case R_CANON:
		if(nFromY != nToY && nFromX != nToX)
			return FALSE;	//炮走直线:

		//炮不吃子时经过的路线中不能有棋子:------------------
		if(position[nToY][nToX] == NO_CHESS)
		{
			if(nFromY == nToY)
			{
				if(nFromX < nToX)
				{
					for(col = nFromX + 1; col < nToX; col++)
					{
						if(position[nFromY][col] != NO_CHESS)
							return FALSE;
					}
				}else
				{
					for(col = nToX + 1; col < nFromX; col++)
					{
						if(position[nFromY][col]!=NO_CHESS)
							return FALSE;
					}
				}
			}else
			{
				if(nFromY < nToY)
				{
					for(row = nFromY + 1; row < nToY; row++)
					{
						if(position[row][nFromX] != NO_CHESS)
							return FALSE;
					}
				}else
				{
					for(row = nToY + 1; row < nFromY; row++)
					{
						if(position[row][nFromX] != NO_CHESS)
							return FALSE;
					}
				}
			}
		}
		//以上是炮不吃子-------------------------------------
		//吃子时:=======================================
		else
		{
			int nCount=0;
			if(nFromY == nToY)
			{
				if(nFromX < nToX)
				{
					for(col = nFromX + 1;col < nToX; col++)
						if(position[nFromY][col] != NO_CHESS)
							nCount++;
					if(nCount != 1)
						return FALSE;
				}else
				{
					for(col = nToX + 1; col < nFromX; col++)
						if(position[nFromY][col] != NO_CHESS)
							nCount++;
					if(nCount != 1)
						return FALSE;
				}
			}else
			{
				if(nFromY < nToY)
				{
					for(row = nFromY + 1;row < nToY; row++)
						if(position[row][nFromX] != NO_CHESS)
							nCount++;
					if(nCount != 1)
						return FALSE;
				}else
				{
					for(row = nToY + 1; row < nFromY; row++)
						if(position[row][nFromX] != NO_CHESS)
							nCount++;
					if(nCount != 1)
						return FALSE;
				}
			}
		}
		//以上是炮吃子时================================
		break;

	default:
		return FALSE;
	}

	return TRUE;
}

/**
 * 枚举给定位置上棋子的所有相关位置，包括可走的位置和可保护的位置
 */
static int GetRelatePiece(BYTE position[MAX_ROW][MAX_COL], int x, int y)
{
	g_PosCount = 0;
	BYTE chessID;
	BYTE flag;
	int col, row;

	chessID = position[y][x];
	switch(chessID)
	{
	case R_KING:
	case B_KING:
		for(row = 0; row < 3; row++)
		{
			for(col = 3; col < 6; col++)
			{
				if(CanTouch(position, x, y, col, row))
					AddPoint(col, row);
			}
		}
		for(row = 7; row < 10; row++)
		{
			for(col = 3; col < 6; col++)
			{
				if(CanTouch(position, x, y, col, row))
					AddPoint(col, row);
			}
		}
		break;

	case R_BISHOP:
		for(row = 7; row < 10; row++)
		{
			for(col = 3; col < 6; col++)
			{
				if(CanTouch(position, x, y, col, row))
					AddPoint(col, row);
			}
		}
		break;

	case B_BISHOP:
		for(row = 0; row < 3; row++)
		{
			for(col = 3; col < 6; col++)
			{
				if(CanTouch(position, x, y, col, row))
					AddPoint(col, row);
			}
		}
		break;

	case R_ELEPHANT:
	case B_ELEPHANT:
		col = x + 2;
		row = y + 2;
		if(col < 9 && row < 10 && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x + 2;
		row = y - 2;
		if(col < 9 && row>=0 && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x - 2;
		row = y + 2;
		if(col >= 0 && row < 10 && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x - 2;
		row = y - 2;
		if(col >= 0 && row >= 0 && CanTouch(position, x, y, col, row))
			AddPoint(col, row);
		break;

	case R_HORSE:
	case B_HORSE:
		col = x + 2;
		row = y + 1;
		if((col < 9 && row < 10) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x + 2;
		row =y - 1;
		if((col < 9 && row >= 0) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x - 2;
		row = y + 1;
		if((col >= 0 && row < 10) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x - 2;
		row = y - 1;
		if((col >= 0 && row >= 0) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x + 1;
		row = y + 2;
		if((col < 9 && row < 10) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x - 1;
		row = y + 2;
		if((col >= 0 && row < 10) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x + 1;
		row = y - 2;
		if((col < 9 && row >= 0) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);

		col = x - 1;
		row = y - 2;
		if((col >= 0 && row >= 0) && CanTouch(position, x, y, col, row))
			AddPoint(col, row);
		break;

	case R_CAR:
	case B_CAR:
		col = x + 1;
		row = y;
		while(col < 9)
		{
			if(NO_CHESS == position[row][col])
				AddPoint(col, row);
			else
			{
				AddPoint(col, row);
				break;
			}
			col++;
		}

		col = x-1;
		row = y;
		while(col >= 0)
		{
			if(NO_CHESS == position[row][col])
				AddPoint(col, row);
			else
			{
				AddPoint(col, row);
				break;
			}
			col--;
		}

		col = x;
		row = y + 1;//
		while(row < 10)
		{
			if(NO_CHESS == position[row][col])
				AddPoint(col, row);
			else
			{
				AddPoint(col, row);
				break;
			}
			row++;
		}

		col = x;
		row = y-1;//
		while(row>=0)
		{
			if(NO_CHESS == position[row][col])
				AddPoint(col, row);
			else
			{
				AddPoint(col, row);
				break;
			}
			row--;
		}
		break;
	case R_PAWN:
		row = y - 1;
		col = x;

		if(row >= 0)
			AddPoint(col, row);

		if(y < 5)
		{
			row=y;
			col=x+1;
			if(col < 9 )
				AddPoint(col, row);
			col=x-1;
			if(col >= 0 )
				AddPoint(col, row);
		}
		break;
	case B_PAWN:
		row = y + 1;
		col = x;

		if(row < 10 )
			AddPoint(col, row);

		if(y > 4)
		{
			row = y;
			col = x + 1;
			if(col < 9)
				AddPoint(col, row);
			col = x - 1;
			if(col >= 0)
				AddPoint(col, row);
		}
		break;
	case B_CANON:
	case R_CANON:
		col = x + 1;		//
		row = y;
		flag = FALSE;
		while(col < 9)
		{
			if(NO_CHESS == position[row][col])
			{
				if(flag == FALSE)
					AddPoint(col, row);
			}
			else
			{
				if(flag == FALSE)
				{
					flag = TRUE;
				}
				else
				{
					AddPoint(col, row);
					break;
				}
			}
			col++;
		}

		col = x - 1;
		flag = FALSE;
		while(col >= 0)
		{
			if(NO_CHESS == position[row][col])
			{
				if(flag == FALSE)
					AddPoint(col, row);
			}
			else
			{
				if(flag == FALSE)
				{
					flag=TRUE;
				}
				else
				{
					AddPoint(col, row);
					break;
				}
			}
			col--;
		}

		col = x;
		row = y + 1;
		flag = FALSE;
		while(row < 10)
		{
			if(NO_CHESS == position[row][col])
			{
				if(flag == FALSE)
					AddPoint(col, row);
			}
			else
			{
				if(flag == FALSE)
				{
					flag = TRUE;
				}
				else
				{
					AddPoint(col, row);
					break;
				}
			}
			row++;
		}

		row = y - 1;	//
		flag = FALSE;
		while(row >= 0)
		{
			if(NO_CHESS == position[row][col])
			{
				if(flag == FALSE)
					AddPoint(col, row);
			}
			else
			{
				if(flag == FALSE)
				{
					flag = TRUE;
				}
				else
				{
					AddPoint(col, row);
					break;
				}
			}
			row--;
		}
		break;

	default:
		break;
	}

	return g_PosCount;
}

/**
 * 估值函数
 * position	待估值的棋盘
 * bIsRedTurn	TRUE为红方走棋，FALSE为黑方走棋子
 */
int Eveluate(BYTE position[MAX_ROW][MAX_COL], BOOL bIsRedTurn)
{
	int row, col, k;
	int chessType, targetType;
	g_count++;

	memset(g_ChessValue,0, 360);
	memset(g_AttackPos,0, 180);
	memset(g_GuardPos,0, 90);
	memset(g_FlexibilityPos, 0, 90);

	for(row = 0; row < MAX_ROW; row++)
	{
		for(col = 0; col < MAX_COL; col++)
		{
			if(position[row][col] != NO_CHESS)
			{
				chessType = position[row][col];
				GetRelatePiece(position, col, row);
				for (k = 0; k < g_PosCount; k++)
				{
					targetType = position[g_stRelatePos[k].y][g_stRelatePos[k].x];
					if (targetType == NO_CHESS)
					{
						g_FlexibilityPos[row][col]++;
					}else
					{
						if (IsSameSide(chessType, targetType))
						{
							g_GuardPos[g_stRelatePos[k].y][g_stRelatePos[k].x]++;
						}else
						{
							g_AttackPos[g_stRelatePos[k].y][g_stRelatePos[k].x]++;
							g_FlexibilityPos[row][col]++;

							switch (targetType)
							{
							case R_KING:
								if (!bIsRedTurn)
									return 18888;
								break;

							case B_KING:
								if (bIsRedTurn)
									return 18888;
								break;

							default:
								g_AttackPos[g_stRelatePos[k].y][g_stRelatePos[k].x]
								     += (30 + (g_BaseValue[targetType] - g_BaseValue[chessType]) / 10) / 10;
								break;
							}
						}
					}
				}
			}
		}
	}

	for(row = 0; row < MAX_ROW; row++)
	{
		for(col = 0; col < MAX_COL; col++)
		{
			if(position[row][col] != NO_CHESS)
			{
				chessType = position[row][col];
				g_ChessValue[row][col]++;
				g_ChessValue[row][col] += g_FlexValue[chessType] * g_FlexibilityPos[row][col];
				g_ChessValue[row][col] += GetPawnValue(col, row, position);
			}
		}
	}

	int nHalfvalue;
	for(row = 0; row < 10; row++)
	{
		for(col = 0; col < 9; col++)
		{
			if(position[row][col] != NO_CHESS)
			{
				chessType = position[row][col];
				nHalfvalue = g_BaseValue[chessType]/16;
				g_ChessValue[row][col] += g_BaseValue[chessType];

				if (IsRed(chessType))
				{
					if (g_AttackPos[row][col])
					{
						if (bIsRedTurn)
						{
							if (chessType == R_KING)
							{
								g_ChessValue[row][col]-= 20;
							}else
							{
								g_ChessValue[row][col] -= nHalfvalue * 2;
								if (g_GuardPos[row][col])
									g_ChessValue[row][col] += nHalfvalue;
							}
						}else
						{
							if (chessType == R_KING)
								return 18888;
							g_ChessValue[row][col] -= nHalfvalue*10;
							if (g_GuardPos[row][col])
								g_ChessValue[row][col] += nHalfvalue*9;
						}
						g_ChessValue[row][col] -= g_AttackPos[row][col];
					}else
					{
						if (g_GuardPos[row][col])
							g_ChessValue[row][col] += 5;
					}
				}else
				{
					if (g_AttackPos[row][col])
					{
						if (!bIsRedTurn)
						{
							if (chessType == B_KING)
							{
								g_ChessValue[row][col] -= 20;
							}else
							{
								g_ChessValue[row][col] -= nHalfvalue * 2;
								if (g_GuardPos[row][col])
									g_ChessValue[row][col] += nHalfvalue;
							}
						}else
						{
							if (chessType == B_KING)
								return 18888;
							g_ChessValue[row][col] -= nHalfvalue*10;
							if (g_GuardPos[row][col])
								g_ChessValue[row][col] += nHalfvalue*9;
						}
						g_ChessValue[row][col] -= g_AttackPos[row][col];
					}else
					{
						if (g_GuardPos[row][col])
							g_ChessValue[row][col] += 5;
					}
				}
			}
		}
	}

	int nRedValue = 0;
	int	nBlackValue = 0;
	for(row = 0; row < 10; row++)
	{
		for(col = 0; col < 9; col++)
		{
			chessType = position[row][col];
//			if (chessType == R_KING || chessType == B_KING)
//				g_ChessValue[row][col] = 10000;
			if (chessType != NO_CHESS)
			{
				if (IsRed(chessType))
					nRedValue += g_ChessValue[row][col];
				else
					nBlackValue += g_ChessValue[row][col];
			}
		}
	}

	if (bIsRedTurn)
		return nRedValue - nBlackValue;
	else
		return  nBlackValue-nRedValue ;
}

