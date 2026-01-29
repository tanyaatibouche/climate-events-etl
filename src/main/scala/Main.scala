object Main extends App {
  println("Climate Events - ETL")

  val result = for {
    events <- DataLoader.loadEvents("data/data_dirty.json")
    _ = println(s"${events.length} events loaded")
  
    validEvents <- Right(DataValidator.filterValid(events))
    _ = println(s"${validEvents.length} valid events")
  } yield {
        val validEvents = loadResult.events.filter(DataValidator.isValid)
        val uniqueEvents = DataValidator.removeDuplicates(validEvents)
        
        val stats = Statistics(
            total_events_parsed = loadResult.totalParsed,
            total_events_valid = uniqueEvents.size,
            parsing_errors = loadResult.parsingErrors,
            invalid_objects = loadResult.events.size - validEvents.size,
            duplicates_removed = validEvents.size - uniqueEvents.size
        )
  }
 }