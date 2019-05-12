import com.opencsv.CSVReader
import java.io.File

fun main() {
    val tweets = CSVReader(File("$resourcesPath/instancia/CTR_TRAIN.csv").bufferedReader()).readAll()

    val unprocessesCorpusT = File("$resourcesPath/unprocessed_corpus/unprocessed_corpusT.txt").bufferedWriter()
    val unprocessesCorpusNT = File("$resourcesPath/unprocessed_corpus/unprocessed_corpusNT.txt").bufferedWriter()

    for ((i, tweet) in tweets.withIndex()) {
        if (tweet.isEmpty() || tweet[0].isBlank()) {
            println("Empty line at ${i+1}")
            continue
        }

        when {
            tweet[1] == "troll" -> unprocessesCorpusT.writeln(tweet[0])
            tweet[1] == "not_troll" -> unprocessesCorpusNT.writeln(tweet[0])
            else -> println("Hey, that's weird: " + tweet[0] + " - " + tweet[1])
        }
    }

    unprocessesCorpusT.close()
    unprocessesCorpusNT.close()
}