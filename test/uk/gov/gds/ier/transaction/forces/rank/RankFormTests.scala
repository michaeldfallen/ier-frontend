package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.test.FormTestSuite

class RankFormTests
  extends FormTestSuite
  with RankForms {

  it should "error out on empty json" in {
    val js = JsNull
    rankForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("rank.serviceNumber") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("rank.rank") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "describe all missing fields" in {
    val js = Json.toJson(
      Map(
        "rank.serviceNumber" -> "",
        "rank.rank" -> ""
      )
    )
    rankForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("rank.serviceNumber") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("rank.rank") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a missing field" in {
    val js = Json.toJson(
      Map(
        "rank.serviceNumber" -> "12345"
      )
    )
    rankForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("rank.rank") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "rank.serviceNumber" -> "12345",
        "rank.rank" -> "Captain"
      )
    )
    rankForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.rank.isDefined should be(true)
        val rank = success.rank.get
        rank.serviceNumber should be(Some("12345"))
        rank.rank should be(Some("Captain"))

      }
    )
  }
}

