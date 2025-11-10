package com.synheart.emotion

import org.junit.Assert.*
import org.junit.Test

class EmotionConfigTest {

    @Test
    fun `default config has correct values`() {
        val config = EmotionConfig()
        
        assertEquals("svm_linear_wrist_sdnn_v1_0", config.modelId)
        assertEquals(60000L, config.windowMs)
        assertEquals(5000L, config.stepMs)
        assertEquals(30, config.minRrCount)
        assertTrue(config.returnAllProbas)
        assertNull(config.hrBaseline)
        assertNull(config.priors)
    }

    @Test
    fun `custom config preserves values`() {
        val priors = mapOf("Amused" to 0.3, "Calm" to 0.4, "Stressed" to 0.3)
        val config = EmotionConfig(
            modelId = "custom_model",
            windowMs = 30000L,
            stepMs = 10000L,
            minRrCount = 50,
            returnAllProbas = false,
            hrBaseline = 70.0,
            priors = priors
        )
        
        assertEquals("custom_model", config.modelId)
        assertEquals(30000L, config.windowMs)
        assertEquals(10000L, config.stepMs)
        assertEquals(50, config.minRrCount)
        assertFalse(config.returnAllProbas)
        assertEquals(70.0, config.hrBaseline, 0.01)
        assertEquals(priors, config.priors)
    }

    @Test
    fun `toString includes key values`() {
        val config = EmotionConfig()
        val str = config.toString()
        
        assertTrue(str.contains("svm_linear_wrist_sdnn_v1_0"))
        assertTrue(str.contains("60s"))
        assertTrue(str.contains("5s"))
        assertTrue(str.contains("30"))
    }
}

