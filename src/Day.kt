import io.github.cdimascio.dotenv.Dotenv
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

abstract class Day<T,V>(
    private val expectedTestPart1: T,
    private val expectedPart1: T,
    private val expectedTestPart2: T,
    private val expectedPart2: T,
) {
    private val year = 2024

    private val dayString = javaClass.name
    private val dayNumber = dayString.replace("Day","").toInt()

    private val testInputFile = getFile("${dayString}_test.txt")
    private val testInputFilePart2 = getFile("${dayString}_test_part2.txt")
    private val inputFile = getFile("${dayString}.txt")

    private val testInput by lazy {
        if (!testInputFile.exists()) {
            testInputFile.writeText("")
        }
        testInputFile.readText()
    }
    private val testInputPart2 by lazy {
        testInputFilePart2.readTextOrNull() ?: testInput
    }
    private val input by lazy {
        if (!inputFile.exists()) {
            loadAndWriteInputFile()
        }
        inputFile.readText()
    }

    abstract fun parseInput(input: String): V
    abstract fun part1(input: V): T
    abstract fun part2(input: V): T

    fun run() {
        println(dayString)
        println("Part 1 - Test")
        assertEquals(expectedTestPart1, part1(parseInput(testInput)))
        println("Part 1")
        assertEquals(expectedPart1, part1(parseInput(input)))
        println("Part 2 - Test")
        assertEquals(expectedTestPart2, part2(parseInput(testInputPart2)))
        println("Part 2")
        assertEquals(expectedPart2, part2(parseInput(input)))
    }

    private fun getFile(name: String) = File("resources", name)

    private fun loadAndWriteInputFile() {
        val sessionCookieValue = Dotenv.load().get("AOC_SESSION_COOKIE_VALUE") ?: error("could not load input")
        OkHttpClient()
            .newCall(
                Request.Builder()
                    .url("https://adventofcode.com/$year/day/$dayNumber/input")
                    .header("Cookie", "session=$sessionCookieValue")
                    .build()
            )
            .execute()
            .body!!
            .string()
            .also { inputFile.writeText(it) }
    }
}