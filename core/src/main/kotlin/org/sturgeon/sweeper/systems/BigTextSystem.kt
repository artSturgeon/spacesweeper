package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.BoundsCheckComponent
import org.sturgeon.sweeper.components.MovementComponent
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.TextComponent
import java.util.*

class BigTextSystem : EntitySystem() {

    private var queue = ArrayList<String>()
    private var showing = false
    //private var current = Entity()

    override fun update(deltaTime: Float) {
        if (!showing) {
            if (queue.size > 0) {
                var msg = queue.get(0)
                showBigText(msg)
                queue.remove(msg)
            }
        }
    }

    public fun addBigText(msg: String) {
        if (showing) {
            speedUpCurrent()
        }
        queue.add(msg)
    }

    private fun speedUpCurrent() {
        var texts = engine.getEntitiesFor(Family.all(TextComponent::class.java, MovementComponent::class.java).get())
        if (texts != null) {
            for (text in texts) {
                text.getComponent(MovementComponent::class.java).velocityX *= 2
            }
        }
    }

    private fun showBigText(msg: String) {
        showing = true
        engine.addEntity(Entity().apply {
            add(PositionComponent(Assets.VIEWPORT_WIDTH, 500f))
            add(MovementComponent(-850f, 0f))

            add(TextComponent(msg).apply {
                scale = 16.0f
                colour = Color(223 / 255f, 113 / 255f, 38 / 255f, 0.75f)
                front = false
            })
            var width = engine.getSystem(RenderingSystem::class.java).getTextWidth(msg, 16f)
            add(BoundsCheckComponent(width, { showing = false }))
        })
    }
}

