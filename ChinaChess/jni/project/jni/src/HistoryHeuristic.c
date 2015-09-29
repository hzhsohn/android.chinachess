
#include "../include/HistoryHeuristic.h"

static int g_HistoryTable[90][90];
static ST_Move g_TargetBuff[100];

#define MAX_HISTORY_TABLE_BYTE	32400	//90 * 90 * 4

/**
 * 清空历史记录表
 */
void ResetHistoryTable(void)
{
	memset(g_HistoryTable, 10, MAX_HISTORY_TABLE_BYTE);
}

/**
 * 得到给定走法的历史得分
 */
int GetHistoryScore(ST_Move *move)
{
	int nFrom, nTo;
	nFrom = move->from.y * 9 + move->from.x;
	nTo = move->to.y * 9 + move->to.x;

	return g_HistoryTable[nFrom][nTo];
}

/**
 * 将最佳走法汇入历史记录
 */
void EnterHistoryScore(ST_Move *move,int depth)
{
	int nFrom, nTo;
	nFrom = move->from.y * 9 + move->from.x;
	nTo = move->to.y * 9 + move->to.x;

	g_HistoryTable[nFrom][nTo] += 2<<depth;
}

/**
 * 对走法队列从小到大排序
 */
static void Merge(ST_Move *source, ST_Move *target, int l,int m, int r)
{
	int i = l;
	int j = m + 1;
	int k = l;
	int q;

	while((i <= m) && (j <= r))
	{
		if (source[i].score <= source[j].score)
			target[k++] = source[i++];
		else
			target[k++] = source[j++];
	}

	if(i > m)
	{
		for (q = j; q <= r; q++)
			target[k++] = source[q];
	}else
	{
		for(q = i; q <= m; q++)
			target[k++] = source[q];
	}
}

/**
 * 对走法队列从大到小排序
 */
static void Merge_A(ST_Move *source, ST_Move *target, int l,int m, int r)
{
	int i = l;
	int j = m + 1;
	int k = l;
	int q;

	while((i <= m) && (j <= r))
	{
		if (source[i].score >= source[j].score)
			target[k++] = source[i++];
		else
			target[k++] = source[j++];
	}

	if(i > m)
	{
		for (q = j; q <= r; q++)
			target[k++] = source[q];
	}else
	{
		for(q = i; q <= m; q++)
			target[k++] = source[q];
	}
}

/**
 * 合并大小为S的相邻的子数组
 */
static void MergePass(ST_Move *source, ST_Move *target, const int s, const int n, const BOOL direction)
{
	int i = 0;
	int j = 0;

	while(i <= n - 2 * s)
	{
		if (direction)
			Merge(source, target, i, i + s - 1, i + 2 * s - 1);
		else
			Merge_A(source, target, i, i + s - 1, i + 2 * s - 1);
		i = i + 2 * s;
	}

	if (i + s < n)
	{
		if (direction)
			Merge(source, target, i, i + s - 1, n - 1);
		else
			Merge_A(source, target, i, i + s - 1, n - 1);
	}else
	{
		for (j = i; j <= n - 1; j++)
			target[j] = source[j];
	}
}

/**
 * 对走法队列归并排序
 * source	待排序数组
 * n		数组的元素个数
 * direction 从大到小或从小到大
 */
void MergeSort(ST_Move *source, int n, BOOL direction)
{
	int s = 1;
	while(s < n)
	{
		MergePass(source, g_TargetBuff, s, n, direction);
		s += s;
		MergePass(g_TargetBuff, source, s, n, direction);
		s += s;
	}
}
