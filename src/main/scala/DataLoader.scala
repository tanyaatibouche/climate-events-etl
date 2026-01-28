import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import scala.io.Source
import scala.util.{Try, Success, Failure}

object DataLoader{
    def loadEvents(filename: String): Either[String, List[Event]] = {
        Try {
            val source = Source.fromFile(filename)
            val content = source.mkString
            source.close()
            content
        } match{
            case Success(jsonString) => {
                decode[List[Json]](jsonString) match {
                    case Right(json) =>Right(json.flatMap(json => json.as[Event].toOption))
                    case Left(error) => Left(error.getMessage)
                }
            }
            case Failure(error) => Left(error.getMessage)
        }
    }
}
