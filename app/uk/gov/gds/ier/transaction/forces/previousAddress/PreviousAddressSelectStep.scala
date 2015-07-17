package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with PreviousAddressSelectMustache
  with PreviousAddressForms
  with WithAddressService {

  val validation = selectAddressFormForPreviousAddress

  val routing = Routes(
    get = routes.PreviousAddressSelectStep.get,
    post = routes.PreviousAddressSelectStep.post,
    editGet = routes.PreviousAddressSelectStep.editGet,
    editPost = routes.PreviousAddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.NationalityStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val address = currentState.previousAddress.flatMap(_.previousAddress)
    val addressWithAddressLine = address.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
        previousAddress = addressWithAddressLine
      )),
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

}
