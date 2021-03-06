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
    val ASTRONAUT = Gdx.files.internal("astronaut1.png")
    val REPAIR_KIT = Gdx.files.internal("repair_kit2.png")
    val FIRE_UP = Gdx.files.internal("fire_up2.png")
    val RECALL_BUTTON = Gdx.files.internal("recall_btn2.png")

    val FONT_FILE = Gdx.files.internal("orbitron.fnt")
    val FONT_IMAGE = Gdx.files.internal("orbitron.png")

    val ASTEROID_SEGMENT_1 = Gdx.files.internal("asteroid_segment1.png")
    val ASTEROID_SEGMENT_2 = Gdx.files.internal("asteroid_segment2.png")
    val ASTEROID_SEGMENT_3 = Gdx.files.internal("asteroid_segment3.png")

    var TURRET_ANIMATION_DUAL = Gdx.files.internal("turret_firing4.png")
    var TURRET_ANIMATION_SINGLE = Gdx.files.internal("turret_firing_single.png")
    val ASTRO_ANIMATION_MOVING = Gdx.files.internal("astronaut_moving2.png")

    val LOGO = Gdx.files.internal("logo2.png")
    var STAR = Gdx.files.internal("star1.png")

    val SND_LASER = Gdx.files.internal("laser2.mp3")
    val SND_EXPLOSION = Gdx.files.internal("explosion1.mp3")
    val SND_POWERUP = Gdx.files.internal("powerup1.mp3")
    val SND_JET = Gdx.files.internal("jet1.mp3")
    val SND_SNAP = Gdx.files.internal("snap1.mp3")
    val SND_RECALL = Gdx.files.internal("recall.mp3")

    val PART_ALL = Gdx.files.internal("part_all.part")
    val PART_ASTEROID = Gdx.files.internal("part_asteroid.part")

    val CURSOR = Gdx.files.internal("cursor1.png")

    val SOME_TEXT = arrayOf("Get to the exit!",
                            "Get to the chopper ! !",
                            "Clear the space",
                            "Shoot the asteroids",
                            "Clear a path",
                            "Keep Going !",
                            "Don't Panic ! !",
                            "You can do it !")

}