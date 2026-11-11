package com.example.game.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Audio Synthesizer for SQ Games Block Puzzle.
 * Synthesizes crisp, responsive sound effects and tranquil ambient music without external audio files.
 */
class SoundSynthesizer {

    private val sampleRate = 22050
    private var sfxVolume = 0.8f
    private var musicVolume = 0.5f
    private var sfxMuted = false
    private var musicMuted = false

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null

    enum class SoundEvent {
        CLICK,
        PICK_BLOCK,
        DROP_BLOCK,
        INVALID_MOVE,
        LINE_CLEAR,
        COMBO,
        STREAK,
        GAME_OVER,
        VICTORY,
        POWERUP
    }

    fun setSfxVolume(vol: Float) {
        sfxVolume = vol.coerceIn(0f, 1f)
    }

    fun setMusicVolume(vol: Float) {
        musicVolume = vol.coerceIn(0f, 1f)
    }

    fun setSfxMuted(muted: Boolean) {
        sfxMuted = muted
    }

    fun setMusicMuted(muted: Boolean) {
        musicMuted = muted
        if (muted) {
            stopAmbientMusic()
        } else {
            startAmbientMusic()
        }
    }

    fun playSound(event: SoundEvent) {
        if (sfxMuted || sfxVolume <= 0.01f) return

        audioScope.launch {
            try {
                val samples = when (event) {
                    SoundEvent.CLICK -> generateClick()
                    SoundEvent.PICK_BLOCK -> generateChirp(440f, 660f, 0.04f)
                    SoundEvent.DROP_BLOCK -> generateDrop()
                    SoundEvent.INVALID_MOVE -> generateInvalid()
                    SoundEvent.LINE_CLEAR -> generateChord(listOf(523.25f, 659.25f, 783.99f), 0.2f)
                    SoundEvent.COMBO -> generateArpeggio(listOf(523.25f, 659.25f, 783.99f, 1046.50f), 0.35f)
                    SoundEvent.STREAK -> generateStreak()
                    SoundEvent.GAME_OVER -> generateGameOver()
                    SoundEvent.VICTORY -> generateVictory()
                    SoundEvent.POWERUP -> generateChirp(300f, 900f, 0.18f)
                }
                playPcm(samples, sfxVolume)
            } catch (_: Exception) {
                // Ignore audio hardware transient exceptions
            }
        }
    }

    fun startAmbientMusic() {
        if (musicMuted || musicJob != null || musicVolume <= 0.01f) return

        musicJob = audioScope.launch {
            // Soothing pentatonic notes in Hz (C4, D4, E4, G4, A4, C5)
            val pentatonicScale = listOf(261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f)
            var noteIndex = 0

            while (isActive && !musicMuted) {
                val freq = pentatonicScale[noteIndex % pentatonicScale.size]
                val duration = 1.2f
                val noteSamples = generateAmbientPad(freq, duration)
                playPcm(noteSamples, musicVolume * 0.35f)

                noteIndex = (noteIndex + (1..3).random()) % pentatonicScale.size
                delay(1400L)
            }
        }
    }

    fun stopAmbientMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playPcm(samples: ShortArray, volume: Float) {
        if (samples.isEmpty()) return
        val bufferSize = samples.size * 2
        var track: AudioTrack? = null
        try {
            val scaledSamples = ShortArray(samples.size) { i ->
                (samples[i] * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            track = AudioTrack.Builder()
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
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(scaledSamples, 0, scaledSamples.size)
            track.play()
            // Wait approximately until playback finishes, then release
            Thread.sleep((samples.size * 1000L / sampleRate) + 20)
        } catch (_: Exception) {
            // Audio hardware busy or interrupted
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }

    // --- Procedural Waveform Generators ---

    private fun generateClick(): ShortArray {
        val count = (sampleRate * 0.015f).toInt()
        return ShortArray(count) { i ->
            val t = i.toFloat() / sampleRate
            val env = exp(-t * 200f)
            (sin(2 * PI * 1200 * t) * env * 20000).toInt().toShort()
        }
    }

    private fun generateDrop(): ShortArray {
        val count = (sampleRate * 0.06f).toInt()
        return ShortArray(count) { i ->
            val t = i.toFloat() / sampleRate
            val freq = 220f - (t / 0.06f) * 120f
            val env = exp(-t * 50f)
            (sin(2 * PI * freq * t) * env * 24000).toInt().toShort()
        }
    }

    private fun generateInvalid(): ShortArray {
        val count = (sampleRate * 0.1f).toInt()
        return ShortArray(count) { i ->
            val t = i.toFloat() / sampleRate
            val env = exp(-t * 25f)
            (sin(2 * PI * 130 * t) * env * 22000).toInt().toShort()
        }
    }

    private fun generateChirp(startFreq: Float, endFreq: Float, durationSec: Float): ShortArray {
        val count = (sampleRate * durationSec).toInt()
        return ShortArray(count) { i ->
            val frac = i.toFloat() / count
            val t = i.toFloat() / sampleRate
            val freq = startFreq + (endFreq - startFreq) * frac
            val env = sin(PI * frac)
            (sin(2 * PI * freq * t) * env * 22000).toInt().toShort()
        }
    }

    private fun generateChord(freqs: List<Float>, durationSec: Float): ShortArray {
        val count = (sampleRate * durationSec).toInt()
        return ShortArray(count) { i ->
            val t = i.toFloat() / sampleRate
            val frac = i.toFloat() / count
            val env = exp(-frac * 4.5f)
            var sum = 0.0
            for (f in freqs) {
                sum += sin(2 * PI * f * t)
            }
            ((sum / freqs.size) * env * 26000).toInt().toShort()
        }
    }

    private fun generateArpeggio(notes: List<Float>, totalDuration: Float): ShortArray {
        val count = (sampleRate * totalDuration).toInt()
        val noteLength = count / notes.size
        return ShortArray(count) { i ->
            val noteIdx = (i / noteLength).coerceIn(0, notes.lastIndex)
            val localI = i % noteLength
            val t = localI.toFloat() / sampleRate
            val frac = localI.toFloat() / noteLength
            val env = exp(-frac * 3.5f)
            (sin(2 * PI * notes[noteIdx] * t) * env * 25000).toInt().toShort()
        }
    }

    private fun generateStreak(): ShortArray {
        val count = (sampleRate * 0.4f).toInt()
        val notes = listOf(587.33f, 739.99f, 880.00f, 1174.66f)
        val noteLength = count / notes.size
        return ShortArray(count) { i ->
            val noteIdx = (i / noteLength).coerceIn(0, notes.lastIndex)
            val localI = i % noteLength
            val t = localI.toFloat() / sampleRate
            val frac = localI.toFloat() / noteLength
            val env = exp(-frac * 3f)
            val f = notes[noteIdx]
            val wave = sin(2 * PI * f * t) + 0.3 * sin(2 * PI * f * 2 * t)
            (wave * env * 18000).toInt().toShort()
        }
    }

    private fun generateGameOver(): ShortArray {
        val count = (sampleRate * 0.45f).toInt()
        return ShortArray(count) { i ->
            val frac = i.toFloat() / count
            val t = i.toFloat() / sampleRate
            val freq = 420f - frac * 240f
            val env = exp(-frac * 2.5f)
            (sin(2 * PI * freq * t) * env * 23000).toInt().toShort()
        }
    }

    private fun generateVictory(): ShortArray {
        val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f, 1318.51f)
        val count = (sampleRate * 0.5f).toInt()
        val noteLength = count / notes.size
        return ShortArray(count) { i ->
            val noteIdx = (i / noteLength).coerceIn(0, notes.lastIndex)
            val localI = i % noteLength
            val t = localI.toFloat() / sampleRate
            val frac = localI.toFloat() / noteLength
            val env = exp(-frac * 2.8f)
            (sin(2 * PI * notes[noteIdx] * t) * env * 24000).toInt().toShort()
        }
    }

    private fun generateAmbientPad(freq: Float, durationSec: Float): ShortArray {
        val count = (sampleRate * durationSec).toInt()
        return ShortArray(count) { i ->
            val frac = i.toFloat() / count
            val t = i.toFloat() / sampleRate
            // Smooth attack and long gentle release
            val env = if (frac < 0.2f) (frac / 0.2f) else exp(-(frac - 0.2f) * 2.5f)
            val wave = sin(2 * PI * freq * t) + 0.25 * sin(2 * PI * (freq * 1.5f) * t)
            (wave * env * 14000).toInt().toShort()
        }
    }
}
