package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.{HasAddressOption, LastAddress}
import uk.gov.gds.ier.assets.RemoteAssets


@Singleton
class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val routing = Routes(
    get = routes.AddressManualStep.get,
    post = routes.AddressManualStep.post,
    editGet = routes.AddressManualStep.editGet,
    editPost = routes.AddressManualStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    val hasUkAddress = Some(true)

    currentState.address flatMap {
      address => address.hasAddress
    } map {
      hasUkAddress => hasUkAddress.hasAddress
    } match {
      case `hasUkAddress` => forces.PreviousAddressFirstStep
      case _ => forces.NationalityStep
    }
  }

  def clearAddressAndUprn(currentState: InprogressForces)= {
    val clearedAddress = currentState.address.map {addr =>
      addr.copy (address = addr.address.map(
        _.copy(uprn = None, addressLine = None)))
      }

    currentState.copy(
      address = clearedAddress
    )
  }

  override val onSuccess = TransformApplication (clearAddressAndUprn) andThen GoToNextIncompleteStep()
}
