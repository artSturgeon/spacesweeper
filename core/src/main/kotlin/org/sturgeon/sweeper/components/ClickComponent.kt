package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component


class ClickComponent(f: () -> Unit) : Component {
    var func = f
}