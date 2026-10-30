package main

import (
	"encoding/json"
	"syscall/js"
)

// GameAction represents incoming telemetry or touch actions from the Jetpack Compose UI
type GameAction struct {
	ActionType string `json:"action_type"` // e.g., "MOVE", "WHISK_SMASH", "MENDING_STITCH"
	Direction  string `json:"direction,omitempty"` // "UP", "DOWN", "LEFT", "RIGHT"
	EntityID   string `json:"entity_id"`   // "patches", "kiki", "sourdough"
}

// GameState represents the current state of the puzzle room to return to UI
type GameState struct {
	PlayerX        int    `json:"player_x"`
	PlayerY        int    `json:"player_y"`
	StuffingPct    int    `json:"stuffing_pct"`
	SeamStatus     string `json:"seam_status"`
	Message        string `json:"message"`
}

var currentState = GameState{
	PlayerX:     2,
	PlayerY:     2,
	StuffingPct: 80,
	SeamStatus:  "STABLE",
	Message:     "Ready for action.",
}

// HandleUIAction is exposed to the JS/WASM runtime and invoked from Kotlin WebViews
func HandleUIAction(this js.Value, args []js.Value) interface{} {
	if len(args) < 1 {
		return js.ValueOf("Error: No action payload provided")
	}

	jsonStr := args[0].String()
	var action GameAction
	err := json.Unmarshal([]byte(jsonStr), &action)
	if err != nil {
		return js.ValueOf("Error parsing JSON action: " + err.Error())
	}

	// Process action logic based on type
	switch action.ActionType {
	case "MOVE":
		switch action.Direction {
		case "UP":
			currentState.PlayerY--
		case "DOWN":
			currentState.PlayerY++
		case "LEFT":
			currentState.PlayerX--
		case "RIGHT":
			currentState.PlayerX++
		}
		currentState.Message = "Moved " + action.Direction + " to (" + string(rune(currentState.PlayerX+'0')) + ", " + string(rune(currentState.PlayerY+'0')) + ")"

	case "WHISK_SMASH":
		currentState.Message = "Kiki unleashed a Whisk Smash! Sugar walls shattered."

	case "MENDING_STITCH":
		if currentState.StuffingPct < 100 {
			currentState.StuffingPct += 10
			currentState.Message = "Applied Mending Stitch. Stuffing restored to " + string(rune(currentState.StuffingPct)) + "%"
		} else {
			currentState.Message = "Stuffing already at maximum capacity."
		}

	default:
		currentState.Message = "Unknown action received: " + action.ActionType
	}

	// Marshal updated state back to JSON for Kotlin UI consumption
	stateBytes, _ := json.Marshal(currentState)
	return js.ValueOf(string(stateBytes))
}

func main() {
	// Register global function for WebAssembly browser/WebView bridge
	js.Global().Set("handlePlushieAction", js.FuncOf(HandleUIAction))
	
	// Keep WASM module running
	select {}
}
