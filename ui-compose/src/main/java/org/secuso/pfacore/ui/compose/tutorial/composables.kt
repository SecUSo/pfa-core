package org.secuso.pfacore.ui.compose.tutorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import kotlinx.coroutines.launch
import org.secuso.pfacore.R
import org.secuso.pfacore.ui.compose.theme.PrivacyFriendlyCoreTheme
import org.secuso.pfacore.ui.compose.theme.secusoAccent
import org.secuso.pfacore.ui.compose.theme.secusoDotListActive
import org.secuso.pfacore.ui.compose.theme.secusoDotListInActive
import kotlin.math.min

@Composable
fun TutorialStageComp(title: String, images: List<Int>, description: String?) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = title, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge, color = Color.White)
        if (images.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(min(images.size, 2)),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(images) {
                    Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(it), contentDescription = "")
                    }
                }
            }
        }
        if (description != null) {
            Text(text = description, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialComp(tutorial: Tutorial) {
    val pagerState = rememberPagerState(pageCount = { tutorial.stages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)) {
        var stage: TutorialStage? = null
        val forwardButtonEnabled = remember {
            derivedStateOf { stage?.let { it.requirements() } ?: true }
        }
        HorizontalPager(state = pagerState, modifier = Modifier
            .background(MaterialTheme.colorScheme.secusoAccent)
            .weight(1f)) {
            stage = tutorial.stages[it]
            stage!!.Display { }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.secusoDotListActive)
        ConstraintLayout(
            Modifier
                .height(52.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secusoAccent)
        ) {
            val (skipRef, pagesRef, nextRef) = createRefs()
            Button(
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.secusoAccent, contentColor = Color.White),
                onClick = {
                    tutorial.onFinish()
                },
                modifier = Modifier.constrainAs(skipRef) {
                    visibility = if (pagerState.canScrollForward) Visibility.Visible else Visibility.Invisible
                    start.linkTo(parent.start)
                }
            ) { Text(text = "Skip") }
            Box(
                Modifier.constrainAs(pagesRef) {
                    horizontalChainWeight = 1f
                    centerTo(parent)
                    start.linkTo(skipRef.end)
                    end.linkTo(nextRef.start)
                }) {
                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.secusoDotListActive else MaterialTheme.colorScheme.secusoDotListInActive
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
            Button(
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secusoAccent,
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.constrainAs(nextRef) {
                    end.linkTo(parent.end)
                },
                enabled = forwardButtonEnabled.value,
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.canScrollForward) {
                            pagerState.scrollToPage(pagerState.currentPage + 1)
                        } else {
                            tutorial.onFinish()
                        }
                    }
                }
            ) {
                Text(text = if (pagerState.canScrollForward) "Next" else "Finish")
            }
        }
    }
}

@Composable
@Preview
fun PrevTutorialComp() {
    val tutorial = buildTutorial {
        stage {
            title = "Test Stage 1"
            description = "Test Description 1"
            images = listOf()
        }
        stage {
            title = "Test Stage 2"
            images = listOf(R.drawable.privacyfriendlyappslogo, R.drawable.secuso_logo_blau_blau)
        }
        stage {
            title = "Test Stage 3"
            description = "This is a longer description to test if everything is displayed as expected"
            images = listOf(R.drawable.privacyfriendlyappslogo)
            content = { Text("This should be the only thing displayed") }
        }
        stage {
            title = "Test Stage 4 -- Correct"
            description = "This is a longer description to test if everything is displayed as expected"
            images = listOf(R.drawable.privacyfriendlyappslogo)
        }
    }
    PrivacyFriendlyCoreTheme {
        TutorialComp(tutorial)
    }
}