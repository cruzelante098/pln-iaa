import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

// TODO: (DUDA)
//  - Al contar las palabras del vocabulario, debemos tener en cuenta las que quitamos? Dado que el
//    vocabulario del corpusT, por ejemplo, es más pequeño (al haber suprimido las palabras

fun main() {
    val resourcesPath = "src/main/resources"

    val learnT = BufferedWriter(FileWriter("$resourcesPath/aprendizaje/aprendizajeT.txt"))
    val learnNT = BufferedWriter(FileWriter("$resourcesPath/aprendizaje/aprendizajeNT.txt"))

    val corpusT = File("$resourcesPath/corpus/corpusT.txt").readLines()
    val corpusNT = File("$resourcesPath/corpus/corpusNT.txt").readLines()

    val wordsT = makeTokensFromList(corpusT)
    val wordsNT = makeTokensFromList(corpusNT)

    val vocabTSize = wordsT.distinct().size
    val vocabNTSize = wordsNT.distinct().size

    val vocabulary = File("$resourcesPath/corpus/vocabulary.txt")
        .readLines()
        .drop(1) // header
        .map { it.replace("Palabra:", "") }

    // count words for trolls and not trolls
    val freqT = HashMap<String, Int>()
    val freqNT = HashMap<String, Int>()

    for (word in vocabulary) {
        calculateFrequency(word, wordsT, freqT)
        calculateFrequency(word, wordsNT, freqNT)
    }

    writeToFile(learnT, wordsT.size, vocabTSize, freqT, vocabulary.size)
    writeToFile(learnNT, wordsNT.size, vocabNTSize, freqNT, vocabulary.size)

    learnT.close()
    learnNT.close()
}

private fun calculateFrequency(word: String, words: List<String>, freq: HashMap<String, Int>) {
    val appearances = appearances(word, words)
    if (appearances == 0) {
        freq["<UNK>"] = freq.getOrDefault("<UNK>", 0) + 1
    } else {
        freq[word] = appearances
    }
}

private fun writeToFile(
    learn: BufferedWriter,
    corpusSize: Int,
    corpusVocabSize: Int,
    freq: HashMap<String, Int>,
    vocabularySize: Int
) {
    learn.writeln("Número de documentos del corpus:$corpusSize")
    learn.writeln("Número de palabras del corpus:${freq.size}")
    for ((word, appearances) in freq.toSortedMap(compareBy(String.CASE_INSENSITIVE_ORDER) { it })) {
        val num = appearances + 1.0 // the first 1.0 is the laplacian smooth
        val den = freq.size + corpusVocabSize/* + 1.0*/ // addition of <UNK>
        val logprob = Math.log(num / den)
        learn.writeln("Palabra:$word  Frec:${appearances + 1}  LogProb:$logprob")
    }
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

private fun makeTokensFromList(lines: List<String>): List<String> {
    return lines
        .flatMap { it.split("""\s+|(?<=[?!,.;:])|(?=[?!,.;:])""".toRegex()) }
        .asSequence()
        .map { it.replace("""[.]""".toRegex(), "") }    // remove dots
        .map { it.replace("""^\d+$""".toRegex(), "") }  // remove words conformed of only numbers
        .filter { it.isNotBlank() }
        .sorted()
        .toList()
}
