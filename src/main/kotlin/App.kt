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

    // counters
    var lineno = 1
    var urlsFound = 0
    var blankLines = 0

    val urlRegex =
        """((http(s)?(://))+(www\.)?([\w\-./])*(\.[a-zA-Z]{2,3}/?))[^\s\n|]*[^.,;:?!@^$ -]( - )?""".toRegex()


    // transformations
    lines.forEach {
        // delete urls
        if (urlRegex.containsMatchIn(it[0])) {
            instancesWithUrls.writeln(it[0])
            it[0] = it[0].replace(urlRegex, "")
            urlsFound++
        }

        // write final output
        corpus.write(it[0] + "\n")
        if (it.size == 2 && it[0].isNotBlank()) {
            if (it[1] == "troll")
                troll.writeln(it[0])
            else
                notTroll.writeln(it[0])
        } else if (it.isNotEmpty()) {
            blankLines++
        }

        lineno++
    }

    // close all files
    troll.close()
    notTroll.close()
    corpus.close()
    instancesWithUrls.close()

    // print results
    val table = TableList(2, "Descripción", "Cantidad").withSpacing(1).withUnicode(true)

    table.addRow("Líneas analizadas", "$lineno")
    table.addRow("Líneas con URLs", "$urlsFound")
    table.addRow("Líneas que han quedado en blanco", "$blankLines")
    table.addRow("Líneas válidas para análisis", "${lineno - blankLines}")

    table.print()
}
