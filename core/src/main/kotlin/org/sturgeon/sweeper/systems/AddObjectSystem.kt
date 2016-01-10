package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.*

class AddObjectSystem(i: Float): IntervalSystem(i) {

    override fun updateInterval() {
        // Add a useful object?

        var thing = Entity()
        var t = Texture(Assets.REPAIR_KIT)
        thing.add(PositionComponent(Assets.VIEWPORT_WIDTH + 20, MathUtils.random(0f, Assets.VIEWPORT_HEIGHT - 50),
                t.width.toFloat(), t.height.toFloat()))
        thing.add(VisualComponent(t))
        thing.add(MovementComponent(MathUtils.random(-100f, -25f),
                MathUtils.random(-50f, 50f)))
        thing.add(ItemComponent(ItemType.STATION_HEALTH))
        thing.add(BoundsCheckComponent())
        thing.add(CollisionComponent())
        engine.addEntity(thing)
    }
}