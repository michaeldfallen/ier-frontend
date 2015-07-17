package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import org.joda.time.DateTime
import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{DOB, DateOfBirth}
import play.api.i18n.Lang
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class DateOfBirthFormTests 
  extends FormTestSuite
  with DateOfBirthForms {

  it should "error out on empty json" in {
    val js = JsNull
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dob.day") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.year") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.month") should be(Seq("Please enter your date of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your date of birth"))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "",
        "dob.month" -> "",
        "dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dob.day") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.year") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.month") should be(Seq("Please enter your date of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your date of birth"))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (month, year)" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "",
        "dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("dob.year") should be(Seq("Please enter your year of birth"))
        hasErrors.errorMessages("dob.month") should be(Seq("Please enter your month of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your year of birth",
          "Please enter your month of birth"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (day, month)" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "",
        "dob.month" -> "",
        "dob.year" -> "1988"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("dob.day") should be(Seq("Please enter your day of birth"))
        hasErrors.errorMessages("dob.month") should be(Seq("Please enter your month of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your month of birth",
          "Please enter your day of birth"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully bind a valid date" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "12",
        "dob.year" -> "1980"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.dob.isDefined should be(true)
        val Some(dob) = success.dob
        dob.day should be(1)
        dob.month should be(12)
        dob.year should be(1980)
      }
    )
  }

  it should "error out on a date in the future" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "12",
        "dob.year" -> (DateTime.now().getYear + 1).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("dob.day") should be(
          Seq("You have entered a date in the future")
        )
        hasErrors.errorMessages("dob.month") should be(
          Seq("You have entered a date in the future")
        )
        hasErrors.errorMessages("dob.year") should be(
          Seq("You have entered a date in the future")
        )
        hasErrors.globalErrorMessages should be(
          Seq("You have entered a date in the future")
        )
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  it should "error out on a date over 120 years old" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "12",
        "dob.year" -> (DateTime.now().getYear - 120).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dob.year") should be(
          Seq("Please check the year you were born")
        )
        hasErrors.globalErrorMessages should be(Seq("Please check the year you were born"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a invalid date values" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "a",
        "dob.month" -> "b",
        "dob.year" -> "c"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(6)
        hasErrors.errorMessages("dob.day") should be(
          Seq("The day you provided is invalid")
        )
        hasErrors.errorMessages("dob.month") should be(
          Seq("The month you provided is invalid")
        )
        hasErrors.errorMessages("dob.year") should be(
          Seq("The year you provided is invalid")
        )
        hasErrors.globalErrorMessages should be(Seq(
          "The year you provided is invalid",
          "The month you provided is invalid",
          "The day you provided is invalid"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "fill and validate correctly" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressOverseas(dob = Some(DOB(1988, 1, 1))))
    filledForm.errors should be(Nil)
  }

  it should "fill and validate error out correctly" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressOverseas(dob = None))
    filledForm.errors should not be(Nil)
  }
}
