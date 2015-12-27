package org.sturgeon.sweeper

import com.badlogic.gdx.Gdx


object Assets {
    val VIEWPORT_WIDTH = 1000f
    val VIEWPORT_HEIGHT = 640f

    val PLAYER = Gdx.files.internal("player1.png")
    val STATION = Gdx.files.internal("station3.png")
    val TURRET = Gdx.files.internal("turret1.png")
    val BULLET = Gdx.files.internal("bullet1.png")
    val ASTEROID = Gdx.files.internal("asteroid1.png")
    val WORLD = Gdx.files.internal("world1.png")

    val ASTEROID_SEGMENT_1 = Gdx.files.internal("asteroid_segment1.png")
    val ASTEROID_SEGMENT_2 = Gdx.files.internal("asteroid_segment2.png")
    val ASTEROID_SEGMENT_3 = Gdx.files.internal("asteroid_segment3.png")

    var TURRET_ANIMATION = Gdx.files.internal("turret_firing2.png")

    val LOGO = Gdx.files.internal("logo1.png")
    var STAR = Gdx.files.internal("star1.png")

    val LASER = Gdx.files.internal("laser1.mp3")

}