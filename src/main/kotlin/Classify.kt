import kotlin.math.ln

enum class Classification {
    TROLL, NOT_TROLL
}

fun classify(
    corpus: List<String>,
    learnT: LearnInfo,
    learnNT: LearnInfo
): List<Classification> {
    val classification = ArrayList<Classification>()
    val probT = learnT.corpusSize.toDouble() / (learnT.corpusSize + learnNT.corpusSize)
    val probNT = learnNT.corpusSize.toDouble() / (learnT.corpusSize + learnNT.corpusSize)

    for ((i, tweet) in corpus.withIndex()) {
        print("${(i + 1) * 100 / corpus.size} %\r")

        // estimate the probability of the line to be troll or not troll
        val jointProbT = jointProb(tweet, learnT.frequencies) + ln(probT)
        val jointProbNT = jointProb(tweet, learnNT.frequencies) + ln(probNT)

        // save to respective file
        if (jointProbT > jointProbNT) {
            classification.add(Classification.TROLL)
        } else {
            classification.add(Classification.NOT_TROLL)
        }
    }

    return classification
}


fun jointProb(tweet: String, learn: List<Word>): Double {
    val words: List<String> = makeTokens(tweet)
    var prob = 0.0
    for (word in words) {
        val found = learn.find { it.word == word }
        prob += found?.prob ?: learn.find { it.word == "<UNK>" }!!.prob
    }
    return prob
}


//fun main() {
//    // corpus to classify
//    val corpus: List<String> = File("$resourcesPath/random_corpus/random_corpus2.txt").readLines()
//
//    val aprendizajeNTFile = File("$resourcesPath/aprendizaje/aprendizajeNT.txt").readLines()
//    val aprendizajeTFile = File("$resourcesPath/aprendizaje/aprendizajeT.txt").readLines()
//
//    val corpusNTSize = Regex("""(\d+)""").find(aprendizajeNTFile[0])!!.value.toDouble()
//    val corpusTSize = Regex("""(\d+)""").find(aprendizajeTFile[0])!!.value.toDouble()
//
//    // previous classified corpuses
//    val aprendizajeNT: List<Word> = convertToWords(aprendizajeNTFile)
//    val aprendizajeT: List<Word> = convertToWords(aprendizajeTFile)
//
//    val probNT = corpusNTSize / (corpusNTSize + corpusTSize)
//    val probT = corpusTSize / (corpusNTSize + corpusTSize)
//
//    println()
//
//    // output files
//    val result = BufferedWriter(FileWriter("$resourcesPath/random_corpus/result2.txt"))
//
//    for ((i, line) in corpus.withIndex()) {
//        print("${(i + 1) * 100 / corpus.size} %\r")
//        val words: List<String> = line.split("""\s+|(?<=[?!,.;:])|(?=[?!,.;:])""".toRegex())
//
//        // estimate the probability of the line to be troll or not troll
//        val jointProbNT = jointProb(words, aprendizajeNT) + ln(probNT)
//        val jointProbT = jointProb(words, aprendizajeT) + ln(probT)
//
//        // save to respective file
//        if (jointProbT > jointProbNT) {
//            result.writeln("T")
//        } else {
//            result.writeln("NT")
//        }
//    }
//
//    result.close()
//}

//fun convertToWords(lines: List<String>) =
//    lines.drop(2).map {
//        val res = wordFreqRegex.matchEntire(it)!!
//        val word: String = res.groups["word"]!!.value
//        val freq: Int = res.groups["freq"]!!.value.toInt()
//        val prob: Double = res.groups["prob"]!!.value.toDouble()
//        Word(word, freq, prob)
//    }
