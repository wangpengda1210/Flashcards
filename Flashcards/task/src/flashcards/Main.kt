package flashcards

import java.io.File
import java.io.FileNotFoundException
import java.lang.StringBuilder
import kotlin.random.Random

val cards = mutableMapOf<String, String>()
val definitions = mutableMapOf<String, String>()
val errors = mutableMapOf<String, Int>()
val log = StringBuilder()

lateinit var outputFile: String

fun main(args: Array<String>) {
    
    if (args.size == 4) {
        if (args[0] == "-import") import(args[1])
        if (args[2] == "-import") import(args[3])
        if (args[0] == "-export") outputFile = args[1]
        if (args[2] == "-export") outputFile = args[3]
    }else if (args.size == 2) {
        if (args[0] == "-import") {
            import(args[1])
            outputFile = ""
        }
        if (args[0] == "-export") outputFile = args[1]
    } else outputFile = ""
    
    mainLoop@while (true) {
        println("Input the action (add, remove, import, export, ask, " +
                "exit, log, hardest card, reset stats):")
        log.appendLine("Input the action (add, remove, import, export, ask, " +
                "exit, log, hardest card, reset stats):")
        val choice = readLine()!!
        log.appendLine(choice)
        when (choice.toLowerCase()) {
            "exit" -> {
                exit()
                break@mainLoop
            }
            "add" -> addCard()
            "remove" -> removeCard()
            "export" -> exportCards()
            "import" -> importCards()
            "ask" -> askCards()
            "hardest card" -> findHardestCard()
            "log" -> outputLog()
            "reset stats" -> {
                errors.clear()
                println("Card statistics have been reset.\n")
                log.appendLine("Card statistics have been reset.\n")
            }
            else -> {
                println("Not a valid action\n")
                log.appendLine("Not a valid action\n")
            }
        }
    }
    
    println("Bye bye!")
    log.appendLine("Bye bye!")
    
}

fun addCard() {
    
    println("The card:")
    log.appendLine("The card:")
    val term = readLine()!!
    log.appendLine(term)
    if (cards.containsKey(term)) {
        println("The card \"$term\" already exists.\n")
        log.appendLine("The card \"$term\" already exists.\n")
        return
    }

    println("The definition of the card:")
    log.appendLine("The definition of the card:")
    val definition = readLine()!!
    log.appendLine(definition)
    if (!cards.containsValue(definition)) {
        cards[term] = definition
        definitions[definition] = term
        println("The pair (\"$term\":\"$definition\") has been added.\n")
        log.appendLine("The pair (\"$term\":\"$definition\") has been added.\n")
    } else {
        println("The definition \"$definition\" already exists.\n")
        log.appendLine("The definition \"$definition\" already exists.\n")
    }
    
}

fun removeCard() {
    
    println("Which card?")
    log.appendLine("Which card?")
    val card = readLine()!!
    log.appendLine(card)
    if (cards.containsKey(card)) {
        cards.remove(card)
        definitions.remove(cards[card])
        println("The card has been removed.\n")
        log.appendLine("The card has been removed.\n")
    } else {
        println("Can't remove \"$card\": there is no such card.\n")
        log.appendLine("Can't remove \"$card\": there is no such card.\n")
    }
    
}

fun exportCards() {
    
    println("File name:")
    log.appendLine("File name:")
    val fileName = readLine()!!
    log.appendLine(fileName)
    val file = File(fileName)
    
    output(file)
    log.appendLine("${cards.size} cards have been saved.\n")
    
}

fun output(file: File) {
    file.printWriter().use {
        for ((term, definition) in cards.entries) {
            val errorTimes = if (!errors.containsKey(term)) 0 else errors[term]
            it.println("\"$term\":\"$definition\":\"$errorTimes\"")
        }
    }
    
    println("${cards.size} cards have been saved.\n")
}

fun importCards() {

    println("File name:")
    log.appendLine("File name:")

    try {
        val fileName = readLine()!!
        import(fileName)
    } catch (e: FileNotFoundException) {
        println("File not found.\n")
        log.appendLine("File not found.\n")
    }

}

fun import(fileName: String) {
    log.appendLine(fileName)
    val lines = File(fileName).readLines()
    for (line in lines) {
        val pair = line.split("\":\"")
        val definition = pair[1]
        val term = pair[0].substring(1, pair[0].length)
        val errorTimes = pair[2].substring(0, pair[2].lastIndex).toInt()
        if (cards.containsKey(term)) cards.remove(term)
        if (errors.containsKey(term)) errors.remove(term)
        if (definitions.containsKey(definition)) definitions.remove(definition)
        cards[term] = definition
        definitions[definition] = term
        if (errorTimes > 0) errors[term] = errorTimes
    }
    println("${lines.size} cards have been loaded.\n")
    log.appendLine("${lines.size} cards have been loaded.\n")
}

fun askCards() {

    println("How many times to ask?")
    log.appendLine("How many times to ask?")
    
    val askTimes = readLine()!!
    log.appendLine(askTimes)
    
    for (i in 1..askTimes.toInt()) {
        val term = cards.keys.toList()[Random.nextInt(0, cards.size)]
        println("Print the definition of \"$term\":")
        log.appendLine("Print the definition of \"$term\":")
        val answer = readLine()!!
        log.appendLine(answer)
        when {
            cards[term] == answer -> {
                println("Correct!")
                log.appendLine("Correct!")
            }
            cards.containsValue(answer) -> {
                println("Wrong. The right answer is \"${cards[term]}\"," +
                        " but your definition is correct for \"${definitions[answer]}\".")
                log.appendLine("Wrong. The right answer is \"${cards[term]}\"," +
                        " but your definition is correct for \"${definitions[answer]}\".")
                if (errors.containsKey(term)) errors[term] = errors[term]!! + 1
                else errors[term] = 1
            }
            else -> {
                println("Wrong. The right answer is \"${cards[term]}\".")
                log.appendLine("Wrong. The right answer is \"${cards[term]}\".")
                if (errors.containsKey(term)) errors[term] = errors[term]!! + 1
                else errors[term] = 1
            }
        }
    }
    println()
    log.appendLine()

}

fun findHardestCard() {
    
    if (errors.isEmpty()) {
        println("There are no cards with errors.\n")
        log.appendLine("There are no cards with errors.\n")
    }
    else {
        var hardestCards = mutableListOf<String>()
        var maxErrorTimes = 0
        
        for ((term, errorTimes) in errors.entries) {
            if (errorTimes > maxErrorTimes) {
                hardestCards = mutableListOf(term)
                maxErrorTimes = errorTimes
            } else if (errorTimes == maxErrorTimes) {
                hardestCards.add(term)
            }
        }
        
        var output = if (hardestCards.size == 1) "The hardest card is " else "The hardest cards are "
        for (hardestCard in hardestCards) {
            output += "\"$hardestCard\", "
        }
        output = output.substring(0, output.lastIndex - 1)
        output += ". You have $maxErrorTimes ${if (maxErrorTimes == 1) "error" else "errors"} " +
                "answering ${if (hardestCards.size == 1) "it" else "them"}.\n"
        println(output)
        log.appendLine(output)
    }
    
}

fun outputLog() {

    println("File name:")
    log.appendLine("File name:")
    
    val fileName = readLine()!!
    log.appendLine(fileName)
    
    File(fileName).printWriter().use {
        it.println(log)
    }
    
    println("The log has been saved.\n")
    log.appendLine("The log has been saved.\n")

}

fun exit() {
    
    if (outputFile.isNotEmpty()) output(File(outputFile))
    
}
