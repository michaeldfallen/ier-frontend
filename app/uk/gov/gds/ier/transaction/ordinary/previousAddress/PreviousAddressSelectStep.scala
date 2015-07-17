package uk.gov.gds.ier.transaction.ordinary.previousAddress

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets

class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with PreviousAddressSelectMustache
  with PreviousAddressForms {

  val validation = selectStepForm

  val routing = Routes(
    get = routes.PreviousAddressSelectStep.get,
    post = routes.PreviousAddressSelectStep.post,
    editGet = routes.PreviousAddressSelectStep.editGet,
    editPost = routes.PreviousAddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    ordinary.OpenRegisterStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithLineFilled = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(addressService.fillAddressLine(_).copy(manualAddress = None))
      )
    }

    currentState.copy(
      previousAddress = addressWithLineFilled,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}

