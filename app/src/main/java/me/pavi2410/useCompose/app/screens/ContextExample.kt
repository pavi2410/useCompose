package me.pavi2410.useCompose.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.pavi2410.useCompose.react.createContext
import me.pavi2410.useCompose.react.useContext

@Suppress("LocalVariableName")
@Composable
fun ContextExample() {
    Column {
        val ColorContext = createContext(Color.Red)

        val outerColor = useContext(ColorContext)
        Box(
            modifier = Modifier
                .size(128.dp)
                .background(outerColor),
            contentAlignment = Alignment.Center
        ) {
            ColorContext.Provider(value = Color.Blue) {
                val innerColor = useContext(ColorContext)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(innerColor)
                )
            }
        }
    }
}
