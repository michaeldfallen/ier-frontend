package uk.gov.gds.ier.transaction.overseas.address

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ ErrorTransformForm, ErrorMessages, FormKeys }
import uk.gov.gds.ier.model.{ OverseasAddress }
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Constraint, Valid, Invalid}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait AddressForms extends OverseasAddressConstraints {
    self: FormKeys with ErrorMessages =>

    lazy val addressMapping = mapping (
            keys.country.key -> optional(nonEmptyText),
            keys.addressLine1.key -> optional(nonEmptyText),
            keys.addressLine2.key -> optional(nonEmptyText),
            keys.addressLine3.key -> optional(nonEmptyText),
            keys.addressLine4.key -> optional(nonEmptyText),
            keys.addressLine5.key -> optional(nonEmptyText)

  ) (OverseasAddress.apply) (OverseasAddress.unapply)
    
    val addressForm = ErrorTransformForm(
        mapping(keys.overseasAddress.key -> optional(addressMapping).verifying (countryRequired, addressDetailsRequired))
        (overseasAddress => InprogressOverseas(address = overseasAddress))(inprogressOverseas => Some(inprogressOverseas.address))
    ) 
}

trait OverseasAddressConstraints extends CommonConstraints {
    self: FormKeys
    with ErrorMessages => 
        
    lazy val countryRequired = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
        optAddress => 
            optAddress match {
                case Some(address) if (!address.country.getOrElse("").trim.isEmpty) => Valid 
                case _ => Invalid("Please enter your country", keys.overseasAddress.country)
            }
    }
    lazy val addressDetailsRequired = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
        optAddress => 
            optAddress match {
                case Some(address) if (!address.addressLine1.getOrElse("").trim.isEmpty ||
                                       !address.addressLine2.getOrElse("").trim.isEmpty ||
                                       !address.addressLine3.getOrElse("").trim.isEmpty ||
                                       !address.addressLine4.getOrElse("").trim.isEmpty ||
                                       !address.addressLine5.getOrElse("").trim.isEmpty) => Valid
                case _ => Invalid("Please enter your address", keys.overseasAddress.addressLine1)
            }
    }
}