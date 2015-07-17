package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class NameMustacheTest
  extends MustacheTestSuite
  with NameMustache
  with NameForms {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = nameForm
    val nameModel = mustache.data(
      emptyApplicationForm,
      Call("GET", "/register-to-vote/forces/name"),
      InprogressForces()
    ).asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/forces/name")

    nameModel.firstName.value should be("")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousNameOptionFalse.value should be("false")
    nameModel.hasPreviousNameOptionTrue.value should be("true")
    nameModel.hasPreviousNameOptionOther.value should be("other")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {
    val partiallyFilledApplicationForm = nameForm.fill(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"
      ))
    ))
    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("GET", "/register-to-vote/forces/name"),
      InprogressForces()
    ).asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/forces/name")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")
    nameModel.hasPreviousNameOptionFalse.value should be("false")
    nameModel.hasPreviousNameOptionTrue.value should be("true")
    nameModel.hasPreviousNameOptionOther.value should be("other")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant name and previous should produce Mustache Model with name and previous name values present" in {
    val partiallyFilledApplicationForm = nameForm.fill(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"
      )),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"
        ))
      ))
    ))
    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("GET", "/register-to-vote/forces/name"),
      InprogressForces()
    ).asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/forces/name")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")
    nameModel.hasPreviousNameOptionFalse.value should be("false")
    nameModel.hasPreviousNameOptionTrue.value should be("true")
    nameModel.hasPreviousNameOptionOther.value should be("other")
    nameModel.previousFirstName.value should be("Jan")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("Kovar")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationFormWithErrors = nameForm.fillAndValidate(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = ""
      ))
    ))
    val nameModel = mustache.data(
      partiallyFilledApplicationFormWithErrors,
      Call("GET", "/register-to-vote/forces/name"),
      InprogressForces()
    ).asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/forces/name")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousNameOptionFalse.value should be("false")
    nameModel.hasPreviousNameOptionTrue.value should be("true")
    nameModel.hasPreviousNameOptionOther.value should be("other")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")

    nameModel.question.errorMessages.toSet should be(
      Set("Please enter your last name", "Please answer this question")
    )
  }
}
