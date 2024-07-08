import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import idle_game.composeapp.generated.resources.Backrgou
import idle_game.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel


@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}


@Composable
@Preview
fun Screen() {
    Scaffold(
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }
            var showDialog by remember { mutableStateOf(false) }


            // Das hier ist mein Background Image
            Image(
                painterResource(Res.drawable.Backrgou),
                contentDescription = "A square",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().fillMaxHeight().fillMaxSize()
            )


            // Hier in der Column zeige ich meine Sachen an
            Column(


                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(13, 105, 16), contentColor = Color.White)
                ) {
                    Column() {
                        Text("Help")
                   
                    }


                }



             if (showDialog) {
                 minimalDialog {
                     showDialog = false
                 }
             }






                Text(
                    "Data Thieves", color = Color.Green,
                    style = MaterialTheme.typography.h1, fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                )
                {
                    Text("Reset Data", fontStyle = FontStyle.Italic)

                }

                gameState?.let { state ->
                    Text(
                        "Data Bank: ${currentMoney?.toHumanReadableString()} Data",
                        style = MaterialTheme.typography.h4, color = Color.Green, fontStyle = FontStyle.Italic
                    )
                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray, contentColor = Color.White),
                        modifier = Modifier.offset(x = 600.dp, y = 0.dp)
                    )

                    {
                        Text(
                            "Collect Data",
                            modifier = Modifier.offset(
                                x = 5.dp, y = 50.dp
                            ).width(100.dp).height(100.dp),

                        )
                    }

                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                        )
                    }
                }
            }
        }
    )
}

fun Button(onClick: () -> Unit, colors: ButtonColors, contentColor: Color) {

}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(8.dp)
            .background(Color(102, 153, 153), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(gameJob.name)
            Text("Level: ${gameJob.level.level}")
            Text("Cost: ${gameJob.level.cost.toHumanReadableString()} Data")
            Text("Earns: ${gameJob.level.earn.toHumanReadableString()} Data")
            Text("Duration: ${gameJob.level.duration.inWholeSeconds} Seconds")
        }
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0, 255, 0), contentColor = Color.White)
            ) {
                Text("Purchase")
            }
        } else {
            Text("Purchased")
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0, 255, 0),
                contentColor = Color.White
            )
        ) {
            Text("Upgrade")
        }
    }
}


@Composable
fun minimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "Collect Data to level up your Automatic Data sources.",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}