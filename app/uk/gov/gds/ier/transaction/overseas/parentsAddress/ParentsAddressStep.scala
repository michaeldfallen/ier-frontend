package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ParentsAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with ParentsAddressLookupMustache
  with ParentsAddressForms
  with WithAddressService {

  val validation = parentsLookupAddressForm

  val routing = Routes(
    get = routes.ParentsAddressStep.get,
    post = routes.ParentsAddressStep.post,
    editGet = routes.ParentsAddressStep.editGet,
    editPost = routes.ParentsAddressStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.parentsAddress match {
      case Some(partialAddress) => {
        val postcode = partialAddress.postcode.trim.toUpperCase
        if (postcode.isEmpty)
          this
        else if (postcode.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else if (addressService.isScotland(postcode))
          GoTo (ExitController.scotland)
        else
          overseas.ParentsAddressSelectStep
      }
      case None => this
    }
  }
}
