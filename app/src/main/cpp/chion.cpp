#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <assert.h>
#include <string.h>
#include <sys/ptrace.h>
#include <string>
#include "base64.h"
#include "md5.h"
#include <android/log.h>
#include "aes.h"

#define   LOG_TAG    "CHIONTAG"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// 获取数组的大小
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
// 指定要注册的类，对应完整的java类名
#define JNIREG_CLASS "com/chion/chionappv1/chionDIY"

char* jstringToChar(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

jstring getiv(JNIEnv* env) {
    int n = 0;
    char s[23];//"N MTIzNDU2Nzg5MGFiY2RlZg"; MTIzNDU2Nzg5MGFiY2RlZg==

    s[n++] = 'N';
    s[n++] = 'M';
    s[n++] = 'T';
    s[n++] = 'I';
    s[n++] = 'z';
    s[n++] = 'N';
    s[n++] = 'D';
    s[n++] = 'U';
    s[n++] = '2';
    s[n++] = 'N';
    s[n++] = 'z';
    s[n++] = 'g';
    s[n++] = '5';
    s[n++] = 'M';
    s[n++] = 'G';
    s[n++] = 'F';
    s[n++] = 'i';
    s[n++] = 'Y';
    s[n++] = '2';
    s[n++] = 'R';
    s[n++] = 'l';
    s[n++] = 'Z';
    s[n++] = 'g';
    char *encode_str = s + 1;
    return env -> NewStringUTF(base64_decode(encode_str).c_str());
}

jstring JNICALL encode(JNIEnv *env, jobject instance, jstring key, jstring str_) {

    char *AES_IV = jstringToChar(env,getiv(env));
    char *AES_KEY = jstringToChar(env,key);
    const char *in = env->GetStringUTFChars(str_, JNI_FALSE);
    string aesResult = encryptByAES(in, AES_KEY,AES_IV,1);
    jstring aesResultjs = env -> NewStringUTF(aesResult.c_str());
    env->ReleaseStringUTFChars( str_, in);
    char *aes_data = jstringToChar(env,aesResultjs);
    //todo 还没有进行向量的混淆
    MD5 md5 = MD5(aes_data);
    std::string md5Result = md5.hexdigest();
//    free(AES_IV);
//    free(AES_KEY);
//    free(aesResultjs);
//    free(aes_data);
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());
}

// Java和JNI函数的绑定表
static JNINativeMethod method_table[] = {
        {"encode", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) encode},
};

// 注册native方法到java中
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass( className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives( clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

int register_ndk_load(JNIEnv *env) {
    // 调用注册方法
    return registerNativeMethods(env, JNIREG_CLASS,
                                 method_table, NELEM(method_table));
}

void CheckPort23946ByTcp()
{
    FILE* pfile=NULL;
    char buf[0x1000]={0};
    // 执行命令
    char* strCatTcp= "cat /proc/net/tcp |grep :5D8A";
    //char* strNetstat="netstat |grep :23946";
    pfile=popen(strCatTcp,"r");
    if(NULL==pfile)
    {
        return;
    }
    // 获取结果
    while(fgets(buf,sizeof(buf),pfile))
    {
        // 执行到这里，判定为调试状态
        return;
    }//while
    pclose(pfile);
}

void antidebug(JNIEnv *env){
    CheckPort23946ByTcp();
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {

    ptrace(PTRACE_TRACEME, 0, 0, 0);//反调试
//这是一种比较简单的防止被调试的方案
// 有更复杂更高明的方案，比如：不用这个ptrace而是每次执行加密解密签先去判断是否被trace,目前的版本不做更多的负载方案，您想做可以fork之后，自己去做

    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }

    register_ndk_load(env);

// 返回jni的版本
    return JNI_VERSION_1_6;
}