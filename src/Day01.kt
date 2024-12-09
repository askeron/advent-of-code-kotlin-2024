import kotlin.math.absoluteValue

class Day01 : Day<Int,List<String>>(11, 1765812, 0, 0) {
    override fun parseInput(input: String): List<String> = input.lines().filter { it.isNotEmpty() }

    override fun part1(input: List<String>): Int {
        val lists = (0..1).toList().map { i -> input.map { it.split("   ")[i].toInt() } }
        return lists.map { it.sorted() }
            .toPair()
            .let { it.first zip it.second }
            .sumOf { (it.first - it.second).absoluteValue }
    }

    override fun part2(input: List<String>): Int {
        return -1
    }
}
