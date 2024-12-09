import kotlin.math.absoluteValue

class Day02 : Day<Int,List<Report>>(2, 246, 4, 318) {
    override fun parseInput(input: String): List<Report> = input.lines().filter { it.isNotEmpty() }
        .map { it.split(" ").map { it.toInt() } }

    override fun part1(input: List<Report>): Int {
        return input.count { it.isSafe() }
    }

    override fun part2(input: List<Report>): Int {
        return input.count { it.isSafe() || it.getAllCombinationsWithOneElementRemoved().any { it.isSafe() } }
    }

    private fun Report.isSafe(): Boolean {
        val diffs = this.zipWithNext().map { it.second - it.first }
        return (diffs.all { it > 0 } || diffs.all { it < 0 }) && diffs.all { it.absoluteValue in 1..3 }
    }

    private fun <T> List<T>.getAllCombinationsWithOneElementRemoved(): List<List<T>> {
        return (indices).map { i -> take(i) + drop(i + 1) }
    }
}

private typealias Report = List<Int>
