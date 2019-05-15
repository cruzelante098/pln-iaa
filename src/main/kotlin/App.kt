import java.io.File

// Raw corpus
private const val pathUnprocessedCorpusT = "$resourcesPath/unprocessed_corpus/unprocessed_corpusT.txt"
private const val pathUnprocessedCorpusNT = "$resourcesPath/unprocessed_corpus/unprocessed_corpusNT.txt"

// Corpus
private const val pathCorpusT = "$resourcesPath/corpus/corpusT.txt"
private const val pathCorpusNT = "$resourcesPath/corpus/corpusNT.txt"
private const val pathCorpusTodo = "$resourcesPath/corpus/corpustodo.txt"
private const val pathVocabulary = "$resourcesPath/corpus/vocabulary.txt"

// reduced corpus
private const val pathReducedCorpusT = "$resourcesPath/corpus_reducido/corpusT.txt"
private const val pathReducedCorpusNT = "$resourcesPath/corpus_reducido/corpusNT.txt"

// Learning
private const val pathLearnT = "$resourcesPath/aprendizaje/aprendizajeT.txt"
private const val pathLearnNT = "$resourcesPath/aprendizaje/aprendizajeNT.txt"

// Random corpus for classification
private const val corpusID = ""
private const val pathRandomCorpus = "$resourcesPath/random_corpus/random_corpus_todo.txt"
private const val pathRandomCorpusSolution = "$resourcesPath/random_corpus/random_corpus_solution_todo.txt"
private const val pathRandomCorpusClassification = "$resourcesPath/corpus/clasificacion.txt"

fun main() {
    ///////////////////////
    // Corpus generation //
    ///////////////////////

    // Read corpus
    val unprocessedCorpusT = File(pathUnprocessedCorpusT).readLines()
    val unprocessedCorpusNT = File(pathUnprocessedCorpusNT).readLines()

    // Process corpus T and NT
    val corpusT = processCorpus(unprocessedCorpusT)
    val corpusNT = processCorpus(unprocessedCorpusNT)

    // Generate vocab for each one
    val tokensT = makeTokens(corpusT)
    val tokensNT = makeTokens(corpusNT)

    val vocabulary = (tokensT + tokensNT).distinct()

    // Write parsed corpus to output
    createCorpusFile(pathCorpusT, corpusT)
    createCorpusFile(pathCorpusNT, corpusNT)
    createCorpusFile(pathCorpusTodo, corpusT + corpusNT)
    createVocabularyFile(pathVocabulary, vocabulary)

    ///////////////////////////////////////////
    // Generate random corpus and learn file //
    ///////////////////////////////////////////

    // complete corpus
//    val corpusT = readCorpusFile(pathCorpusT)
//    val corpusNT = readCorpusFile(pathCorpusNT)

//    // 75% from corpus
//    val reducedCorpusT = ArrayList<String>()
//    val reducedCorpusNT = ArrayList<String>()
//
//    val randomCorpus2 = ArrayList<String>()
//    val randomCorpus2Solution = ArrayList<Classification>()
//
//    for (i in 0 until (corpusT.size * 0.8).toInt()) {
//        reducedCorpusT.add(corpusT[i])
//    }
//
//    for (i in 0 until (corpusNT.size * 0.8).toInt()) {
//        reducedCorpusNT.add(corpusNT[i])
//    }
//
//    createCorpusFile(pathReducedCorpusT, reducedCorpusT)
//    createCorpusFile(pathReducedCorpusNT, reducedCorpusNT)
//
//    for (i in (corpusT.size * 0.8).toInt() until corpusT.size) {
//        randomCorpus2Solution.add(Classification.TROLL)
//        randomCorpus2.add(corpusT[i])
//    }
//
//    for (i in (corpusNT.size * 0.8).toInt() until corpusNT.size) {
//        randomCorpus2Solution.add(Classification.NOT_TROLL)
//        randomCorpus2.add(corpusNT[i])
//    }
//
//    createCorpusFile(pathRandomCorpus, randomCorpus2)
//    createClassificationFile(pathRandomCorpusSolution, randomCorpus2Solution)

    /////////////////////////////
    // Calculating probability //
    /////////////////////////////

    // -------------
//    val corpusT = readCorpusFile(pathCorpusT)
//    val corpusNT = readCorpusFile(pathCorpusNT)
//    val vocabulary = (makeTokens(reducedCorpusT) + makeTokens(reducedCorpusNT)).distinct()
    // -------------

    val probT = calculateProbability(corpusT, vocabulary)
    val probNT = calculateProbability(corpusNT, vocabulary)

    createLearnFile(pathLearnT, LearnInfo(corpusT.size, makeTokens(corpusT).size, probT))
    createLearnFile(pathLearnNT, LearnInfo(corpusNT.size, makeTokens(corpusNT).size, probNT))

    //////////////
    // Classify //
    //////////////

    val randomCorpus = readCorpusFile(pathRandomCorpus)
    val learnT = readLearnFile(pathLearnT)
    val learnNT = readLearnFile(pathLearnNT)
    val classification = classify(randomCorpus, learnT, learnNT)
    createClassificationFile(pathRandomCorpusClassification, classification)

    /////////////////////////////////////
    // Check classification percentage //
    /////////////////////////////////////

    checkSuccess(pathRandomCorpusSolution, pathRandomCorpusClassification)
}
