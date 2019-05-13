import com.github.ajalt.mordant.TermColors
import java.io.File
import kotlin.math.round

fun checkSuccess(solutionPath: String, classificationPath: String) {
    val solution = File(solutionPath).readLines()
    val classification = File(classificationPath).readLines()

    if (solution.size != classification.size) {
        throw IllegalStateException("Files don't have the same amount of instances")
    }

    var correctT = 0
    var correctNT = 0
    var incorrectT = 0
    var incorrectNT = 0

    for (i in 0 until solution.size) {
        if (solution[i] == classification[i] && classification[i] == "T") {
            correctT++
        } else if (solution[i] == classification[i] && classification[i] == "NT") {
            correctNT++
        } else if (solution[i] != classification[i] && solution[i] == "NT") {
            incorrectNT++
        } else if (solution[i] != classification[i] && solution[i] == "T") {
            incorrectT++
        } else {
            throw Error()
        }
    }

    println("[ CORRECT ] T  as  T:".padEnd(25) + "$correctT   ".padEnd(7) + "${(correctT.toDouble()    * 100 / solution.size).round(2)} %")
    println("[ CORRECT ] NT as NT:".padEnd(25) + "$correctNT  ".padEnd(7) + "${(correctNT.toDouble()   * 100 / solution.size).round(2)} %")
    println("[INCORRECT] T  as NT:".padEnd(25) + "$incorrectT ".padEnd(7) + "${(incorrectT.toDouble()  * 100 / solution.size).round(2)} %")
    println("[INCORRECT] NT as  T:".padEnd(25) + "$incorrectNT".padEnd(7) + "${(incorrectNT.toDouble() * 100 / solution.size).round(2)} %")

    val hits = ((correctT + correctNT) * 100.toDouble() / solution.size).round(2)
    val misses = ((incorrectT + incorrectNT) * 100.toDouble() / solution.size).round(2)

    val color = when {
        hits > 80 -> TermColors().brightGreen
        hits > 75 -> TermColors().cyan
        hits > 70 -> TermColors().yellow
        else -> TermColors().red
    }

    println(color("\n[   HITS  ] $hits %"))
    println("[  MISSES ] $misses %")

    println("[  TOTAL  ] ${solution.size}")
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}