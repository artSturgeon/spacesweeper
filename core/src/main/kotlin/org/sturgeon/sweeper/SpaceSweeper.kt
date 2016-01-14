package org.sturgeon.sweeper

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap

class SpaceSweeper : Game() {

    public val engine: Engine by lazy { Engine() }

    override fun create() {
        val pm = Pixmap(Gdx.files.internal("cursor1.png"))
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, pm.width/2, pm.height/2))
        pm.dispose()
        setScreen(PlayScreen(this))
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.render()
    }
}
