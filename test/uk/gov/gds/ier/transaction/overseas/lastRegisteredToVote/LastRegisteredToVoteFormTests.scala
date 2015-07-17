package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.LastRegisteredType

class LastRegisteredToVoteFormTests
  extends FormTestSuite
  with LastRegisteredToVoteForms {

  it should "error out on empty json" in {
    val js = JsNull
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("lastRegisteredToVote.registeredType") should be(
          Seq("Please answer this question")
        )
        hasErrors.errorMessages("lastRegisteredToVote") should be(
          Seq("Please answer this question")
        )
        hasErrors.errors.size should be(3)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> ""
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("lastRegisteredToVote.registeredType") should be(
          Seq("Please answer this question")
        )
        hasErrors.errorMessages("lastRegisteredToVote") should be(
          Seq("Please answer this question")
        )
        hasErrors.errors.size should be(3)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on nonsense values" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "blarghh"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("lastRegisteredToVote.registeredType") should be(
          Seq("blarghh is not a valid registration type")
        )
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("blarghh is not a valid registration type"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "bind successfully to army" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "forces"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.lastRegisteredToVote.isDefined should be(true)
        val Some(lastRegisteredToVote) = success.lastRegisteredToVote

        lastRegisteredToVote.lastRegisteredType should be(LastRegisteredType.Forces)
      }
    )
  }

  it should "bind successfully to crown" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "crown"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.lastRegisteredToVote.isDefined should be(true)
        val Some(lastRegisteredToVote) = success.lastRegisteredToVote

        lastRegisteredToVote.lastRegisteredType should be(LastRegisteredType.Crown)
      }
    )
  }

  it should "bind successfully to council" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "council"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.lastRegisteredToVote.isDefined should be(true)
        val Some(lastRegisteredToVote) = success.lastRegisteredToVote

        lastRegisteredToVote.lastRegisteredType should be(LastRegisteredType.Council)
      }
    )
  }

  it should "bind successfully to uk" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "ordinary"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.lastRegisteredToVote.isDefined should be(true)
        val Some(lastRegisteredToVote) = success.lastRegisteredToVote

        lastRegisteredToVote.lastRegisteredType should be(LastRegisteredType.Ordinary)
      }
    )
  }

  it should "bind successfully to uk living abroad" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "overseas"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.lastRegisteredToVote.isDefined should be(true)
        val Some(lastRegisteredToVote) = success.lastRegisteredToVote

        lastRegisteredToVote.lastRegisteredType should be(LastRegisteredType.Overseas)
      }
    )
  }

  it should "bind successfully to not-registered" in {
    val js = Json.toJson(
      Map(
        "lastRegisteredToVote.registeredType" -> "not-registered"
      )
    )
    lastRegisteredToVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.lastRegisteredToVote.isDefined should be(true)
        val Some(lastRegisteredToVote) = success.lastRegisteredToVote

        lastRegisteredToVote.lastRegisteredType should be(LastRegisteredType.NotRegistered)
      }
    )
  }
}
