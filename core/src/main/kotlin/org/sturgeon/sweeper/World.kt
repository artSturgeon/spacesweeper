package org.sturgeon.sweeper

import org.sturgeon.sweeper.entities.Station

class World {
    companion object {
        var score = 0
        var lastScore = 0
        var highScore = 0

        var astronauts = 1
        var astronautHealth = 100

        var firing_speed = 1.50f
        var firing_speed_dec = 0.25f
        var guns = 1

        var objectChance = 90
        var objectDecrement = 5

        var objectSpeedMin = -25f
        var objectSpeedMax = -100f
        var objectSpeedY = 25f


        var level = 1

        var asteroidSpeedMin = -25f
        var asteroidSpeedMax = -100f

        var asteroidSpeedY = 5f

        val ASTEROID_INTERVAL = 5f
        val ASTEROID_DAMAGE = 10
        val STATION_HEALTH = 100
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

        fun resetWorld() {
            astronauts = 1
            astronautHealth = 100
            asteroidSpeedMin = -25f
            asteroidSpeedMax = -100f
            asteroidSpeedY = 5f
            firing_speed = 1.5f
            objectChance = 90
            objectDecrement = 5
            objectSpeedMin = -25f
            objectSpeedMax = -100f
            objectSpeedY = 25f
            guns = 1
            level = 1
        }
    }
}
