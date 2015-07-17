package uk.gov.gds.ier.transaction.ordinary.previousAddress

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with PreviousAddressManualMustache
  with PreviousAddressForms {

  val validation = manualStepForm

  val routing = Routes(
    get = routes.PreviousAddressManualStep.get,
    post = routes.PreviousAddressManualStep.post,
    editGet = routes.PreviousAddressManualStep.editGet,
    editPost = routes.PreviousAddressManualStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    ordinary.OpenRegisterStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithClearedSelectedOne = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(
          _.copy(
            uprn = None,
            addressLine = None)))
    }

    currentState.copy(
      previousAddress = addressWithClearedSelectedOne,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

}
