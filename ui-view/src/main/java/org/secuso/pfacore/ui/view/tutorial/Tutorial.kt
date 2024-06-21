package org.secuso.pfacore.ui.view.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import org.secuso.pfacore.ui.view.Inflatable
import org.secuso.ui.view.databinding.TutorialStageBinding
import kotlin.math.min
import org.secuso.pfacore.model.tutorial.Tutorial as MTutorial
import org.secuso.pfacore.model.tutorial.TutorialStage as MTutorialStage

class TutorialStage(
    title: String,
    images: List<Int>,
    description: String?,
    requirements: () -> Boolean,
    val content: ((TutorialStage) -> Inflatable)? = null
): MTutorialStage(title, images, description, requirements), Inflatable {
    override fun inflate(inflater: LayoutInflater, root: ViewGroup, owner: LifecycleOwner): View {
        return content?.invoke(this)?.inflate(inflater, root, owner) ?: run {
            val binding = TutorialStageBinding.inflate(inflater, root, false)
            binding.title = this.title
            binding.desc = this.description
            if (images.isNotEmpty()) {
                binding.images.columnCount = min(2, images.size)
                for (image in images) {
                    val img = ImageView(root.context)
                    img.setImageResource(image)
                    binding.images.addView(img)
                }
            }
            binding.root
        }
    }

    class Builder: MTutorialStage.Builder<TutorialStage>() {
        var content: ((TutorialStage) -> Inflatable)? = null
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
