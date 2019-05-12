import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

fun BufferedWriter.writeln(line: String) =
    write(line + "\n")

val table: TableList = TableList(2, "Descripción", "Cantidad").withSpacing(1).withUnicode(true)

// counters
var lineno = 1
var blankLines = 0
var validLines = 0

enum class Modifications(var message: String, var amount: Int) {
    URL("Instancias que contienen urls", 0),
    ASCII("Instancias que contienen códigos ASCII", 0),
    MENTION("Instancias que contienen menciones", 0),
    PAREN("Instancias que contienen oraciones entre paréntesis", 0),
    SYMBOL("Instancias con símbolos sueltos", 0);
}


// array of valid tweets
val parsedTweets = ArrayList<String>()

// route to resources from project root
const val resourcesPath = "src/main/resources"

// instance, contains tweet classified as troll or not troll
val tweets: MutableList<Array<String>> = CSVReader(BufferedReader(FileReader("$resourcesPath/instancia/CTR_TRAIN.csv"))).readAll()



fun main() {
    val troll = BufferedWriter(FileWriter("$resourcesPath/corpus/corpusT.txt"))
    val notTroll = BufferedWriter(FileWriter("$resourcesPath/corpus/corpusNT.txt"))

    val corpus = BufferedWriter(FileWriter("$resourcesPath/corpus/corpustodo.txt"))

    val instancesWithUrls = BufferedWriter(FileWriter("$resourcesPath/debug/instances_url.txt"))
    val instancesWithAsciiCodes = BufferedWriter(FileWriter("$resourcesPath/debug/instances_ascii.txt"))
    val instancesWithMentions = BufferedWriter(FileWriter("$resourcesPath/debug/instances_mentions.txt"))
    val instancesWithParenthesis = BufferedWriter(FileWriter("$resourcesPath/debug/instances_parenthesis.txt"))
    val instancesWithAloneSymbols = BufferedWriter(FileWriter("$resourcesPath/debug/instances_alone_symbols.txt"))

    // regex used to clean tweets
    val urlRegex = """((http(s)?(://))+(www\.)?([\w\-./])*(\.[a-zA-Z]{2,3}/?))[^\s\n|]*[^.,;:?!@^$ -]( - )?""".toRegex()
    val asciiCodeRegex = """&#?\w+;""".toRegex()
    val mentionRegex = """[@]('|\w)+?(\s|\b)""".toRegex()
    val parenthesisRegex = """\(([~\w])|([~\w]|[.,!?])\)""".toRegex()
    val aloneSymbolsRegex = """(?<=\s|^)([\-,.´!?'#@"*(){}\[\]:;$€%&¬/\\=º^])(?=\s|$)""".toRegex()

    // transformations
    tweets.forEach {
        var tweet = it[0]

        tweet = replace(tweet, urlRegex, "", instancesWithUrls, Modifications.URL)
        tweet = replace(tweet, asciiCodeRegex, "", instancesWithAsciiCodes, Modifications.ASCII)
        tweet = replace(tweet, mentionRegex, "", instancesWithMentions, Modifications.MENTION)
        tweet = replace(tweet, parenthesisRegex, "$1$2", instancesWithParenthesis, Modifications.PAREN)
        tweet = replace(tweet, aloneSymbolsRegex, "", instancesWithAloneSymbols, Modifications.SYMBOL)

        // write final output to corpora
        writeToFile(it, tweet, troll, notTroll, corpus)

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
    val tokens = makeTokensFromList(parsedTweets)

    // write the words to the file
    vocabulary.writeln("Numero de palabras:" + tokens.size.toString())
    tokens.forEach { vocabulary.writeln("Palabra:$it") }

    vocabulary.close()

    // print results
    table.addRow("Líneas analizadas", "$lineno")
    Modifications.values().forEach { table.addRow(it.message, it.amount.toString()) }
    table.addRow("Líneas que han quedado en blanco", "$blankLines")
    table.addRow("Líneas válidas para análisis", "$validLines")
    table.addRow("Palabras analizadas", "${tokens.size}")

    table.print()
}

private fun writeToFile(it: Array<String>, tweet: String, troll: BufferedWriter, notTroll: BufferedWriter, corpus: BufferedWriter) {
    if (it.size == 2 && tweet.isNotBlank()) {
        if (it[1] == "troll") {
            troll.writeln(tweet)
        } else {
            notTroll.writeln(tweet)
        }
        corpus.writeln(tweet)
        parsedTweets.add(tweet)
        validLines++
    } else if (it.isNotEmpty()) {
        blankLines++
    } else {
        println("[WARN] invalid line: $it")
    }
}


fun replace(tweet: String, regex: Regex, replacement: String, debugFile: BufferedWriter, mod: Modifications): String {
    var parsedTweet = tweet
    if (regex.containsMatchIn(tweet)) {
        debugFile.writeln(tweet)
        parsedTweet = tweet.replace(regex, replacement)
        debugFile.writeln(parsedTweet + "\n")
        mod.amount++
    }
    return parsedTweet
}


private fun makeTokensFromList(lines: List<String>): List<String> {
    return lines
        .flatMap { it.split("""\s+|(?<=[?!,.;:])|(?=[?!,.;:])""".toRegex()) } // splits by dots, comma, colon, semicolon
        .asSequence()
//        .map { it.replace("""[ !-/ :-@ \[-` {-~ ]*""".toRegex(RegexOption.COMMENTS), "") }
        .map { it.replace("""[.]""".toRegex(), "") }    // remove dots
        .map { it.replace("""^\d+$""".toRegex(), "") }  // remove words conformed of only numbers
        .filter { it.isNotBlank() }
        .distinct()
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        .toList()
}
