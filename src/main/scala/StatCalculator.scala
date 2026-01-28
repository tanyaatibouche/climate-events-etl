object StatCalculator {
    
    def topTen(events: List[Event], n: Int = 10): List[WorstEvent] = {
        val worstDamage = events.sortBy(_.damage).take(n).map(e => TopEvent(e.name, e.damage))
    }

    def countByType(events: List[Event]): List[Event] = {
        events.groupBy(_.eventType)
    }

    def countBySeverity(events: List[Event]): List[Event] = {
        events.groupBy(_.severity)
    }

    def countByYear(events: List[Event], n: Int = 5): List[Event] = {
        events.sortBy(-_.year).take(n)
    }
    
    def countByCountry(events: List[Event], n: Int = 10): List[Event] = {

    }
}