package io.github.brainage04.togglesprint.waypoint

/**
 * A user-created waypoint in one dimension of one world. Serialised with Gson; every parameter
 * has a default so Gson uses the no-arg constructor and missing fields keep these defaults.
 */
class Waypoint(
    var name: String = "",
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    /** The dimension's ID: 0 is the Overworld, -1 the Nether and 1 the End. */
    var dimension: Int = 0,
    /** 0xRRGGBB. */
    var colour: Int = 0xFFFFFF,
    var visible: Boolean = true,
) {
    fun coordinates(): String = "$x, $y, $z"
}
