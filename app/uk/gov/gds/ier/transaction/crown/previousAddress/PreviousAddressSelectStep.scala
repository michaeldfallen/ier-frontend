package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.CrownStep
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with PreviousAddressSelectMustache
  with PreviousAddressForms
  with WithAddressService {

  val validation = selectStepForm

  val routing = Routes(
    get = routes.PreviousAddressSelectStep.get,
    post = routes.PreviousAddressSelectStep.post,
    editGet = routes.PreviousAddressSelectStep.editGet,
    editPost = routes.PreviousAddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.NationalityStep
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

