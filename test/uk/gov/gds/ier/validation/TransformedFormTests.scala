package uk.gov.gds.ier.validation

import uk.gov.gds.ier.test.UnitTestSuite
import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.test.FakeRequest
import play.api.data.Form

class TransformedFormTests extends UnitTestSuite {

  it should "validate like a form" in {
    val data = Map("foo" -> "john")
    val form = Form(single("foo" -> number))
    val boundForm = form.bind(data)

    val transformedForm = ErrorTransformForm(form)
    val boundTransformedForm = transformedForm.bind(data)
    boundTransformedForm.errors shouldNot be(boundForm.errors)
    boundTransformedForm.error("foo") should be(boundForm.error("foo"))
    boundTransformedForm.globalErrorMessages should be(Seq("error.number"))
  }

  it should "transform any errors that have Keys as args" in {
    lazy val constraint = Constraint[String] ("test constraint") {
      str => if (str == "John") Valid else Invalid("Not John", Key("foo.notJohn"))
    }
    val data = Map("foo" -> "jim")
    val form = Form(single("foo" -> text) verifying constraint)
    val boundForm = form.bind(data)

    val transformedForm = ErrorTransformForm(form)
    val boundTransformedForm = transformedForm.bind(data)
    boundTransformedForm.errors shouldNot be(boundForm.errors)
    boundTransformedForm.errorMessages("foo.notJohn") should be(Seq("Not John"))
    boundTransformedForm.globalErrorMessages should be(Seq("Not John"))
  }

  it should "not transform any errors that have Keys as args" in {
    lazy val constraint = Constraint[String] ("test constraint") {
      str => if (str == "John") Valid else Invalid("Not John")
    }
    val data = Map("foo" -> "jim")
    val form = Form(single("foo" -> text) verifying constraint)
    val boundForm = form.bind(data)

    val transformedForm = ErrorTransformForm(form)
    val boundTransformedForm = transformedForm.bind(data)
    boundTransformedForm.globalErrorMessages shouldNot be(Seq("Not John"))
  }

  behavior of "bind from request"
  case class Foo(bar1: String, bar2: String)
  val FooForm = ErrorTransformForm[Foo](
    mapping(
      "foo.bar1" -> default(text, "was_empty"),
      "foo.bar2" -> default(text, "was_empty")
    ) (
      Foo.apply
    ) (
      Foo.unapply
    ) verifying Constraint[Foo]("foo") {
      foo =>
        if(foo.bar1 == "fail me") Invalid("bar1 failed on purpose")
        else Valid
    }
  )

  it should "use data from key-value map for valid request" in {
    val sutForm = FooForm.bindFromRequest()(
      FakeRequest()
        .withFormUrlEncodedBody(
          "foo.bar1" -> "jim",
          "foo.bar2" -> "jam"
         )
    )

    sutForm.data should be(Map("foo.bar1" -> "jim", "foo.bar2" -> "jam"))
    sutForm.value should not be(None)
    sutForm.hasGlobalErrors should be(false)
  }

  it should "use data default data from object when keys are not present" in {
    val sutForm = FooForm.bindFromRequest()(
      FakeRequest()
    )

    sutForm.data should be(Map("foo.bar1" -> "was_empty", "foo.bar2" -> "was_empty"))
    sutForm.value should not be(None)
    sutForm.hasGlobalErrors should be(false)
  }

  it should "use data from key-value map for invalid request (object not mapped)" in {
    val sutForm = FooForm.bindFromRequest()(
      FakeRequest()
        .withFormUrlEncodedBody(
        "foo.bar1" -> "fail me",
        "foo.bar2" -> "jam"
      )
    )

    sutForm.data should be(Map("foo.bar1" -> "fail me", "foo.bar2" -> "jam"))
    sutForm.value should be(None)
    sutForm.hasGlobalErrors should be(true)
  }

  it should "fill any values not specified into the data map" in {
    val sutForm = FooForm.bindFromRequest()(
      FakeRequest()
        .withFormUrlEncodedBody(
        "foo.bar2" -> "jam"
      )
    )

    sutForm.data should be(Map("foo.bar1" -> "was_empty", "foo.bar2" -> "jam"))
    sutForm.value should not be(None)
    sutForm.hasGlobalErrors should be(false)
  }

}
