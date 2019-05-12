import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.math.ln

data class Word(val word: String, val freq: Int, val prob: Double)

val wordFreqRegex = """Palabra:(?<word>.*?)\s{2}Frec:(?<freq>\d+?)\s{2}LogProb:(?<prob>-?\d+?\.\d+)""".toRegex()

fun main() {
    val resourcesPath = "src/main/resources"

    // corpus to classify
    val corpus: List<String> = File("$resourcesPath/random_corpus/random_corpus.txt").readLines()

    val aprendizajeNTFile = File("$resourcesPath/aprendizaje/aprendizajeNT.txt").readLines()
    val aprendizajeTFile = File("$resourcesPath/aprendizaje/aprendizajeT.txt").readLines()

    val corpusNTSize = Regex("""(\d+)""").find(aprendizajeNTFile[0])!!.value.toDouble()
    val corpusTSize = Regex("""(\d+)""").find(aprendizajeTFile[0])!!.value.toDouble()

    // previous classified corpuses
    val aprendizajeNT: List<Word> = convertToWords(aprendizajeNTFile)
    val aprendizajeT: List<Word> = convertToWords(aprendizajeTFile)

    val probNT = corpusNTSize / (corpusNTSize + corpusTSize)
    val probT = corpusTSize / (corpusNTSize + corpusTSize)

    println()

    // output files
    val result = BufferedWriter(FileWriter("$resourcesPath/random_corpus/result.txt"))

    for ((i, line) in corpus.withIndex()) {
        print("${(i+1)*100 / corpus.size} %\r")
        val words: List<String> = line.split("""\s+|(?<=[?!,.;:])|(?=[?!,.;:])""".toRegex())

        // estimate the probability of the line to be troll or not troll
        val jointProbNT = jointProb(words, aprendizajeNT) + ln(probNT)
        val jointProbT = jointProb(words, aprendizajeT)   + ln(probT)

        // save to respective file
        if (jointProbT > jointProbNT) {
            result.writeln("T")
        } else {
            result.writeln("NT")
        }
    }

    result.close()
}

fun jointProb(tweet: List<String>, aprendizaje: List<Word>): Double {
    var prob = 0.0
    for (word in tweet) {
        val found = aprendizaje.find { it.word == word }
        prob += found?.prob ?: aprendizaje.find { it.word == "<UNK>" }!!.prob
    }
    return prob
}

fun convertToWords(lines: List<String>) =
    lines.drop(2).map {
        val res = wordFreqRegex.matchEntire(it)!!
        val word: String = res.groups["word"]!!.value
        val freq: Int = res.groups["freq"]!!.value.toInt()
        val prob: Double = res.groups["prob"]!!.value.toDouble()
        Word(word, freq, prob)
    }
