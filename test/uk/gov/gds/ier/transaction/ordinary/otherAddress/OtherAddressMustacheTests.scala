package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{OtherAddress}
import uk.gov.gds.ier.model.OtherAddress._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class OtherAddressMustacheTests
  extends MustacheTestSuite
  with OtherAddressForms
  with OtherAddressMustache {

  behavior of "OtherAddressMustache"
  it should "create model from empty form correctly" in {
    val form = otherAddressForm

    val result = mustache.data(
      form,
      Call("POST", "/some-post-url"),
      InprogressOrdinary()
    ).asInstanceOf[OtherAddressModel]

    result.question.postUrl should be("/some-post-url")
    result.question.number should be("step_a_of_b")
    result.question.title should be("ordinary_otheraddr_title")
    result.question.errorMessages should be(Seq.empty)

    result.hasOtherAddressHome.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressHome.id should be("otherAddress_hasOtherAddress_secondHome")
    result.hasOtherAddressHome.attributes should be("")
    result.hasOtherAddressStudent.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressStudent.id should be("otherAddress_hasOtherAddress_student")
    result.hasOtherAddressStudent.attributes should be("")
    result.hasOtherAddressNone.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressNone.id should be("otherAddress_hasOtherAddress_none")
    result.hasOtherAddressNone.attributes should be("")

    result.hasOtherAddress.classes should be("")
  }

  it should "mark true checkbox when hasOtherAddress = student" in {
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(StudentOtherAddress))
    ))

    val result = mustache.data(
      form,
      Call("POST", "/some-post-url"),
      InprogressOrdinary()
    ).asInstanceOf[OtherAddressModel]

    result.hasOtherAddressStudent.attributes should be("checked=\"checked\"")
    result.hasOtherAddressHome.attributes should be("")
    result.hasOtherAddressNone.attributes should be("")
  }

  it should "mark true checkbox when hasOtherAddress = secondHome" in {
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(HomeOtherAddress))
    ))

    val result = mustache.data(
      form,
      Call("POST", "/some-post-url"),
      InprogressOrdinary()
    ).asInstanceOf[OtherAddressModel]

    result.hasOtherAddressHome.attributes should be("checked=\"checked\"")
    result.hasOtherAddressStudent.attributes should be("")
    result.hasOtherAddressNone.attributes should be("")
  }

  it should "mark false checkbox when hasOtherAddress = none" in {
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(NoOtherAddress))
    ))

    val result = mustache.data(
      form,
      Call("POST", "/some-post-url"),
      InprogressOrdinary()
    ).asInstanceOf[OtherAddressModel]

    result.hasOtherAddressStudent.attributes should be("")
    result.hasOtherAddressHome.attributes should be("")
    result.hasOtherAddressNone.attributes should be("checked=\"checked\"")
  }

  it should "display invalid with emtpy validated form" in {
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary())

    val result = mustache.data(
      form,
      Call("POST", "/some-post-url"),
      InprogressOrdinary()
    ).asInstanceOf[OtherAddressModel]

    result.hasOtherAddress.classes should be("invalid")
  }
}
