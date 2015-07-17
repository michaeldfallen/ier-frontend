package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with PreviousAddressManualMustache
  with PreviousAddressForms {

  val validation = manualAddressFormForPreviousAddress

  val routing = Routes(
    get = routes.PreviousAddressManualStep.get,
    post = routes.PreviousAddressManualStep.post,
    editGet = routes.PreviousAddressManualStep.editGet,
    editPost = routes.PreviousAddressManualStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.NationalityStep
  }

}
