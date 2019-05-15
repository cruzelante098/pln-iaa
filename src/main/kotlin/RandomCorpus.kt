import java.io.File

fun main() {
    val corpusT = File("$resourcesPath/corpus/corpusT.txt").readLines()
    val corpusNT = File("$resourcesPath/corpus/corpusNT.txt").readLines()

    val randomCorpus = File("$resourcesPath/random_corpus/random_corpus_todo.txt").bufferedWriter()
    val randomCorpusSolution = File("$resourcesPath/random_corpus/random_corpus_solution_todo.txt").bufferedWriter()

    for (i in 0 until corpusT.size) {
        randomCorpus.writeln(corpusT[i])
        randomCorpusSolution.writeln(Classification.TROLL.sym)
    }

    for (i in 0 until corpusNT.size) {
        randomCorpus.writeln(corpusNT[i])
        randomCorpusSolution.writeln(Classification.NOT_TROLL.sym)
    }

//    for (i in 0 until 8000) {
//        if (Math.random() > 0.5) {
//            val index = (0 until corpusT.size).shuffled().first()
//            randomCorpus.writeln(corpusT[index])
//            randomCorpusSolution.writeln(Classification.TROLL.sym)
//        } else {
//            val index = (0 until corpusNT.size).shuffled().first()
//            randomCorpus.writeln(corpusNT[index])
//            randomCorpusSolution.writeln(Classification.NOT_TROLL.sym)
//        }
//    }

    randomCorpus.close()
    randomCorpusSolution.close()
}
