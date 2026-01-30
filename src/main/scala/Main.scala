object Main extends App {
    val inputFiles = List("data/data_clean.json", "data/data_dirty.json", "data/data_large.json")

    private def baseName(path: String): String = {
        val name = path.split('/').lastOption.getOrElse(path)
        val dot = name.lastIndexOf('.')
        if (dot > 0) name.substring(0, dot) else name
    }

    private def processOneFile(input: String): Either[String, Unit] = {
        val start = System.nanoTime()

        for {
            loadResult <- DataLoader.loadEvents(input)
            allEvents = loadResult.events
            validEvents = allEvents.filter(DataValidator.isValid)
            uniqueEvents = DataValidator.removeDuplicates(validEvents)

            stats = Statistics(
                total_events_parsed = loadResult.totalParsed,
                total_events_valid = uniqueEvents.size,
                parsing_errors = loadResult.parsingErrors,
                invalid_objects = allEvents.size - validEvents.size,
                duplicates_removed = validEvents.size - uniqueEvents.size
            )

            report = ReportGenerator.generateReport(uniqueEvents, stats)
            perf = {
                val seconds = (System.nanoTime() - start).toDouble / 1e9
                ReportGenerator.Performance(seconds, if (seconds > 0) loadResult.totalParsed / seconds else 0)
            }
            _ = println(f"$input: ${perf.processing_seconds}%.3fs")
            outBase = baseName(input)
            _ <- ReportGenerator.writeReport(report, s"reports/results_$outBase.json", s"reports/report_$outBase.txt", perf)
            _ <- ReportGenerator.writeBonusReport(uniqueEvents, s"reports/bonus_$outBase.txt")
        } yield ()
    }

    val (failures, successes) = inputFiles.map(processOneFile).partitionMap(identity)

    println(s"Finished: ${successes.size} success, ${failures.size} failed")
    if (failures.nonEmpty) failures.foreach(e => println(s"- $e"))
}
