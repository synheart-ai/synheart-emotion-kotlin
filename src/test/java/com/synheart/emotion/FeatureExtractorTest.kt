package com.synheart.emotion

import org.junit.Assert.*
import org.junit.Test

class FeatureExtractorTest {

    @Test
    fun `extractHrMean calculates correct mean`() {
        val hrValues = listOf(70.0, 72.0, 75.0, 73.0)
        val mean = FeatureExtractor.extractHrMean(hrValues)
        
        assertEquals(72.5, mean, 0.01)
    }

    @Test
    fun `extractHrMean returns zero for empty list`() {
        val mean = FeatureExtractor.extractHrMean(emptyList())
        assertEquals(0.0, mean, 0.01)
    }

    @Test
    fun `extractSdnn calculates standard deviation`() {
        // Simple test with known values
        val rrIntervals = listOf(800.0, 850.0, 820.0, 830.0, 840.0)
        val sdnn = FeatureExtractor.extractSdnn(rrIntervals)
        
        assertTrue(sdnn > 0)
        assertTrue(sdnn < 100) // Reasonable range for SDNN
    }

    @Test
    fun `extractSdnn returns zero for insufficient data`() {
        val sdnn1 = FeatureExtractor.extractSdnn(emptyList())
        val sdnn2 = FeatureExtractor.extractSdnn(listOf(800.0))
        
        assertEquals(0.0, sdnn1, 0.01)
        assertEquals(0.0, sdnn2, 0.01)
    }

    @Test
    fun `extractRmssd calculates root mean square`() {
        val rrIntervals = listOf(800.0, 850.0, 820.0, 830.0, 840.0)
        val rmssd = FeatureExtractor.extractRmssd(rrIntervals)
        
        assertTrue(rmssd > 0)
        assertTrue(rmssd < 100) // Reasonable range for RMSSD
    }

    @Test
    fun `extractRmssd returns zero for insufficient data`() {
        val rmssd1 = FeatureExtractor.extractRmssd(emptyList())
        val rmssd2 = FeatureExtractor.extractRmssd(listOf(800.0))
        
        assertEquals(0.0, rmssd1, 0.01)
        assertEquals(0.0, rmssd2, 0.01)
    }

    @Test
    fun `extractFeatures returns all required features`() {
        val hrValues = listOf(70.0, 72.0, 75.0)
        val rrIntervals = listOf(800.0, 850.0, 820.0, 830.0)
        
        val features = FeatureExtractor.extractFeatures(hrValues, rrIntervals)
        
        assertTrue(features.containsKey("hr_mean"))
        assertTrue(features.containsKey("sdnn"))
        assertTrue(features.containsKey("rmssd"))
        assertTrue(features["hr_mean"]!! > 0)
        assertTrue(features["sdnn"]!! >= 0)
        assertTrue(features["rmssd"]!! >= 0)
    }

    @Test
    fun `extractFeatures includes motion data when provided`() {
        val hrValues = listOf(70.0)
        val rrIntervals = listOf(800.0, 850.0)
        val motion = mapOf("accel_x" to 0.5, "accel_y" to 0.3)
        
        val features = FeatureExtractor.extractFeatures(hrValues, rrIntervals, motion)
        
        assertEquals(0.5, features["accel_x"], 0.01)
        assertEquals(0.3, features["accel_y"], 0.01)
    }

    @Test
    fun `cleanRrIntervals removes outliers`() {
        // Include some invalid values
        val rrIntervals = listOf(
            200.0,  // Too low (below MIN_VALID_RR_MS)
            800.0,  // Valid
            850.0,  // Valid
            2500.0, // Too high (above MAX_VALID_RR_MS)
            820.0   // Valid
        )
        
        val cleaned = FeatureExtractor.cleanRrIntervals(rrIntervals)
        
        assertEquals(3, cleaned.size)
        assertTrue(cleaned.all { it >= FeatureExtractor.MIN_VALID_RR_MS })
        assertTrue(cleaned.all { it <= FeatureExtractor.MAX_VALID_RR_MS })
    }

    @Test
    fun `cleanRrIntervals removes large jumps`() {
        val rrIntervals = listOf(
            800.0,  // Valid
            850.0,  // Valid (50ms jump)
            1200.0, // Large jump (>250ms) - should be removed
            820.0   // Valid
        )
        
        val cleaned = FeatureExtractor.cleanRrIntervals(rrIntervals)
        
        // Should have 3 values (800, 850, 820) - 1200 removed due to large jump
        assertTrue(cleaned.size >= 2)
        assertFalse(cleaned.contains(1200.0))
    }

    @Test
    fun `validateFeatures returns true for valid features`() {
        val features = mapOf(
            "hr_mean" to 72.0,
            "sdnn" to 45.0,
            "rmssd" to 32.0
        )
        val required = listOf("hr_mean", "sdnn", "rmssd")
        
        assertTrue(FeatureExtractor.validateFeatures(features, required))
    }

    @Test
    fun `validateFeatures returns false for missing features`() {
        val features = mapOf("hr_mean" to 72.0)
        val required = listOf("hr_mean", "sdnn", "rmssd")
        
        assertFalse(FeatureExtractor.validateFeatures(features, required))
    }

    @Test
    fun `validateFeatures returns false for NaN values`() {
        val features = mapOf(
            "hr_mean" to Double.NaN,
            "sdnn" to 45.0,
            "rmssd" to 32.0
        )
        val required = listOf("hr_mean", "sdnn", "rmssd")
        
        assertFalse(FeatureExtractor.validateFeatures(features, required))
    }

    @Test
    fun `normalizeFeatures applies z-score normalization`() {
        val features = mapOf("hr_mean" to 72.0)
        val mu = mapOf("hr_mean" to 70.0)
        val sigma = mapOf("hr_mean" to 10.0)
        
        val normalized = FeatureExtractor.normalizeFeatures(features, mu, sigma)
        
        // (72 - 70) / 10 = 0.2
        assertEquals(0.2, normalized["hr_mean"]!!, 0.01)
    }

    @Test
    fun `normalizeFeatures handles zero sigma`() {
        val features = mapOf("hr_mean" to 72.0)
        val mu = mapOf("hr_mean" to 70.0)
        val sigma = mapOf("hr_mean" to 0.0)
        
        val normalized = FeatureExtractor.normalizeFeatures(features, mu, sigma)
        
        // Should return 0.0 when sigma is zero
        assertEquals(0.0, normalized["hr_mean"]!!, 0.01)
    }
}

