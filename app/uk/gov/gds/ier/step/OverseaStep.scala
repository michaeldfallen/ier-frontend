package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.controller.routes.ErrorController
import uk.gov.gds.ier.transaction.overseas.{InprogressOverseas, WithOverseasControllers}
import uk.gov.gds.ier.transaction.overseas.confirmation.routes.ConfirmationStep

trait OverseaStep
  extends StepController[InprogressOverseas]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets
  with WithOverseasControllers { self: StepTemplate[InprogressOverseas] =>
    val manifestOfT = manifest[InprogressOverseas]
    def factoryOfT() = InprogressOverseas()
    def timeoutPage() = ErrorController.ordinaryTimeout
    val confirmationRoute = ConfirmationStep.get
}

