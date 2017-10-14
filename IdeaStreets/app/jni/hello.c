#include "com_example_hellondk_JniTest.h"

JNIEXPORT jstring JNICALL Java_com_stav_ideastreet_test_JniTest_getString
  (JNIEnv *env, jobject jobj){
  return (*env)->NewStringUTF(env, "Hello Jni!!!");
  }