package com.flownote.util

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class VoiceRecorder {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentRecordingFile: File? = null
    private var isRecordingActive = false
    private var isPlayingActive = false

    /**
     * Start recording audio to the specified output file
     * @param outputFile File where the audio will be saved
     * @throws IOException if recording cannot be started
     */
    @Throws(IOException::class)
    fun startRecording(context: android.content.Context, outputFile: File) {
        if (isRecordingActive) {
            throw IllegalStateException("Recording is already in progress")
        }

        currentRecordingFile = outputFile

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)

            try {
                prepare()
                start()
                isRecordingActive = true
            } catch (e: IOException) {
                release()
                throw e
            }
        }
    }

    /**
     * Stop the current recording and release resources
     * @return The file containing the recorded audio, or null if no recording was in progress
     */
    fun stopRecording(): File? {
        if (!isRecordingActive) {
            return null
        }

        mediaRecorder?.apply {
            try {
                stop()
            } catch (e: RuntimeException) {
                // Ignore if stop fails (recording was too short, etc.)
            } finally {
                release()
            }
        }
        mediaRecorder = null
        isRecordingActive = false

        return currentRecordingFile
    }

    /**
     * Play an audio file
     * @param file Audio file to play
     * @param onComplete Callback invoked when playback completes
     * @throws IOException if playback cannot be started
     */
    @Throws(IOException::class)
    fun playAudio(file: File, onComplete: (() -> Unit)? = null) {
        if (isPlayingActive) {
            stopPlayback()
        }

        if (!file.exists()) {
            throw IOException("Audio file does not exist: ${file.absolutePath}")
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener {
                isPlayingActive = false
                onComplete?.invoke()
            }
            setOnErrorListener { _, _, _ ->
                isPlayingActive = false
                true
            }
            prepare()
            start()
            isPlayingActive = true
        }
    }

    /**
     * Stop audio playback
     */
    fun stopPlayback() {
        if (!isPlayingActive) {
            return
        }

        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isPlayingActive = false
    }

    /**
     * Pause audio playback
     */
    fun pausePlayback() {
        if (isPlayingActive && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    /**
     * Resume audio playback
     */
    fun resumePlayback() {
        if (isPlayingActive && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean = isRecordingActive

    /**
     * Check if currently playing
     */
    fun isPlaying(): Boolean = isPlayingActive && mediaPlayer?.isPlaying == true

    /**
     * Get current playback position in milliseconds
     */
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    /**
     * Get total duration of current audio in milliseconds
     */
    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    /**
     * Release all resources
     * Should be called when done with the recorder
     */
    fun release() {
        stopRecording()
        stopPlayback()
    }
}
