package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import uk.gov.gds.ier.test.FormTestSuite

class DateLeftSpecialFormsTests
  extends FormTestSuite
  with DateLeftSpecialForms {

  it should "error out on empty json" in {
    val js = JsNull
    dateLeftSpecialForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("dateLeftSpecial.month") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("dateLeftSpecial.year") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "dateLeftSpecial.month" -> "",
        "dateLeftSpecial.year" -> ""
      )
    )
    dateLeftSpecialForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("dateLeftSpecial.month") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("dateLeftSpecial.year") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing year" in {
    val js = Json.toJson(
      Map(
        "dateLeftSpecial.month" -> "10",
        "dateLeftSpecial.year" -> ""
      )
    )
    dateLeftSpecialForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dateLeftSpecial.year") should be(Seq(
          "Please enter the year when you left"))
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter the year when you left"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing month" in {
    val js = Json.toJson(
      Map(
        "dateLeftSpecial.month" -> "",
        "dateLeftSpecial.year" -> "2000"
      )
    )
    dateLeftSpecialForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dateLeftSpecial.month") should be(Seq(
          "Please enter the month when you left"))
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter the month when you left"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on future date" in {
    val js = Json.toJson(
      Map(
        "dateLeftSpecial.month" -> "10",
        "dateLeftSpecial.year" -> "2545"
      )
    )
    dateLeftSpecialForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("dateLeftSpecial.month") should be(Seq("You have entered a date in the future"))
        hasErrors.errorMessages("dateLeftSpecial.year") should be(Seq("You have entered a date in the future"))
        hasErrors.globalErrorMessages should be(Seq("You have entered a date in the future"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "bind successfully on valid year and month" in {
    val js = Json.toJson(
      Map(
        "dateLeftSpecial.month" -> "10",
        "dateLeftSpecial.year" -> "2000"
      )
    )
    dateLeftSpecialForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.dateLeftSpecial.isDefined should be(true)
        val Some(dateLeft) = success.dateLeftSpecial

        dateLeft.date.year should be(2000)
        dateLeft.date.month should be(10)
      }
    )
  }
}
