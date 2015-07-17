package uk.gov.gds.ier.controller

import com.google.inject.Inject
import uk.gov.gds.ier.service.AddressService
import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.client.ApiResults
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.ier.exception.PostcodeLookupFailedException
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.model.Addresses
import com.google.inject.Singleton

@Singleton
class PostcodeController @Inject()(
    val addressService: AddressService,
    val serialiser: JsonSerialiser
  ) extends Controller with ApiResults with WithSerialiser with IerForms {

  def lookupAddress(postcode: String) = Action {
    implicit request =>
      postcodeForm.bind(Map("postcode" -> postcode)).fold(
        errors => badResult("errors" -> errors.errorsAsMap),
        postcode =>
          try {
            val addresses = addressService.lookupPartialAddress(postcode)
            okResult(Addresses(addresses))
          } catch {
            case e:PostcodeLookupFailedException => serverErrorResult("error" -> e.getMessage)
          }
      )
  }
}
