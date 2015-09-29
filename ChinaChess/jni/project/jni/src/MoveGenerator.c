
#include "../include/MoveGenerator.h"
#include "../include/chess.h"

ST_Move g_stMoveList[MAX_DEPTH][MAX_STEP];

static int move_count = 0;

/**
 * init move generator
 */
void InitMoveGeneartor(void)
{
	move_count = 0;
	memset(g_stMoveList, 0x00, MAX_DEPTH * MAX_STEP * sizeof(ST_Move));
}

/**
 * check move is valid
 */
BOOL IsValidMove(BYTE position[MAX_ROW][MAX_COL], int fromX, int fromY, int toX, int toY)
{
	int i,j;
	int moveId;
	int targetId;

	if(fromX == toX && fromY == toY)	//
	{
		//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,line %d--move chess == target chess---\n",__FUNCTION__,__LINE__);
		return FALSE;
	}

	if(toX >= MAX_COL || toY >= MAX_ROW)	//out of map
	{
		//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,line %d--position out of map---\n",__FUNCTION__,__LINE__);
		return FALSE;
	}
	//dump_position();
	moveId = position[fromY][fromX];
	targetId = position[toY][toX];
	if(moveId == NO_CHESS)
	{
		//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,line %d--move chess id is no_chess---\n",__FUNCTION__,__LINE__);
		return FALSE;
	}

	if(targetId != NO_CHESS)
	{
		if(IsSameSide(moveId, targetId) == TRUE)	//the same side
		{
			//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,line %d--the same side---\n",__FUNCTION__,__LINE__);
			return FALSE;
		}
	}

#if 0
	switch(moveId)
	{
	case B_KING:
		if (targetId == R_KING)
		{
			if (fromX != toX)
				return FALSE;
			for (i = fromY + 1; i < toY; i++)
				if (position[i][fromX] != NO_CHESS)
					return FALSE;
		}
		else
		{
			if (toY > 2 || toX > 5 || toX < 3)
				return FALSE;
			if(abs(fromY - toY) + abs(toX - fromX) > 1)
				return FALSE;
		}
		break;
	case R_BISHOP:

		if (toY < 7 || toX > 5 || toX < 3)
			return FALSE;

		if (abs(fromY - toY) != 1 || abs(toX - fromX) != 1)
			return FALSE;

		break;

	case B_BISHOP:

		if (toY > 2 || toX > 5 || toX < 3)
			return FALSE;

		if (abs(fromY - toY) != 1 || abs(toX - fromX) != 1)
			return FALSE;

		break;

	case R_ELEPHANT:

		if(toY < 5)
			return FALSE;

		if(abs(fromX-toX) != 2 || abs(fromY-toY) != 2)
			return FALSE;

		if(position[(fromY + toY) / 2][(fromX + toX) / 2] != NO_CHESS)
			return FALSE;

		break;

	case B_ELEPHANT:

		if(toY > 4)
			return FALSE;

		if(abs(fromX-toX) != 2 || abs(fromY-toY) != 2)
			return FALSE;

		if(position[(fromY + toY) / 2][(fromX + toX) / 2] != NO_CHESS)
			return FALSE;

		break;

	case B_PAWN:

		if(toY < fromY)
			return FALSE;

		if( fromY < 5 && fromY == toY)
			return FALSE;

		if(toY - fromY + abs(toX - fromX) > 1)
			return FALSE;

		break;

	case R_PAWN:

		if(toY > fromY)
			return FALSE;

		if( fromY > 4 && fromY == toY)
			return FALSE;

		if(fromY - toY + abs(toX - fromX) > 1)
			return FALSE;

		break;

	case R_KING:
		if (targetId == B_KING)
		{
			if (fromX != toX)
				return FALSE;
			for (i = fromY - 1; i > toY; i--)
				if (position[i][fromX] != NO_CHESS)
					return FALSE;
		}
		else
		{
			if (toY < 7 || toX > 5 || toX < 3)
				return FALSE;
			if(abs(fromY - toY) + abs(toX - fromX) > 1)
				return FALSE;
		}
		break;

	case B_CAR:
	case R_CAR:

		if(fromY != toY && fromX != toX)
			return FALSE;

		if(fromY == toY)
		{
			if(fromX < toX)
			{
				for(i = fromX + 1; i < toX; i++)
					if(position[fromY][i] != NO_CHESS)
						return FALSE;
			}
			else
			{
				for(i = toX + 1; i < fromX; i++)
					if(position[fromY][i] != NO_CHESS)
						return FALSE;
			}
		}
		else
		{
			if(fromY < toY)
			{
				for(j = fromY + 1; j < toY; j++)
					if(position[j][fromX] != NO_CHESS)
						return FALSE;
			}
			else
			{
				for(j= toY + 1; j < fromY; j++)
					if(position[j][fromX] != NO_CHESS)
						return FALSE;
			}
		}

		break;

	case B_HORSE:
	case R_HORSE:

		if(!((abs(toX-fromX)==1 && abs(toY-fromY)==2)
			||(abs(toX-fromX)==2&&abs(toY-fromY)==1)))
			return FALSE;

		if	(toX-fromX==2)
		{
			i=fromX+1;
			j=fromY;
		}
		else if	(fromX-toX==2)
		{
			i=fromX-1;
			j=fromY;
		}
		else if	(toY-fromY==2)
		{
			i=fromX;
			j=fromY+1;
		}
		else if	(fromY-toY==2)
		{
			i=fromX;
			j=fromY-1;
		}

		if(position[j][i] != NO_CHESS)
			return FALSE;

		break;

	case B_CANON:
	case R_CANON:

		if(fromY!=toY && fromX!=toX)
			return FALSE;

		if(position[toY][toX] == NO_CHESS)
		{
			if(fromY == toY)
			{
				if(fromX < toX)
				{
					for(i = fromX + 1; i < toX; i++)
						if(position[fromY][i] != NO_CHESS)
							return FALSE;
				}
				else
				{
					for(i = toX + 1; i < fromX; i++)
						if(position[fromY][i]!=NO_CHESS)
							return FALSE;
				}
			}
			else
			{
				if(fromY < toY)
				{
					for(j = fromY + 1; j < toY; j++)
						if(position[j][fromX] != NO_CHESS)
							return FALSE;
				}
				else
				{
					for(j = toY + 1; j < fromY; j++)
						if(position[j][fromX] != NO_CHESS)
							return FALSE;
				}
			}
		}

		else
		{
			int count=0;
			if(fromY == toY)
			{
				if(fromX < toX)
				{
					for(i=fromX+1;i<toX;i++)
						if(position[fromY][i]!=NO_CHESS)
							count++;
						if(count != 1)
							return FALSE;
				}
				else
				{
					for(i=toX+1;i<fromX;i++)
						if(position[fromY][i] != NO_CHESS)
							count++;
						if(count!=1)
							return FALSE;
				}
			}
			else
			{
				if(fromY<toY)
				{
					for(j=fromY+1;j<toY;j++)
						if(position[j][fromX]!=NO_CHESS)
							count++;
						if(count!=1)
							return FALSE;
				}
				else
				{
					for(j=toY+1;j<fromY;j++)
						if(position[j][fromX] != NO_CHESS)
							count++;
						if(count!=1)
							return FALSE;
				}
			}
		}
		break;
	default:
		return FALSE;
	}
#else
	switch(moveId)
	{
	case B_KING:
		if(targetId == R_KING)
		{
			if(fromX != toX)
				return FALSE;

			for(i = fromY + 1; i < toY; i++)
			{
				if(position[i][fromX] != NO_CHESS)
					return FALSE;
			}
		}else
		{
			if(toY > 2 || toX > 5 || toX < 3)
				return FALSE;		//超出九宫

			if(abs(fromY - toY) + abs(toX - fromX) > 1)
				return FALSE;		//只能在直线走一步
		}

		break;
	case R_KING:
		if(targetId == B_KING)
		{
			if(fromX != toX)
				return FALSE;

			for(i = fromY - 1; i > toY; i--)
			{
				if(position[i][fromX] != NO_CHESS)
					return FALSE;
			}
		}else
		{
			if(toY < 7 || toX > 5 || toX < 3)
				return FALSE;	//超出九宫

			if(abs(fromY - toY) + abs(toX - fromX) > 1)
				return FALSE;	//只能在直线走一步
		}

		break;
	case R_BISHOP:
		if(toY < 7 || toX > 5 || toX < 3)
			return FALSE;		//超出九宫

		if((abs(fromY - toY) != 1) || (abs(toX - fromX) != 1))
			return FALSE;		//士走斜线

		break;
	case B_BISHOP:
		if(toY > 2 || toX > 5 || toX < 3)
			return FALSE;		//超出九宫

		if((abs(fromY - toY) != 1) || (abs(toX - fromX) != 1))
			return FALSE;		//士走斜线

		break;
	case R_ELEPHANT:
		if(toY < 5)
			return FALSE;	//红相不能过河

		if((abs(fromX - toX) != 2) || (abs(fromY - toY) != 2))
			return FALSE;	//相走田字

		if(position[(fromY + toY) / 2][(fromX + toX) / 2] != NO_CHESS)
			return FALSE;	//相眼上不能有棋子

		break;
	case B_ELEPHANT:
		if(toY > 4)
			return FALSE;	//黑象不能过河

		if((abs(fromX - toX) != 2) || (abs(fromY - toY) != 2))
			return FALSE;	//象走田字

		if(position[(fromY + toY) / 2][(fromX + toX) / 2] != NO_CHESS)
			return FALSE;	//象眼上不能有棋子

		break;
	case B_PAWN:
		if(toY < fromY)
			return FALSE;	//兵不能后退

		if((fromY < 5) && (fromY == toY))
			return FALSE;	//过河前只能直走

		if(toY - fromY + abs(toX - fromX) > 1)
			return FALSE;	//兵只能走一步直线

		break;
	case R_PAWN:
		if(toY > fromY)
			return FALSE;	//卒不能后退

		if(fromY > 4 && fromY == toY)
			return FALSE;	//卒过河前只能向前

		if(fromY - toY + abs(toX - fromX) > 1)
			return FALSE;	//卒只能走一步直线

		break;
	case B_CAR:
	case R_CAR:
		if((fromY != toY) && (fromX != toX))
			return FALSE;	//俥只能走直线

		if(fromY == toY)
		{
			if(fromX < toX)
			{
				for(i = fromX + 1; i < toX; i++)
				{
					if(position[fromY][i] != NO_CHESS)
						return FALSE;
				}
			}else
			{
				for(i = toX + 1; i < fromX; i++)
				{
					if(position[fromY][i] != NO_CHESS)
						return FALSE;
				}
			}
		}else
		{
			if(fromY < toY)
			{
				for(j = fromY + 1; j < toY; j++)
				{
					if(position[j][fromX] != NO_CHESS)
						return FALSE;
				}
			}else
			{
				for(j = toY + 1; j < fromY; j++)
				{
					if(position[j][fromX] != NO_CHESS)
						return FALSE;
				}
			}
		}

		break;
	case B_HORSE:
	case R_HORSE:
		if(!(((abs(toX - fromX) == 1) && (abs(toY - fromY) == 2))
				|| ((abs(toX - fromX) == 2) && (abs(toY - fromY) == 1))))
			return FALSE;	//马走日字

		if(toX - fromX == 2)
		{
			i = fromX + 1;
			j = fromY;
		}else if(fromX - toX == 2)
		{
			i = fromX - 1;
			j = fromY;
		}else if(toY - fromY == 2)
		{
			i = fromX;
			j = fromY + 1;
		}else if(fromY - toY == 2)
		{
			i = fromX;
			j = fromY - 1;
		}
		if(position[j][i] != NO_CHESS)
			return FALSE;	//马腿被绊

		break;
	case B_CANON:
	case R_CANON:
		if(fromY != toY && fromX != toX)
			return FALSE;	//炮走直线

		if(position[toY][toX] == NO_CHESS)	//炮没有吃对方棋子时，只走直线不能中途有棋子阻挡
		{
			if(fromY == toY)
			{
				if(fromX < toX)
				{
					for(i = fromX + 1; i < toX; i++){
						if(position[fromY][i] != NO_CHESS)
							return FALSE;
					}
				}else
				{
					for(i = toX + 1; i < fromX; i++)
					{
						if(position[fromY][i] != NO_CHESS)
							return FALSE;
					}
				}
			}else
			{
				if(fromY < toY)
				{
					for(j = fromY + 1; j < toY; j++)
					{
						if(position[j][fromX] != NO_CHESS)
							return FALSE;
					}
				}else
				{
					for(j = toY + 1; j < fromY; j++)
					{
						if(position[j][fromX] != NO_CHESS)
							return FALSE;
					}
				}
			}
		}else	//炮吃对方棋子时，中间只能有一个棋子
		{
			int count = 0;	//计算中间棋子个数
			if(fromY == toY)
			{
				if(fromX < toX)
				{
					for(i = fromX + 1; i < toX; i++)
					{
						if(position[fromY][i] != NO_CHESS)
							count++;
					}
				}else
				{
					for(i = toX + 1; i < fromX; i++)
					{
						if(position[fromY][i] != NO_CHESS)
							count++;
					}
				}
			}else
			{
				if(fromY < toY)
				{
					for(j = fromY + 1; j < toY; j++)
					{
						if(position[j][fromX] != NO_CHESS)
							count++;
					}
				}else
				{
					for(j = toY + 1; j < fromY; j++)
					{
						if(position[j][fromX] != NO_CHESS)
							count++;
					}
				}
			}
			if(count != 1)	//中间有且仅有一个棋子
				return FALSE;
		}

		break;
	default:
		return FALSE;
	}
#endif

	return TRUE;
}

/**
 * add move to movelist
 */
static int AddMove(int fromX, int fromY, int toX, int toY, int depth)
{
	//if(depth >= MAX_DEPTH)
	//	return -1;
	g_stMoveList[depth][move_count].from.x = fromX;
	g_stMoveList[depth][move_count].from.y = fromY;
	g_stMoveList[depth][move_count].to.x = toX;
	g_stMoveList[depth][move_count].to.y = toY;

	move_count++;
	return move_count;
}

static void Gen_KingMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	for(row = 0; row < 3; row++)
	{
		for(col = 3; col < 6; col++)
			if(IsValidMove(position,x,y,col,row) == TRUE)
				AddMove(x,y,col,row,depth);
	}

	for(row = 7; row < MAX_ROW; row++)
	{
		for(col = 3; col < 6; col++)
			if(IsValidMove(position,x,y,col,row) == TRUE)
				AddMove(x,y,col,row,depth);
	}
}

static void Gen_RBishopMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	for(row = 7; row < MAX_ROW; row++)
	{
		for(col = 3; col < 6; col++)
			if(IsValidMove(position,x,y,col,row) == TRUE)
				AddMove(x,y,col,row,depth);
	}
}

static void Gen_BBishopMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	for(row = 0; row < 3; row++)
	{
		for(col = 3; col < 6; col++)
			if(IsValidMove(position,x,y,col,row) == TRUE)
				AddMove(x,y,col,row,depth);
	}
}

static void Gen_ElephantMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;

	//插入右下方的有效走法
	col = x + 2;
	row = y + 2;
	if(col < MAX_COL && row < MAX_ROW && (IsValidMove(position,x,y,col,row) == TRUE))
		AddMove(x,y,col,row,depth);

	//插入右上方的有效走法
	col = x + 2;
	row = y - 2;
	if(col < MAX_COL && row >= 0 && (IsValidMove(position,x,y,col,row) == TRUE))
		AddMove(x,y,col,row,depth);

	//插入左下方的有效走法
	col = x - 2;
	row = y + 2;
	if(col >= 0 && row < MAX_ROW && (IsValidMove(position,x,y,col,row) == TRUE))
		AddMove(x,y,col,row,depth);

	//插入左上方的有效走法
	col = x - 2;
	row = y - 2;
	if(col >= 0 && row >= 0 && (IsValidMove(position,x,y,col,row) == TRUE))
		AddMove(x,y,col,row,depth);
}

static void Gen_HorseMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;

	//插入右下方的有效走法
	col = x + 2;
	row = y + 1;
	if(col < MAX_COL && row < MAX_ROW && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);
	col = x + 1;
	row = y + 2;
	if(col < MAX_COL && row < MAX_ROW && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);

	//插入右上方的有效走法
	col = x + 2;
	row = y - 1;
	if(col < MAX_COL && row >= 0 && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);
	col = x + 1;
	row = y - 2;
	if(col < MAX_COL && row >= 0 && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);

	//插入左上方的有效走法
	col = x - 2;
	row = y - 1;
	if(col >= 0 && row >= 0 && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);
	col = x - 1;
	row = y - 2;
	if(col >= 0 && row >= 0 && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);

	//插入左下方的有效走法
	col = x - 2;
	row = y + 1;
	if(col >= 0 && row < MAX_ROW && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);
	col = x - 1;
	row = y + 2;
	if(col >= 0 && row < MAX_ROW && IsValidMove(position,x,y,col,row))
		AddMove(x,y,col,row,depth);
}

static void Gen_RPawnMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	int chessID = position[y][x];

	row = y - 1;
	col = x;
	if(row > 0 && (! IsSameSide(chessID, position[row][col])))
		AddMove(x,y,col,row,depth);

	if(y < 5)
	{
		row = y;
		col = x + 1;	//右边
		if(col < MAX_COL && (! IsSameSide(chessID,position[row][col])))
			AddMove(x,y,col,row,depth);

		col = x - 1;	//左边
		if(col >= 0 && (! IsSameSide(chessID,position[row][col])))
			AddMove(x,y,col,row,depth);
	}
}

static void Gen_BPawnMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	int chessID = position[y][x];

	row = y + 1;
	col = x;
	if(row < MAX_ROW && (! IsSameSide(chessID,position[row][col])))
		AddMove(x,y,col,row,depth);

	if(y > 4)	//已过河
	{
		row = y;
		col = x + 1;
		if(col < MAX_COL && (! IsSameSide(chessID,position[row][col])))
			AddMove(x,y,col,row,depth);

		col = x - 1;
		if(col >= 0 && (! IsSameSide(chessID,position[row][col])))
			AddMove(x,y,col,row,depth);
	}
}

static void Gen_CarMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	int chessID = position[y][x];

	//插入右边的有效走法
	col = x + 1;
	row = y;
	while(col < MAX_COL)
	{
		if(NO_CHESS == position[row][col])
			AddMove(x,y,col,row,depth);
		else
		{
			if(! IsSameSide(chessID,position[row][col]))
				AddMove(x,y,col,row,depth);
			break;
		}
		col++;
	}

	//插入左边的有效走法
	col = x -1;
	row = y;
	while(col >= 0)
	{
		if(NO_CHESS == position[row][col])
			AddMove(x,y,col,row,depth);
		else
		{
			if(! IsSameSide(chessID,position[row][col]))
				AddMove(x,y,col,row,depth);
			break;
		}
		col--;
	}

	//插入下方的有效走法
	col = x;
	row = y + 1;
	while(row < MAX_ROW)
	{
		if(NO_CHESS == position[row][col])
			AddMove(x,y,col,row,depth);
		else
		{
			if(! IsSameSide(chessID,position[row][col]))
				AddMove(x,y,col,row,depth);
			break;
		}
		row++;
	}

	//插入上方的有效走法
	col = x;
	row = y - 1;
	while(row >= 0)
	{
		if(NO_CHESS == position[row][col])
			AddMove(x,y,col,row,depth);
		else
		{
			if(! IsSameSide(chessID,position[row][col]))
				AddMove(x,y,col,row,depth);
			break;
		}
		row--;
	}
}

static void Gen_CanonMove(BYTE position[MAX_ROW][MAX_COL], int x, int y, int depth)
{
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","%s",__FUNCTION__);
	int row, col;
	int chessID = position[y][x];
	BOOL flag;

	//插入右边的有效走法
	col = x + 1;
	row = y;
	flag = FALSE;
	while(col < MAX_COL)
	{
		if(NO_CHESS == position[row][col])
		{
			if(flag == FALSE)
				AddMove(x,y,col,row,depth);
		}else
		{
			if(flag == FALSE)
				flag = TRUE;
			else	//中间有棋子
			{
				if(!IsSameSide(chessID,position[row][col]))
					AddMove(x,y,col,row,depth);
				break;
			}
		}
		col++;
	}

	//插入左边的有效走法
	col = x - 1;
	row = y;
	flag = FALSE;
	while(col >= 0)
	{
		if(NO_CHESS == position[row][col])
		{
			if(flag == FALSE)
				AddMove(x,y,col,row,depth);
		}else
		{
			if(flag == FALSE)
				flag = TRUE;
			else
			{
				if(!IsSameSide(chessID,position[row][col]))
					AddMove(x,y,col,row,depth);
				break;
			}
		}
		col--;
	}

	//插入下方的有效走法
	col = x;
	row = y + 1;
	flag = FALSE;
	while(row < MAX_ROW)
	{
		if(NO_CHESS == position[row][col])
		{
			if(flag == FALSE)
				AddMove(x,y,col,row,depth);
		}else
		{
			if(flag == FALSE)
				flag = TRUE;
			else
			{
				if(!IsSameSide(chessID,position[row][col]))
					AddMove(x,y,col,row,depth);
				break;
			}
		}
		row++;
	}

	//插入上方的有效走法
	col = x;
	row = y - 1;
	flag = FALSE;
	while(row >= 0)
	{
		if(NO_CHESS == position[row][col])
		{
			if(flag == FALSE)
				AddMove(x,y,col,row,depth);
		}else
		{
			if(flag == FALSE)
				flag = TRUE;
			else
			{
				if(!IsSameSide(chessID,position[row][col]))
					AddMove(x,y,col,row,depth);
				break;
			}
		}
		row--;
	}
}

#if 0
BOOL WillKillKing(BYTE position[MAX_ROW][MAX_COL], BYTE actionX, BYTE actionY, ST_Position *BKingPos, ST_Position *RKingPos)
{
	if(position[actionY][actionX] == NO_CHESS)
		FALSE;

	BYTE chessID = position[actionY][actionX];
	if(IsRed(chessID))
		continue;
	if(side && IsBlack(chessID))
		continue;

	switch(chessID)
	{
	/*
	case R_KING:
	case B_KING:
		Gen_KingMove(position, col, actionY, 0);
		break;
	*/
	case R_BISHOP:
		Gen_RBishopMove(position, col, actionY, depth);
		break;
	case B_BISHOP:
		Gen_BBishopMove(position, col, actionY, depth);
		break;
	case R_ELEPHANT:
	case B_ELEPHANT:
		Gen_ElephantMove(position, col, actionY, depth);
		break;
	case R_HORSE:
	case B_HORSE:
		Gen_HorseMove(position, col, actionY, depth);
		break;
	case R_CAR:
	case B_CAR:
		Gen_CarMove(position, col, actionY, depth);
		break;
	case R_PAWN:
		Gen_RPawnMove(position, col, row, depth);
		break;
	case B_PAWN:
		Gen_BPawnMove(position, col, row, depth);
		break;
	case B_CANON:
	case R_CANON:
		Gen_CanonMove(position, col, row, depth);
		break;
	default:
		break;
	}
}
#endif

/**
 * generator all move
 */
int CreateAllPossibleMove(BYTE position[MAX_ROW][MAX_COL], int depth, int side)
{
	int row, col;
	BYTE chessID;

	move_count = 0;

	for(row = 0; row < MAX_ROW; row++)
	{
		for(col = 0; col < MAX_COL; col++)
		{
			if(position[row][col] == NO_CHESS)
				continue;

			chessID = position[row][col];
			if(!side && IsRed(chessID))
				continue;
			if(side && IsBlack(chessID))
				continue;

			switch(chessID)
			{
			case R_KING:
			case B_KING:
				Gen_KingMove(position, col, row, depth);
				break;
			case R_BISHOP:
				Gen_RBishopMove(position, col, row, depth);
				break;
			case B_BISHOP:
				Gen_BBishopMove(position, col, row, depth);
				break;
			case R_ELEPHANT:
			case B_ELEPHANT:
				Gen_ElephantMove(position, col, row, depth);
				break;
			case R_HORSE:
			case B_HORSE:
				Gen_HorseMove(position, col, row, depth);
				break;
			case R_CAR:
			case B_CAR:
				Gen_CarMove(position, col, row, depth);
				break;
			case R_PAWN:
				Gen_RPawnMove(position, col, row, depth);
				break;
			case B_PAWN:
				Gen_BPawnMove(position, col, row, depth);
				break;
			case B_CANON:
			case R_CANON:
				Gen_CanonMove(position, col, row, depth);
				break;
			default:
				break;
			}
		}
	}
	return move_count;
}
