package org.secuso.pfacore.ui.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import org.secuso.pfacore.model.tutorial.Gravity
import org.secuso.pfacore.model.tutorial.ImageLayoutInfos
import org.secuso.pfacore.ui.Inflatable
import org.secuso.ui.view.databinding.TutorialStageBinding
import kotlin.math.min
import org.secuso.pfacore.model.tutorial.Tutorial as MTutorial
import org.secuso.pfacore.model.tutorial.TutorialStage as MTutorialStage

class TutorialStage(
    title: String,
    images: ImageLayoutInfos,
    description: String?,
    requirements: () -> Boolean,
    val content: ((TutorialStage) -> Inflatable)? = null,
): MTutorialStage(title, images, description, requirements), Inflatable {
    override fun inflate(inflater: LayoutInflater, root: ViewGroup, owner: LifecycleOwner): View {
        return content?.invoke(this)?.inflate(inflater, root, owner) ?: run {
            val binding = TutorialStageBinding.inflate(inflater, root, false)
            binding.title = this.title
            binding.desc = this.description
            if (images is ImageLayoutInfos.Multiple) {
                val imgs = images as ImageLayoutInfos.Multiple
                binding.images.columnCount = min(imgs.columns, imgs.images.size)
                for ((image, gravity) in imgs.images) {
                    val img = ImageView(root.context)
                    img.setImageResource(image)
                    img.layoutParams = GridLayout.LayoutParams().apply { setGravity(gravity.gravity) }
                    binding.images.addView(img)
                }
            } else if (images is ImageLayoutInfos.Single) {
                binding.images.addView(ImageView(root.context).apply {
                    setImageResource((images as ImageLayoutInfos.Single).image)
                })
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

        fun single(id: Int) = ImageLayoutInfos.Single(id)

        fun multiple(vararg ids: Int, initializer: ImageLayoutInfos.Multiple.() -> Unit = {}): ImageLayoutInfos.Multiple {
            val params = ids.map { it to Gravity.CENTER }.toTypedArray()
            return ImageLayoutInfos.Multiple().apply { images = params.toList() }.apply(initializer)
        }
        fun multiple(vararg params: Pair<Int, Gravity>, initializer: ImageLayoutInfos.Multiple.() -> Unit) =
            ImageLayoutInfos.Multiple().apply{ images = params.toList() }.apply(initializer)
    }
}

typealias Tutorial = MTutorial<TutorialStage>
fun buildTutorial(initializer: MTutorial.Builder<TutorialStage, TutorialStage.Builder>.() -> Unit): MTutorial<TutorialStage> {
    return MTutorial.build({ TutorialStage.Builder() }, initializer)
}
