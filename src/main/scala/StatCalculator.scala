object StatCalculator {

    def topDeadliest(events: List[Event], n: Int = 10): List[DeadliestEvent] = {
        events
            .sortBy(-_.casualties)  
            .take(n)
            .map(e => DeadliestEvent(
                name = e.name.getOrElse("N/A"),
                eventType = e.eventType,
                casualties = e.casualties,
                year = e.year
            ))
    }

    def mostExpensive(events: List[Event], n: Int = 10): List[MostExpensiveEvent] = {
        events
            .filter(_.damage.isDefined)  
            .sortBy(-_.damage.get)
            .take(n)
            .map(e => MostExpensiveEvent(
                name = e.name.getOrElse(e.eventType),
                eventType = e.eventType,
                damage = e.damage.get,
                year = e.year
            ))
    }

    def countByType(events: List[Event]): Map[String, Int] = {
        events.groupBy(_.eventType).view.mapValues(_.size).toMap
    }

    def countBySeverity(events: List[Event]): Map[String, Int] = {
        events.groupBy(_.severity)
            .view.mapValues(_.size).toMap
            .map { case (k, v) => (k.toString, v) }
    }

    def countByYear(events: List[Event], n: Int = 5): Map[String, Int] = {
        events.groupBy(_.year)
            .view.mapValues(_.size).toMap
            .toList.sortBy(-_._1).take(n)  // Trie par année décroissante, prend les n premières
            .map { case (k, v) => (k.toString, v) }
            .toMap
    }

    def countByCountry(events: List[Event], n: Int = 10): Map[String, Int] = {
        events.groupBy(_.country)
            .view.mapValues(_.size).toMap
            .toList.sortBy(-_._2).take(n)  
            .toMap
    }

    def totalCasualties(events: List[Event]): Int = {
        events.map(_.casualties).sum
    }

    def totalAffected(events: List[Event]): Long = {
        events.map(_.affected.toLong).sum
    }

    def totalDamage(events: List[Event]): Double = {
        events.flatMap(_.damage).sum
    }
}