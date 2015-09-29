# Test that LOCAL_CFLAGS works for both C and C++ sources
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES := $(JNI_H_INCLUDE)

LOCAL_MODULE    := ChinaChess

LOCAL_SRC_FILES := Board.c \
                   MoveGenerator.c \
                   chinachess_mid_NativeInterface.c \
                   Eveluation.c \
                   SearchEngineInterface.c \
                   PVSEngine.c \
                   NegaMaxEngine.c \
                   FAlphaBetaEngine.c \
                   AlphaBetaEngine.c \
                   HistoryHeuristic.c \
                   AlphaBetaAndHH.c \

LOCAL_CFLAGS    := -DBANANA=100
LOCAL_LDLIBS :=  -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)
