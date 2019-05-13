import java.io.File

// Raw corpus
private const val pathUnprocessedCorpusT = "$resourcesPath/unprocessed_corpus/unprocessed_corpusT.txt"
private const val pathUnprocessedCorpusNT = "$resourcesPath/unprocessed_corpus/unprocessed_corpusNT.txt"

// Corpus
private const val pathCorpusT = "$resourcesPath/corpus/corpusT.txt"
private const val pathCorpusNT = "$resourcesPath/corpus/corpusNT.txt"
private const val pathCorpusTodo = "$resourcesPath/corpus/corpustodo.txt"
private const val pathVocabulary = "$resourcesPath/corpus/vocabulary.txt"

// Learning
private const val pathLearnT = "$resourcesPath/aprendizaje/aprendizajeT.txt"
private const val pathLearnNT = "$resourcesPath/aprendizaje/aprendizajeNT.txt"

// Random corpus for classification
private const val corpusID = ""
private const val pathRandomCorpus = "$resourcesPath/random_corpus/random_corpus$corpusID.txt"
private const val pathRandomCorpusSolution = "$resourcesPath/random_corpus/random_corpus_solution$corpusID.txt"
private const val pathRandomCorpusClassification = "$resourcesPath/random_corpus/result$corpusID.txt"

fun main() {
    ///////////////////////
    // Corpus generation //
    ///////////////////////

//    // Read corpus
//    val unprocessedCorpusT = File(pathUnprocessedCorpusT).readLines()
//    val unprocessedCorpusNT = File(pathUnprocessedCorpusNT).readLines()
//
//    // Process corpus T and NT
//    val corpusT = processCorpus(unprocessedCorpusT)
//    val corpusNT = processCorpus(unprocessedCorpusNT)
//
//    // Generate vocab for each one
//    val tokensT = makeTokens(corpusT)
//    val tokensNT = makeTokens(corpusNT)
//
//    val vocabulary = (tokensT + tokensNT).distinct()
//
//    // Write parsed corpus to output
//    createCorpusFile(pathCorpusT, corpusT)
//    createCorpusFile(pathCorpusNT, corpusNT)
//    createCorpusFile(pathCorpusTodo, corpusT + corpusNT)
//    createVocabularyFile(pathVocabulary, vocabulary)

    ///////////////////////////////////////////
    // Generate random corpus and learn file //
    ///////////////////////////////////////////



    /////////////////////////////
    // Calculating probability //
    /////////////////////////////

    // -------------
    val corpusT = readCorpusFile(pathCorpusT)
    val corpusNT = readCorpusFile(pathCorpusNT)
    val vocabulary = (makeTokens(corpusT) + makeTokens(corpusNT)).distinct()
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
