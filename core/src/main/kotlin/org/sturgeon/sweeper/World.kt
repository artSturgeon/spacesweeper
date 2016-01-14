package org.sturgeon.sweeper

import org.sturgeon.sweeper.entities.Station

class World {
    companion object {
        var score = 0
        var lastScore = 0
        var highScore = 0

        var astronauts = 3

        var firing_speed = 1.50f
        var firing_speed_dec = 0.25f
        var guns = 1

        var objectChance = 90
        var objectDecrement = 5

        var level = 1

        val ASTEROID_DAMAGE = 10
        var asteroidSpeedMin = -25f
        var asteroidSpeedMax = -100f

        var asteroidSpeedY = 5f

        val STATION_HEALTH = 10
        val LIFELINE_LENGTH = 400f

        lateinit var station: Station

        fun increaseFiringSpeed() {
            firing_speed -= firing_speed_dec

            if (firing_speed == 0.5f) {
                guns ++
                firing_speed_dec = 0.125f
                station.upgradeTurret()
            }
            if (firing_speed < 0.125) firing_speed = 0.125f
        }
    }
}
