package org.sturgeon.sweeper.Accessors

import aurelienribon.tweenengine.TweenAccessor
import org.sturgeon.sweeper.components.PositionComponent

class PositionAccessor: TweenAccessor<PositionComponent> {
    companion object TweenTypes {
        val POSITION = 1
        val SCALE = 2
        val WIDTH = 3
        val ANGLE = 4
    }

    override fun getValues(pc: PositionComponent?, type: Int, returns: FloatArray?): Int {
        var count = 2
        when(type) {
            POSITION -> {
                returns!![0] = pc!!.x
                returns[1] = pc!!.y
            }
            SCALE -> {
                returns!![0] = pc!!.scaleX
                returns!![1] = pc!!.scaleY
            }
            WIDTH -> {
                returns!![0] = pc!!.width
                count = 1
            }
            ANGLE -> {
                returns!![0] = pc!!.angle
                count = 1
            }
        }

        return count
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
            WIDTH -> {
                pc!!.width = newValues!![0]
            }
            ANGLE -> {
                pc!!.angle = newValues!![0]
            }
        }
    }
}