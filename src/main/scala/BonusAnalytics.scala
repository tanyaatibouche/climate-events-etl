object BonusAnalytics {

    def decade(year: Int): Int = (year / 10) * 10

    def eventsPerDecade(events: Seq[Event]): Seq[(Int, Int)] =
        events
            .groupBy(e => decade(e.year))
            .toSeq
            .map((dec, es) => (dec, es.size))
            .sortBy(_._1)
    
    def severity5PerDecade(events: Seq[Event]): Seq[(Int, Int)] =
        events
            .filter(_.severity == 5)
            .groupBy(e => decade(e.year))
            .toSeq
            .map((dec, es) => (dec, es.size))
            .sortBy(_._1)

    def isMajor(e: Event): Boolean = e.severity >= 4 || e.casualties >= 100 || e.damage.exists(_ >= 1e9)
    def isNamed(e: Event): Boolean = e.name.exists(_.trim.nonEmpty)

    def namedMajorEvents(events: Seq[Event]): Seq[Event] =
        events
            .filter(e => isNamed(e) && isMajor(e))
            .sortBy(e => (-e.severity, -e.casualties, -e.damage.getOrElse(0.0)))

    case class NamedHurricaneStats(
        count: Int,
        totalCasualties: Int,
        totalDamage: Double,
        avgCasualties: Double,
        avgDamage: Double
    )
    
    def namedHurricaneStats(events: Seq[Event]): NamedHurricaneStats = {
        val hurricanes = events.filter(e => e.eventType == "Hurricane" && isNamed(e))

        val count = hurricanes.size
        val totalCasualties = hurricanes.map(_.casualties).sum
        val totalDamage = hurricanes.flatMap(_.damage).sum

        NamedHurricaneStats(
            count,
            totalCasualties,
            totalDamage,
            if (count == 0) 0.0 else totalCasualties.toDouble / count,
            if (count == 0) 0.0 else totalDamage / count
        )
    }
    
    def topRegions(events: Seq[Event], n: Int = 10): Seq[(String, Int)] =
        events
            .groupBy(_.region)
            .toSeq
            .map((region, es) => (region, es.size))
            .sortBy((_, count) => -count)
            .take(n)

    def mostFrequentTypeByRegion(events: Seq[Event]): Seq[(String, String, Int)] =
        events
            .groupBy(_.region)
            .toSeq
            .map((region, es) =>
                val (bestType, bestCount) =
                    es.groupBy(_.eventType)
                        .toSeq
                        .map { case (t, ts) => (t, ts.size) }
                        .sortBy { case (t, c) => (-c, t) }
                        .head

                (region, bestType, bestCount))
            .sortBy((_, _, count) => -count)
    }