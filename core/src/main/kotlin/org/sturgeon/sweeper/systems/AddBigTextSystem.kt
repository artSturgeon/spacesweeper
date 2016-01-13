package org.sturgeon.sweeper.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.systems.BigTextSystem


class AddBigTextSystem(var i:Float) : IntervalSystem(i) {
    override fun updateInterval() {
        // Actually this probably belongs in another system...
        if (MathUtils.random(1,100) < 25) {
            var msg = Assets.SOME_TEXT.get(MathUtils.random(0, Assets.SOME_TEXT.size - 1))
            engine.getSystem(BigTextSystem::class.java).addBigText(msg)
        }
    }


}