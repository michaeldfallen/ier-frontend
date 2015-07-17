package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, ForcesStep}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.model.HasAddressOption

@Singleton
class AddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with AddressFirstMustache
  with AddressFirstForms
  with AddressForms {

  val validation = addressFirstForm

  val routing = Routes(
    get = routes.AddressFirstStep.get,
    post = routes.AddressFirstStep.post,
    editGet = routes.AddressFirstStep.editGet,
    editPost = routes.AddressFirstStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    currentState.address.map(_.address) match {
      case Some(address) =>
        if (address.exists(_.postcode.isEmpty)) forces.AddressStep
        else if (address.exists(_.manualAddress.isDefined)) forces.AddressManualStep
        else if (address.exists(_.uprn.isDefined)) forces.AddressSelectStep
        else forces.AddressStep
      case _ => forces.AddressStep
    }
  }

  def clearPreviousAddress(currentState: InprogressForces) = {
    currentState.address flatMap (_.hasAddress) match {
      case Some(HasAddressOption.No) => currentState.copy(previousAddress = None)
      case _ => currentState
    }
  }

  override val onSuccess = TransformApplication(clearPreviousAddress) andThen GoToNextStep()
}

