package org.sturgeon.sweeper.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.entities.Star

class StarfieldSystem(var i:Float) : IntervalSystem(i) {

    override fun updateInterval() {
        var star = Star(Assets.VIEWPORT_WIDTH+100, MathUtils.random(0f, Assets.VIEWPORT_HEIGHT))
        engine.addEntity(star)
    }
}