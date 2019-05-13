import kotlin.math.ln

fun calculateProbability(corpus: List<String>, vocabulary: List<String>): List<Word> {
    val freq = HashMap<String, Int>()
    val tokens = makeTokens(corpus).sorted()

    vocabulary.forEach { word ->
        freq[word] = appearances(word, tokens)
    }

    freq["<UNK>"] = 0

    val wordFrecuency = ArrayList<Word>()

    for ((word, appearances) in freq.toSortedMap(/*compareBy(String.CASE_INSENSITIVE_ORDER) { it }*/)) {
        val frequency = appearances + 1 // the first 1.0 is the laplacian smooth
        val totalSize = tokens.size + vocabulary.size + 1.0 // addition of <UNK>
        val logprob = ln(frequency / totalSize)
        wordFrecuency.add(Word(word, frequency, logprob))
    }

    return wordFrecuency
}

fun appearances(word: String, words: List<String>): Int {
    var appearances = 0
    val pos = words.binarySearch(word)
    return if (pos < 0) {
        0
    } else {
        var i = pos - 1
        while (i >= 0 && words[i] == word) {
            appearances++
            i--
        }
        i = pos
        while (i < words.size && words[i] == word) {
            appearances++
            i++
        }
        appearances
    }
}


//fun appearances(word: String, tokens: List<String>): Int {
//    var appearances = 0
//    for (token in tokens) {
//        if (token == word) {
//            appearances++
//        }
//    }
//    return appearances
//}

//fun main() {
//    val learnT = BufferedWriter(FileWriter("$resourcesPath/aprendizaje/aprendizajeT.txt"))
//    val learnNT = BufferedWriter(FileWriter("$resourcesPath/aprendizaje/aprendizajeNT.txt"))
//
//    val corpusT = File("$resourcesPath/corpus/corpusT.txt").readLines()
//    val corpusNT = File("$resourcesPath/corpus/corpusNT.txt").readLines()
//
//    val wordsT = makeTokens(corpusT)
//    val wordsNT = makeTokens(corpusNT)
//
//    val vocabulary = File("$resourcesPath/corpus/vocabulary.txt")
//        .readLines()
//        .drop(1) // header
//        .map { it.replace("Palabra:", "") }
//
//    // count words for trolls and not trolls
//    val freqT = HashMap<String, Int>()
//    val freqNT = HashMap<String, Int>()
//
//    for (word in vocabulary) {
//        calculateFrequency(word, wordsT, freqT)
//        calculateFrequency(word, wordsNT, freqNT)
//    }
//
//    writeToFile(learnT, freqT, corpusT.size, wordsT.size, wordsT.distinct().size)
//    writeToFile(learnNT, freqNT, corpusNT.size, wordsNT.size, wordsNT.distinct().size)
//
//    learnT.close()
//    learnNT.close()
//}
//
//private fun calculateFrequency(word: String, words: List<String>, freq: HashMap<String, Int>) {
//    freq[word] = appearances(word, words)
//}
//
//private fun writeToFile(
//    learn: BufferedWriter,
//    freq: HashMap<String, Int>,
//    corpusSize: Int,
//    corpusWordsAmount: Int,
//    corpusVocabularySize: Int
//) {
//    learn.writeln("Número de documentos del corpus:$corpusSize")
//    learn.writeln("Número de palabras del corpus:$corpusWordsAmount")
//    freq["<UNK>"] = 0
//    for ((word, appearances) in freq.toSortedMap(compareBy(String.CASE_INSENSITIVE_ORDER) { it })) {
//        val num = appearances + 1 // the first 1.0 is the laplacian smooth
//        val den = corpusWordsAmount + corpusVocabularySize + 1.0 // addition of <UNK>
//        val logprob = ln(num / den)
//        learn.writeln("Palabra:$word  Frec:$num  LogProb:$logprob")
//    }
//}
//
