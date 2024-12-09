import kotlin.math.absoluteValue

class Day02 : Day<Int,List<List<Int>>>(2, 246, 0, 0) {
    override fun parseInput(input: String): List<List<Int>> = input.lines().filter { it.isNotEmpty() }
        .map { it.split(" ").map { it.toInt() } }

    override fun part1(input: List<List<Int>>): Int {
        return input.count { report ->
            val diffs = report.zipWithNext().map { it.second - it.first }
            (diffs.all { it > 0 } || diffs.all { it < 0 }) && diffs.all { it.absoluteValue in 1..3 }
        }
    }

    override fun part2(input: List<List<Int>>): Int {
        return -1
    }
}
