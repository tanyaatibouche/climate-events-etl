object Main extends App {
  println("Climate Events - ETL (multi-files)\n")

  val inputFiles: List[String] = List("data/data_clean.json", "data/data_dirty.json", "data/data_large.json")

  private def baseName(path: String): String = {
    val name = path.split('/').lastOption.getOrElse(path)
    val dot = name.lastIndexOf('.')
    if (dot > 0) name.substring(0, dot) else name
  }

  private def processOneFile(input: String): Either[String, (String, String)] = {
    val start = System.nanoTime()

    val result = for {
      loadResult <- DataLoader.loadEvents(input)
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

      val report = ReportGenerator.generateReport(uniqueEvents, stats)

      val end = System.nanoTime()
      val seconds = (end - start).toDouble / 1e9
      val eps = if (seconds <= 0) 0.0 else totalParsed.toDouble / seconds
      val perf = ReportGenerator.Performance(seconds, eps)

      val outBase = baseName(input)
      val jsonOut = s"reports/results_${outBase}.json"
      val txtOut  = s"reports/report_${outBase}.txt"

      val bonusOut = s"reports/bonus_${outBase}.txt"

      ReportGenerator.writeBonusReport(uniqueEvents, bonusOut) match {
        case Right(_) => println(s"   -> $bonusOut")
        case Left(e)  => println(s"   bonus report failed: $e")
      }

      (report, perf, jsonOut, txtOut, stats)
    }

    result match {
      case Left(err) => Left(s"$input -> load/parse failed: $err")
      case Right((report, perf, jsonOut, txtOut, stats)) =>
        ReportGenerator.writeReport(report, jsonOut, txtOut, perf) match {
          case Right(_) =>
            println(s"✅ $input")
            println(s"   -> $jsonOut")
            println(s"   -> $txtOut")
            println(s"   Stats: parsed=${stats.total_events_parsed}, valid=${stats.total_events_valid}, parsingErrors=${stats.parsing_errors}, invalid=${stats.invalid_objects}, dupRemoved=${stats.duplicates_removed}\n")
            Right((jsonOut, txtOut))

          case Left(err) =>
            Left(s"$input -> write failed: $err")
        }
    }
  }

  val results: List[Either[String, (String, String)]] = inputFiles.map(processOneFile)

  val (failures, successes) = results.partitionMap(identity)

  println("====================================")
  println(s"Finished: ${successes.size} success, ${failures.size} failed")
  if (failures.nonEmpty) {
    println("\n❌ Failures:")
    failures.foreach(e => println(s"- $e"))
  }
  println("====================================")
}