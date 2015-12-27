package org.sturgeon.sweeper.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.BoundsCheckComponent
import org.sturgeon.sweeper.components.MovementComponent
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.VisualComponent


class Star(x: Float, y: Float) : Entity() {
    init {
        var t = Texture(Assets.STAR)

        var pc = PositionComponent(x, y, t.width.toFloat(), t.height.toFloat())

        var scale = MathUtils.random(0.1f, 1.2f)
        pc.scaleX = scale
        pc.scaleY = scale

        add(pc)

        add(VisualComponent(t, 0))
        add(MovementComponent(MathUtils.random(-300f, -100f), 0f))
        add(BoundsCheckComponent())
    }
}