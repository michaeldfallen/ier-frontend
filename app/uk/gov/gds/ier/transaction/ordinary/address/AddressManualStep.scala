package uk.gov.gds.ier.transaction.ordinary.address

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val routing = Routes(
    get = routes.AddressManualStep.get,
    post = routes.AddressManualStep.post,
    editGet = routes.AddressManualStep.editGet,
    editPost = routes.AddressManualStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
      ordinary.OtherAddressStep
  }
}
