
#include <jni.h>
#include <android/log.h>
#include "../include/chinachess_mid_NativeInterface.h"
#include "../include/Board.h"

static jint result;

#if 0
static jclass nativeInterface_jclass = NULL;
static jmethodID gameOver_methodID = NULL;
static jmethodID killKing_methodID = NULL;
#endif

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    initBoard
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_initBoard
  (JNIEnv *env, jobject obj)
{
	if(ResetBoardDefault() == TRUE)
		return chinachess_mid_NativeInterface_JNI_TRUE;
	else
		return chinachess_mid_NativeInterface_JNI_FALSE;
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    moveChess
 * Signature: (IIII)Z
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_moveChess
  (JNIEnv *env, jobject obj, jint fromX, jint fromY, jint toX, jint toY)
{
	if(moveChess(fromX,fromY,toX,toY) == FALSE)
	{
		__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,from Col=%d,from Row=%d,to Col=%d,to Row=%d-->move Fail\n",
					__FUNCTION__,fromX,fromY,toX,toY);
		return chinachess_mid_NativeInterface_JNI_FALSE;
	}

	__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s,from Col=%d,from Row=%d,to Col=%d,to Row=%d-->move Success\n",
						__FUNCTION__,fromX,fromY,toX,toY);
#if 0
	/* call java method */
	if(nativeInterface_jclass == NULL)
	{
		nativeInterface_jclass = (*env)->FindClass(env, "chinachess/mid/NativeInterface");
		if((*env)->ExceptionOccurred(env))
		{
			__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---jclass is null\n",__FUNCTION__);
			goto RETURN_VALUE;
		}
	}

	if(gameOver_methodID == NULL)
	{
		gameOver_methodID = (*env)->GetStaticMethodID(env, nativeInterface_jclass, "gameOver", "(I)V");
		if((*env)->ExceptionOccurred(env))
		{
			__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---get gameOver method fail\n",__FUNCTION__);
			goto RETURN_VALUE;
		}
	}
	if(killKing_methodID == NULL)
	{
		killKing_methodID = (*env)->GetStaticMethodID(env, nativeInterface_jclass, "killKing", "(I)V");
		if((*env)->ExceptionOccurred(env))
		{
			__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---get killKing method fail\n",__FUNCTION__);
			goto RETURN_VALUE;
		}
	}

	if(gameOver_methodID != NULL)
	{
		(*env)->CallStaticVoidMethod(env, nativeInterface_jclass, gameOver_methodID, 1);
		if((*env)->ExceptionOccurred(env))
		{
			__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---call gameOver method fail\n",__FUNCTION__);
			goto RETURN_VALUE;
		}
	}
	if(killKing_methodID != NULL)
	{
		(*env)->CallStaticVoidMethod(env, nativeInterface_jclass, killKing_methodID, 1);
		if((*env)->ExceptionOccurred(env))
		{
			__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---call killKing method fail\n",__FUNCTION__);
			goto RETURN_VALUE;
		}
	}
	/* end call java method */
#endif

RETURN_VALUE:
	return chinachess_mid_NativeInterface_JNI_TRUE;
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    getNextMove
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_getNextMove
	(JNIEnv *env, jobject obj, jintArray array)
{
	jint result = 0;
	jint *moves = NULL;

	moves = (*env)->GetIntArrayElements(env, array, 0);

	if(GetNextStep(moves) == TRUE)
	{
		__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---return true---fromx=%d,fromy=%d,tox=%d,toy=%d\n",
						__FUNCTION__,moves[0],moves[1],moves[2],moves[3]);
		result = chinachess_mid_NativeInterface_JNI_TRUE;
	}else
	{
		__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s---return false\n",__FUNCTION__);
		result = chinachess_mid_NativeInterface_JNI_FALSE;
	}

	if(moves != NULL)
		(*env)->ReleaseIntArrayElements(env, array, moves, 0);

	return result;
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    setChessID
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_setChessIdByPosition
	(JNIEnv *env, jobject jobj, jint x, jint y, jint chessID)
{
	setChessIDByPosition(x, y, (BYTE)chessID);
	return chinachess_mid_NativeInterface_JNI_TRUE;
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    setGameLevel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_chinachess_mid_NativeInterface_setGameLevel
	(JNIEnv *env, jobject obj, jint level)
{
	switch(level)
	{
	case 1:	//easy
		SetGameLevel(EASY);
		break;
	case 2:	//normal
		SetGameLevel(NORMAL);
		break;
	case 3:	//hard
		SetGameLevel(HARD);
		break;
	default:
		SetGameLevel(NORMAL);
		break;
	}
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    resetBoardDefault
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_resetBoardDefault
	(JNIEnv *env, jobject obj)
{
	return Java_chinachess_mid_NativeInterface_initBoard(env, obj);
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    resetBoard
 * Signature: ([[B)I
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_resetBoard
  (JNIEnv *env, jobject obj, jbyteArray array)
{
	//jint size = (*env)->GetArrayLength(env, board);
	//__android_log_print(ANDROID_LOG_DEBUG,"AI","function %s----int array size = %d\n",__FUNCTION__,size);

	jbyte *board = (*env)->GetByteArrayElements(env, array, 0);
	ResetBoard(board);
	(*env)->ReleaseByteArrayElements(env, array, board, 0);

	return chinachess_mid_NativeInterface_JNI_FALSE;
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    isGameOver
 * Signature: ()I
 *
 * return 0, game not over
 * return 1, winner color is red
 * return -1, winner color is black
 */
JNIEXPORT jint JNICALL Java_chinachess_mid_NativeInterface_getGameStatus
	(JNIEnv *env, jobject obj)
{
	return getGameStatus();
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    setGameStatus
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_chinachess_mid_NativeInterface_setGameStatus
  (JNIEnv *env, jobject jobj, jint status)
{
	setGameStatus(status);
}

/*
 * Class:     chinachess_mid_NativeInterface
 * Method:    getAuthorName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_chinachess_mid_NativeInterface_getAuthorName
  (JNIEnv *env, jobject obj)
{
	return (*env)->NewStringUTF(env,"chenxl Email:shift_sun_hot@163.com");
}


