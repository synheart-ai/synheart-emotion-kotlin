package com.synheart.emotion

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class EmotionResultTest {

    @Test
    fun `fromInference creates result with top emotion`() {
        val timestamp = Date()
        val probabilities = mapOf(
            "Amused" to 0.5,
            "Calm" to 0.3,
            "Stressed" to 0.2
        )
        val features = mapOf(
            "hr_mean" to 72.0,
            "sdnn" to 45.0,
            "rmssd" to 32.0
        )
        val model = mapOf("id" to "test_model", "version" to "1.0")
        
        val result = EmotionResult.fromInference(timestamp, probabilities, features, model)
        
        assertEquals(timestamp, result.timestamp)
        assertEquals("Amused", result.emotion)
        assertEquals(0.5, result.confidence, 0.01)
        assertEquals(probabilities, result.probabilities)
        assertEquals(features, result.features)
        assertEquals(model, result.model)
    }

    @Test
    fun `fromInference handles empty probabilities`() {
        val timestamp = Date()
        val probabilities = emptyMap<String, Double>()
        val features = emptyMap<String, Double>()
        val model = emptyMap<String, Any>()
        
        val result = EmotionResult.fromInference(timestamp, probabilities, features, model)
        
        assertEquals("", result.emotion)
        assertEquals(0.0, result.confidence, 0.01)
    }

    @Test
    fun `toString includes emotion and confidence`() {
        val timestamp = Date()
        val probabilities = mapOf("Amused" to 0.75, "Calm" to 0.25)
        val features = mapOf("hr_mean" to 72.0, "sdnn" to 45.0, "rmssd" to 32.0)
        val model = mapOf("id" to "test")
        
        val result = EmotionResult.fromInference(timestamp, probabilities, features, model)
        val str = result.toString()
        
        assertTrue(str.contains("Amused"))
        assertTrue(str.contains("75.0%") || str.contains("75%"))
        assertTrue(str.contains("features"))
    }
}

