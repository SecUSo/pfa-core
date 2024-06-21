package org.secuso.pfacore.ui.compose.tutorial

import androidx.compose.runtime.Composable
import org.secuso.pfacore.model.tutorial.Tutorial as MTutorial
import org.secuso.pfacore.ui.compose.Displayable
import org.secuso.pfacore.model.tutorial.TutorialStage as MTutorialStage

class TutorialStage(
    title: String,
    images: List<Int>,
    description: String?,
    requirements: () -> Boolean,
    val content: @Composable (TutorialStage) -> Unit = {
        TutorialStageComp(it.title, it.images, it.description)
    }
): MTutorialStage(title, images, description, requirements), Displayable {
    @Composable
    override fun Display(onClick: (() -> Unit)?) = content(this)

    class Builder: MTutorialStage.Builder<TutorialStage>() {
        var content: (@Composable (TutorialStage) -> Unit)? = null
        override fun build(): TutorialStage {
            return if (content != null) {
                TutorialStage(super.title, super.images, super.description, super.requirements, content!!)
            } else {
                TutorialStage(super.title, super.images, super.description, super.requirements)
            }
        }
    }
}

typealias Tutorial = MTutorial<TutorialStage>
fun buildTutorial(initializer: MTutorial.Builder<TutorialStage, TutorialStage.Builder>.() -> Unit): MTutorial<TutorialStage> {
    return MTutorial.build({ TutorialStage.Builder() }, initializer)
}

//class Tutorial(
//    stages: List<TutorialStage>,
//): MTutorial<TutorialStage>(stages), Displayable {
//    @Composable
//    override fun Display(onClick: (() -> Unit)?) {
//        TutorialComp(this, onFinish)
//    }
//
//    companion object {
//        fun build(initializer: Builder<TutorialStage, TutorialStage.Builder>.() -> Unit): Tutorial {
//            return Tutorial(build({ TutorialStage.Builder() }, initializer).stages)
//        }
//    }
//}