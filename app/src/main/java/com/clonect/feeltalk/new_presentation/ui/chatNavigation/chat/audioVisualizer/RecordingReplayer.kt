package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import kotlin.math.ceil


class RecordingReplayer(
    private val context: Context,
    private val audioFile: File,
    private val visualizer: VisualizerView,
) {
    companion object {
        private const val RECORDING_SAMPLE_RATE = 44100
    }

    private val mediaPlayer = MediaPlayer.create(context, Uri.fromFile(audioFile))

    private val audioDuration: Long
    private var mSampleInterval: Long = 100
    private var fileReadCount = 0

    private var replayTime: Long = 0
    private val inputStream = DataInputStream(FileInputStream(audioFile))
    private var bufferSize: Int
    private var recordBufferSize: Int

    var isReplaying = false

    init {
        recordBufferSize = AudioRecord.getMinBufferSize(
            RECORDING_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, Uri.fromFile(audioFile))
        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        audioDuration = duration?.toLong() ?: 0

        fileReadCount = ceil((audioDuration.toDouble() / (mSampleInterval.toDouble()))).toInt()
        bufferSize = ceil((audioFile.length().toDouble() - 44f) / fileReadCount.toDouble()).toInt()

        infoLog("bufferSize: $bufferSize")
        infoLog("interval: $mSampleInterval")
        infoLog("audio duration: $duration")
        infoLog("fileReadCount: $fileReadCount")
    }

    fun replay() {
        if (mediaPlayer == null) return

        CoroutineScope(Dispatchers.IO).launch {
            delay(mSampleInterval)
            mediaPlayer.start()
        }
        visualizer.reset()
        isReplaying = true

        val buf = ByteArray(bufferSize)

        // remove .wav file header
        inputStream.read(ByteArray(44))

        CoroutineScope(Dispatchers.Main).launch {
            for (count in 0..fileReadCount) {
                if (!isReplaying)
                    break

                val readSize = withContext(Dispatchers.IO) {
                    inputStream.read(buf, 0, buf.size)
                }
                if (readSize == -1) {
                    infoLog("readSize is -1")
                    break
                }

                val tempBuf = buf.take(recordBufferSize).toByteArray()
                val decibel = DecibelCalculator.calculate(tempBuf, tempBuf.size)
                infoLog("decibel: $decibel")

                visualizer.receive(decibel)

                delay(mSampleInterval)
                replayTime += mSampleInterval
            }


            for (i in 0..2) {
                visualizer.receive(0)
                delay(mSampleInterval)
            }

            isReplaying = false
            stop()
            infoLog("iterate while fileReadCount done")
        }
    }


    fun resume() {

    }

    fun pause() {
        isReplaying = false
        mediaPlayer?.stop()
    }

    fun stop() {
        pause()
        replayTime = 0
        inputStream.close()
    }
}