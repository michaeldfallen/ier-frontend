package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressManualMustacheTests
  extends MustacheTestSuite
  with AddressForms
  with WithMockForcesControllers
  with AddressManualMustache {

  when(mockAddressStep.routing).thenReturn(routes("/register-to-vote/forces/address"))

  it should "empty progress form should produce empty Model (manualData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.postcode.value should be ("")
    addressModel.maLineOne.value should be ("")
    addressModel.maLineTwo.value should be ("")
    addressModel.maLineThree.value should be ("")
    addressModel.maCity.value should be ("")

  }

  it should "have correct title hasAddress = yes and living there" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
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
            city = Some("Worcester")
          ))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What is your UK address?")
  }

  it should "have correct title hasAddress = yes and not living there" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
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
            city = Some("Worcester")
          ))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What is your UK address?")
  }

  it should "have correct title hasAddress = no" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
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
            city = Some("Worcester")
          ))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What was your last UK address?")
  }

  it should "populate a manual address from the form" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
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
            city = Some("Worcester")
          ))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }
}
