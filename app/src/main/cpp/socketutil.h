//
// Created by yen on 5/28/22.
//

#ifndef SOCKET_UTIL
#define SOCKET_UTIL
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "mysocket"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

void release();
int connectRemote(const char*,const int);
int sendData(const char*, const int);
#endif