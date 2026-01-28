object Main extends App {
  println("Climate Events - ETL")

  val result = for {
    events <- DataLoader.loadEvents("data/data_dirty.json")
    _ = println(s"${events.length} events loaded")
  
    validEvents <- Right(DataValidator.filterValid(events))
    _ = println(s"${validEvents.length} valid events")
  } yield validEvents
 }