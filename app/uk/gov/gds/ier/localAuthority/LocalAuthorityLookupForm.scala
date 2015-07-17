package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.data.Forms._
import uk.gov.gds.ier.validation.PostcodeValidator
import play.api.data.validation.{Invalid, Valid, Constraint}

trait LocalAuthorityLookupForm {
  self: FormKeys =>

  lazy val localAuthorityLookupForm = ErrorTransformForm(
    mapping(
      keys.postcode.key -> optional(text)
        .verifying(postcodeNotEmpty)
    )
    (postcode => LocalAuthorityRequest(postcode.getOrElse("")))
    (localAuthRequest => Some(Some(localAuthRequest.postcode)))
    .verifying(isPostcodeValid)
  )

  lazy val postcodeNotEmpty = Constraint[Option[String]](keys.postcode.key) {
    case Some(postcode) if(postcode.nonEmpty) => Valid
    case _ => Invalid("ordinary_address_error_pleaseEnterYourPostcode", keys.postcode)
  }

  lazy val isPostcodeValid = Constraint[LocalAuthorityRequest](keys.postcode.key) {
    localAuthorityRequest =>
      PostcodeValidator.isValid(localAuthorityRequest.postcode) match {
        case true => Valid
        case _ => Invalid("ordinary_address_error_postcodeIsNotValid", keys.postcode)
      }
  }

}