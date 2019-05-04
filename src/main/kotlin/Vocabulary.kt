import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter


/* Ampliations */

fun BufferedWriter.writeln(line: String) {
    write(line + "\n")
}


data class Tweet(val content: String, val troll: Boolean)
data class ReplaceResult(val tweet: String, val found: Int)

fun main() {
    val resourcesPath = "src/main/resources"

    val tweets = CSVReader(BufferedReader(FileReader("$resourcesPath/instancia/CTR_TRAIN.csv"))).readAll()

    val troll = BufferedWriter(FileWriter("$resourcesPath/corpus/corpusT.txt"))
    val notTroll = BufferedWriter(FileWriter("$resourcesPath/corpus/corpusNT.txt"))
    val corpus = BufferedWriter(FileWriter("$resourcesPath/corpus/corpustodo.txt"))

    val instancesWithUrls = BufferedWriter(FileWriter("$resourcesPath/debug/instances_url.txt"))
    val instancesWithAsciiCodes = BufferedWriter(FileWriter("$resourcesPath/debug/instances_ascii.txt"))
    val instancesWithMentions = BufferedWriter(FileWriter("$resourcesPath/debug/instances_mentions.txt"))
    val instancesWithParenthesis = BufferedWriter(FileWriter("$resourcesPath/debug/instances_parenthesis.txt"))
    val instancesWithAloneSymbols = BufferedWriter(FileWriter("$resourcesPath/debug/instances_alone_symbols.txt"))

    // counters
    var lineno = 1
    var blankLines = 0
    var validLines = 0
    var urlsFound = 0
    var asciiCodesFound = 0
    var mentionsFound = 0
    var parenthesisFound = 0
    var aloneSymbolsFound = 0

    // regex used to clean tweets
    val urlRegex = """((http(s)?(://))+(www\.)?([\w\-./])*(\.[a-zA-Z]{2,3}/?))[^\s\n|]*[^.,;:?!@^$ -]( - )?""".toRegex()
    val asciiCodeRegex = """&#?\w+;""".toRegex()
    val mentionRegex = """[@]('|\w)+?(\s|\b)""".toRegex()
    val parenthesisRegex = """\(([~\w])|([~\w]|[.,!?])\)""".toRegex()
    val aloneSymbolsRegex = """(?<=\s|^)([\-,.´!?'#@"*(){}\[\]:;$€%&¬/\\=º^])(?=\s|$)""".toRegex()

    // array of valid tweets
    val parsedTweets = ArrayList<Tweet>()

    // transformations
    tweets.forEach {
        // delete urls
        with(replaceWith(it[0], urlRegex, "", instancesWithUrls)) {
            it[0] = tweet
            urlsFound += found
        }

        with(replaceWith(it[0], asciiCodeRegex, "", instancesWithAsciiCodes)) {
            it[0] = tweet
            asciiCodesFound += found
        }

        with(replaceWith(it[0], mentionRegex, "", instancesWithMentions)) {
            it[0] = tweet
            mentionsFound += found
        }

        with(replaceWith(it[0], parenthesisRegex, "$1$2", instancesWithParenthesis)) {
            it[0] = tweet
            parenthesisFound += found
        }

        with(replaceWith(it[0], aloneSymbolsRegex, "", instancesWithAloneSymbols)) {
            it[0] = tweet
            aloneSymbolsFound += found
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
    instancesWithParenthesis.close()
    instancesWithAloneSymbols.close()

    ///////////////////////////
    // vocabulary generation //
    ///////////////////////////
    val vocabulary = BufferedWriter(FileWriter("src/main/resources/corpus/vocabulary.txt"))

    // delete unuseful characters and only leave unique words
    val tokens = makeTokensFromList(parsedTweets.map { it.content })

    // write the words to the file
    vocabulary.writeln("Numero de palabras:" + tokens.size.toString())
    tokens.forEach { vocabulary.writeln("Palabra:$it") }

    vocabulary.close()

    // print results
    val table = TableList(2, "Descripción", "Cantidad").withSpacing(1).withUnicode(true)

    table.addRow("Líneas analizadas", "$lineno")
    table.addRow("Líneas con URLs", "$urlsFound")
    table.addRow("Líneas con códigos ascii", "$asciiCodesFound")
    table.addRow("Líneas con hashtags o menciones", "$mentionsFound")
    table.addRow("Líneas que han quedado en blanco", "$blankLines")
    table.addRow("Líneas con paréntesis", "$parenthesisFound")
    table.addRow("Líneas con símbolos sueltos", "$aloneSymbolsFound")
    table.addRow("Líneas válidas para análisis", "$validLines")
    table.addRow("Palabras analizadas", "${tokens.size}")

    table.print()
}

private fun replaceWith(tweet: String, regex: Regex, replacement: String, output: BufferedWriter): ReplaceResult {
    var parsedTweet: String = tweet
    var amount = 0
    if (regex.containsMatchIn(tweet)) {
        output.writeln(tweet)
        parsedTweet = tweet.replace(regex, replacement)
        output.writeln(parsedTweet + "\n")
        amount++
    }
    return ReplaceResult(parsedTweet, amount)
}

private fun makeTokensFromList(lines: List<String>): List<String> {
    return lines
        .flatMap { it.split("""\s+|(?<=[?!,.;:])|(?=[?!,.;:])""".toRegex()) }
        .asSequence()
//        .map { it.replace("""[ !-/ :-@ \[-` {-~ ]*""".toRegex(RegexOption.COMMENTS), "") }
        .map { it.replace("""[.]""".toRegex(), "") }    // remove dots
        .map { it.replace("""^\d+$""".toRegex(), "") }  // remove words conformed of only numbers
        .filter { it.isNotBlank() }
        .distinct()
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        .toList()
}
