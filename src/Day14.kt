class Day14 : Day<Int,List<Day14.Robot>>(12, 217328832, -1, 7412) {
    override fun parseInput(input: String): List<Robot> = input.lines().filter { it.isNotEmpty() }
        .map {
            val (position, velocity) = it.split(" ").map {
                val (x, y) = it.split("=")[1].split(",")
                Point(x.toInt(), y.toInt())
            }
            Robot(position, velocity)
        }

    override fun part1(robots: List<Robot>): Int {
        val state = State(robots)
        println("initial state:")
        state.println()
        for (i in 1..100) {
            robots.forEach { it.move(state) }
            println("state after $i seconds:")
            state.println()
        }
        return state.quadrants.map { quadrant -> robots.count { quadrant.contains(it.position) } }
            .reduce(Int::times)
    }

    override fun part2(robots: List<Robot>): Int {
        val state = State(robots)
        if (state.testRun) return -1
        println("initial state:")
        state.println()
        for (i in 1..10000000) {
            robots.forEach { it.move(state) }
            println("state after $i seconds:")
            state.println()

            // // first try assuming all robot would make a non filled christmas tree
            // val linesWithCount = robots.map { it.position }.distinct().groupingBy { it.y }.eachCount()
            // if (linesWithCount.entries.count { it.key > 4 } <= 6) return i

            // after knowing the picture
            val positions = robots.map { it.position }
            val robotsWithNeighboursCount = positions.count { it.neighboursNotDiagonal.any { it in positions } }
            if (robotsWithNeighboursCount > robots.size / 2) return i
        }
        error("no solution found")
    }

    class Robot(position: Point, private val velocity: Point) {
        var position: Point = position
            private set

        fun move(state: State) {
            position += velocity
            if (position.x < 0) position += Point(state.roomSize.x, 0)
            if (position.y < 0) position += Point(0, state.roomSize.y)
            if (position.x > state.roomSize.x - 1) position -= Point(state.roomSize.x, 0)
            if (position.y > state.roomSize.y - 1) position -= Point(0, state.roomSize.y)
        }
    }

    data class State(val robots: List<Robot>) {
        val testRun = robots.map { it.position.x }.all { it in 0..11 }
        val roomSize = if (testRun) Point(11, 7) else Point(101, 103)
        val roomMaxPoint = roomSize - Point(1, 1)
        val roomMiddlePoint = Point(roomMaxPoint.x / 2, roomMaxPoint.y / 2)
        val quadrants = listOf(
            Point(0, 0),
            Point(1, 0),
            Point(0, 1),
            Point(1, 1),
        )
            .map { it * (roomMiddlePoint + Point(1, 1)) }
            .map { Quadrant(Point(0, 0) + it, roomMiddlePoint - Point(1, 1) + it) }

        fun println() {
            val positions = robots.map { it.position }
            val positionsWithCount = positions.groupingBy { it }.eachCount()
            val matrix = matrixString(maxPoint = roomMaxPoint) { x, y -> positionsWithCount[Point(x, y)]?.digitToChar() ?: '.' }
            println(matrix)
        }
    }

    data class Quadrant(val minPoint: Point, val maxPoint: Point) {
        fun contains(point: Point): Boolean {
            return point.x in minPoint.x..maxPoint.x && point.y in minPoint.y..maxPoint.y
        }
    }

}
