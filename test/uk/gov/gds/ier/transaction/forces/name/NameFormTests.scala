package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.test.FormTestSuite

class NameFormTests
  extends FormTestSuite
  with NameForms {

  it should "error out on empty json" in {
    val js = JsNull
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(5)
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("previousName") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your full name", "Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "describe all missing fields" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "",
        "name.middleNames" -> "joe",
        "name.lastName" -> "",
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.firstName" -> "",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> ""
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(8)
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name",
          "Please enter your first name",
          "Please enter your last name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "not accept whitespace" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "   ",
        "name.middleNames" -> "joe",
        "name.lastName" -> "   ",
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.firstName" -> "   ",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "   "
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(8)
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name",
          "Please enter your first name",
          "Please enter your last name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "require you to enter full names" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your full name",
          "Please enter your full previous name"
        ))
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name.firstName" -> Seq("Please enter your full name"),
          "name.lastName" -> Seq("Please enter your full name"),
          "previousName.previousName" -> Seq("Please enter your full previous name"),
          "previousName.previousName.firstName" -> Seq("Please enter your full previous name"),
          "previousName.previousName.lastName" -> Seq("Please enter your full previous name")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "check for too long names" in {
    val inputDataJson = Json.toJson(
      Map(
        "name.firstName" -> textTooLong,
        "name.middleNames" -> textTooLong,
        "name.lastName" -> textTooLong,
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.firstName" -> textTooLong,
        "previousName.previousName.middleNames" -> textTooLong,
        "previousName.previousName.lastName" -> textTooLong
      )
    )
    nameForm.bind(inputDataJson).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name.firstName" -> Seq("First name can be no longer than 35 characters"),
          "name.middleNames" -> Seq("Middle names can be no longer than 100 characters"),
          "name.lastName" -> Seq("Last name can be no longer than 35 characters"),
          "previousName.previousName.firstName" -> Seq("Previous first name can be no longer than 35 characters"),
          "previousName.previousName.middleNames" -> Seq("Previous middle names can be no longer than 100 characters"),
          "previousName.previousName.lastName" -> Seq("Previous last name can be no longer than 35 characters")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "name.middleNames" -> "joe",
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.middleNames" -> "joe"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(8)
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))


        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name",
          "Please enter your first name",
          "Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a missing field" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "john",
        "name.middleNames" -> "joe",
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.middleNames" -> "joe",
        "previousName.previousName.firstName" -> "john"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your last name","Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith",
        "previousName.hasPreviousName" -> "false",
        "previousName.hasPreviousNameOption" -> "false"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.name.isDefined should be(true)
        val name = success.name.get
        name.firstName should be("John")
        name.lastName should be("Smith")
        name.middleNames should be(Some("joe"))

        success.previousName.isDefined should be(true)
        val previousName = success.previousName.get
        previousName.previousName.isDefined should be(false)
        previousName.hasPreviousName should be(false)
        previousName.hasPreviousNameOption should be("false")
      }
    )
  }
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith",
        "previousName.hasPreviousName" -> "true",
        "previousName.hasPreviousNameOption" -> "true",
        "previousName.previousName.firstName" -> "Jonny",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "Bloggs"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.name.isDefined should be(true)
        val name = success.name.get

        name.firstName should be("John")
        name.lastName should be("Smith")
        name.middleNames should be(Some("joe"))

        success.previousName.isDefined should be(true)
        success.previousName.get.hasPreviousName should be(true)
        success.previousName.get.hasPreviousNameOption should be("true")
        val previousName = success.previousName.get
        previousName.previousName.get.firstName should be("Jonny")
        previousName.previousName.get.middleNames should be(Some("Joe"))
        previousName.previousName.get.lastName should be("Bloggs")
      }
    )
  }

  it should "ignore invalid input if previousName = false" in {
    val js = Map(
      "name.firstName" -> "John",
      "name.lastName" -> "Smith",
      "previousName.hasPreviousName" -> "false",
      "previousName.hasPreviousNameOption" -> "false",
      "previousName.firstName" -> "Jonny"
    )
    nameForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(name) = success.name
        name should have(
          'firstName ("John"),
          'middleNames (None),
          'lastName ("Smith")
        )
        val Some(previousName) = success.previousName
        previousName should have(
          'hasPreviousName (false),
          'hasPreviousNameOption ("false"),
          'previousName (None)
        )
      }
    )
  }
}

