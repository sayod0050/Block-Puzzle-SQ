package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.GameMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Premium, ultra-smooth procedural audio engine for S&S Games.
 * Features a real-time streaming ambient synthesizer (MODE_STREAM)
 * producing serene generative melodies, warm pads, and rich marimba/crystal sound effects.
 */
class SoundManager(context: Context) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Default)

    var sfxVolume: Float = 0.8f
    var musicVolume: Float = 0.5f
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val sampleRate = 44100
    private var musicJob: Job? = null
    private var musicTrack: AudioTrack? = null
    private var currentMode: GameMode = GameMode.CLASSIC

    // Reusable SFX Static Tracks
    private val clickBuffer by lazy { generateWoodTapBuffer() }
    private val pickUpBuffer by lazy { generatePickUpBuffer() }
    private val placeBuffer by lazy { generateDropMarimbaBuffer() }
    private val invalidBuffer by lazy { generateSoftThudBuffer() }

    init {
        scope.launch {
            clickBuffer
            pickUpBuffer
            placeBuffer
            invalidBuffer
        }
    }

    // --- CONTINUOUS STREAMING MUSIC GENERATOR ---

    fun startMusic(mode: GameMode) {
        currentMode = mode
        if (musicJob != null && musicJob?.isActive == true) return
        if (musicVolume <= 0.01f) return

        musicJob = scope.launch(Dispatchers.IO) {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufSize * 2, 4096)

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                musicTrack = track
                track.setVolume(musicVolume * 0.35f)
                track.play()

                val chunkSize = 2048
                val chunk = ShortArray(chunkSize)

                // Warm pentatonic chord frequencies (C, E, G, B, D)
                val progression = listOf(
                    listOf(261.63, 329.63, 392.00, 493.88), // Cmaj7
                    listOf(220.00, 261.63, 329.63, 392.00), // Am7
                    listOf(174.61, 220.00, 261.63, 329.63), // Fmaj7
                    listOf(196.00, 246.94, 293.66, 392.00)  // G
                )

                val melodyNotes = listOf(
                    523.25, 587.33, 659.25, 783.99, 880.0, 1046.50
                )

                var sampleIndex = 0L
                var chordIndex = 0
                val chordLengthSamples = sampleRate * 4 // 4 seconds per chord

                var bellNoteFreq = 659.25
                var bellStartSample = 0L
                var bellDecay = 1.2

                while (isActive) {
                    track.setVolume(musicVolume * 0.35f)
                    val activeChord = progression[chordIndex % progression.size]

                    for (i in 0 until chunkSize) {
                        val t = sampleIndex.toDouble() / sampleRate
                        sampleIndex++

                        // Switch chords smoothly every 4 seconds
                        if (sampleIndex % chordLengthSamples == 0L) {
                            chordIndex++
                            // Trigger a delicate, peaceful ambient bell note
                            bellNoteFreq = melodyNotes.random()
                            bellStartSample = sampleIndex
                            bellDecay = 0.8 + (sampleIndex % 5) * 0.2
                        }

                        // Warm Lush Pad Synthesizer: 4 chord voices with slow chorus modulation
                        var padSample = 0.0
                        val lfo = 1.0 + 0.003 * sin(2.0 * PI * 0.4 * t)
                        for (freq in activeChord) {
                            val fMod = freq * lfo
                            padSample += sin(2.0 * PI * fMod * t)
                            padSample += 0.35 * sin(4.0 * PI * fMod * t) // warm 2nd harmonic
                        }
                        padSample /= (activeChord.size * 1.5)

                        // Delicate ambient bell overlay
                        val bellT = (sampleIndex - bellStartSample).toDouble() / sampleRate
                        var bellSample = 0.0
                        if (bellT in 0.0..3.0) {
                            val bellEnv = exp(-bellT * (2.5 / bellDecay))
                            bellSample = (sin(2.0 * PI * bellNoteFreq * bellT) +
                                    0.4 * sin(2.0 * PI * bellNoteFreq * 2.0 * bellT)) * bellEnv * 0.6
                        }

                        val mixed = (padSample * 0.65 + bellSample * 0.35) * 16000.0
                        chunk[i] = mixed.toInt().coerceIn(-32768, 32767).toShort()
                    }

                    if (musicVolume > 0.01f) {
                        track.write(chunk, 0, chunkSize)
                    }
                }

                track.stop()
                track.release()
            } catch (_: Exception) {
            } finally {
                musicTrack = null
            }
        }
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
        try {
            musicTrack?.stop()
            musicTrack?.release()
        } catch (_: Exception) {}
        musicTrack = null
    }

    // --- SOUND EFFECTS PLAYBACK ---

    private fun playSound(buffer: ShortArray, volMult: Float = 1.0f) {
        if (sfxVolume <= 0.01f) return
        val volume = (sfxVolume * volMult).coerceIn(0f, 1f)

        scope.launch(Dispatchers.IO) {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.setVolume(volume)
                track.write(buffer, 0, buffer.size)
                track.play()

                val dur = (buffer.size.toDouble() / sampleRate * 1000).toLong() + 30
                kotlinx.coroutines.delay(dur)
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun playClick() {
        playSound(clickBuffer, 0.45f)
        vibrateLight()
    }

    fun playPickUp() {
        playSound(pickUpBuffer, 0.55f)
        vibrateTick()
    }

    fun playPlace() {
        playSound(placeBuffer, 0.75f)
        vibrateLight()
    }

    fun playInvalid() {
        playSound(invalidBuffer, 0.55f)
        vibrateHeavy()
    }

    fun playLineClear(linesCount: Int) {
        val notes = when (linesCount) {
            1 -> listOf(523.25, 659.25, 783.99) // C5, E5, G5
            2 -> listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            3 -> listOf(523.25, 659.25, 783.99, 1046.50, 1318.51) // + E6
            else -> listOf(523.25, 659.25, 783.99, 1046.50, 1318.51, 1567.98) // + G6
        }
        val buffer = generateGlockenspielArpeggio(notes, 0.08)
        playSound(buffer, 0.95f)
        vibrateMedium()
    }

    fun playStreak(streak: Int) {
        val notes = listOf(440.0, 554.37, 659.25, 880.0, 1108.73)
        val count = (streak + 1).coerceIn(2, notes.size)
        val buffer = generateGlockenspielArpeggio(notes.take(count), 0.09)
        playSound(buffer, 1.0f)
        vibrateDoublePulse()
    }

    fun playPowerUp() {
        val notes = listOf(659.25, 830.61, 987.77, 1318.51)
        val buffer = generateGlockenspielArpeggio(notes, 0.06)
        playSound(buffer, 0.85f)
        vibrateLight()
    }

    fun playVictory() {
        val notes = listOf(523.25, 659.25, 783.99, 1046.50)
        val buffer = generateGlockenspielArpeggio(notes, 0.12)
        playSound(buffer, 1.0f)
        vibrateDoublePulse()
    }

    fun playGameOver() {
        val notes = listOf(440.0, 392.0, 349.23, 293.66)
        val buffer = generateGlockenspielArpeggio(notes, 0.14)
        playSound(buffer, 0.85f)
        vibrateHeavy()
    }

    // --- SYNTHESIZED SFX BUFFERS ---

    private fun generateWoodTapBuffer(): ShortArray {
        val duration = 0.035
        val n = (sampleRate * duration).toInt()
        val buf = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 140.0)
            val s = (sin(2.0 * PI * 1400.0 * t) + 0.3 * sin(2.0 * PI * 2200.0 * t)) * env
            buf[i] = (s * 20000).toInt().toShort()
        }
        return buf
    }

    private fun generatePickUpBuffer(): ShortArray {
        val duration = 0.08
        val n = (sampleRate * duration).toInt()
        val buf = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val freq = 580.0 + (t / duration) * 240.0 // rising tone
            val env = exp(-t * 28.0)
            val s = sin(2.0 * PI * freq * t) * env
            buf[i] = (s * 18000).toInt().toShort()
        }
        return buf
    }

    private fun generateDropMarimbaBuffer(): ShortArray {
        val duration = 0.14
        val n = (sampleRate * duration).toInt()
        val buf = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val freq = 340.0 * (1.0 - t * 0.8)
            val env = exp(-t * 30.0)
            val s = (sin(2.0 * PI * freq * t) + 0.35 * sin(4.0 * PI * freq * t)) * env
            buf[i] = (s * 25000).toInt().toShort()
        }
        return buf
    }

    private fun generateSoftThudBuffer(): ShortArray {
        val duration = 0.12
        val n = (sampleRate * duration).toInt()
        val buf = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 35.0)
            val s = sin(2.0 * PI * 110.0 * t) * env
            buf[i] = (s * 18000).toInt().toShort()
        }
        return buf
    }

    private fun generateGlockenspielArpeggio(notes: List<Double>, noteDur: Double): ShortArray {
        val totalDur = noteDur * notes.size + 0.35
        val n = (sampleRate * totalDur).toInt()
        val buf = ShortArray(n)

        for ((idx, freq) in notes.withIndex()) {
            val start = (idx * noteDur * sampleRate).toInt()
            val noteSamples = (sampleRate * 0.35).toInt()

            for (i in 0 until noteSamples) {
                val tIdx = start + i
                if (tIdx >= n) break
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 11.0)
                val s = (sin(2.0 * PI * freq * t) + 0.4 * sin(2.0 * PI * freq * 2.0 * t)) * env
                val cur = buf[tIdx].toInt()
                val next = (cur + s * 14000).toInt()
                buf[tIdx] = next.coerceIn(-32768, 32767).toShort()
            }
        }
        return buf
    }

    // --- HAPTICS ---

    private fun vibrateTick() {
        if (!hapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(10)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateLight() {
        if (!hapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateMedium() {
        if (!hapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(45)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateHeavy() {
        if (!hapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(75, 200))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(75)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateDoublePulse() {
        if (!hapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 25, 50, 35), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 25, 50, 35), -1)
            }
        } catch (_: Exception) {}
    }
}
