package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.controller.routes._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.ordinary.confirmation.routes

trait OrdinaryStep
  extends StepController[InprogressOrdinary]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets { self: StepTemplate[InprogressOrdinary] =>
  val manifestOfT = manifest[InprogressOrdinary]
  def factoryOfT() = InprogressOrdinary()
  def timeoutPage() = ErrorController.ordinaryTimeout
  val confirmationRoute = routes.ConfirmationStep.get
}

