package org.sturgeon.sweeper.Accessors

import aurelienribon.tweenengine.TweenAccessor
import org.sturgeon.sweeper.components.VisualComponent

class VisualAccessor: TweenAccessor<VisualComponent> {

    companion object TweenTypes {
        val ALPHA = 1
    }

    override fun getValues(vc: VisualComponent?, type: Int, returns: FloatArray?): Int {
        when (type) {
            ALPHA -> {
                returns!![0] = vc!!.alpha
            }
        }

        return 1
    }

    override fun setValues(vc: VisualComponent?, type: Int, newValues: FloatArray?) {
        when (type) {
            ALPHA -> {
                vc!!.alpha = newValues!![0]
            }
        }
    }
}