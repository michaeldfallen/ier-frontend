package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class AddressManualMustacheTests
  extends MustacheTestSuite
  with AddressForms
  with WithMockCrownControllers
  with AddressManualMustache {

  when(mockAddressManualStep.routing).thenReturn(routes("/register-to-vote/crown/address/manual"))
  when(mockAddressStep.routing).thenReturn(routes("/register-to-vote/crown/address"))

  it should "empty progress form should produce empty Model (manualData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/address/manual"),
      InprogressCrown()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("")
    addressModel.maLineOne.value should be ("")
    addressModel.maLineTwo.value should be ("")
    addressModel.maLineThree.value should be ("")
    addressModel.maCity.value should be ("")

  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(manualData) - lastAddress = yes and living there" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "WR26NJ",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/manual"),
      InprogressCrown()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(manualData) - lastAddress = yes and not living there" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndNotLivingThere),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "WR26NJ",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/manual"),
      InprogressCrown()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(manualData) - lastAddress = false" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "WR26NJ",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/manual"),
      InprogressCrown()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }

}
