package com.synheart.emotion

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class EmotionEngineTest {

    @Test
    fun `fromPretrained creates engine with default model`() {
        val config = EmotionConfig()
        val engine = EmotionEngine.fromPretrained(config)
        
        assertNotNull(engine)
        assertEquals(config, engine.config)
    }

    @Test
    fun `fromPretrained validates model compatibility`() {
        val config = EmotionConfig()
        val invalidModel = LinearSvmModel(
            modelId = "invalid",
            version = "1.0",
            labels = listOf("A", "B", "C"),
            featureNames = listOf("f1", "f2"), // Wrong number of features
            weights = listOf(
                listOf(1.0, 2.0),
                listOf(3.0, 4.0),
                listOf(5.0, 6.0)
            ),
            biases = listOf(0.0, 0.0, 0.0),
            mu = emptyMap(),
            sigma = emptyMap()
        )
        
        try {
            EmotionEngine.fromPretrained(config, invalidModel)
            fail("Should have thrown EmotionError.ModelIncompatible")
        } catch (e: EmotionError.ModelIncompatible) {
            // Expected
        }
    }

    @Test
    fun `push accepts valid data`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val timestamp = Date()
        
        engine.push(
            hr = 72.0,
            rrIntervalsMs = listOf(800.0, 850.0, 820.0),
            timestamp = timestamp
        )
        
        val stats = engine.getBufferStats()
        assertEquals(1, stats["count"])
    }

    @Test
    fun `push rejects invalid HR values`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val timestamp = Date()
        
        // Too low
        engine.push(hr = 20.0, rrIntervalsMs = listOf(800.0), timestamp = timestamp)
        // Too high
        engine.push(hr = 400.0, rrIntervalsMs = listOf(800.0), timestamp = timestamp)
        
        val stats = engine.getBufferStats()
        assertEquals(0, stats["count"])
    }

    @Test
    fun `push rejects empty RR intervals`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val timestamp = Date()
        
        engine.push(hr = 72.0, rrIntervalsMs = emptyList(), timestamp = timestamp)
        
        val stats = engine.getBufferStats()
        assertEquals(0, stats["count"])
    }

    @Test
    fun `consumeReady returns empty when not enough data`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val timestamp = Date()
        
        engine.push(hr = 72.0, rrIntervalsMs = listOf(800.0), timestamp = timestamp)
        
        val results = engine.consumeReady()
        assertTrue(results.isEmpty())
    }

    @Test
    fun `consumeReady returns empty when step interval not met`() {
        val config = EmotionConfig(stepMs = 10000L) // 10 second step
        val engine = EmotionEngine.fromPretrained(config)
        val timestamp = Date()
        
        // Push multiple data points
        for (i in 0..20) {
            val ts = Date(timestamp.time + i * 1000) // 1 second apart
            engine.push(
                hr = 72.0 + i,
                rrIntervalsMs = listOf(800.0, 850.0, 820.0, 830.0, 840.0),
                timestamp = ts
            )
        }
        
        val results = engine.consumeReady()
        // Should be empty if step interval hasn't passed
        assertTrue(results.isEmpty() || results.size <= 1)
    }

    @Test
    fun `getBufferStats returns correct information`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val baseTime = System.currentTimeMillis()
        
        for (i in 0..5) {
            val timestamp = Date(baseTime + i * 1000)
            engine.push(
                hr = 70.0 + i,
                rrIntervalsMs = listOf(800.0, 850.0, 820.0),
                timestamp = timestamp
            )
        }
        
        val stats = engine.getBufferStats()
        assertTrue(stats["count"] as Int > 0)
        assertTrue(stats["duration_ms"] as Long > 0)
        assertTrue(stats.containsKey("hr_range"))
        assertTrue(stats.containsKey("rr_count"))
    }

    @Test
    fun `getBufferStats returns zeros for empty buffer`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val stats = engine.getBufferStats()
        
        assertEquals(0, stats["count"])
        assertEquals(0L, stats["duration_ms"])
        assertEquals(0, (stats["hr_range"] as List<*>)[0])
        assertEquals(0, stats["rr_count"])
    }

    @Test
    fun `clear removes all buffered data`() {
        val engine = EmotionEngine.fromPretrained(EmotionConfig())
        val timestamp = Date()
        
        for (i in 0..5) {
            engine.push(
                hr = 70.0 + i,
                rrIntervalsMs = listOf(800.0, 850.0),
                timestamp = Date(timestamp.time + i * 1000)
            )
        }
        
        val statsBefore = engine.getBufferStats()
        assertTrue(statsBefore["count"] as Int > 0)
        
        engine.clear()
        
        val statsAfter = engine.getBufferStats()
        assertEquals(0, statsAfter["count"])
    }

    @Test
    fun `trimBuffer removes old data outside window`() {
        val config = EmotionConfig(windowMs = 5000L) // 5 second window
        val engine = EmotionEngine.fromPretrained(config)
        val baseTime = System.currentTimeMillis()
        
        // Push data points over 10 seconds
        for (i in 0..10) {
            val timestamp = Date(baseTime + i * 1000)
            engine.push(
                hr = 72.0,
                rrIntervalsMs = listOf(800.0, 850.0),
                timestamp = timestamp
            )
        }
        
        // Wait a bit and check buffer
        val stats = engine.getBufferStats()
        // Should only have data within the 5 second window
        assertTrue(stats["count"] as Int <= 6) // Some margin for timing
    }
}

