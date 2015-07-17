package uk.gov.gds.ier.transaction.forces.waysToVote

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{WaysToVote, WaysToVoteType}
import play.api.libs.json.JsSuccess
import play.api.i18n.Lang

class WaysToVoteFormTests
  extends FormTestSuite
  with WaysToVoteForms {

  it should "error out on empty input" in {
    val emptyRequest = Map.empty[String, String]
    waysToVoteForm.bind(emptyRequest).fold(
      formWithErrors => {
        formWithErrors.errors("waysToVote").head.message should be ("Please answer this question")
        formWithErrors.globalErrorMessages should be (Seq("Please answer this question"))
      },
      formWithSuccess => fail("Should have thrown an error")
    )
  }

  it should "bind successfully on in person going to polling station option" in {
    val request = Json.toJson(
      Map(
        "waysToVote.wayType" -> "in-person"
      )
    )
    waysToVoteForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.waysToVote.isDefined should be(true)
        formWithSuccess.waysToVote should be(Some(WaysToVote(WaysToVoteType.InPerson)))
      }
    )
  }

  it should "bind successfully on by post option" in {
    val request = Json.toJson(
      Map(
        "waysToVote.wayType" -> "by-post"
      )
    )
    waysToVoteForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.waysToVote.isDefined should be(true)
        formWithSuccess.waysToVote should be(Some(WaysToVote(WaysToVoteType.ByPost)))
      }
    )
  }

  it should "bind successfully on by proxy option" in {
    val request = Json.toJson(
      Map(
        "waysToVote.wayType" -> "by-proxy"
      )
    )
    waysToVoteForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.waysToVote.isDefined should be(true)
        formWithSuccess.waysToVote should be(Some(WaysToVote(WaysToVoteType.ByProxy)))
      }
    )
  }

  it should "error out on incorrect way to vote type" in {
    val request = Json.toJson(
      Map(
        "waysToVote.wayType" -> "foofoo"
      )
    )
    waysToVoteForm.bind(request).fold(
      formWithErrors => {
        formWithErrors.errors("waysToVote.wayType").head.message should be ("Unknown type")
        formWithErrors.globalErrorMessages should be (Seq("Unknown type"))
      },
      formWithSuccess => fail("Should have thrown an error")
    )
  }
}
