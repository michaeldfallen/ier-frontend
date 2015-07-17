package uk.gov.gds.ier.transaction.overseas.openRegister

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import scala.Some
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm

  val routing = Routes(
    get = routes.OpenRegisterStep.get,
    post = routes.OpenRegisterStep.post,
    editGet = routes.OpenRegisterStep.editGet,
    editPost = routes.OpenRegisterStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.WaysToVoteStep
  }

  override def isStepComplete(currentState: InprogressOverseas) = {
    currentState.openRegisterOptin.isDefined
  }
}
