import java.io.BufferedWriter
import java.io.File


// route to resources from project root
const val resourcesPath = "src/main/resources"
val wordFreqRegex = """Palabra:(?<word>.*?)\s{2}Frec:(?<freq>\d+?)\s{2}LogProb:(?<prob>-?\d+?\.\d+)""".toRegex()

data class Word(val word: String, val freq: Int, val prob: Double)
data class LearnInfo(val corpusSize: Int, val tokensAmount: Int, val frequencies: List<Word>)

fun BufferedWriter.writeln(line: String) =
    write(line + "\n")

fun makeTokens(lines: List<String>): List<String> {
    return lines
        .flatMap { makeTokens(it) }
        .sorted()
        .toList()
}

fun makeTokens(lines: String): List<String> {
    return lines.split("""\s+|([?!,.;:()])\1*""".toRegex())
//        .map { it.replace("""([?!,.;:])\1*""".toRegex(), "") }
        .asSequence()
        .map { it.replace("""\s*\d+\s*""".toRegex(), "") }
        .map { it.replace("""(['"();.?!*:,])\1*""".toRegex(), "") }
        .map { it.replace("""(['"();.?!*:,])(.*?)\1""".toRegex(), "$2") }
        .map {
            if (it.length > 1 && it[0] == it[0].toUpperCase() && it.substring(1) == it.substring(1).toLowerCase())
                it.toLowerCase()
            else
                it
        }
        .filter { it.isNotBlank() }
        .toList()
}

fun readCorpusFile(path: String): List<String> {
    return File(path).bufferedReader().readLines()
}

//fun readVocabularyFile(path: String): List<String> {
//    File(path).bufferedWriter().use { file ->
//        file.writeln("Número de palabras:${vocabulary.size}")
//        vocabulary.forEach { file.writeln("Palabra:$it") }
//    }
//}

fun readLearnFile(path: String): LearnInfo {
    val file = File(path).bufferedReader().readLines()
    val numbers = Regex("""(\d+)""")
    val corpusSize = numbers.find(file[0])!!.value.toInt()
    val tokensAmount = numbers.find(file[1])!!.value.toInt()
    val frequencies = file.drop(2).map {
        val res = wordFreqRegex.matchEntire(it)!!
        val word: String = res.groups["word"]!!.value
        val freq: Int = res.groups["freq"]!!.value.toInt()
        val prob: Double = res.groups["prob"]!!.value.toDouble()
        Word(word, freq, prob)
    }
    return LearnInfo(corpusSize, tokensAmount, frequencies)
}

fun createCorpusFile(path: String, corpus: List<String>) {
    File(path).bufferedWriter().use { file ->
        corpus.forEach { file.writeln(it) }
    }
}

fun createVocabularyFile(path: String, vocabulary: List<String>) {
    File(path).bufferedWriter().use { file ->
        file.writeln("Número de palabras:${vocabulary.size}")
        vocabulary.forEach { file.writeln("Palabra:$it") }
    }
}

fun createLearnFile(path: String, learnInfo: LearnInfo) {
    File(path).bufferedWriter().use { file ->
        file.writeln("Número de documentos del corpus:${learnInfo.corpusSize}")
        file.writeln("Número de palabras del corpus:${learnInfo.tokensAmount}")
        learnInfo.frequencies.forEach {
            file.writeln("Palabra:${it.word}  Frec:${it.freq}  LogProb:${it.prob}")
        }
    }
}


fun createClassificationFile(path: String, classification: List<Classification>) {
    File(path).bufferedWriter().use { file ->
        classification.forEach {
            when (it) {
                Classification.TROLL -> file.writeln("T")
                Classification.NOT_TROLL -> file.writeln("NT")
            }
        }
    }
}


//fun makeVocabularyFromList(lines: List<String>): List<String> {
//    return lines
//        .flatMap { it.split("""\s+|(?<=[?!,.;:])|(?=[?!,.;:])""".toRegex()) } // splits by dots, comma, colon, semicolon
//        .asSequence()
////        .map { it.replace("""[ !-/ :-@ \[-` {-~ ]*""".toRegex(RegexOption.COMMENTS), "") }
//        .map { it.replace("""[.]""".toRegex(), "") }    // remove dots
//        .map { it.replace("""^\d+$""".toRegex(), "") }  // remove words conformed of only numbers
//        .filter { it.isNotBlank() }
//        .distinct()
//        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
//        .toList()
//}
