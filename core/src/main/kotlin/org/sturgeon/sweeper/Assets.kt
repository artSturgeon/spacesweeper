package org.sturgeon.sweeper

import com.badlogic.gdx.Gdx


object Assets {
    val VIEWPORT_WIDTH = 1000f
    val VIEWPORT_HEIGHT = 640f

    val PLAYER = Gdx.files.internal("player1.png")
    val STATION = Gdx.files.internal("station3.png")
    val TURRET = Gdx.files.internal("turret1.png")
    val BULLET = Gdx.files.internal("bullet1.png")
    val ASTEROID = Gdx.files.internal("asteroid2.png")
    val WORLD = Gdx.files.internal("world2.png")
    val SOLAR_PANEL = Gdx.files.internal("solar_panel1.png")

    val FONT_FILE = Gdx.files.internal("orbitron.fnt")
    val FONT_IMAGE = Gdx.files.internal("orbitron.png")

    val ASTEROID_SEGMENT_1 = Gdx.files.internal("asteroid_segment1.png")
    val ASTEROID_SEGMENT_2 = Gdx.files.internal("asteroid_segment2.png")
    val ASTEROID_SEGMENT_3 = Gdx.files.internal("asteroid_segment3.png")

    var TURRET_ANIMATION = Gdx.files.internal("turret_firing4.png")

    val LOGO = Gdx.files.internal("logo2.png")
    var STAR = Gdx.files.internal("star1.png")

    val SND_LASER = Gdx.files.internal("laser1.mp3")
    val SND_EXPLOSION = Gdx.files.internal("explosion1.mp3")

    val PART_ALL = Gdx.files.internal("part_all.part")
    val PART_ASTEROID = Gdx.files.internal("part_asteroid.part")

    val SOME_TEXT = arrayOf("Get to the exit!",
                            "Get to the chopper ! !",
                            "Clear the space",
                            "Shoot the asteroids",
                            "Clear a path",
                            "Keep Going !",
                            "Don't Panic ! !")

}