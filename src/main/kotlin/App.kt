import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

fun BufferedWriter.writeln(line: String) {
    write(line + "\n")
}

fun main() {
    val lines = CSVReader(BufferedReader(FileReader("src/main/resources/CTR_TRAIN.csv"))).readAll()
    val troll = BufferedWriter(FileWriter("src/main/resources/corpus/corpusT.csv"))
    val notTroll = BufferedWriter(FileWriter("src/main/resources/corpus/corpusNT.csv"))
    val corpus = BufferedWriter(FileWriter("src/main/resources/corpus/corpustodo.csv"))

    val instancesWithUrls = BufferedWriter(FileWriter("src/main/resources/debug/instances_url.csv"))
    val instancesWithHasgtagsOrMentions = BufferedWriter(FileWriter("src/main/resources/debug/instances_hashtags-mentions.csv"))

    // counters
    var lineno = 1
    var blankLines = 0
    var validLines = 0
    var urlsFound = 0
    var hashtagsAndMentionsFound = 0

    // regex used to clean tweets
    val urlRegex =
        """((http(s)?(://))+(www\.)?([\w\-./])*(\.[a-zA-Z]{2,3}/?))[^\s\n|]*[^.,;:?!@^$ -]( - )?""".toRegex()

    val hashtagOrMentionRegex = """[@#]('|\w)+(\s|\b)""".toRegex()


    // array of valid tweets
    val tweets = ArrayList<String>()

    // transformations
    lines.forEach {
        // delete urls
        if (urlRegex.containsMatchIn(it[0])) {
            instancesWithUrls.writeln(it[0])
            it[0] = it[0].replace(urlRegex, "")
            instancesWithUrls.writeln(it[0] + "\n")
            urlsFound++
        }

        // remove hashtags and mentions
        if(hashtagOrMentionRegex.containsMatchIn(it[0])) {
            instancesWithHasgtagsOrMentions.writeln(it[0])
            it[0] = it[0].replace(hashtagOrMentionRegex, "")
            instancesWithHasgtagsOrMentions.writeln(it[0] + "\n")
            hashtagsAndMentionsFound++
        }

        // write final output to corpora
        if (it.size == 2 && it[0].isNotBlank()) {
            if (it[1] == "troll") {
                troll.writeln(it[0])
            } else {
                notTroll.writeln(it[0])
            }
            corpus.writeln(it[0])
            tweets.add(it[0])
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
    instancesWithHasgtagsOrMentions.close()

    ///////////////////////////
    // vocabulary generation //
    ///////////////////////////
    val vocabulary = BufferedWriter(FileWriter("src/main/resources/corpus/vocabulary.csv"))

    val tokens = tweets
        .flatMap { it.split("""\s+""".toRegex()) }
        .map { it.replace("""[!-/:-@\[-`{-~]*""".toRegex(), "") }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()
    var words = 0
    tokens.forEach { vocabulary.writeln(it); words++ }

    vocabulary.close()

    // print results
    val table = TableList(2, "Descripción", "Cantidad").withSpacing(1).withUnicode(true)

    table.addRow("Líneas analizadas", "$lineno")
    table.addRow("Líneas con URLs", "$urlsFound")
    table.addRow("Líneas con hashtags o menciones", "$hashtagsAndMentionsFound")
    table.addRow("Líneas que han quedado en blanco", "$blankLines")
    table.addRow("Líneas válidas para análisis", "$validLines")
    table.addRow("Palabras analizadas", "$words")

    table.print()

}
