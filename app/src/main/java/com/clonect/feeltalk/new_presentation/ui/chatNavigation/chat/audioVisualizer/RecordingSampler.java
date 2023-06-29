package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

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

    private CalculateVolumeListener mVolumeListener;
    private int mSamplingInterval = 100;
    private Timer mTimer;
    private long recordTime;
    private RecordTimeListener recordTimeListener;

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
     * setter of samplingInterval
     *
     * @param samplingInterval interval volume sampling
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
    }

    private void runRecording() {
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
                if (recordTimeListener != null) {
                    recordTimeListener.onTime(recordTime);
                }

                mAudioRecord.read(buf, 0, mBufSize);

                int decibel = calculateDecibel(buf);
                if (mVisualizerViews != null && !mVisualizerViews.isEmpty()) {
                    for (int i = 0; i < mVisualizerViews.size(); i++) {
                        mVisualizerViews.get(i).receive(decibel);
                    }
                }

                // callback for return input value
                if (mVolumeListener != null) {
                    mVolumeListener.onCalculateVolume(decibel);
                }
            }
        }, 0, mSamplingInterval);
    }

    private int calculateDecibel(byte[] buf) {
        int sum = 0;
        for (int i = 0; i < mBufSize; i++) {
            sum += Math.abs(buf[i]);
        }
        // avg 10-50
        return sum / mBufSize;
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

    public interface CalculateVolumeListener {

        /**
         * calculate input volume
         *
         * @param volume mic-input volume
         */
        void onCalculateVolume(int volume);
    }


    public long getRecordTime() {
        return recordTime;
    }

    public void setOnRecordTimeListener(RecordTimeListener listener) {
        recordTimeListener = listener;
    }

    public interface RecordTimeListener {
        void onTime(long recordTime);
    }
}