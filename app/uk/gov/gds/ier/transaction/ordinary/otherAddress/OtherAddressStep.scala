package uk.gov.gds.ier.transaction.ordinary.otherAddress

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class OtherAddressStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with OtherAddressForms
  with OtherAddressMustache {

  val validation = otherAddressForm

  val routing = Routes(
    get = routes.OtherAddressStep.get,
    post = routes.OtherAddressStep.post,
    editGet = routes.OtherAddressStep.editGet,
    editPost = routes.OtherAddressStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    ordinary.PreviousAddressFirstStep
  }
}

