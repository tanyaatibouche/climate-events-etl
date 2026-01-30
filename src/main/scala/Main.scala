object Main extends App {
    println("Climate Events - ETL")

    val result = for {
        loadResult <- DataLoader.loadEvents("data/data_dirty.json")
    } yield {
        val allEvents = loadResult.events
        val totalParsed = loadResult.totalParsed
        val parsingErrors = loadResult.parsingErrors

        val validEvents = allEvents.filter(DataValidator.isValid)
        val invalidCount = allEvents.size - validEvents.size

        val uniqueEvents = DataValidator.removeDuplicates(validEvents)
        val duplicatesRemoved = validEvents.size - uniqueEvents.size

        val stats = Statistics(
            total_events_parsed = totalParsed,
            total_events_valid = uniqueEvents.size,
            parsing_errors = parsingErrors,
            invalid_objects = invalidCount,
            duplicates_removed = duplicatesRemoved
        )

        println(s"Total parsés: $totalParsed")
        println(s"Erreurs parsing: $parsingErrors")
        println(s"Objets invalides: $invalidCount")
        println(s"Doublons supprimés: $duplicatesRemoved")
        println(s"Events valides: ${uniqueEvents.size}")

        (uniqueEvents, stats)
    }

    result match {
        case Right((events, stats)) => println(s"\nStats finales: $stats")
        case Left(error) => println(s"Erreur: $error")
    }
}
