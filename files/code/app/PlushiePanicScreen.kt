package com.ejectedmedia.plushiepanic.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Ejected Media Theme Colors ---
val IndustrialMoss = Color(0xFF101612)
val CopperOxidation = Color(0xFF4A6B6C)
val FairyFlossGlitch = Color(0xFFFFB3C6)
val SoftVelvetDark = Color(0xFF191D1B)
val StuffedGreen = Color(0xFF52B788)
val WarningRed = Color(0xFFE05353)

@Composable
fun PlushiePanicScreen(
    onMoveAction: (String) -> Unit,
    onSpecialAction: (String) -> Unit
) {
    var stuffingLevel by remember { mutableStateOf(0.80f) }
    var seamStatus by remember { mutableStateOf("STABLE") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialMoss)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP BAR ---
            TopBarSection()

            // --- GAME VIEWPORT SIMULATOR ---
            ViewportSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )

            // --- STATUS & TELEMETRY HUD ---
            TelemetryHudSection(
                stuffingLevel = stuffingLevel,
                seamStatus = seamStatus
            )

            // --- ACTION DECK (CONTROLS) ---
            ActionDeckSection(
                onMoveAction = onMoveAction,
                onSpecialAction = onSpecialAction
            )
        }
    }
}

@Composable
fun TopBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SoftVelvetDark)
            .border(1.dp, CopperOxidation, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "[≡ MENU]",
            color = FairyFlossGlitch,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = "PLUSHIE PANIC v1.4",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = "[⚙ STATS]",
            color = CopperOxidation,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ViewportSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1210))
            .border(1.5.dp, CopperOxidation.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "── 8x8 GRID VIEWPORT ──",
                color = CopperOxidation,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "[P] ──> [B] (Sled)\n      [#] (Sugar Wall)\n      [D] (Dust Bunny)",
                color = FairyFlossGlitch.copy(alpha = 0.9f),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun TelemetryHudSection(stuffingLevel: Float, seamStatus: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SoftVelvetDark)
            .border(1.dp, CopperOxidation, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "STUFFING: ${(stuffingLevel * 100).toInt()}%",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            Text(
                text = "SEAM: [$seamStatus]",
                color = StuffedGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { stuffingLevel },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = StuffedGreen,
            trackColor = IndustrialMoss,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "ACTIVE UNIT: Patches (Fluff Guard)",
            color = FairyFlossGlitch,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ActionDeckSection(
    onMoveAction: (String) -> Unit,
    onSpecialAction: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- D-PAD / DIRECTIONAL CONTROLS ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.size(110.dp)
        ) {
            ActionButton(text = "▲", modifier = Modifier.size(36.dp)) { onMoveAction("UP") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(text = "◀", modifier = Modifier.size(36.dp)) { onMoveAction("LEFT") }
                ActionButton(text = "▶", modifier = Modifier.size(36.dp)) { onMoveAction("RIGHT") }
            }
            ActionButton(text = "▼", modifier = Modifier.size(36.dp)) { onMoveAction("DOWN") }
        }

        // --- SPECIAL ACTION DECK (FLOATING ACTION BUTTONS) ---
        Column(
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SpecialActionButton(text = "WHISK-SMASH") { onSpecialAction("WHISK_SMASH") }
            SpecialActionButton(text = "MENDING STITCH") { onSpecialAction("MEND_STITCH") }
        }
    }
}

@Composable
fun ActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CopperOxidation,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SpecialActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SoftVelvetDark,
            contentColor = FairyFlossGlitch
        ),
        border = BorderStroke(1.dp, FairyFlossGlitch.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
```modifiers