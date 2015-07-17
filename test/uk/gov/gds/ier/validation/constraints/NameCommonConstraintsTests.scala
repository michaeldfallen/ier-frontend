package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.validation.{Invalid, Valid}

class NameCommonConstraintsTests
  extends FormTestSuite 
  with NameCommonConstraints {

  behavior of "NameCommonConstraints.firstNameTooLong"

  it should "be valid for a short name" in {
    firstNameNotTooLong.apply(Some(Name("NameNotTooLong", None, "Smith"))) should be(Valid)
  }

  it should "be invalid for a long name" in {
    firstNameNotTooLong.apply(Some(Name("NameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
      "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong",
      None, "Smith"))) should be(Invalid(firstNameMaxLengthError, keys.name.firstName))
  }

  behavior of "NameCommonConstraints.lastNameTooLong"

  it should "be valid for a short name" in {
    lastNameNotTooLong.apply(Some(Name("John", None, "NameNotTooLong"))) should be(Valid)
  }

  it should "be invalid for a long name" in {
    lastNameNotTooLong.apply(Some(Name("John", None, "NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong"))) should be(Invalid(lastNameMaxLengthError, keys.name.lastName))
  }

  behavior of "NameCommonConstraints.middleNamesTooLong"

  it should "be valid for a short name" in {
    middleNamesNotTooLong.apply(Some(Name("John", Some("NameNotTooLong"), "Smith"))) should be(Valid)
  }

  it should "be valid for a no middleName" in {
    middleNamesNotTooLong.apply(Some(Name("John", None, "Smith"))) should be(Valid)
  }

  it should "be invalid for a long name" in {
    middleNamesNotTooLong.apply(Some(Name("John", Some("NameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
      "NameNotTooLong"), "Smith"))) should be(Invalid(middleNameMaxLengthError, keys.name.middleNames))
  }


  behavior of "NameCommonConstraints.prevFirstNameTooLong"

  it should "be valid for a short name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("NameNotTooLong", None, "Smith"))
    ))

    prevFirstNameNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be invalid for a long name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("NameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong" +
        "NameIsWayTooLongNameIsWayTooLongNameIsWayTooLongNameIsWayTooLong",
        None, "Smith"))
    ))

    prevFirstNameNotTooLong.apply(samplePreviousName) should be(Invalid(
      previousFirstNameMaxLengthError,
      keys.previousName.previousName.firstName))
  }

  behavior of "NameCommonConstraints.prevLastNameTooLong"

  it should "be valid for a short name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("John", None, "NameNotTooLong"))
    ))

    prevLastNameNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be invalid for a long name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("John", None, "NameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLong"))
    ))

    prevLastNameNotTooLong.apply(samplePreviousName) should be(Invalid(
      previousLastNameMaxLengthError,
      keys.previousName.previousName.lastName))
  }

  behavior of "NameCommonConstraints.prevMiddleNamesTooLong"

  it should "be valid for a short name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("John", Some("NameNotTooLong"), "Smith"))
    ))
    prevMiddleNamesNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be valid for a no middleName" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("John", None, "Smith"))
    ))

    prevMiddleNamesNotTooLong.apply(samplePreviousName) should be(Valid)
  }

  it should "be invalid for a long name" in {
    val samplePreviousName = Some(PreviousName(
      hasPreviousName = true,
      hasPreviousNameOption = "true",
      previousName = Some(Name("John", Some("NameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLongNameNotTooLong" +
        "NameNotTooLong"), "Smith"))
    ))

    prevMiddleNamesNotTooLong.apply(samplePreviousName) should be(Invalid(
      previousMiddleNameMaxLengthError,
      keys.previousName.previousName.middleNames))
  }

}
