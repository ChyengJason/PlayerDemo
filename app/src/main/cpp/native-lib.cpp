#include <jni.h>
#include <string>
#include "android_log.h"

extern "C" {
#include "libavcodec/avcodec.h" // 编码
#include "libavformat/avformat.h" // 封装格式
#include "libswscale/swscale.h" // 变换信息
#include <unistd.h>
#include <android/native_window_jni.h>
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_jscheng_playerdemo_FFmpeg_stringFromJNI(JNIEnv *env, jobject instance) {
    std::string hello = avcodec_configuration();
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_jscheng_playerdemo_FFmpeg_renderVideo(JNIEnv *env, jobject instance, jstring path_,
                                               jobject surface) {
    const char *path = env->GetStringUTFChars(path_, 0);

    // 注册FFmpeg所有组件
    av_register_all();
    // 获取上下文
    AVFormatContext *avFormatContext = avformat_alloc_context();
    // 打开视频地址
    int error;
    char buf[] = "";
    if ((error = avformat_open_input(&avFormatContext, path, NULL, NULL)) < 0) {
        av_strerror(error, buf, 1024);
        LOGE("Couldn't open file %s: %d(%s)", path, error, buf);
        LOGE("打开视频失败");
        return;
    }
    // 获取视频内容
    if (avformat_find_stream_info(avFormatContext, NULL) < 0) {
        LOGE(" 获取内容失败 ");
        return;
    }
    // 获取视频流
    int video_index = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_index = i;
        }
    }
    LOGE("视频流: %d", video_index);
    // 获取解码器上下文
    AVCodecContext *avCodecContext = avFormatContext->streams[video_index]->codec;
    // 获取解码器
    AVCodec *avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    // 打开解码器
    if (avcodec_open2(avCodecContext, avCodec, NULL) < 0) {
        LOGE("打开解码器失败");
        return;
    }

    // 申请AVPacket
    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    av_init_packet(packet);
    // 申请AVFrame
    AVFrame *frame = av_frame_alloc();//分配一个AVFrame结构体,AVFrame结构体一般用于存储原始数据，指向解码后的原始帧
    AVFrame *rgb_frame = av_frame_alloc();//分配一个AVFrame结构体，指向存放转换成rgb后的帧

    // 缓存区
    uint8_t *out_buffer= (uint8_t *)av_malloc(avpicture_get_size(AV_PIX_FMT_RGBA,
                               avCodecContext->width,avCodecContext->height));

    LOGE("width: %d, height: %d", avCodecContext->width,avCodecContext->height);
    // 关联缓存区
    avpicture_fill((AVPicture*)rgb_frame, out_buffer, AV_PIX_FMT_RGBA, avCodecContext->width, avCodecContext->height);

    // 视频图像的转换, 比如格式转换
    SwsContext* swsContext = sws_getContext(avCodecContext->width, avCodecContext->height, avCodecContext->pix_fmt,
                                            avCodecContext->width, avCodecContext->height, AV_PIX_FMT_RGBA,
                                            SWS_BICUBIC, NULL, NULL, NULL);
    // 获取NativeWindow
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    if(nativeWindow == 0){
        LOGE("获取NativeWindow失败");
        return;
    }

    //视频缓冲区
    ANativeWindow_Buffer native_outBuffer;

    LOGE("开始解码");
    int frameCount = 0;
    while(av_read_frame(avFormatContext, packet) >= 0) {
        LOGE("解码: %d ", packet->stream_index);
        if (packet->stream_index == video_index) {
            // 解码
            avcodec_decode_video2(avCodecContext, frame, &frameCount, packet);
            // 配置NativeWindow
            ANativeWindow_setBuffersGeometry(nativeWindow, avCodecContext->width, avCodecContext->height, WINDOW_FORMAT_RGBA_8888);
            // 上锁
            ANativeWindow_lock(nativeWindow, &native_outBuffer, NULL);
            // 转换成RGBA格式
            sws_scale(swsContext, (const uint8_t *const *)frame->data, frame->linesize, 0,
                      frame->height, rgb_frame->data, rgb_frame->linesize);

            uint8_t *dst= (uint8_t *) native_outBuffer.bits;
            int destStride=native_outBuffer.stride*4;
            uint8_t * src=  rgb_frame->data[0];
            int srcStride = rgb_frame->linesize[0];

            for (int i = 0; i < avCodecContext->height; ++i) {
                // 将rgb_frame中每一行的数据复制给nativewindow
                memcpy(dst + i * destStride,  src + i * srcStride, srcStride);
            }
            ANativeWindow_unlockAndPost(nativeWindow);
            usleep(1000 * 16);
        }
        av_free_packet(packet);
    }
    LOGE("结束解码");

    //释放
    ANativeWindow_release(nativeWindow);
    av_frame_free(&frame);
    av_frame_free(&rgb_frame);
    avcodec_close(avCodecContext);
    avformat_free_context(avFormatContext);

    env->ReleaseStringUTFChars(path_, path);
}