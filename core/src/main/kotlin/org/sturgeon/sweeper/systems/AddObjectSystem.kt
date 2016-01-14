package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.*

class AddObjectSystem(i: Float): IntervalSystem(i) {

    override fun updateInterval() {
        // random chance of adding an object
        if (MathUtils.random(0, 100) > World.objectChance) return

        var thing = Entity()
        // don't like having to repeat code like this but Kotlin
        //  was giving me grief about uninitialised objects...
        // random chance of which object
        var type = ItemType.values().get(MathUtils.random(0, ItemType.values().size-1))
        when (type) {
            ItemType.STATION_HEALTH -> {
                var t = Texture(Assets.REPAIR_KIT)
                thing.add(PositionComponent(Assets.VIEWPORT_WIDTH + 20, MathUtils.random(0f, Assets.VIEWPORT_HEIGHT - 50),
                        t.width.toFloat(), t.height.toFloat()))
                thing.add(VisualComponent(t))
            }
            ItemType.FIRE_UP -> {
                var t = Texture(Assets.FIRE_UP)
                thing.add(PositionComponent(Assets.VIEWPORT_WIDTH + 20, MathUtils.random(0f, Assets.VIEWPORT_HEIGHT - 50),
                        t.width.toFloat(), t.height.toFloat()))
                thing.add(VisualComponent(t))
            }
        }

        thing.add(MovementComponent(MathUtils.random(-100f, -25f),
                MathUtils.random(-50f, 50f)))
        thing.add(ItemComponent(type))
        thing.add(BoundsCheckComponent())
        thing.add(CollisionComponent())
        engine.addEntity(thing)
    }
}