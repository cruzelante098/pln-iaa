import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter


/* Ampliations */

fun BufferedWriter.writeln(line: String) {
    write(line + "\n")
}

fun <K> HashMap<K, Int>.addOrInc(key: K) {
    if (this.containsKey(key)) {
        this[key] = (this[key] ?: error("Key must be not null")) + 1
    } else {
        this[key] = 0
    }
}

data class Tweet(val content: String, val troll: Boolean)
data class ReplaceResult(val tweet: String, val found: Int)

fun main() {
    val tweets = CSVReader(BufferedReader(FileReader("src/main/resources/instancia/CTR_TRAIN.csv"))).readAll()

    val troll = BufferedWriter(FileWriter("src/main/resources/corpus/corpusT.txt"))
    val notTroll = BufferedWriter(FileWriter("src/main/resources/corpus/corpusNT.txt"))
    val corpus = BufferedWriter(FileWriter("src/main/resources/corpus/corpustodo.txt"))

    val instancesWithUrls = BufferedWriter(FileWriter("src/main/resources/debug/instances_url.txt"))
    val instancesWithAsciiCodes = BufferedWriter(FileWriter("src/main/resources/debug/instances_ascii.txt"))
    val instancesWithMentions = BufferedWriter(FileWriter("src/main/resources/debug/instances_mentions.txt"))

    // counters
    var lineno = 1
    var blankLines = 0
    var validLines = 0
    var urlsFound = 0
    var asciiCodesFound = 0
    var mentionsFound = 0

    // regex used to clean tweets
    val urlRegex = """((http(s)?(://))+(www\.)?([\w\-./])*(\.[a-zA-Z]{2,3}/?))[^\s\n|]*[^.,;:?!@^$ -]( - )?""".toRegex()
    val asciiCodeRegex = """&#?\w+;""".toRegex()
    val mentionRegex = """[@]('|\w)+?(\s|\b)""".toRegex()

    // array of valid tweets
    val parsedTweets = ArrayList<Tweet>()

    // transformations
    tweets.forEach {
        // delete urls
        with(replaceByNothing(it[0], urlRegex, instancesWithUrls)) {
            it[0] = tweet
            urlsFound += found
        }

        with(replaceByNothing(it[0], asciiCodeRegex, instancesWithAsciiCodes)) {
            it[0] = tweet
            asciiCodesFound += found
        }

        with(replaceByNothing(it[0], mentionRegex, instancesWithMentions)) {
            it[0] = tweet
            mentionsFound += found
        }

        // write final output to corpora
        if (it.size == 2 && it[0].isNotBlank()) {
            if (it[1] == "troll") {
                troll.writeln(it[0])
            } else {
                notTroll.writeln(it[0])
            }
            corpus.writeln(it[0])
            parsedTweets.add(Tweet(it[0], it[1] == "troll"))
            validLines++
        } else if (it.isNotEmpty()) {
            blankLines++
        } else {
            println("[WARN] invalid line: $it")
        }

        lineno++
    }

    // close all files
    troll.close()
    notTroll.close()
    corpus.close()
    instancesWithUrls.close()
    instancesWithAsciiCodes.close()
    instancesWithMentions.close()

    ///////////////////////////
    // vocabulary generation //
    ///////////////////////////
    val vocabulary = BufferedWriter(FileWriter("src/main/resources/corpus/vocabulary.txt"))
    // delete unuseful characters and only leave unique words
    val tokens = makeTokensFromList(parsedTweets.map { it.content })

    // write the words to the file
    vocabulary.writeln("Numero de palabras: " + tokens.size.toString())
    tokens.forEach { vocabulary.writeln("Palabra: $it") }

    vocabulary.close()

    ///////////////
    // Frequency //
    ///////////////
    val learnT = CSVWriter(BufferedWriter(FileWriter("src/main/resources/aprendizaje/aprendizajeT.csv")))
    val learnNT = CSVWriter(BufferedWriter(FileWriter("src/main/resources/aprendizaje/aprendizajeNT.csv")))

    // count words for trolls and not trolls
    val freqT = HashMap<String, Int>()
    val freqNT = HashMap<String, Int>()

    for (tweet in parsedTweets) {
        val words = makeTokensFromLine(tweet.content)
        if (tweet.troll) {
            words.forEach { freqT.addOrInc(it) }
        } else {
            words.forEach { freqNT.addOrInc(it) }
        }
    }

    // TODO:
    //  - formatear el header del documento
    //  - suavizado laplaciano
    //  - tratamiento de palabras desconocidas

    learnT.close()
    learnNT.close()

    // print results
    val table = TableList(2, "Descripción", "Cantidad").withSpacing(1).withUnicode(true)

    table.addRow("Líneas analizadas", "$lineno")
    table.addRow("Líneas con URLs", "$urlsFound")
    table.addRow("Líneas con códigos ascii", "$asciiCodesFound")
    table.addRow("Líneas con hashtags o menciones", "$mentionsFound")
    table.addRow("Líneas que han quedado en blanco", "$blankLines")
    table.addRow("Líneas válidas para análisis", "$validLines")
    table.addRow("Palabras analizadas", "${tokens.size}")

    table.print()
}

private fun replaceByNothing(tweet: String, regex: Regex, output: BufferedWriter): ReplaceResult {
    var parsedTweet: String = tweet
    var amount = 0
    if (regex.containsMatchIn(tweet)) {
        output.writeln(tweet)
        parsedTweet = tweet.replace(regex, "")
        output.writeln(parsedTweet + "\n")
        amount++
    }
    return ReplaceResult(parsedTweet, amount)
}

private fun makeTokensFromList(lines: List<String>): List<String> {
    return lines
        .flatMap { it.split("""\s+""".toRegex()) }
        .asSequence()
//        .map { it.replace("""[ !-/ :-@ \[-` {-~ ]*""".toRegex(RegexOption.COMMENTS), "") }
        .map { it.replace("""[.]""".toRegex(), "") }    // remove dots
        .map { it.replace("""^\d+$""".toRegex(), "") }  // remove words conformed of only numbers
        .filter { it.isNotBlank() }
        .distinct()
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        .toList()
}

private fun makeTokensFromLine(line: String): List<String> {
    return line.split("""\s+""".toRegex())
        .asSequence()
//        .map { it.replace("""[ !-/ :-@ \[-` {-~ ]*""".toRegex(RegexOption.COMMENTS), "") }
        .map { it.replace("""[.]""".toRegex(), "") }    // remove dots
        .map { it.replace("""^\d+$""".toRegex(), "") }  // remove words conformed of only numbers
        .filter { it.isNotBlank() }
        .toList()
}
