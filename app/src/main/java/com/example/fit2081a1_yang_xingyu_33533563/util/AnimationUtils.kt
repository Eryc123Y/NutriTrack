package com.example.fit2081a1_yang_xingyu_33533563.util

import android.view.Choreographer
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Utility class with animation optimizations for better performance and smoother transitions
 */
object AnimationUtils {
    
    /**
     * Spring animation spec optimized for transitions with reduced jank
     */
    val smoothSpringSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    /**
     * Tween animation spec optimized for page transitions
     */
    fun smoothTweenSpec(durationMs: Int = 400) = tween<Float>(
        durationMillis = durationMs,
        delayMillis = 0
    )
    
    /**
     * Ensures animations run at the optimal time in the render pipeline by synchronizing
     * with the next frame through Choreographer
     */
    @Composable
    fun SyncAnimationsWithFrames() {
        val view = LocalView.current
        
        DisposableEffect(view) {
            // Ensure hardware acceleration is enabled for smoother animations
            view.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            
            onDispose {
                // Reset layer type when no longer needed
                view.setLayerType(android.view.View.LAYER_TYPE_NONE, null)
            }
        }
    }
    
    /**
     * Waits for the next frame before continuing execution to ensure animations
     * start at the beginning of a frame for maximum smoothness
     */
    suspend fun waitForNextFrame() = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            // Create a proper FrameCallback implementation
            val callback = object : Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    continuation.resume(Unit)
                }
            }
            
            // Register the callback
            Choreographer.getInstance().postFrameCallback(callback)
            
            // Handle cancellation by removing the callback
            continuation.invokeOnCancellation {
                Choreographer.getInstance().removeFrameCallback(callback)
            }
        }
    }
    
    /**
     * Measures the frame time to detect jank in animations
     * Returns frame time metrics that can be used to adapt animation complexity
     */
    @Composable
    fun MeasureFrameTime(): FrameTimeMetrics {
        var avgFrameTimeMs by remember { mutableStateOf(16.67f) } // Target 60fps
        var frameTimeMs by remember { mutableStateOf(16.67f) }
        var jankDetected by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            val frameCallback = object : Choreographer.FrameCallback {
                private var lastFrameTimeNanos = 0L
                private var frameCount = 0
                private var totalFrameTimeMs = 0.0f
                
                override fun doFrame(frameTimeNanos: Long) {
                    if (lastFrameTimeNanos != 0L) {
                        val elapsedMs = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000f
                        frameTimeMs = elapsedMs
                        
                        totalFrameTimeMs += elapsedMs
                        frameCount++
                        
                        if (frameCount == 30) { // Calculate average after 30 frames
                            avgFrameTimeMs = totalFrameTimeMs / frameCount
                            totalFrameTimeMs = 0.0f
                            frameCount = 0
                        }
                        
                        // Detect jank (frames taking longer than 30ms - implies less than 33fps)
                        jankDetected = elapsedMs > 30.0f
                    }
                    
                    lastFrameTimeNanos = frameTimeNanos
                    Choreographer.getInstance().postFrameCallback(this)
                }
            }
            
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
        
        return remember(avgFrameTimeMs, frameTimeMs, jankDetected) {
            FrameTimeMetrics(avgFrameTimeMs, frameTimeMs, jankDetected)
        }
    }
    
    /**
     * Data class to hold frame time metrics
     */
    data class FrameTimeMetrics(
        val averageFrameTimeMs: Float,
        val currentFrameTimeMs: Float,
        val isJankDetected: Boolean
    ) {
        /**
         * Returns true if the device is able to handle complex animations
         */
        val canHandleComplexAnimations: Boolean get() = averageFrameTimeMs < 20.0f && !isJankDetected
    }
} 