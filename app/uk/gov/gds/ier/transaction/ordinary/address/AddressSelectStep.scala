package uk.gov.gds.ier.transaction.ordinary.address

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  Addresses,
  PossibleAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.controller.routes.ExitController
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with AddressSelectMustache
  with AddressForms {

  val validation = addressForm

  val routing = Routes(
    get = routes.AddressSelectStep.get,
    post = routes.AddressSelectStep.post,
    editGet = routes.AddressSelectStep.editGet,
    editPost = routes.AddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    currentState.address.map(_.postcode) match {
      case Some(postcode) if postcode.trim.toUpperCase.startsWith("BT") => GoTo (ExitController.northernIreland)
      case _ => ordinary.OtherAddressStep
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithAddressLine = currentState.address.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      address = addressWithAddressLine,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}
