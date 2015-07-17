package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class LastUkAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with LastUkAddressLookupMustache
  with LastUkAddressForms
  with OverseasFormImplicits
  with WithAddressService {

  val validation = lookupAddressForm

  val routing = Routes(
    get = routes.LastUkAddressStep.get,
    post = routes.LastUkAddressStep.post,
    editGet = routes.LastUkAddressStep.editGet,
    editPost = routes.LastUkAddressStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.lastUkAddress match {
      case Some(partialAddress) => {
        val postcode = partialAddress.postcode.trim.toUpperCase
        if (postcode.isEmpty)
          this
        else if (postcode.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else if (addressService.isScotland(postcode))
          GoTo (ExitController.scotland)
        else
          overseas.LastUkAddressSelectStep
      }
      case None => this
    }
  }
}
