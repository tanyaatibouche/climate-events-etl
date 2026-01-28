case class Event (
    id: Int,
    eventType: String,
    name: Option[String],
    country: String,
    region: String,
    date: String,
    year: Int,
    severity: Int,
    casualties: Int,
    affected: Int,
    damage: Option[Double],
    temperature: Option[Double],
    windSpeed: Option[Int]
)

case class WorstEvent (
    name: Option[String],
    damage: Option[Double]
)

case class DeadliestEvent (
    name: Option[String],
    casualties: Int
)


