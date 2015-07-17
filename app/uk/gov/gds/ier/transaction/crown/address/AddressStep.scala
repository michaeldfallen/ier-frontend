package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.controller.routes.ExitController
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with AddressLookupMustache
  with AddressForms {

  val validation = lookupAddressForm

  val routing = Routes(
    get = routes.AddressStep.get,
    post = routes.AddressStep.post,
    editGet = routes.AddressStep.editGet,
    editPost = routes.AddressStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    val fromNI = true
    val fromScotland = true
    val hasPostcode = true

    val address = currentState.address.flatMap(_.address)
    val postcode = address.exists(!_.postcode.trim.toUpperCase.isEmpty)
    val ni = address.exists(_.postcode.startsWith("BT"))
    val scot = address.exists(addr => addressService.isScotland(addr.postcode))

    (postcode, ni, scot) match {
      case (_, `fromNI`, _) => GoTo (ExitController.northernIreland)
      case (_, _, `fromScotland`) => GoTo (ExitController.scotland)
      case (`hasPostcode`, _, _) => crown.AddressSelectStep
      case _ => this
    }
  }

  override val onSuccess = {
    GoToNextStep()
  }
}
