package com.example.fit2081a1_yang_xingyu_33533563.view.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * This file contains custom page transition effects for HorizontalPager
 * Designed by AI
 */

/**
 * Enum class defining different page transition effects
 */
enum class PageTransitionEffect {
    SLIDE,
    ZOOM,
    DEPTH,
    STACK,
    FADE,
    CUBE,
    NONE
}

/**
 * Creates a modifier with a custom page transition effect for HorizontalPager items
 *
 * @param page The page index
 * @param pagerState The current state of the pager
 * @param effect The type of transition effect to apply
 * @return A Modifier with the appropriate transformation applied
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pageTransition(
    page: Int,
    pagerState: PagerState,
    effect: PageTransitionEffect = PageTransitionEffect.SLIDE
): Modifier {
    return when (effect) {
        PageTransitionEffect.ZOOM -> this.then(zoomOutPageTransition(page, pagerState))
        PageTransitionEffect.DEPTH -> this.then(depthPageTransition(page, pagerState))
        PageTransitionEffect.STACK -> this.then(stackPageTransition(page, pagerState))
        PageTransitionEffect.FADE -> this.then(fadePageTransition(page, pagerState))
        PageTransitionEffect.CUBE -> this.then(cubePageTransition(page, pagerState))
        PageTransitionEffect.SLIDE -> this.then(slidePageTransition(page, pagerState))
        PageTransitionEffect.NONE -> this
    }
}

/**
 * Helper function to create a smoother fraction for animations
 */
@OptIn(ExperimentalFoundationApi::class)
private fun calculateSmoothFraction(page: Int, pagerState: PagerState): Float {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
    val absPageOffset = pageOffset.absoluteValue
    
    // Apply a slight easing to the offset for smoother transitions
    return FastOutSlowInEasing.transform(1f - min(absPageOffset, 1f))
}

/**
 * Creates a standard slide transition for pager items
 */
@OptIn(ExperimentalFoundationApi::class)
private fun slidePageTransition(
    page: Int,
    pagerState: PagerState
): Modifier = Modifier.graphicsLayer {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
    val absPageOffset = pageOffset.absoluteValue
    val smoothFraction = calculateSmoothFraction(page, pagerState)
    
    // Apply a smooth alpha transition
    alpha = 0.5f + (0.5f * smoothFraction)
    
    // Apply a directional slide effect with slight parallax
    translationX = size.width * 0.2f * if (pageOffset > 0) absPageOffset else -absPageOffset
    
    // Add subtle scaling for better depth feel
    val scale = 0.9f + (0.1f * smoothFraction)
    scaleX = scale
    scaleY = scale
}

/**
 * Creates a zoom out transition for pager items
 */
@OptIn(ExperimentalFoundationApi::class)
private fun zoomOutPageTransition(
    page: Int,
    pagerState: PagerState
): Modifier = Modifier.graphicsLayer {
    val smoothFraction = calculateSmoothFraction(page, pagerState)
    
    // Enhanced scaling effect
    val scale = 0.8f + (0.2f * smoothFraction)
    scaleX = scale
    scaleY = scale
    
    // Improved fade effect
    alpha = 0.4f + (0.6f * smoothFraction)
    
    // Add a subtle rotation for more visual interest
    rotationZ = (1f - smoothFraction) * 2f * if ((pagerState.currentPage - page) > 0) 1f else -1f
}

/**
 * Creates a depth transition effect where pages appear to move in z-space
 */
@OptIn(ExperimentalFoundationApi::class)
private fun depthPageTransition(
    page: Int,
    pagerState: PagerState
): Modifier = Modifier.graphicsLayer {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
    pageOffset.absoluteValue
    val smoothFraction = calculateSmoothFraction(page, pagerState)
    
    // Enhanced scale with improved interpolation
    val scale = 0.85f + (0.15f * smoothFraction)
    scaleX = scale
    scaleY = scale
    
    // Reduced shadow elevation for smoother transitions
    shadowElevation = 8f * smoothFraction
    
    // Better alpha transition
    alpha = 0.5f + (0.5f * smoothFraction)
    
    // Reduce translation for less jumpy effect
    translationY = 10f * (1f - smoothFraction)
}

/**
 * Creates a stack transition where pages appear to stack on top of each other
 */
@OptIn(ExperimentalFoundationApi::class)
private fun stackPageTransition(
    page: Int,
    pagerState: PagerState
): Modifier = Modifier.graphicsLayer {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
    
    if (pageOffset > 0) {
        // Pages to the right (forward) slide in from right with a smooth curve
        val fraction = FastOutSlowInEasing.transform(1f - min(pageOffset, 1f))
        translationX = size.width * (1f - fraction)
        alpha = fraction
        scaleX = 0.9f + (0.1f * fraction)
        scaleY = 0.9f + (0.1f * fraction)
    } else if (pageOffset < 0) {
        // Pages to the left (backward) moved aside with improved scaling
        val absOffset = pageOffset.absoluteValue
        val fraction = FastOutSlowInEasing.transform(min(absOffset, 1f))
        translationX = -size.width * 0.15f * fraction
        scaleX = 0.95f - (0.05f * fraction)
        scaleY = 0.95f - (0.05f * fraction)
        alpha = 1f - (fraction * 0.4f)
    }
}

/**
 * Creates a fade transition for pager items
 */
@OptIn(ExperimentalFoundationApi::class)
private fun fadePageTransition(
    page: Int,
    pagerState: PagerState
): Modifier = Modifier.graphicsLayer {
    val smoothFraction = calculateSmoothFraction(page, pagerState)
    
    // Improved fade effect with better interpolation
    alpha = 0.3f + (0.7f * smoothFraction)
    
    // Enhanced scale effect - more subtle
    val scale = 0.96f + (0.04f * smoothFraction)
    scaleX = scale
    scaleY = scale
    
    // Reduced vertical translation for smoother effect
    translationY = 15f * (1f - smoothFraction)
}

/**
 * Creates a cube rotation transition effect
 */
@OptIn(ExperimentalFoundationApi::class)
private fun cubePageTransition(
    page: Int,
    pagerState: PagerState
): Modifier = Modifier.graphicsLayer {
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
    val absPageOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)
    
    // Enhanced cube rotation with better interpolation
    rotationY = pageOffset * -60f // Reduced rotation angle for smoother effect
    
    // Improved 3D effect
    cameraDistance = 12f * density
    
    // Better translation for the cube sides
    if (pageOffset > 0) {
        // Moving forward (right)
        translationX = size.width * absPageOffset * 0.85f
    } else if (pageOffset < 0) {
        // Moving backward (left)
        translationX = -size.width * absPageOffset * 0.85f
    }
    
    // Improved alpha for better visibility during rotation
    alpha = lerp(
        start = 0.6f,
        stop = 1f,
        fraction = 1f - absPageOffset.coerceIn(0f, 0.5f) * 2f
    )
}

/**
 * Custom animation specs for pager scrolling with improved physics
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberCustomPagerFlingBehavior(
    pagerState: PagerState
): FlingBehavior {
    // Use spring-based animations for smoother, more natural scrolling
    return PagerDefaults.flingBehavior(
        state = pagerState,
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
}