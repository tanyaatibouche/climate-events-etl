import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.Try

object ReportGenerator {
    case class OutputStatistics(
        total_events_parsed: Int,
        total_events_valid: Int,
        parsing_errors: Int,
        duplicates_removed: Int
    )

    case class OutputDeadliestEvent(
        name: String,
        `type`: String,
        casualties: Int,
        year: Int
    )

    case class OutputMostExpensiveEvent(
        name: String,
        `type`: String,
        damage: Double,
        year: Int
    )

    case class OutputRegionStats(
        region: String,
        events: Int
    )

    case class OutputAnalysisReport(
        statistics: OutputStatistics,
        events_by_type: Map[String, Int],
        events_by_severity: Map[String, Int],
        deadliest_events: List[OutputDeadliestEvent],
        most_expensive_events: List[OutputMostExpensiveEvent],
        events_by_year: Map[String, Int],
        events_by_country: Map[String, Int],
        total_casualties: Int,
        total_affected: Long,
        total_damage: Double,
        average_casualties_by_type: Map[String, Double],
        most_affected_regions: List[OutputRegionStats]
    )

    case class Performance(
        processing_seconds: Double,
        entries_per_second: Double
    )
    
    private val allTypes = List("Hurricane", "Flood", "Earthquake", "Wildfire", "Drought", "Tornado")
    private val allSeverities = List("1", "2", "3", "4", "5")

    private def normalize(keys: List[String], data: Map[String, Int]): Map[String, Int] =
        keys.map(k => k -> data.getOrElse(k, 0)).toMap

    private def avgCasualtiesByType(events: List[Event]): Map[String, Double] =
        allTypes.map { t =>
            val es = events.filter(_.eventType == t)
            val avg = if (es.isEmpty) 0.0 else es.map(_.casualties).sum.toDouble / es.size
            t -> avg
        }.toMap

    private def topRegions(events: List[Event], n: Int): List[OutputRegionStats] =
        events
        .groupBy(_.region)
        .toList
        .sortBy { case (_, es) => -es.map(_.affected.toLong).sum }
        .take(n)
        .map { case (r, es) => OutputRegionStats(r, es.size) }

    // ======= 1. GENERATION DU RAPPORT =======
    def generateReport(events: List[Event], stats: Statistics): OutputAnalysisReport = {

        val byType = normalize(allTypes, StatCalculator.countByType(events))
        val bySeverity = normalize(allSeverities, StatCalculator.countBySeverity(events))

        val deadliest = StatCalculator.topDeadliest(events).map { e =>
            OutputDeadliestEvent(e.name, e.eventType, e.casualties, e.year)
        }

        val expensive = StatCalculator.mostExpensive(events).map { e =>
            OutputMostExpensiveEvent(e.name, e.eventType, e.damage, e.year)
        }

        val outStats = OutputStatistics(
            stats.total_events_parsed,
            stats.total_events_valid,
            stats.parsing_errors,
            stats.duplicates_removed
        )

        OutputAnalysisReport(
            statistics = outStats,
            events_by_type = byType,
            events_by_severity = bySeverity,
            deadliest_events = deadliest,
            most_expensive_events = expensive,
            events_by_year = StatCalculator.countByYear(events, 5),
            events_by_country = StatCalculator.countByCountry(events, 10),
            total_casualties = StatCalculator.totalCasualties(events),
            total_affected = StatCalculator.totalAffected(events),
            total_damage = StatCalculator.totalDamage(events),
            average_casualties_by_type = avgCasualtiesByType(events),
            most_affected_regions = topRegions(events, 5)
        )
    }

    // ======= 2. ECRITURE DES FICHIERS =======
    def writeReport( report: OutputAnalysisReport, jsonFile: String, txtFile: String, perf: Performance ): Either[String, Unit] = {
        for {
            _ <- writeJson(report, jsonFile)
            _ <- writeText(report, txtFile, perf)
        } yield ()
    }

    private def writeJson(report: OutputAnalysisReport, filename: String): Either[String, Unit] = {
        Try {
            val path = Paths.get(filename)
            val parent = path.getParent
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent)
            }

            val json = report.asJson.spaces2
            Files.write(path, json.getBytes(StandardCharsets.UTF_8))
        }.toEither.left.map(_.getMessage).map(_ => ())
    }

    private def writeText(report: OutputAnalysisReport, filename: String, perf: Performance): Either[String, Unit] = {
        Try {
            val path = Paths.get(filename)
            val parent = path.getParent
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent)
            }

            val text = buildText(report, perf)
            Files.write(path, text.getBytes(StandardCharsets.UTF_8))
        }.toEither.left.map(_.getMessage).map(_ => ())
    }

    // ======= FORMAT TXT =======
    private def buildText(r: OutputAnalysisReport, p: Performance): String = {
        s"""    
===============================================
RAPPORT D'ANALYSE - ÉVÉNEMENTS CLIMATIQUES
===============================================

📊 STATISTIQUES DE PARSING
---------------------------
- Entrées totales lues      : ${r.statistics.total_events_parsed}
- Entrées valides           : ${r.statistics.total_events_valid}
- Erreurs de parsing        : ${r.statistics.parsing_errors}
- Doublons supprimés        : ${r.statistics.duplicates_removed}

📊 IMPACT GLOBAL
-----------------
- Total victimes            : ${r.total_casualties}
- Total personnes affectées : ${r.total_affected}
- Coût total des dégâts     : ${r.total_damage / 1e6} M

⏱️ PERFORMANCE
---------------
- Temps de traitement       : ${p.processing_seconds}
- Entrées/seconde           : ${p.entries_per_second}

===============================================
"""
    }
}