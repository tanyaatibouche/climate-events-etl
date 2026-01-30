import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import scala.io.Source
import scala.util.{Try, Success, Failure}

case class LoadResult(
    events: List[Event],
    totalParsed: Int,
    parsingErrors: Int
)

object DataLoader {
    def loadEvents(filename: String): Either[String, LoadResult] = {
        Try {
            val source = Source.fromFile(filename)
            val content = source.mkString
            source.close()
            content
        } match {
            case Success(jsonString) =>
                decode[List[Json]](jsonString) match {
                    case Right(jsonList) =>
                        val results = jsonList.map(_.as[Event])
                        val events = results.collect { case Right(e) => e }
                        val errors = results.count(_.isLeft)
                        Right(LoadResult(
                            events = events,
                            totalParsed = jsonList.size,
                            parsingErrors = errors
                        ))
                    case Left(error) => Left(error.getMessage)
                }
            case Failure(error) => Left(error.getMessage)
        }
    }
}
