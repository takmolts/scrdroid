package org.client.scrcpy.decoder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioDecoder {

    private static final String TAG = "Scrcpy";

    public static final String MIMETYPE_AUDIO_AAC = "audio/mp4a-latm";

    private MediaCodec mCodec;
    private Worker mWorker;
    private AtomicBoolean mIsConfigured = new AtomicBoolean(false);

    private AudioTrack audioTrack;
    private final int SAMPLE_RATE = 48000;

    private void initAudioTrack() {
        int bufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes, AudioTrack.MODE_STREAM);
    }

    public void decodeSample(byte[] data, int offset, int size, long presentationTimeUs, int flags) {
        if (mWorker != null) {
            mWorker.decodeSample(data, offset, size, presentationTimeUs, flags);
        }
    }

    public void configure(byte[] data) {
        if (mWorker != null) {
            mWorker.configure(data);
        }
    }


    public void start() {
        if (mWorker == null) {
            mWorker = new Worker();
            mWorker.setRunning(true);
            mWorker.start();
        }
    }

    private boolean muted = false;

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (audioTrack != null) {
            if (muted) {
                audioTrack.setStereoVolume(0f, 0f);
            } else {
                audioTrack.setStereoVolume(1f, 1f);
            }
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void stop() {
        if (mWorker != null) {
            mWorker.setRunning(false);
            mWorker = null;
            mIsConfigured.set(false);
            if (mCodec != null) {
                try {
                    mCodec.stop();
                } catch (IllegalStateException e) {
                    Log.w(TAG, "audio codec stop failed: " + e);
                }
            }
            if (audioTrack != null) {
                try {
                    audioTrack.stop();
                } catch (IllegalStateException e) {
                    Log.w(TAG, "audioTrack stop failed: " + e);
                }
            }
        }
    }

    private class Worker extends Thread {

        private AtomicBoolean mIsRunning = new AtomicBoolean(false);

        Worker() {
        }

        private void setRunning(boolean isRunning) {
            mIsRunning.set(isRunning);
        }

        private void configure(byte[] data) {
            if (mIsConfigured.get()) {
                mIsConfigured.set(false);
                if (mCodec != null) {
                    try {
                        mCodec.stop();
                    } catch (IllegalStateException ignore) {
                        // 既に停止済み
                    }
                }
                if (audioTrack != null) {
                    try {
                        audioTrack.stop();
                    } catch (IllegalStateException ignore) {
                        // 既に停止済み
                    }
                }
            }
            MediaFormat format = MediaFormat.createAudioFormat(MIMETYPE_AUDIO_AAC, SAMPLE_RATE, 2);
            // 设置比特率
            format.setInteger(MediaFormat.KEY_BIT_RATE, 128000);
            // adts 0
            // format.setInteger(MediaFormat.KEY_IS_ADTS, 1);
            format.setByteBuffer("csd-0", ByteBuffer.wrap(data));

            try {
                mCodec = MediaCodec.createDecoderByType(MIMETYPE_AUDIO_AAC);
                mCodec.configure(format, null, null, 0);
                mCodec.start();
                mIsConfigured.set(true);

                // 初始化音频播放器
                initAudioTrack();
                // audio track 启动
                audioTrack.play();
            } catch (IOException | IllegalStateException | IllegalArgumentException e) {
                // 例外を投げると呼び出し元の通信ループが落ちて接続断になるため、
                // 未設定のままにして次の CONFIG で再構成させる
                Log.w(TAG, "audio codec configure failed: " + e);
                mIsConfigured.set(false);
            }
        }


        @SuppressWarnings("deprecation")
        public void decodeSample(byte[] data, int offset, int size, long presentationTimeUs, int flags) {
            if (mIsConfigured.get() && mIsRunning.get()) {
                try {
                    int index = mCodec.dequeueInputBuffer(-1);
                    if (index >= 0) {
                        ByteBuffer buffer;

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            buffer = mCodec.getInputBuffers()[index];
                            buffer.clear();
                        } else {
                            buffer = mCodec.getInputBuffer(index);
                        }
                        if (buffer != null) {
                            buffer.put(data, offset, size);
                            mCodec.queueInputBuffer(index, 0, size, presentationTimeUs, flags);
                        }
                    }
                } catch (IllegalStateException e) {
                    // 通信ループを落とさず、次の CONFIG まで投入をやめる
                    Log.w(TAG, "audio decodeSample skipped: " + e);
                    mIsConfigured.set(false);
                }
            }
        }

        @Override
        public void run() {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            while (mIsRunning.get()) {
                try {
                    if (mIsConfigured.get()) {
                        int index = mCodec.dequeueOutputBuffer(info, 0);
                        // Log.e("Scrcpy", "Audio Decoder: " + index);
                        if (index >= 0) {
                            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                                break;
                            }
                            // Log.e("Scrcpy", "Audio success get frame: " + index);

                            // 读取 pcm 数据，写入 audiotrack 播放
                            ByteBuffer outputBuffer = mCodec.getOutputBuffer(index);
                            if (outputBuffer != null) {
                                byte[] data = new byte[info.size];
                                outputBuffer.get(data);
                                outputBuffer.clear();
                                audioTrack.write(data, 0, info.size);
                            }
                            // release
                            mCodec.releaseOutputBuffer(index, true);
                        }
                    } else {
                        // just waiting to be configured, then decode and render
                        Thread.sleep(5);
                    }
                } catch (IllegalStateException e) {
                    // 再構成待ちに戻すだけでスレッドは維持する
                    mIsConfigured.set(false);
                } catch (InterruptedException ignore) {
                    break;
                }
            }
        }
    }
}