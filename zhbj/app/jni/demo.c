#include "jni.h"

JNIEXPORT jstring JNICALL Java_com_stav_zhbj_test_Jni_getString
  (JNIEnv *env, jobject jobj){
  return (*env)->NewStringUTF(env, "Hello Jni!!!");
  }