package uk.gov.gds.ier.transaction.forces.dateOfBirth

import org.joda.time.DateTime
import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{DOB, DateOfBirth}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class DateOfBirthFormTests 
  extends FormTestSuite
  with DateOfBirthForms {

  it should "error out on empty json" in {
    val js = JsNull
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dob.dob.day") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.dob.year") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("Please enter your date of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your date of birth"))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "",
        "dob.dob.month" -> "",
        "dob.dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dob.dob.day") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.dob.year") should be(Seq("Please enter your date of birth"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("Please enter your date of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your date of birth"))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (month, year)" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "",
        "dob.dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("dob.dob.year") should be(Seq("Please enter your year of birth"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("Please enter your month of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your year of birth",
          "Please enter your month of birth"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (day, month)" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "",
        "dob.dob.month" -> "",
        "dob.dob.year" -> "1988"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("dob.dob.day") should be(Seq("Please enter your day of birth"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("Please enter your month of birth"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your month of birth",
          "Please enter your day of birth"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully bind a valid date" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> "1980"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.dob.isDefined should be(true)
        val Some(dob) = success.dob.get.dob
        dob.day should be(1)
        dob.month should be(12)
        dob.year should be(1980)
      }
    )
  }

  it should "successfully bind a valid date and ignore an invalid reason" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> "1980",
        "dob.noDob.reason" -> "", //shouldn't be empty
        "dob.noDob.range" -> "foo" //should be one of the 4 valid values
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.dob.isDefined should be(true)
        val Some(dob) = success.dob.get.dob
        dob.day should be(1)
        dob.month should be(12)
        dob.year should be(1980)
      }
    )
  }

  it should "error out on a date in the future" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> (DateTime.now().getYear + 1).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("dob.dob.day") should be(
          Seq("You have entered a date in the future")
        )
        hasErrors.errorMessages("dob.dob.month") should be(
          Seq("You have entered a date in the future")
        )
        hasErrors.errorMessages("dob.dob.year") should be(
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

  it should "error out on a date over 100 years old" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> (DateTime.now().getYear - 120).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dob.dob.year") should be(
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
        "dob.dob.day" -> "a",
        "dob.dob.month" -> "b",
        "dob.dob.year" -> "c"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(6)
        hasErrors.errorMessages("dob.dob.day") should be(
          Seq("The day you provided is invalid")
        )
        hasErrors.errorMessages("dob.dob.month") should be(
          Seq("The month you provided is invalid")
        )
        hasErrors.errorMessages("dob.dob.year") should be(
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

  it should "bind successfully on noDob reason and range" in {
    val js = Json.toJson(
      Map(
        "dob.noDob.reason" -> "Uh, yeah, I dunno",
        "dob.noDob.range" -> "18to70"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.dob.isDefined should be(true)
        val Some(dob) = success.dob
        dob.dob should be(None)
        dob.noDob.isDefined should be(true)
        val Some(noDob) = dob.noDob
        noDob.reason should be(Some("Uh, yeah, I dunno"))
        noDob.range should be(Some("18to70"))
      }
    )
  }

  it should "error out on invalid noDob reason" in {
     val js = Json.toJson(
      Map(
        "dob.noDob.reason" -> "",
        "dob.noDob.range" -> "18to70"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dob.noDob.reason") should be(
          Seq("Please provide a reason")
        )
        hasErrors.globalErrorMessages should be(
          Seq("Please provide a reason")
        )
      },
      success => fail("Should have thrown an error")
    )
  }
  it should "error out on invalid noDob range" in {
     val js = Json.toJson(
      Map(
        "dob.noDob.reason" -> "Uh, just cause",
        "dob.noDob.range" -> "blar"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dob.noDob.range") should be(
          Seq("Please select a rough age range")
        )
        hasErrors.globalErrorMessages should be(
          Seq("Please select a rough age range")
        )
      },
      success => fail("Should have thrown an error")
    )
  }
  it should "fill and validate correctly" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressForces(dob = Some(DateOfBirth(Some(DOB(1988, 1, 1)), None))))
    filledForm.errors should be(Nil)
  }

  it should "fill and validate error out correctly" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressForces(dob = None))
    filledForm.errors should not be(Nil)
  }
}
