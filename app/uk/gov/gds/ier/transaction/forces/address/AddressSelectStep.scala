package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  LastAddress,
  HasAddressOption,
  Addresses,
  PossibleAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with AddressSelectMustache
  with AddressForms
  with WithAddressService {

  val validation = selectStepForm

  val routing = Routes(
    get = routes.AddressSelectStep.get,
    post = routes.AddressSelectStep.post,
    editGet = routes.AddressSelectStep.editGet,
    editPost = routes.AddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    import HasAddressOption._

    currentState.address flatMap {
      address => address.hasAddress
    } match {
      case Some(YesAndLivingThere) => forces.PreviousAddressFirstStep
      case Some(YesAndNotLivingThere) => forces.PreviousAddressFirstStep
      case _ => forces.NationalityStep
    }
  }

  def fillInAddressAndCleanManualAddress(currentState: InprogressForces) = {
    val addressWithAddressLine = currentState.address.map { lastUkAddress =>
      lastUkAddress.copy (
        address = lastUkAddress.address.map(addressService.fillAddressLine(_).copy(manualAddress = None))
      )
    }

    currentState.copy(
      address = addressWithAddressLine,
      possibleAddresses = None
    )
  }

  override val onSuccess = TransformApplication(fillInAddressAndCleanManualAddress) andThen GoToNextIncompleteStep()

}
