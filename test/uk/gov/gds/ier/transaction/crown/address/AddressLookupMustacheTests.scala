package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.test.MustacheTestSuite
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class AddressLookupMustacheTest
  extends MustacheTestSuite
  with AddressForms
  with AddressLookupMustache {

  it should "empty progress form should produce empty Model (lookupData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/address/lookup"),
      InprogressCrown()
    ).asInstanceOf[LookupModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")

    addressModel.postcode.value should be ("")
  }

  it should "progress form with valid values should produce Mustache Model with values present"+
    " (lookupData) - lastUkAddress = true" in {
    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = Some("Fake street 123"),
          uprn = Some("1234567"),
          postcode = "WR26NJ",
          manualAddress = None
        ))
      ))
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/lookup"),
      InprogressCrown()
    ).asInstanceOf[LookupModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")

    addressModel.postcode.value should be ("WR26NJ")

  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(lookupData) - lastUkAddress = false" in {
    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          addressLine = Some("Fake street 123"),
          uprn = Some("1234567"),
          postcode = "WR26NJ",
          manualAddress = None
        ))
      ))
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/lookup"),
      InprogressCrown()
    ).asInstanceOf[LookupModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")

    addressModel.postcode.value should be ("WR26NJ")

  }
}
