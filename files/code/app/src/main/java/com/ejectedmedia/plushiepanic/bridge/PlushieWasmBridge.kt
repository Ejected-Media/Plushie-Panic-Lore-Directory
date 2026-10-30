package com.ejectedmedia.plushiepanic.bridge

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data model matching the Go backend GameState structure
data class GameStateDto(
    val player_x: Int,
    val player_y: Int,
    val stuffing_pct: Int,
    val seam_status: String,
    val message: String
)

data class GameActionDto(
    val action_type: String,
    val direction: String? = null,
    val entity_id: String
)

class PlushieWasmBridge(
    private val webView: WebView
) {
    private val gson = Gson()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _gameState = MutableStateFlow(
        GameStateDto(2, 2, 80, "STABLE", "Bridge initialized. Waiting for Go/WASM engine...")
    )
    val gameState: StateFlow<GameStateDto> = _gameState.asStateFlow()

    /**
     * Dispatches an action from Kotlin Jetpack Compose into the Go WebAssembly runtime via WebView JS evaluation.
     */
    fun dispatchAction(actionType: String, entityId: String, direction: String? = null) {
        val actionPayload = GameActionDto(
            action_type = actionType,
            direction = direction,
            entity_id = entityId
        )
        val jsonString = gson.toJson(actionPayload)

        coroutineScope.launch {
            // Evaluates the global function registered by the Go/WASM binary
            val script = "window.handlePlushieAction('$jsonString');"
            webView.evaluateJavascript(script) { resultJson ->
                // resultJson from evaluateJavascript is typically quoted as a JSON string
                val cleanedJson = unquoteJsonResult(resultJson)
                if (cleanedJson.startsWith("{")) {
                    try {
                        val newState = gson.fromJson(cleanedJson, GameStateDto::class.java)
                        _gameState.value = newState
                    } catch (e: Exception) {
                        // Fallback handling if JSON parsing fails
                    }
                }
            }
        }
    }

    /**
     * Optional incoming bridge callback if the Go WebAssembly module needs to push data back to Kotlin directly.
     */
    @JavascriptInterface
    fun postStateFromWasm(stateJson: String) {
        try {
            val newState = gson.fromJson(stateJson, GameStateDto::class.java)
            coroutineScope.launch {
                _gameState.value = newState
            }
        } catch (e: Exception) {
            // Handle parse errors
        }
    }

    private fun unquoteJsonResult(raw: String?): String {
        if (raw == null) return ""
        return if (raw.startsWith("\"") && raw.endsWith("\"")) {
            raw.substring(1, raw.length - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
        } else {
            raw
        }
    }
}
