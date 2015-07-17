package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.test.{FormTestSuite, WithMockAddressService}
import uk.gov.gds.ier.model.{PartialPreviousAddress,
  PartialManualAddress,
  Addresses,
  PartialAddress,
  MovedHouseOption
}

class PreviousAddressYesFormTests
  extends FormTestSuite
  with PreviousAddressForms
  with WithMockAddressService{

  behavior of "previous address form where user enters postcode"
  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )

    postcodeStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.previousAddress.isDefined should be(true)
        val Some(partialPreviousAddress) = success.previousAddress

        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get

        previousAddress.postcode should be("SW1A 1AA")
        previousAddress.uprn should be(None)
        previousAddress.manualAddress should be(None)
        previousAddress.addressLine should be (None)
      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map(
      "previousAddress.previousAddress.postcode" -> "")
    )

    postcodeStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.postcode" -> Seq("ordinary_previousAddress_postcode_error_enterPostcode")
        ))
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on empty input" in {
    val js = JsNull

    postcodeStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.postcode" -> Seq("ordinary_previousAddress_postcode_error_enterPostcode")
        ))
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on missing values in the input" in {
    val js = Json.toJson(Map("" -> ""))

    postcodeStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.postcode" -> Seq("ordinary_previousAddress_postcode_error_enterPostcode")
        ))
      },
      success => fail("Should have failed out")
    )
  }


  behavior of "previous address form with selection of addresses for given postcode"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A1AA"
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.previousAddress.isDefined should be(true)
        val partialPreviousAddress = success.previousAddress.get
        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> "SW1A1AA"
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.previousAddress.isDefined should be(true)
        val partialPreviousAddress = success.previousAddress.get
        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get
        previousAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        previousAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty input" in {
    val js = JsNull

    selectStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_previousAddress_select_error_answerThis"))
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.uprn" -> Seq("ordinary_previousAddress_select_error_answerThis")
        ))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty input values" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.address" -> "",
        "previousAddress.previousAddress.postcode" -> ""
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_previousAddress_select_error_answerThis"))
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.uprn" -> Seq("ordinary_previousAddress_select_error_answerThis")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind possible Address list" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.previousAddress.uprn" -> "12345678",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.previousAddress.isDefined should be(true)
        val Some(partialPreviousAddress) = success.previousAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get

        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

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
        "previousAddress.postcode" -> "SW1A 1AA",
        // no previousAddress.uprn here means no address selected
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.globalErrorMessages should be(Seq("ordinary_previousAddress_select_error_answerThis"))
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.uprn" -> Seq("ordinary_previousAddress_select_error_answerThis")
        ))
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
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        // no previousAddress.uprn here means no address selected
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.previousAddress.isDefined should be(true)
        val Some(partialPreviousAddress) = success.previousAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get

        previousAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        previousAddress.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "previousAddress.previousAddress.uprn" -> "87654321",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    selectStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.previousAddress.isDefined should be(true)
        val Some(partialPreviousAddress) = success.previousAddress

        success.possibleAddresses.isDefined should be(false)

        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get

        previousAddress.uprn should be(Some("87654321"))
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  behavior of "previous address form where user can enter manual address"

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress" -> "",
        "previousAddress.previousAddress.postcode" -> ""
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.manualAddress" -> Seq("ordinary_previousAddress_manual_error_answerThis")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "succeed on valid input" in {
    val js = Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> "SW1A1AA"
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        fail(hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.isDefined should be(true)
        val partialPreviousAddress = success.previousAddress.get
        partialPreviousAddress.previousAddress.isDefined should be(true)
        val previousAddress = partialPreviousAddress.previousAddress.get
        previousAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        previousAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty value for manual address" in {
    val js =  Json.toJson(
      Map(
        "address.manualAddress.lineOne" -> "",
        "address.manualAddress.lineTwo" -> "",
        "address.manualAddress.lineThree" -> "",
        "address.manualAddress.city" -> "",
        "previousAddress.postcode" -> "SW1A1AA"
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.manualAddress" -> Seq("ordinary_previousAddress_manual_error_answerThis")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty value in postcode for manual address" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> ""
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        // empty postcode does not have user friendly message
        // we assume UI guarantees it is present from previous step
        hasErrors.globalErrorMessages should be(Seq("ordinary_previousAddress_postcode_error_invalidPostcode", "ordinary_previousAddress_manual_error_answerThis"))
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.postcode" -> Seq("ordinary_previousAddress_postcode_error_invalidPostcode"),
          "previousAddress.previousAddress.manualAddress" -> Seq("ordinary_previousAddress_manual_error_answerThis")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on incorrect value in postcode for manual address" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> "3463463534"
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.postcode" -> Seq("ordinary_previousAddress_postcode_error_invalidPostcode")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json for manual address" in {
    val js =  JsNull

    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.manualAddress" -> Seq("ordinary_previousAddress_manual_error_answerThis")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty city for manual address" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.manualAddress.city" -> Seq("ordinary_previousAddress_manual_error_cityRequired")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty lineOne for manual address" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.previousAddress.manualAddress.lineOne" -> Seq("ordinary_previousAddress_manual_error_oneAddressLineRequired")
        ))
        hasErrors.globalErrorsAsText() should be("ordinary_previousAddress_manual_error_oneAddressLineRequired")
      },
      success => fail("Should have errored out")

    )
  }

  it should "not error out on empty lineTwo and lineThree for manual address" in {
    val js =  Json.toJson(
      Map(
        "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "previousAddress.previousAddress.manualAddress.lineTwo" -> "",
        "previousAddress.previousAddress.manualAddress.lineThree" -> "",
        "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    manualStepForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        val prevAddress = success.previousAddress.flatMap{ _.previousAddress }
        val manualAddress = prevAddress.flatMap{ _.manualAddress }

        manualAddress.isDefined should be(true)
        manualAddress.flatMap{ _.lineTwo } should be(None)
        manualAddress.flatMap{ _.lineThree } should be(None)
      }
    )
  }
}
