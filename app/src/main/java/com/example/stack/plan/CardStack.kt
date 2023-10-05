package com.example.stack.plan

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.example.stack.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardStack(
    swipeResults: MutableMap<String,Int>,
    modifier: Modifier = Modifier,
    items: MutableList<Item>,
    styles: MutableList<String>,
    thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f) },
    velocityThreshold: Dp = 125.dp,
    onSwipeLeft: (item: Item, swipeResult: Map<String,Int>) -> Unit = { _, _ -> },
    onSwipeRight: (item: Item) -> Unit = {},
    onEmptyStack: (lastItem: Item) -> Unit = {}
) {
    var i by remember {
        mutableStateOf(items.size - 1)
    }

    if (i == -1) {
        onEmptyStack(items.last())
    }

    val cardStackController = rememberCardStackController()

    cardStackController.onSwipeLeft = {
        onSwipeLeft(items[i], swipeResults)
        Log.i("tinder","i = $i")
        i--
    }

    cardStackController.onSwipeRight = {
        onSwipeRight(items[i])
        swipeResults[styles[items.size - 1 - i]] = swipeResults[styles[items.size - 1 - i]]!!.plus(1)
        Log.i("tinder","i = $i")
        i--
    }


    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        val stack = createRef()
        val title = createRef() // Adjust the guideline position as needed

        // Title Composable
        Text(
            text = "What are your fitness goals？",

            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }.padding(20.dp).wrapContentHeight(),
            color = Color.Black,
            fontSize = 30.sp,
            fontFamily= FontFamily(Font(R.font.rubika_medium2)),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = modifier
                .constrainAs(stack) {
                    top.linkTo(title.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .draggableStack(
                    controller = cardStackController,
                    thresholdConfig = thresholdConfig,
                    velocityThreshold = velocityThreshold
                )
                .fillMaxWidth()

        ) {
            items.asReversed().forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.moveTo(
                        x = if (index == i) cardStackController.offsetX.value else 0f,
                        y = if (index == i) cardStackController.offsetY.value else 0f
                    )
                        .fillMaxHeight()
                        .visible(visible = index == i || index == i -1)
                        .graphicsLayer(
                            rotationZ = if (index == i) cardStackController.rotation.value else 0f,
                            scaleX = if (index < i) cardStackController.scale.value else 1f,
                            scaleY = if (index < i) cardStackController.scale.value else 1f
                        ),
                    item,
                    cardStackController
                )
            }
        }
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    item: Item,
    cardStackController: CardStackController
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f) // Allow the Box to take remaining space
                .fillMaxWidth()
        ) {
            if (item.url != null) {
                Image(
                    painter = painterResource(id = item.url),
                    contentDescription = "My Image Description",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()

                )
//                AsyncImage(
//                    model = item.url,
//                    contentDescription = "",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = item.text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )

                Text(text = item.subText, color = Color.White, fontSize = 20.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Height wraps the content
                .padding(10.dp)
        ) {
            IconButton(
                modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp),
                onClick = { cardStackController.swipeLeft() },
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "",
                    tint = peachPink,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                modifier = Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp),
                onClick = { cardStackController.swipeRight() }
            ) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "",
                    tint = green10,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}
data class Item(
    val url: Int? = null,
    val text: String = "",
    val subText: String = ""
)

fun Modifier.moveTo(
    x: Float,
    y: Float
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x.roundToInt(), y.roundToInt())
    }
})

fun Modifier.visible(
    visible: Boolean = true
) = this.then(Modifier.layout{ measurable, constraints ->
    val placeable = measurable.measure(constraints)

    if (visible) {
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    } else {
        layout(0, 0) {}
    }
})


@Composable
fun YourComposeLayout(
    title: String,
    image: String,
    onButton1Click: () -> Unit,
    onButton2Click: () -> Unit
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(8.dp)
            )

            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .clip(shape = MaterialTheme.shapes.medium)
//                .background(MaterialTheme.colorScheme.primary)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onButton1Click,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(text = "Button 1")
                }

                Button(
                    onClick = onButton2Click,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(text = "Button 2")
                }
            }
        }
    }
}