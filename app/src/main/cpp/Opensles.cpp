//
// Created by chengjunsen on 2018/10/9.
//
#include "Opensles.h"
#include "android_log.h"

// static 类对象必须要在类外进行初始化
PcmDataFunc* Opensles::getPcmDataInterface = NULL;

Opensles::Opensles() {

}

Opensles::~Opensles() {

}

bool Opensles::createEngine() {
    SLresult result;
    result = slCreateEngine(&mEngineObject, 0, NULL, 0, NULL, NULL);// 创建引擎
    result = (*mEngineObject)->Realize(mEngineObject, SL_BOOLEAN_FALSE);// 实现 mEngineObject 接口对象
    result = (*mEngineObject)->GetInterface(mEngineObject, SL_IID_ENGINE, &mEngineEngine);//通过 mEngineObject 的GetInterface方法初始化 mEngineEngine
    return result == SL_RESULT_SUCCESS;
}

bool Opensles::createMixVolume() {
    SLresult result;
    result = (*mEngineEngine)->CreateOutputMix(mEngineEngine, &mOutputMixObject, 0, 0, 0); // 用引擎对象创建混音器对象
    result = (*mOutputMixObject)->Realize(mOutputMixObject, SL_BOOLEAN_FALSE); // 实现混音器接口对象
    result = (*mOutputMixObject)->GetInterface(mOutputMixObject, SL_IID_ENVIRONMENTALREVERB, &mOutputMixEnvirRevarb); // 初始化 mOutputMixEnvirRevarb
    mOutputMixEnvirReverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_DEFAULT;
    // 设置
    if (result == SL_RESULT_SUCCESS) {
        result = (*mOutputMixEnvirRevarb)->SetEnvironmentalReverbProperties(mOutputMixEnvirRevarb, &mOutputMixEnvirReverbSettings);
    }
    return result == SL_RESULT_SUCCESS;
}

void Opensles::createPlayer(size_t samplerate, size_t channelCount) {
    // 缓冲区队列
    SLDataLocator_AndroidSimpleBufferQueue bufferQueue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};

    // pcm 参数配置
    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM, // 播放pcm格式的数据
            channelCount, // 声道数量
            samplerate * 1000, // 采样率 SL_SAMPLINGRATE_44_1
            SL_PCMSAMPLEFORMAT_FIXED_16,// 采样位数 16位
            SL_PCMSAMPLEFORMAT_FIXED_16,// 包含位数 跟采样位数一致就行
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT, // 立体声（前左前右）
            SL_BYTEORDER_LITTLEENDIAN // 结束标志
    };

    SLDataSource slDataSource = {&bufferQueue, &pcm};

    SLDataLocator_OutputMix slDataLocatorOutputMix = {SL_DATALOCATOR_OUTPUTMIX, mOutputMixObject};

    SLDataSink slDataSink = {&slDataLocatorOutputMix, NULL};

    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND, SL_IID_VOLUME};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

    // 创建播放器
    (*mEngineEngine)->CreateAudioPlayer(mEngineEngine, &mAudioPlayer, &slDataSource, &slDataSink, 3, ids, req);
    // 初始化播放器
    (*mAudioPlayer)->Realize(mAudioPlayer, SL_BOOLEAN_FALSE);
    // 获取Player接口
    (*mAudioPlayer)->GetInterface(mAudioPlayer, SL_IID_PLAY, &mAudioPlayerInterface);

    // 注册缓冲区
    (*mAudioPlayer)->GetInterface(mAudioPlayer, SL_IID_BUFFERQUEUE, &mBufferQueueInterface);
    // 设置回调接口
    (*mBufferQueueInterface)->RegisterCallback(mBufferQueueInterface, getDataQueueCallBack, NULL);
    // 播放
    (*mAudioPlayerInterface)->SetPlayState(mAudioPlayerInterface,SL_PLAYSTATE_PLAYING);
    // 开始播放
    getDataQueueCallBack(mBufferQueueInterface, NULL);
}

void Opensles::getDataQueueCallBack(SLAndroidSimpleBufferQueueItf bufferQueueInterface, void* context) {
    size_t buffersize = 0;
    void *buffer;
    getPcmDataInterface(&buffer, &buffersize);
    if(buffer != NULL && buffersize != 0){
        // 将得到的数据加入到队列中
        (*bufferQueueInterface)->Enqueue(bufferQueueInterface, buffer, buffersize);
    }
}

void Opensles::release() {
    if(mAudioPlayer != NULL){
        (*mAudioPlayer)->Destroy(mAudioPlayer);
        mAudioPlayer = NULL;
        mBufferQueueInterface = NULL;
        mAudioPlayerInterface = NULL;
    }
    if(mOutputMixObject != NULL){
        (*mOutputMixObject)->Destroy(mOutputMixObject);
        mOutputMixObject = NULL;
        mOutputMixEnvirRevarb = NULL;
    }
    if(mEngineObject != NULL){
        (*mEngineObject)->Destroy(mEngineObject);
        mEngineObject = NULL;
        mEngineEngine = NULL;
    }
}

void Opensles::setPcmDataInterface(PcmDataFunc func) {
    getPcmDataInterface = func;
}
