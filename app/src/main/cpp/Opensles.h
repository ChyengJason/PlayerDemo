//
// Created by chengjunsen on 2018/10/9.
//
#ifndef PLAYERDEMO_OPENSLES_H
#define PLAYERDEMO_OPENSLES_H
#include <string>

extern "C" {
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
};

typedef void PcmDataFunc(void **, size_t *);

class Opensles {
public:
    Opensles();
    ~Opensles();

    bool createEngine(); // 创建引擎
    bool createMixVolume(); // 创建混音器
    void createPlayer(size_t samplerate, size_t channelCount); // 创建播放器
    void release(); // 释放
    void setPcmDataInterface(PcmDataFunc func);
private:
    static void getDataQueueCallBack(SLAndroidSimpleBufferQueueItf bufferQueueInterface, void* context);
    static PcmDataFunc *getPcmDataInterface; // 获取pcm数据的接口

    SLObjectItf mEngineObject; // 引擎接口对象
    SLEngineItf mEngineEngine; // 具体引擎对象实例
    SLObjectItf mOutputMixObject; // 混音器接口对象
    SLEnvironmentalReverbItf  mOutputMixEnvirRevarb; // 具体混音器对象实例
    SLEnvironmentalReverbSettings mOutputMixEnvirReverbSettings;
    SLObjectItf mAudioPlayer; // 播放器接口对象
    SLPlayItf mAudioPlayerInterface; // 播放器接口
    SLAndroidSimpleBufferQueueItf  mBufferQueueInterface;// 缓冲区队列接口
};


#endif //PLAYERDEMO_OPENSLES_H
