import java.io.File

fun main() {
    val corpusT = File("$resourcesPath/corpus/corpusT.txt").readLines()
    val corpusNT = File("$resourcesPath/corpus/corpusNT.txt").readLines()

    val randomCorpus = File("$resourcesPath/random_corpus/random_corpus3.txt").bufferedWriter()
    val randomCorpusSolution = File("$resourcesPath/random_corpus/random_corpus_solution3.txt").bufferedWriter()

    for (i in 0 until 7000) {
        if (Math.random() > 0.5) {
            val index = (0 until corpusT.size).shuffled().first()
            randomCorpus.writeln(corpusT[index])
            randomCorpusSolution.writeln("T")
        } else {
            val index = (0 until corpusNT.size).shuffled().first()
            randomCorpus.writeln(corpusNT[index])
            randomCorpusSolution.writeln("NT")
        }
    }

    randomCorpus.close()
    randomCorpusSolution.close()
}