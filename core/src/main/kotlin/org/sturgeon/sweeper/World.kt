package org.sturgeon.sweeper

import org.sturgeon.sweeper.entities.Station

class World {
    companion object {
        var score = 0
        var lastScore = 0
        var highScore = 0
        var astronauts = 3
        var firing_speed = 1
        var level = 1

        val TURRENT_HEALTH = 100
        val ASTEROID_DAMAGE = 10
        val STATION_HEALTH = 10
        val LIFELINE_LENGTH = 420f
        lateinit var station: Station
    }

    //var astronaut: Entity

}
