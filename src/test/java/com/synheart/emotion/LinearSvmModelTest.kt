package com.synheart.emotion

import org.junit.Assert.*
import org.junit.Test

class LinearSvmModelTest {

    @Test
    fun `createDefault creates valid model`() {
        val model = LinearSvmModel.createDefault()
        
        assertEquals("wesad_emotion_v1_0", model.modelId)
        assertEquals("1.0", model.version)
        assertEquals(3, model.labels.size)
        assertTrue(model.labels.contains("Amused"))
        assertTrue(model.labels.contains("Calm"))
        assertTrue(model.labels.contains("Stressed"))
        assertEquals(3, model.featureNames.size)
        assertTrue(model.featureNames.contains("hr_mean"))
        assertTrue(model.featureNames.contains("sdnn"))
        assertTrue(model.featureNames.contains("rmssd"))
    }

    @Test
    fun `createDefault has correct dimensions`() {
        val model = LinearSvmModel.createDefault()
        
        assertEquals(3, model.weights.size) // 3 classes
        assertEquals(3, model.biases.size) // 3 classes
        assertEquals(3, model.weights[0].size) // 3 features
        assertEquals(3, model.mu.size)
        assertEquals(3, model.sigma.size)
    }

    @Test
    fun `validate returns true for default model`() {
        val model = LinearSvmModel.createDefault()
        assertTrue(model.validate())
    }

    @Test
    fun `predict returns probabilities for valid features`() {
        val model = LinearSvmModel.createDefault()
        val features = mapOf(
            "hr_mean" to 72.0,
            "sdnn" to 45.0,
            "rmssd" to 32.0
        )
        
        val probabilities = model.predict(features)
        
        assertEquals(3, probabilities.size)
        assertTrue(probabilities.containsKey("Amused"))
        assertTrue(probabilities.containsKey("Calm"))
        assertTrue(probabilities.containsKey("Stressed"))
        
        // Probabilities should sum to ~1.0
        val sum = probabilities.values.sum()
        assertEquals(1.0, sum, 0.01)
        
        // All probabilities should be between 0 and 1
        probabilities.values.forEach { prob ->
            assertTrue(prob >= 0.0)
            assertTrue(prob <= 1.0)
        }
    }

    @Test
    fun `predict throws BadInput for missing features`() {
        val model = LinearSvmModel.createDefault()
        val features = mapOf("hr_mean" to 72.0) // Missing sdnn and rmssd
        
        try {
            model.predict(features)
            fail("Should have thrown EmotionError.BadInput")
        } catch (e: EmotionError.BadInput) {
            // Expected
        }
    }

    @Test
    fun `predict throws BadInput for NaN features`() {
        val model = LinearSvmModel.createDefault()
        val features = mapOf(
            "hr_mean" to Double.NaN,
            "sdnn" to 45.0,
            "rmssd" to 32.0
        )
        
        try {
            model.predict(features)
            fail("Should have thrown EmotionError.BadInput")
        } catch (e: EmotionError.BadInput) {
            // Expected
        }
    }

    @Test
    fun `getMetadata returns complete information`() {
        val model = LinearSvmModel.createDefault()
        val metadata = model.getMetadata()
        
        assertEquals("wesad_emotion_v1_0", metadata["id"])
        assertEquals("1.0", metadata["version"])
        assertEquals("embedded", metadata["type"])
        assertEquals(3, metadata["num_classes"])
        assertEquals(3, metadata["num_features"])
        assertTrue(metadata.containsKey("labels"))
        assertTrue(metadata.containsKey("feature_names"))
    }

    @Test
    fun `model initialization validates dimensions`() {
        try {
            LinearSvmModel(
                modelId = "test",
                version = "1.0",
                labels = listOf("A", "B"),
                featureNames = listOf("f1", "f2"),
                weights = listOf(listOf(1.0, 2.0)), // Only 1 class, but 2 labels
                biases = listOf(0.0),
                mu = emptyMap(),
                sigma = emptyMap()
            )
            fail("Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
}

