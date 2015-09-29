
#ifndef __HISTORY_HEURISTIC_H_
#define __HISTORY_HEURISTIC_H_

#include "chess.h"

void ResetHistoryTable();
int GetHistoryScore(ST_Move *move);
void EnterHistoryScore(ST_Move *move,int depth);
void MergeSort(ST_Move *source, int n, BOOL direction);

#endif
