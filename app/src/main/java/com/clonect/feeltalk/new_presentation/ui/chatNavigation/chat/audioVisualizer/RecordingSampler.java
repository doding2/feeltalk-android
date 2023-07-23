package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.clonect.feeltalk.common.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Sampling AudioRecord Input
 * This output send to {@link VisualizerView}
 *
 * Created by tyorikan on 2015/06/09.
 */
public class RecordingSampler {

    private static final int RECORDING_SAMPLE_RATE = 44100;

    private AudioRecord mAudioRecord;
    private boolean mIsRecording;
    private int mBufSize;
    private File voiceCacheFile;
    private FileOutputStream cacheStream;

    private CalculateVolumeListener mVolumeListener;
    private int mSamplingInterval = 10;
    private Timer mTimer;
    private long recordTime;

    private List<VisualizerView> mVisualizerViews = new ArrayList<>();

    public RecordingSampler(Context context) {
        initAudioRecord(context);
    }

    /**
     * link to VisualizerView
     *
     * @param visualizerView {@link VisualizerView}
     */
    public void link(VisualizerView visualizerView) {
        mVisualizerViews.add(visualizerView);
    }

    /**
     * setter of CalculateVolumeListener
     *
     * @param volumeListener CalculateVolumeListener
     */
    public void setVolumeListener(CalculateVolumeListener volumeListener) {
        mVolumeListener = volumeListener;
    }

    /**
     * setter of UiRepresentInterval
     *
     * @param samplingInterval interval volume sampling for ui representation
     */
    public void setSamplingInterval(int samplingInterval) {
        mSamplingInterval = samplingInterval;
    }

    /**
     * getter isRecording
     *
     * @return true:recording, false:not recording
     */
    public boolean isRecording() {
        return mIsRecording;
    }

    private void initAudioRecord(Context context) {
        int bufferSize = AudioRecord.getMinBufferSize(
                RECORDING_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDING_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
        );

        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            mBufSize = bufferSize;
        }

        try {
            voiceCacheFile = new File(context.getCacheDir(), Constants.VOICE_CACHE_FILE_NAME);
            cacheStream = new FileOutputStream((voiceCacheFile));
            writeWavHeader((OutputStream) cacheStream, (short) 1, mAudioRecord.getSampleRate(), (short) 16);
        } catch (Exception e) {
            Log.d("Feeltalk", "오디오 리코딩 초기화 에러 발생: " + e.getLocalizedMessage());
        }
    }

    private void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
        // WAV 포맷에 필요한 little endian 포맷으로 다중 바이트의 수를 raw byte로 변환한다.
        byte[] littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels * (bitDepth / 8))
                .putShort((short) (channels * (bitDepth / 8)))
                .putShort(bitDepth)
                .array();
        // 최고를 생성하지는 않겠지만, 적어도 쉽게만 가자.
        out.write(new byte[]{
                'R', 'I', 'F', 'F', // Chunk ID
                0, 0, 0, 0, // Chunk Size (나중에 업데이트 될것)
                'W', 'A', 'V', 'E', // Format
                'f', 'm', 't', ' ', //Chunk ID
                16, 0, 0, 0, // Chunk Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // Num of Channels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // Byte Rate
                littleBytes[10], littleBytes[11], // Block Align
                littleBytes[12], littleBytes[13], // Bits Per Sample
                'd', 'a', 't', 'a', // Chunk ID
                0, 0, 0, 0, //Chunk Size (나중에 업데이트 될 것)
        });
    }

    private void updateWavHeader(File wav) throws IOException {
        byte[] sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // 아마 이 두 개를 계산할 때 좀 더 좋은 방법이 있을거라 생각하지만..
                .putInt((int) (wav.length() - 8)) // ChunkSize
                .putInt((int) (wav.length() - 44)) // Chunk Size
                .array();
        RandomAccessFile accessWave = null;
        try {
            accessWave = new RandomAccessFile(wav, "rw"); // 읽기-쓰기 모드로 인스턴스 생성
            // ChunkSize
            accessWave.seek(4); // 4바이트 지점으로 가서
            accessWave.write(sizes, 0, 4); // 사이즈 채움
            // Chunk Size
            accessWave.seek(40); // 40바이트 지점으로 가서
            accessWave.write(sizes, 4, 4); // 채움
        } catch (IOException ex) {
            // 예외를 다시 던지나, finally 에서 닫을 수 있음
            throw ex;
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close();
                } catch (IOException ex) {
                    // 무시
                }
            }
        }
    }


    /**
     * start AudioRecord.read
     */
    public void startRecording() {
        mTimer = new Timer();
        mAudioRecord.startRecording();
        mIsRecording = true;
        recordTime = 0;
        runRecording();
    }

    /**
     * stop AudioRecord.read
     */
    public void stopRecording() {
        mIsRecording = false;
        mTimer.cancel();

        if (mVisualizerViews != null && !mVisualizerViews.isEmpty()) {
            for (int i = 0; i < mVisualizerViews.size(); i++) {
                mVisualizerViews.get(i).receive(0);
            }
        }

        try {
            cacheStream.close();
            updateWavHeader(voiceCacheFile);
        } catch (Exception e) {
            Log.d("FeeltalkInfo", "stopRecording error: " + e.getLocalizedMessage());
        }
    }

    private void runRecording() {
        if (mVisualizerViews != null && !mVisualizerViews.isEmpty()) {
            for (int i = 0; i < mVisualizerViews.size(); i++) {
                mVisualizerViews.get(i).drawDefaultView();
            }
        }

        final byte buf[] = new byte[mBufSize];

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                // stop recording
                if (!mIsRecording) {
                    mAudioRecord.stop();
                    return;
                }

                recordTime += mSamplingInterval;

                int status = mAudioRecord.read(buf, 0, mBufSize);
                try {
                    if (status != AudioRecord.ERROR_INVALID_OPERATION && status != AudioRecord.ERROR_BAD_VALUE) {
                        cacheStream.write(buf, 0, buf.length);
                    }
                } catch (Exception e) {
                    Log.d("FeeltalkInfo", "오디오 리코딩 buffer write error: " + e.getLocalizedMessage());
                }

                int decibel = calculateDecibel(buf, status);
                if (recordTime % 20 == 0) {
                    if (mVisualizerViews != null && !mVisualizerViews.isEmpty()) {
                        for (int i = 0; i < mVisualizerViews.size(); i++) {
                            mVisualizerViews.get(i).receive(decibel);
                        }
                    }
                }

                // callback for return input value
                if (mVolumeListener != null) {
                    mVolumeListener.onCalculateVolume(decibel);
                }
            }
        }, 0, mSamplingInterval);
    }

    private int calculateDecibel(byte[] buf, int size) {
        double sum = 0;
        for (int i = 0; i < size/2; i++) {
            double y = (buf[i*2] | buf[i*2+1] << 8);
            sum += y * y;
        }
        double amplitude = sum / (buf.length / 2.0);
        return (int) (20 * Math.log10(amplitude / 32768.0));
    }

    /**
     * release member object
     */
    public void release() {
        stopRecording();
        mAudioRecord.release();
        mAudioRecord = null;
        mTimer = null;
    }

    public File getVoiceRecordFile() {
        return voiceCacheFile;
    }


    public interface CalculateVolumeListener {

        /**
         * calculate input volume
         *
         * @param volume mic-input volume
         */
        void onCalculateVolume(int volume);
    }

}