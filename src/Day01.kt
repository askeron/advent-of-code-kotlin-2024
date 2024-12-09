import kotlin.math.absoluteValue

class Day01 : Day<Int,List<String>>(11, 1765812, 31, 20520794) {
    override fun parseInput(input: String): List<String> = input.lines().filter { it.isNotEmpty() }

    private fun getLists(input: List<String>): Pair<List<Int>, List<Int>> = (0..1).toList()
        .map { i -> input.map { it.split("   ")[i].toInt() } }
        .map { it.sorted() }
        .toPair()

    override fun part1(input: List<String>): Int {
        return getLists(input)
            .let { it.first zip it.second }
            .sumOf { (it.first - it.second).absoluteValue }
    }

    override fun part2(input: List<String>): Int {
        val lists = getLists(input)
        val countMap = lists.second.groupingBy { it }.eachCount()
        return lists.first.sumOf { it * (countMap[it] ?: 0) }
    }
}
