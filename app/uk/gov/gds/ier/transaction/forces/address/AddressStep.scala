package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import scala.Some
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with AddressLookupMustache
  with AddressForms {

  val validation = lookupAddressForm

  val routing = Routes(
    get = routes.AddressStep.get,
    post = routes.AddressStep.post,
    editGet = routes.AddressStep.editGet,
    editPost = routes.AddressStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {

    if (currentState.address.exists(_.address.exists(_.postcode.startsWith("BT"))))
      GoTo (ExitController.northernIreland)
    else if (currentState.address.exists(_.address.exists(addr => addressService.isScotland(addr.postcode))))
      GoTo (ExitController.scotland)
    else forces.AddressSelectStep
  }

  override val onSuccess = {
    GoToNextStep()
  }
}
