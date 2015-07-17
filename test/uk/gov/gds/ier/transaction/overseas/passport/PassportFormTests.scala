package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class PassportFormTests
  extends FormTestSuite
  with PassportForms {

  behavior of "PassportForms.passportCheckForm"
  it should "successfully bind (true)" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true"
      )
    )
    passportCheckForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.passport.isDefined should be(true)
        val Some(passport) = success.passport

        passport.hasPassport should be(true)
        passport.bornInsideUk should be(Some(true))
      }
    )
  }

  it should "successfully bind (false)" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "false",
        "passport.bornInsideUk" -> "false"
      )
    )
    passportCheckForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.passport.isDefined should be(true)
        val Some(passport) = success.passport

        passport.hasPassport should be(false)
        passport.bornInsideUk should be(Some(false))
      }
    )
  }

  it should "successfully bind (true, empty)" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true"
      )
    )
    passportCheckForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.passport.isDefined should be(true)
        val Some(passport) = success.passport

        passport.hasPassport should be(true)
        passport.bornInsideUk should be(None)
      }
    )
  }

  it should "error out on missing bornInsideUk (false, empty)" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "false"
      )
    )
    passportCheckForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.bornInsideUk") should be(
          Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error with empty json" in {
    val js = JsNull

    passportCheckForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please answer this question")
        hasErrors.errorMessages("passport.hasPassport") should be(error)
        hasErrors.errorMessages("passport.bornInsideUk") should be(error)
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  behavior of "PassportForms.passportDetailsForm"
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "12345",
        "passport.passportDetails.authority" -> "London",
        "passport.passportDetails.issueDate.day" -> "1",
        "passport.passportDetails.issueDate.month" -> "12",
        "passport.passportDetails.issueDate.year" -> "1990"
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.passport.isDefined should be(true)
        val Some(passport) = success.passport

        passport.hasPassport should be(true)
        passport.bornInsideUk should be(Some(true))

        passport.details.isDefined should be(true)
        val Some(details) = passport.details

        details.passportNumber should be("12345")
        details.authority should be("London")
        details.issueDate.day should be(1)
        details.issueDate.month should be(12)
        details.issueDate.year should be(1990)
      }
    )
  }

  it should "error out on empty passport details" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "",
        "passport.passportDetails.authority" -> "",
        "passport.passportDetails.issueDate.day" -> "",
        "passport.passportDetails.issueDate.month" -> "",
        "passport.passportDetails.issueDate.year" -> ""
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.passportDetails") should be(
          Seq("Please answer this question")
        )
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing passport details" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true"
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please answer this question")
        hasErrors.errorMessages("passport.passportDetails.passportNumber") should be(error)
        hasErrors.errorMessages("passport.passportDetails.authority") should be(error)
        hasErrors.errorMessages("passport.passportDetails.issueDate.day") should be(error)
        hasErrors.errorMessages("passport.passportDetails.issueDate.month") should be(error)
        hasErrors.errorMessages("passport.passportDetails.issueDate.year") should be(error)
        hasErrors.errorMessages("passport.passportDetails.issueDate") should be(error)
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty passport Number" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "",
        "passport.passportDetails.authority" -> "London",
        "passport.passportDetails.issueDate.day" -> "1",
        "passport.passportDetails.issueDate.month" -> "12",
        "passport.passportDetails.issueDate.year" -> "1990"
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please provide your Passport Number")
        hasErrors.errorMessages("passport.passportDetails") should be(error)
        hasErrors.errorMessages("passport.passportDetails.passportNumber") should be(error)
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty authority" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "12345",
        "passport.passportDetails.authority" -> "",
        "passport.passportDetails.issueDate.day" -> "1",
        "passport.passportDetails.issueDate.month" -> "12",
        "passport.passportDetails.issueDate.year" -> "1990"
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please provide your Authority or Place of Issue")
        hasErrors.errorMessages("passport.passportDetails") should be(error)
        hasErrors.errorMessages("passport.passportDetails.authority") should be(error)
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty issue date" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "12345",
        "passport.passportDetails.authority" -> "London",
        "passport.passportDetails.issueDate.day" -> "",
        "passport.passportDetails.issueDate.month" -> "",
        "passport.passportDetails.issueDate.year" -> ""
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.passportDetails.issueDate.day") should be(
          Seq("Please enter a day"))
        hasErrors.errorMessages("passport.passportDetails.issueDate.month") should be(
          Seq("Please enter a month"))
        hasErrors.errorMessages("passport.passportDetails.issueDate.year") should be(
          Seq("Please enter a year"))
        hasErrors.globalErrors.size should be(3)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on person older than 115 years" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "12345",
        "passport.passportDetails.authority" -> "London",
        "passport.passportDetails.issueDate.day" -> "1",
        "passport.passportDetails.issueDate.month" -> "1",
        "passport.passportDetails.issueDate.year" -> "1899"
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.passportDetails.issueDate.year") should be(
          Seq("Please check the passport issue year"))
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on future issue date" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.passportDetails.passportNumber" -> "12345",
        "passport.passportDetails.authority" -> "London",
        "passport.passportDetails.issueDate.day" -> "1",
        "passport.passportDetails.issueDate.month" -> "1",
        "passport.passportDetails.issueDate.year" -> "20000"
      )
    )

    passportDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.passportDetails.issueDate.day") should be(
          Seq("You have entered a date in the future"))
        hasErrors.errorMessages("passport.passportDetails.issueDate.month") should be(
          Seq("You have entered a date in the future"))
        hasErrors.errorMessages("passport.passportDetails.issueDate.year") should be(
          Seq("You have entered a date in the future"))
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  behavior of "PassportForms.citizenDetailsForm"
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.citizenDetails.howBecameCitizen" -> "Naturalisation",
        "passport.citizenDetails.dateBecameCitizen.day" -> "1",
        "passport.citizenDetails.dateBecameCitizen.month" -> "12",
        "passport.citizenDetails.dateBecameCitizen.year" -> "1990",
        "passport.citizenDetails.birthplace" -> "Wellington"
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.passport.isDefined should be(true)
        val Some(passport) = success.passport

        passport.hasPassport should be(true)
        passport.bornInsideUk should be(Some(true))

        passport.citizen.isDefined should be(true)
        val Some(citizen) = passport.citizen

        citizen.howBecameCitizen should be("Naturalisation")
        citizen.dateBecameCitizen.day should be(1)
        citizen.dateBecameCitizen.month should be(12)
        citizen.dateBecameCitizen.year should be(1990)
        citizen.birthplace should be("Wellington")
      }
    )
  }

  it should "error out on missing citizen details" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.citizenDetails.howBecameCitizen" -> "",
        "passport.citizenDetails.dateBecameCitizen.day" -> "",
        "passport.citizenDetails.dateBecameCitizen.month" -> "",
        "passport.citizenDetails.dateBecameCitizen.year" -> "",
        "passport.citizenDetails.birthplace" -> ""
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please answer this question")
        hasErrors.errorMessages("passport.citizenDetails") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.howBecameCitizen") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.day") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.month") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.year") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.birthplace") should be(error)
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing how became citizen explanation" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.citizenDetails.howBecameCitizen" -> "",
        "passport.citizenDetails.dateBecameCitizen.day" -> "1",
        "passport.citizenDetails.dateBecameCitizen.month" -> "12",
        "passport.citizenDetails.dateBecameCitizen.year" -> "1990",
        "passport.citizenDetails.birthplace" -> "Wellington"
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please provide your explanation of how you became a British Citizen")
        hasErrors.errorMessages("passport.citizenDetails") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.howBecameCitizen") should be(error)
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing birthplace" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.citizenDetails.howBecameCitizen" -> "Naturalisation",
        "passport.citizenDetails.dateBecameCitizen.day" -> "1",
        "passport.citizenDetails.dateBecameCitizen.month" -> "12",
        "passport.citizenDetails.dateBecameCitizen.year" -> "1990",
        "passport.citizenDetails.birthplace" -> ""
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => {
        val error = Seq("Please provide your town or city and county of birth")
        hasErrors.errorMessages("passport.citizenDetails") should be(error)
        hasErrors.errorMessages("passport.citizenDetails.birthplace") should be(error)
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing date became citizen" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "true",
        "passport.bornInsideUk" -> "true",
        "passport.citizenDetails.howBecameCitizen" -> "Naturalisation",
        "passport.citizenDetails.dateBecameCitizen.day" -> "",
        "passport.citizenDetails.dateBecameCitizen.month" -> "",
        "passport.citizenDetails.dateBecameCitizen.year" -> "",
        "passport.citizenDetails.birthplace" -> "Wellington"
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.day") should be(
          Seq("Please enter a day"))
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.month") should be(
          Seq("Please enter a month"))
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.year") should be(
          Seq("Please enter a year"))
        hasErrors.globalErrors.size should be(3)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on date became citizen date being in the future" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "false",
        "passport.bornInsideUk" -> "false",
        "passport.citizenDetails.howBecameCitizen" -> "Naturalisation",
        "passport.citizenDetails.dateBecameCitizen.day" -> "1",
        "passport.citizenDetails.dateBecameCitizen.month" -> "1",
        "passport.citizenDetails.dateBecameCitizen.year" -> "3000",
        "passport.citizenDetails.birthplace" -> "Wellington"
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.day") should be(
          Seq("You have entered a date in the future"))
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.month") should be(
          Seq("You have entered a date in the future"))
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.year") should be(
          Seq("You have entered a date in the future"))
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on date became citizen date being too long ago" in {
    val js = Json.toJson(
      Map(
        "passport.hasPassport" -> "false",
        "passport.bornInsideUk" -> "false",
        "passport.citizenDetails.howBecameCitizen" -> "Naturalisation",
        "passport.citizenDetails.dateBecameCitizen.day" -> "1",
        "passport.citizenDetails.dateBecameCitizen.month" -> "1",
        "passport.citizenDetails.dateBecameCitizen.year" -> "1800",
        "passport.citizenDetails.birthplace" -> "Wellington"
      )
    )

    citizenDetailsForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("passport.citizenDetails.dateBecameCitizen.year") should be(Seq("Please check the year you became a citizen"))
        hasErrors.globalErrors.size should be(1)
      },
      success => fail("Should have errored out")
    )
  }
}
