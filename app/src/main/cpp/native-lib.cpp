#include <jni.h>
#include <string>

#include "socketutil.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myphone_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT int JNICALL
Java_com_example_myphone_util_SocketConnection_connectToPC(JNIEnv *env, jobject thiz, jstring ip,
                                                           jint port) {
    const char * _ip = env->GetStringUTFChars(ip, 0);
    LOGI("connect to port %d", port);
    return connectRemote(_ip, port);
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myphone_util_SocketConnection_sendStringData(JNIEnv *env, jobject thiz, jstring data,
                                                              jint len) {
    const char * _data = env->GetStringUTFChars(data, 0);
    return sendData(_data, len);
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myphone_util_SocketConnection_sendByteData(JNIEnv *env, jobject thiz,
                                                            jbyteArray data, jint len) {
    const char* _data = (char*) env->GetByteArrayElements(data, 0);
    return sendData(_data, len);
}