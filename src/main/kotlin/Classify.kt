import kotlin.math.ln

enum class Classification(val sym: String) {
    TROLL("T"), NOT_TROLL("nT");
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
