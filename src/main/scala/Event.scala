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

case class DeadliestEvent(
    name: String,
    eventType: String,
    casualties: Int,
    year: Int
)

case class MostExpensiveEvent(
    name: String,
    eventType: String,
    damage: Double,
    year: Int
)

case class RegionStats(
    region: String,
    events: Int
)

case class Statistics(
    total_events_parsed: Int,
    total_events_valid: Int,
    parsing_errors: Int,
    invalid_objects: Int,
    duplicates_removed: Int
)

case class AnalysisReport(
    statistics: Statistics,
    events_by_type: Map[String, Int],
    events_by_severity: Map[String, Int],
    deadliest_events: List[DeadliestEvent],
    most_expensive_events: List[MostExpensiveEvent],
    events_by_year: Map[String, Int],
    events_by_country: Map[String, Int],
    total_casualties: Int,
    total_affected: Long,
    total_damage: Double,
    average_casualties_by_type: Map[String, Double],
    most_affected_regions: List[RegionStats]
)
