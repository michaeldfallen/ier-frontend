package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{PartialManualAddress, Addresses, PartialAddress}

class AddressFormTests
  extends FormTestSuite
  with AddressForms {

  behavior of "AddressForms.addressForm"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "address.address.uprn" -> "12345678",
        "address.address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val lastUkAddress = success.address.get
        lastUkAddress.address.flatMap(_.uprn) should be(Some("12345678"))
        lastUkAddress.address.map(_.postcode) should be(Some("SW1A1AA"))
      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "address.address.manualAddress.lineTwo" -> "Moseley Road",
        "address.address.manualAddress.lineThree" -> "Hallow",
        "address.address.manualAddress.city" -> "Worcester",
        "address.address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val lastUkAddress = success.address.get
        lastUkAddress.address.flatMap(_.manualAddress) should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        lastUkAddress.address.map(_.postcode) should be(Some("SW1A1AA"))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please enter your postcode"))
        hasErrors.errorMessages("address.address.postcode") should be(Seq("Please enter your postcode"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "address.address.postcode" -> ""
      )
    )
    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please enter your postcode"))
        hasErrors.errorMessages("address.address.postcode") should be(Seq("Please enter your postcode"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "address.address.manualAddress" -> "",
        "address.address.postcode" -> ""
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind possible Address list" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD",
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "address.address.uprn" -> "12345678",
        "address.address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(lastUkAddress) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        lastUkAddress.address.flatMap(_.uprn) should be(Some("12345678"))
        lastUkAddress.address.map(_.postcode) should be(Some("SW1A 1AA"))

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "error out if it looks like you haven't selected your address" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "address.address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.address.uprn") should be(
          Seq("Please select your address")
        )
        hasErrors.globalErrorMessages should be(Seq("Please select your address"))
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  it should "not error if you haven't selected your address but there is a manual address" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "address.address.manualAddress.lineTwo" -> "Moseley Road",
        "address.address.manualAddress.lineThree" -> "Hallow",
        "address.address.manualAddress.city" -> "Worcester",
        "address.address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail("Should not fail"),
      success => {
        success.address.isDefined should be(true)
        val Some(lastUkAddress) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        lastUkAddress.address.flatMap(_.manualAddress) should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        lastUkAddress.address.map(_.postcode) should be(Some("SW1A 1AA"))

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "address.address.uprn" -> "87654321",
        "address.address.postcode" -> "SW1A 1AA",
        "address.possibleAddresses.jsonList" -> "",
        "address.possibleAddresses.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(lastUkAddress) = success.address

        success.possibleAddresses.isDefined should be(false)

        lastUkAddress.address.flatMap(_.uprn) should be(Some("87654321"))
        lastUkAddress.address.map(_.postcode) should be(Some("SW1A 1AA"))
      }
    )
  }

  behavior of "AddressForms.lookupForm"

  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "address.address.postcode" -> "SW1A 1AA"
      )
    )

    lookupAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(lastUkAddress) = success.address

        lastUkAddress.address.map(_.postcode) should be(Some("SW1A 1AA"))
        lastUkAddress.address.flatMap(_.uprn) should be(None)
        lastUkAddress.address.flatMap(_.manualAddress) should be(None)
        lastUkAddress.address.flatMap(_.addressLine) should be (None)
      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map("address.address.postcode" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on empty json" in {
    val js = JsNull

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on missing values" in {
    val js = Json.toJson(Map("" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  behavior of "AddressForms.manualAddressForm"

  it should "succeed on valid input" in {
    val js = Json.toJson(
      Map(
        "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "address.address.manualAddress.lineTwo" -> "Moseley Road",
        "address.address.manualAddress.lineThree" -> "Hallow",
        "address.address.manualAddress.city" -> "Worcester",
        "address.address.postcode" -> "SW1A1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val lastUkAddress = success.address.get
        lastUkAddress.address.flatMap(_.manualAddress) should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        lastUkAddress.address.map(_.postcode) should be(Some("SW1A1AA"))
      }
    )
  }

  it should "error out on empty json for manual address" in {
    val js =  JsNull

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on all empty values for manual address" in {
    val js =  Json.toJson(
      Map(
        "address.address.manualAddress.lineOne" -> "",
        "address.address.manualAddress.lineTwo" -> "",
        "address.address.manualAddress.lineThree" -> "",
        "address.address.manualAddress.city" -> "",
        "address.address.postcode" -> "SW1A 1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on all empty lines for manual address" in {
    val js =  Json.toJson(Map(
      "address.address.manualAddress.lineOne" -> "",
      "address.address.manualAddress.lineTwo" -> "",
      "address.address.manualAddress.lineThree" -> "",
      "address.address.manualAddress.city" -> "Worcester",
      "address.address.postcode" -> "SW1A 1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "address.address.manualAddress" -> Seq("At least one address line is required")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind when lineOne is not empty" in {
    val js = Json.toJson(Map(
      "address.address.manualAddress.lineOne" -> "line one",
      "address.address.manualAddress.lineTwo" -> "",
      "address.address.manualAddress.lineThree" -> "",
      "address.address.manualAddress.city" -> "Worcester",
      "address.address.postcode" -> "SW1A1AA"
    ))

    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.address
        val Some(partialAddress) = lastUkAddress.address
        val Some(manualAddress) = partialAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = Some("line one"),
          lineTwo = None,
          lineThree = None,
          city = Some("Worcester")
        ))

        partialAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind when lineTwo is not empty" in {
    val js = Json.toJson(Map(
      "address.address.manualAddress.lineOne" -> "",
      "address.address.manualAddress.lineTwo" -> "line two",
      "address.address.manualAddress.lineThree" -> "",
      "address.address.manualAddress.city" -> "Worcester",
      "address.address.postcode" -> "SW1A1AA"
    ))

    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.address
        val Some(partialAddress) = lastUkAddress.address
        val Some(manualAddress) = partialAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = None,
          lineTwo = Some("line two"),
          lineThree = None,
          city = Some("Worcester")
        ))

        partialAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind when lineThree is not empty" in {
    val js = Json.toJson(Map(
      "address.address.manualAddress.lineOne" -> "",
      "address.address.manualAddress.lineTwo" -> "",
      "address.address.manualAddress.lineThree" -> "line three",
      "address.address.manualAddress.city" -> "Worcester",
      "address.address.postcode" -> "SW1A1AA"
    ))

    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.address
        val Some(partialAddress) = lastUkAddress.address
        val Some(manualAddress) = partialAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = None,
          lineTwo = None,
          lineThree = Some("line three"),
          city = Some("Worcester")
        ))

        partialAddress.postcode should be("SW1A1AA")
      }
    )
  }

}
