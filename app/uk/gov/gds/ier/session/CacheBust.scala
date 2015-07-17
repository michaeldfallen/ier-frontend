package uk.gov.gds.ier.session

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

case class CacheBust[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[SimpleResult] = {
    action(request) map { result =>
      result.withHeaders(
        "Cache-Control" -> "no-cache, max-age=0, must-revalidate, no-store"
      )
    }
  }

  lazy val parser = action.parser
}