import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun <T> assertEquals(expected: T, actual: T) {
    check(expected == actual) { "expected $expected but found $actual" }
}

// helpers from 2021 START

fun <T> List<T>.allDistinct() = this.size == this.distinct().size

fun <T : Comparable<T>> List<T>.isSorted() = this == this.sorted()

fun <T> List<T>.singleValue(): T {
    check(size == 1) { "list has not 1 but $size items" }
    return get(0)
}

fun <T> List<T>.toPair(): Pair<T, T> {
    check(size == 2) { "list has not 2 but $size items" }
    return get(0) to get(1)
}

infix fun <A, B, C> Pair<A, B>.toTriple(that: C): Triple<A, B, C> = Triple(first, second, that)

fun <T> T.transform(times: Int, transform: (T) -> T): T {
    var result = this
    repeat(times) {
        result = transform.invoke(result)
    }
    return result
}

fun <T> List<T>.transformUntilNoChange(transform: (List<T>) -> List<T>) : List<T> =
    transform.invoke(this).let {
        if (it == this) {
            it
        } else {
            it.transformUntilNoChange(transform)
        }
    }

fun List<String>.IntMatrixToPointMap() = charMatrixToPointMap().map { (a, b) -> a to b.digitToInt() }.toMap()

fun List<String>.charMatrixToPointMap() = flatMapIndexed { y, s ->
    s.toCharArray().mapIndexed { x, c -> Point(x,y) to c }
}.toMap()

fun Int.gaussSum() = (this * (this + 1)) / 2

fun <T> List<T>.getAllDistinctCombinations(): List<List<T>> {
    var result = this.map { listOf(it) }
    repeat(this.size - 1) {
        result = result.zipInList(this).filter { it.allDistinct() }
    }
    return result
}

fun <T> List<List<T>>.zipInList(list: List<T>): List<List<T>> = this.flatMap { x -> list.map { x.plus(it) } }

fun List<Int>.toIntByDigits(): Int {
    assert(all { it in 0..9 })
    var result = 0
    forEach {
        result = result * 10 + it
    }
    return result
}

fun <T> List<T>.nonUnique() = this.groupingBy { it }.eachCount().filter { it.value > 1 }

fun <T> MutableList<T>.removeLast(n: Int): List<T> = mutableListOf<T>()
    .also { repeat(n) { _ -> it += removeLast() } }.toList().reversed()

fun <T> List<List<T>>.turnMatrix(): List<List<T>> = (0 until this[0].size).map { i -> this.map { it[i] } }

fun <K, V> Map<K, V>.inverted() = entries.associate{(k,v)-> v to k}

fun Iterable<Point>.mirror() = map { Point(it.y, it.x) }.toList()

fun Iterable<Point>.maxPoint(): Point = Point(maxOf { it.x }, maxOf { it.y })

fun Iterable<Point>.matrixString(pointChar: Char, noPointChar: Char): String =
    matrixString(maxPoint = maxPoint()) { x, y -> if (contains(Point(x, y))) pointChar else noPointChar }

fun matrixString(maxPoint: Point, charFunction: (Int, Int) -> Char): String {
    return (0..maxPoint.y).joinToString("\n") { y ->
        (0..maxPoint.x).joinToString("") { x ->
            charFunction.invoke(x, y).toString()
        }
    }
}

fun Map<Point, Int>.matrixString() = matrixString(maxPoint = keys.maxPoint()) { x, y -> this[Point(x,y)]!!.digitToChar()}

data class Point(val x: Int, val y: Int) {
    operator fun unaryMinus() = Point(-x, -y)
    operator fun plus(b: Point) = Point(x + b.x, y + b.y)
    operator fun minus(b: Point) = this + (-b)
    operator fun times(b: Point) = Point(x * b.x, y * b.y)
    val values = listOf(x, y)

    val neighboursNotDiagonal by lazy { listOf(
        Point(-1,0),
        Point(1,0),
        Point(0,-1),
        Point(0,1),
    ).map { this + it } }

    val neighboursWithItself by lazy { listOf(
        Point(-1,-1),
        Point(-1,0),
        Point(-1,1),
        Point(0,-1),
        Point(0,0),
        Point(0,1),
        Point(1,-1),
        Point(1,0),
        Point(1,1),
    ).map { this + it } }

    val neighbours by lazy { neighboursWithItself.filterNot { it == this } }

    val sign by lazy { Point(x.sign, y.sign) }

    companion object {
        val LEFT = Point(-1,0)
        val RIGHT = Point(1,0)
        val UP = Point(0,-1)
        val DOWN = Point(0,1)
    }
}

// helpers from 2021 END

// from 2021 Day15
fun <T> shortestPathByDijkstra(
    edgesWithCosts: Set<Triple<T, T, Int>>,
    start: T,
    end: T,
): Pair<List<T>, Int>? {
    val costsMap = mutableMapOf<T, Int>()
    val previousNodeMap = mutableMapOf<T, T>()
    val edgesMap = edgesWithCosts.groupBy({ it.first }) { it.second to it.third }
    val queue = LinkedList<T>()
    val processed = mutableSetOf<T>()

    costsMap[start] = 0
    queue += start

    while (queue.isNotEmpty()) {
        val from = queue.minByOrNull { costsMap[it] ?: Int.MAX_VALUE }!!
        queue.remove(from)
        processed += from
        val fromCosts = costsMap[from] ?: Int.MAX_VALUE
        edgesMap[from]?.forEach { edge ->
            val to = edge.first
            val toCosts = fromCosts + edge.second
            if ((costsMap[to] ?: Int.MAX_VALUE) > toCosts) {
                costsMap[to] = toCosts
                previousNodeMap[to] = from
            }
            if (to !in processed && to !in queue) {
                queue += to
            }
        }
    }

    val reversedPath = mutableListOf(end)
    while (reversedPath.last() != start) {
        reversedPath += previousNodeMap[reversedPath.last()] ?: return null
    }
    return reversedPath.toList().reversed() to costsMap[end]!!
}

// copied and then modified from takeWhile()
inline fun <T> Iterable<T>.takeWhilePlusOne(predicate: (T) -> Boolean): List<T> {
    val list = ArrayList<T>()
    for (item in this) {
        list.add(item)
        if (!predicate(item))
            break
    }
    return list
}

fun <T> List<T>.splitInHalf(): Pair<List<T>, List<T>> {
    check(size % 2 == 0) { "not an even size" }
    return take(size / 2) to drop(size / 2)
}

fun <T> Iterable<T>.split(seperator: T): List<List<T>> = split { it == seperator }

fun <T> Iterable<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val itemsLeft = this.toMutableList()
    if (itemsLeft.isEmpty()) return emptyList()
    val result = mutableListOf<List<T>>()
    val currentSegment = mutableListOf<T>()
    while (itemsLeft.isNotEmpty()) {
        val item = itemsLeft.removeAt(0)
        if (predicate.invoke(item)) {
            result += currentSegment.toList()
            currentSegment.clear()
        } else {
            currentSegment += item
        }
    }
    result += currentSegment.toList()
    return result.toList()
}

// helpers from 2018 START

fun <T> List<T>.timesAllElements(count: Int): List<T> {
    return (1..count).flatMap { this }
}

// from https://stackoverflow.com/questions/48007311/how-do-i-infinitely-repeat-a-sequence-in-kotlin
fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

// helpers from 2018 END

// helpers from 2023 START

fun File.readTextOrNull(charset: Charset = Charsets.UTF_8): String? = takeIf { exists() }?.readText(charset)

fun <T> T?.println(): T? = also { println(it) }

fun <T> doWithRetries(
    timesToTry: Int,
    waitBetweenTriesInMillis: Long = 0,
    supplier: () -> T
): T {
    var triesLeft = timesToTry
    while (triesLeft > 1) {
        triesLeft--
        try {
            return supplier.invoke()
        } catch (e: Exception) {
            // just forget Exception, as it is tried again
        }
        Thread.sleep(waitBetweenTriesInMillis)
    }
    return supplier.invoke()
}
// helpers from 2023 END

// helpers from 2024 START

