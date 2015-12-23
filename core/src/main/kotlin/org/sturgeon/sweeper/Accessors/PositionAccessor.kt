package org.sturgeon.sweeper.Accessors

import aurelienribon.tweenengine.TweenAccessor
import org.sturgeon.sweeper.components.PositionComponent

class PositionAccessor: TweenAccessor<PositionComponent> {
    companion object TweenTypes {
        val POSITION = 1
        val SCALE = 2
    }

    override fun getValues(pc: PositionComponent?, type: Int, returns: FloatArray?): Int {
        when(type) {
            POSITION -> {
                returns!![0] = pc!!.x
                returns[1] = pc!!.y
                        }
            SCALE -> {
                returns!![0] = pc!!.scaleX
                returns!![1] = pc!!.scaleY
            }

        }

        return 2 // two values in array
    }

    override fun setValues(pc: PositionComponent?, type: Int, newValues: FloatArray?) {
        when (type) {
            POSITION -> {
                pc!!.x = newValues!![0]
                pc!!.y = newValues!![1]
            }
            SCALE -> {
                pc!!.scaleX = newValues!![0]
                pc!!.scaleY = newValues!![1]
            }
        }
    }
}