object DataValidator {
    val validTypes: Set[String] = Set("Hurricane", "Flood", "Earthquake", "Wildfire", "Drought", "Tornado")
    def isValid(event: Event): Boolean = {
        event.year >= 1900 && 
        event.year <= 2025 &&
        event.severity >= 1 &&
        event.severity <= 5 &&
        event.casualties >= 0 &&
        validTypes.contains(event.eventType)
    }

    def removeDuplicates(event: List[Event]): List[Event] = {
        event.groupBy(_.id).values.map(_.head).toList
    }

    def filterValid(events: List[Event]): List[Event] = {
        removeDuplicates(events.filter(isValid))
    }
}