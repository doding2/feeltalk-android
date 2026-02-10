package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
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
    var isCompleted = false
    private var lastReadFileReadCount = 0

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
        visualizer.drawDefaultView()
        isReplaying = true

        val buf = ByteArray(bufferSize)

        // remove .wav file header
        inputStream.read(ByteArray(44))

        CoroutineScope(Dispatchers.Main).launch {
            for (count in 0..fileReadCount) {
                if (!isReplaying) {
                    lastReadFileReadCount = count
                    break
                }

                val readSize = withContext(Dispatchers.IO) {
                    inputStream.read(buf, 0, buf.size)
                }
                if (readSize != -1) {
                    var tempBuf = buf.take(recordBufferSize).toByteArray()
                    val firstDecibel = DecibelCalculator.calculate(tempBuf, tempBuf.size)

                    val lastDecibel = if (firstDecibel >= 70) {
                        tempBuf = buf.takeLast(recordBufferSize).toByteArray()
                        DecibelCalculator.calculate(tempBuf, tempBuf.size)
                    } else {
                        firstDecibel
                    }
                    val wholeDecibel = if (lastDecibel >= 70) {
                        DecibelCalculator.calculate(buf, buf.size)
                    } else {
                        lastDecibel
                    }
                    val decibel = if (firstDecibel >= 70 && lastDecibel >= 70 && wholeDecibel >= 70) {
                        0
                    } else {
                        lastDecibel
                    }

                    visualizer.receive(decibel)
                }

                delay(mSampleInterval)
                replayTime += mSampleInterval

                if (count == fileReadCount) {
                    isCompleted = true
                }
            }

            isReplaying = false
        }
    }

    fun resume() {
        isReplaying = true
        CoroutineScope(Dispatchers.IO).launch {
            delay(mSampleInterval)
            mediaPlayer.start()
        }

        val buf = ByteArray(bufferSize)

        CoroutineScope(Dispatchers.Main).launch {
            for (count in lastReadFileReadCount..fileReadCount) {
                if (!isReplaying) {
                    lastReadFileReadCount = count
                    break
                }

                val readSize = withContext(Dispatchers.IO) {
                    inputStream.read(buf, 0, buf.size)
                }
                if (readSize != -1) {
                    var tempBuf = buf.take(recordBufferSize).toByteArray()
                    val firstDecibel = DecibelCalculator.calculate(tempBuf, tempBuf.size)

                    val lastDecibel = if (firstDecibel >= 70) {
                        tempBuf = buf.takeLast(recordBufferSize).toByteArray()
                        DecibelCalculator.calculate(tempBuf, tempBuf.size)
                    } else {
                        firstDecibel
                    }
                    val wholeDecibel = if (lastDecibel >= 70) {
                        DecibelCalculator.calculate(buf, buf.size)
                    } else {
                        lastDecibel
                    }
                    val decibel = if (firstDecibel >= 70 && lastDecibel >= 70 && wholeDecibel >= 70) {
                        0
                    } else {
                        lastDecibel
                    }

                    visualizer.receive(decibel)
                }

                delay(mSampleInterval)
                replayTime += mSampleInterval

                if (count == fileReadCount) {
                    isCompleted = true
                }
            }

            isReplaying = false
        }
    }

    fun pause() {
        isReplaying = false
        mediaPlayer?.pause()
    }

    fun stop() {
        isReplaying = false
        isCompleted = true
        mediaPlayer?.stop()
        mediaPlayer?.release()
        replayTime = 0
        lastReadFileReadCount = 0
        inputStream.close()
    }
}