private data class Modification(val description: String, val regex: Regex, val replacement: String, var amount: Int = 0)

fun processCorpus(unprocessedCorpus: List<String>): List<String> {
    val corpus = ArrayList<String>()

    val modifications = arrayOf(
        Modification(
            "Deletes URLs",
            Regex("""((http(s)?(://))+(www\.)?([\w\-./])*(\.[a-zA-Z]{2,3}/?))[^\s\n|]*[^.,;:?!@^$ -]( - )?"""),
            ""
        ),
        Modification("Deletes ASCII codes", Regex("""&#?\w+;"""), ""),
        Modification("Deletes user mentions", Regex("""[@]('|\w)+?(\s|\b)"""), ""),
        Modification("Deletes parenthesis", Regex("""\(([~\w])|([~\w]|[.,!?])\)"""), "$1$2"),
        Modification(
            "Deletes loose symbols",
            Regex("""(?<=\s|^)([\-,.´!?'#@"*(){}\[\]:;$€%&¬/\\=º^])(?=\s|$)"""),
            ""
        ) // review, not workin exactly well
    )

    // transformations
    for (tweet in unprocessedCorpus) {
        var parsedTweet: String = tweet

        for (mod in modifications) {
            if (mod.regex.containsMatchIn(tweet)) {
                parsedTweet = parsedTweet.replace(mod.regex, mod.replacement)
                mod.amount++
            }
        }

        corpus.add(parsedTweet)
    }

    for (mod in modifications) {
        println("${mod.description}: ${mod.amount}")
    }

    return corpus
}

