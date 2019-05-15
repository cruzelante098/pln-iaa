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
