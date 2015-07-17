package uk.gov.gds.ier.step

import uk.gov.gds.ier.session.{SessionHandling, SessionLogging}
import play.api.mvc.{AnyContent, Action, Controller}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import uk.gov.gds.ier.session.SessionHandling

trait ConfirmationStepController[T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with Step[T]
  with StepTemplate[T]
  with Controller
  with SessionLogging
  with Logging
  with WithSerialiser
  with WithConfig
  with WithEncryption {

  val Action = play.api.mvc.Action
  implicit val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val validation: ErrorTransformForm[T]
  val routing: Routes
  def get:Action[AnyContent]
  def post:Action[AnyContent]

  def goToNext(currentState: T) = {
    Redirect(routing.get)
  }

  def nextStep(currentState: T) = this
  def isStepComplete(currentState: T) = false
}
