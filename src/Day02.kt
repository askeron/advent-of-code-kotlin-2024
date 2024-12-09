import kotlin.math.absoluteValue

class Day02 : Day<Int,List<List<Int>>>(2, 246, 4, 318) {
    override fun parseInput(input: String): List<List<Int>> = input.lines().filter { it.isNotEmpty() }
        .map { it.split(" ").map { it.toInt() } }

    override fun part1(input: List<List<Int>>): Int {
        return input.count { isReportSafe(it) }
    }

    override fun part2(input: List<List<Int>>): Int {
        return input.count { isReportSafe(it) || it.getAllCombinationsWithOneElementRemoved().any { isReportSafe(it) } }
    }

    private fun isReportSafe(report: List<Int>): Boolean {
        val diffs = report.zipWithNext().map { it.second - it.first }
        return (diffs.all { it > 0 } || diffs.all { it < 0 }) && diffs.all { it.absoluteValue in 1..3 }
    }

    fun <T> List<T>.getAllCombinationsWithOneElementRemoved(): List<List<T>> {
        return (indices).map { i -> take(i) + drop(i + 1) }
    }
}
