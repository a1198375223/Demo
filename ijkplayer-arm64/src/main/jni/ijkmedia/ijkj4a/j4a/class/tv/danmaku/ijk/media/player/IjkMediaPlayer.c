/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * https://github.com/Bilibili/jni4android
 * This file is automatically generated by jni4android, do not modify.
 */

#include "IjkMediaPlayer.h"

typedef struct J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer {
    jclass id;

    jfieldID field_mNativeMediaPlayer;
    jfieldID field_mNativeMediaDataSource;
    jfieldID field_mNativeAndroidIO;
    jmethodID method_postEventFromNative;
    jmethodID method_onSelectCodec;
    jmethodID method_onNativeInvoke;
} J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer;
static J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer;

jlong J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaPlayer__get(JNIEnv *env, jobject thiz)
{
    return (*env)->GetLongField(env, thiz, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaPlayer);
}

jlong J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaPlayer__get__catchAll(JNIEnv *env, jobject thiz)
{
    jlong ret_value = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaPlayer__get(env, thiz);
    if (J4A_ExceptionCheck__catchAll(env)) {
        return 0;
    }

    return ret_value;
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaPlayer__set(JNIEnv *env, jobject thiz, jlong value)
{
    (*env)->SetLongField(env, thiz, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaPlayer, value);
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaPlayer__set__catchAll(JNIEnv *env, jobject thiz, jlong value)
{
    J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaPlayer__set(env, thiz, value);
    J4A_ExceptionCheck__catchAll(env);
}

jlong J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaDataSource__get(JNIEnv *env, jobject thiz)
{
    return (*env)->GetLongField(env, thiz, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaDataSource);
}

jlong J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaDataSource__get__catchAll(JNIEnv *env, jobject thiz)
{
    jlong ret_value = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaDataSource__get(env, thiz);
    if (J4A_ExceptionCheck__catchAll(env)) {
        return 0;
    }

    return ret_value;
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaDataSource__set(JNIEnv *env, jobject thiz, jlong value)
{
    (*env)->SetLongField(env, thiz, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaDataSource, value);
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaDataSource__set__catchAll(JNIEnv *env, jobject thiz, jlong value)
{
    J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeMediaDataSource__set(env, thiz, value);
    J4A_ExceptionCheck__catchAll(env);
}

jlong J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeAndroidIO__get(JNIEnv *env, jobject thiz)
{
    return (*env)->GetLongField(env, thiz, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeAndroidIO);
}

jlong J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeAndroidIO__get__catchAll(JNIEnv *env, jobject thiz)
{
    jlong ret_value = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeAndroidIO__get(env, thiz);
    if (J4A_ExceptionCheck__catchAll(env)) {
        return 0;
    }

    return ret_value;
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeAndroidIO__set(JNIEnv *env, jobject thiz, jlong value)
{
    (*env)->SetLongField(env, thiz, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeAndroidIO, value);
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeAndroidIO__set__catchAll(JNIEnv *env, jobject thiz, jlong value)
{
    J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__mNativeAndroidIO__set(env, thiz, value);
    J4A_ExceptionCheck__catchAll(env);
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__postEventFromNative(JNIEnv *env, jobject weakThiz, jint what, jint arg1, jint arg2, jobject obj)
{
    (*env)->CallStaticVoidMethod(env, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_postEventFromNative, weakThiz, what, arg1, arg2, obj);
}

void J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__postEventFromNative__catchAll(JNIEnv *env, jobject weakThiz, jint what, jint arg1, jint arg2, jobject obj)
{
    J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__postEventFromNative(env, weakThiz, what, arg1, arg2, obj);
    J4A_ExceptionCheck__catchAll(env);
}

jstring J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec(JNIEnv *env, jobject weakThiz, jstring mimeType, jint profile, jint level)
{
    return (*env)->CallStaticObjectMethod(env, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_onSelectCodec, weakThiz, mimeType, profile, level);
}

jstring J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__catchAll(JNIEnv *env, jobject weakThiz, jstring mimeType, jint profile, jint level)
{
    jstring ret_object = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec(env, weakThiz, mimeType, profile, level);
    if (J4A_ExceptionCheck__catchAll(env) || !ret_object) {
        return NULL;
    }

    return ret_object;
}

jstring J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__asGlobalRef__catchAll(JNIEnv *env, jobject weakThiz, jstring mimeType, jint profile, jint level)
{
    jstring ret_object   = NULL;
    jstring local_object = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__catchAll(env, weakThiz, mimeType, profile, level);
    if (J4A_ExceptionCheck__catchAll(env) || !local_object) {
        ret_object = NULL;
        goto fail;
    }

    ret_object = J4A_NewGlobalRef__catchAll(env, local_object);
    if (!ret_object) {
        ret_object = NULL;
        goto fail;
    }

fail:
    J4A_DeleteLocalRef__p(env, &local_object);
    return ret_object;
}

const char *J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__asCBuffer(JNIEnv *env, jobject weakThiz, jstring mimeType, jint profile, jint level, char *out_buf, int out_len)
{
    const char *ret_value = NULL;
    const char *c_str     = NULL;
    jstring local_string = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec(env, weakThiz, mimeType, profile, level);
    if (J4A_ExceptionCheck__throwAny(env) || !local_string) {
        goto fail;
    }

    c_str = (*env)->GetStringUTFChars(env, local_string, NULL );
    if (J4A_ExceptionCheck__throwAny(env) || !c_str) {
        goto fail;
    }

    strlcpy(out_buf, c_str, out_len);
    ret_value = out_buf;

fail:
    J4A_ReleaseStringUTFChars__p(env, local_string, &c_str);
    J4A_DeleteLocalRef__p(env, &local_string);
    return ret_value;
}

const char *J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__asCBuffer__catchAll(JNIEnv *env, jobject weakThiz, jstring mimeType, jint profile, jint level, char *out_buf, int out_len)
{
    const char *ret_value = NULL;
    const char *c_str     = NULL;
    jstring local_string = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__catchAll(env, weakThiz, mimeType, profile, level);
    if (J4A_ExceptionCheck__catchAll(env) || !local_string) {
        goto fail;
    }

    c_str = (*env)->GetStringUTFChars(env, local_string, NULL );
    if (J4A_ExceptionCheck__catchAll(env) || !c_str) {
        goto fail;
    }

    strlcpy(out_buf, c_str, out_len);
    ret_value = out_buf;

fail:
    J4A_ReleaseStringUTFChars__p(env, local_string, &c_str);
    J4A_DeleteLocalRef__p(env, &local_string);
    return ret_value;
}

jstring J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString(JNIEnv *env, jobject weakThiz, const char *mimeType_cstr__, jint profile, jint level)
{
    jstring ret_object = NULL;
    jstring mimeType = NULL;

    mimeType = (*env)->NewStringUTF(env, mimeType_cstr__);
    if (J4A_ExceptionCheck__throwAny(env) || !mimeType)
        goto fail;

    ret_object = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec(env, weakThiz, mimeType, profile, level);
    if (J4A_ExceptionCheck__throwAny(env) || !ret_object) {
        ret_object = NULL;
        goto fail;
    }

fail:
    J4A_DeleteLocalRef__p(env, &mimeType);
    return ret_object;
}

jstring J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString__catchAll(JNIEnv *env, jobject weakThiz, const char *mimeType_cstr__, jint profile, jint level)
{
    jstring ret_object = NULL;
    jstring mimeType = NULL;

    mimeType = (*env)->NewStringUTF(env, mimeType_cstr__);
    if (J4A_ExceptionCheck__catchAll(env) || !mimeType)
        goto fail;

    ret_object = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__catchAll(env, weakThiz, mimeType, profile, level);
    if (J4A_ExceptionCheck__catchAll(env) || !ret_object) {
        ret_object = NULL;
        goto fail;
    }

fail:
    J4A_DeleteLocalRef__p(env, &mimeType);
    return ret_object;
}

jstring J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString__asGlobalRef__catchAll(JNIEnv *env, jobject weakThiz, const char *mimeType_cstr__, jint profile, jint level)
{
    jstring ret_object   = NULL;
    jstring local_object = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString__catchAll(env, weakThiz, mimeType_cstr__, profile, level);
    if (J4A_ExceptionCheck__catchAll(env) || !local_object) {
        ret_object = NULL;
        goto fail;
    }

    ret_object = J4A_NewGlobalRef__catchAll(env, local_object);
    if (!ret_object) {
        ret_object = NULL;
        goto fail;
    }

fail:
    J4A_DeleteLocalRef__p(env, &local_object);
    return ret_object;
}

const char *J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString__asCBuffer(JNIEnv *env, jobject weakThiz, const char *mimeType_cstr__, jint profile, jint level, char *out_buf, int out_len)
{
    const char *ret_value = NULL;
    const char *c_str     = NULL;
    jstring local_string = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString(env, weakThiz, mimeType_cstr__, profile, level);
    if (J4A_ExceptionCheck__throwAny(env) || !local_string) {
        goto fail;
    }

    c_str = (*env)->GetStringUTFChars(env, local_string, NULL );
    if (J4A_ExceptionCheck__throwAny(env) || !c_str) {
        goto fail;
    }

    strlcpy(out_buf, c_str, out_len);
    ret_value = out_buf;

fail:
    J4A_ReleaseStringUTFChars__p(env, local_string, &c_str);
    J4A_DeleteLocalRef__p(env, &local_string);
    return ret_value;
}

const char *J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString__asCBuffer__catchAll(JNIEnv *env, jobject weakThiz, const char *mimeType_cstr__, jint profile, jint level, char *out_buf, int out_len)
{
    const char *ret_value = NULL;
    const char *c_str     = NULL;
    jstring local_string = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onSelectCodec__withCString__catchAll(env, weakThiz, mimeType_cstr__, profile, level);
    if (J4A_ExceptionCheck__catchAll(env) || !local_string) {
        goto fail;
    }

    c_str = (*env)->GetStringUTFChars(env, local_string, NULL );
    if (J4A_ExceptionCheck__catchAll(env) || !c_str) {
        goto fail;
    }

    strlcpy(out_buf, c_str, out_len);
    ret_value = out_buf;

fail:
    J4A_ReleaseStringUTFChars__p(env, local_string, &c_str);
    J4A_DeleteLocalRef__p(env, &local_string);
    return ret_value;
}

jboolean J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onNativeInvoke(JNIEnv *env, jobject weakThiz, jint what, jobject args)
{
    return (*env)->CallStaticBooleanMethod(env, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id, class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_onNativeInvoke, weakThiz, what, args);
}

jboolean J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onNativeInvoke__catchAll(JNIEnv *env, jobject weakThiz, jint what, jobject args)
{
    jboolean ret_value = J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer__onNativeInvoke(env, weakThiz, what, args);
    if (J4A_ExceptionCheck__catchAll(env)) {
        return false;
    }

    return ret_value;
}

int J4A_loadClass__J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer(JNIEnv *env)
{
    int         ret                   = -1;
    const char *J4A_UNUSED(name)      = NULL;
    const char *J4A_UNUSED(sign)      = NULL;
    jclass      J4A_UNUSED(class_id)  = NULL;
    int         J4A_UNUSED(api_level) = 0;

    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id != NULL)
        return 0;

    sign = "tv/danmaku/ijk/media/player/IjkMediaPlayer";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id = J4A_FindClass__asGlobalRef__catchAll(env, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id == NULL)
        goto fail;

    class_id = class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id;
    name     = "mNativeMediaPlayer";
    sign     = "J";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaPlayer = J4A_GetFieldID__catchAll(env, class_id, name, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaPlayer == NULL)
        goto fail;

    class_id = class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id;
    name     = "mNativeMediaDataSource";
    sign     = "J";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaDataSource = J4A_GetFieldID__catchAll(env, class_id, name, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeMediaDataSource == NULL)
        goto fail;

    class_id = class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id;
    name     = "mNativeAndroidIO";
    sign     = "J";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeAndroidIO = J4A_GetFieldID__catchAll(env, class_id, name, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.field_mNativeAndroidIO == NULL)
        goto fail;

    class_id = class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id;
    name     = "postEventFromNative";
    sign     = "(Ljava/lang/Object;IIILjava/lang/Object;)V";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_postEventFromNative = J4A_GetStaticMethodID__catchAll(env, class_id, name, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_postEventFromNative == NULL)
        goto fail;

    class_id = class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id;
    name     = "onSelectCodec";
    sign     = "(Ljava/lang/Object;Ljava/lang/String;II)Ljava/lang/String;";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_onSelectCodec = J4A_GetStaticMethodID__catchAll(env, class_id, name, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_onSelectCodec == NULL)
        goto fail;

    class_id = class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.id;
    name     = "onNativeInvoke";
    sign     = "(Ljava/lang/Object;ILandroid/os/Bundle;)Z";
    class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_onNativeInvoke = J4A_GetStaticMethodID__catchAll(env, class_id, name, sign);
    if (class_J4AC_tv_danmaku_ijk_media_player_IjkMediaPlayer.method_onNativeInvoke == NULL)
        goto fail;

    J4A_ALOGD("J4ALoader: OK: '%s' loaded\n", "tv.danmaku.ijk.media.player.IjkMediaPlayer");
    ret = 0;
fail:
    return ret;
}
