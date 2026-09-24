package com.sagar.matchmate.core.mvi

/**
 * Marker interface for single-event side effects (e.g. Navigation, Toast, Snackbar) in MVI pattern.
 * Marker interface for one-time UI effects.
 *
 * Effects are events that should happen once rather than
 * become part of the persistent screen state.
 *
 * Examples:
 *
 * - Show Snackbar
 * - Navigate
 * - Show Toast
 * - Open another screen
 */
interface UiEffect
