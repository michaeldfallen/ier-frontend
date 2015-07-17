package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.{WithConfig, WithEncryption}
import uk.gov.gds.ier.serialiser.WithSerialiser
import scala.concurrent.ExecutionContext
import play.api.mvc._

trait SessionCleaner extends ResultHandling {
  self: WithEncryption with WithSerialiser with WithConfig =>

  object NewSession {
    def in[A](action: Action[A]) (implicit context: ExecutionContext) = {
      Action.async(action.parser) { implicit request =>
        action(request) map { realResult => realResult.withFreshSession() }
      }
    }
  }

  object ClearSession {
    def in[A](action: Action[A]) (implicit context: ExecutionContext) = {
      Action.async(action.parser) { implicit request =>
        action(request) map { realResult => realResult.emptySession() }
      }
    }
  }
}
