
#ifndef __CHESS_H_
#define __CHESS_H_

#include "define.h"
#include "chinachess_mid_NativeInterface.h"

//black chess
#define NO_CHESS	0
#define B_KING		1
#define B_CAR		2
#define	B_HORSE		3
#define	B_CANON		4
#define	B_BISHOP	5
#define	B_ELEPHANT	6
#define	B_PAWN		7
#define	B_BEGIN		B_KING
#define	B_END		B_PAWN
//red chess
#define	R_KING		8
#define	R_CAR		9
#define	R_HORSE		10
#define	R_CANON		11
#define	R_BISHOP	12
#define	R_ELEPHANT	13
#define	R_PAWN		14
#define	R_BEGIN		R_KING
#define	R_END		R_PAWN

#define MAX_ROW		10
#define MAX_COL		9

#define IsBlack(x)		(((x) >= B_BEGIN) && ((x) <= B_END))
#define IsRed(x)		(((x) >= R_BEGIN) && ((x) <= R_END))
#define IsSameSide(x,y)	((IsBlack(x) && IsBlack(y))||(IsRed(x) && IsRed(y)))

typedef struct {
	BYTE x;
	BYTE y;
}ST_Position;

typedef struct {
	short 		chessID;	//chess id
	ST_Position	from;		//from position
	ST_Position	to;			//to position
	int			score;		//value
}ST_Move;

#endif
