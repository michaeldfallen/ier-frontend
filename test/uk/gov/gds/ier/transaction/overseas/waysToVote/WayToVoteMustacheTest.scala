package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

/**
 * Unit test to test form to Mustache model transformation.
 */
class WayToVoteMustacheTest
  extends MustacheTestSuite
  with WaysToVoteForms
  with WaysToVoteMustache {

  it should "produce valid empty model when application is empty" in {
    val emptyApplicationForm = waysToVoteForm
    val model = mustache.data(
      emptyApplicationForm,
      new Call("POST","/register-to-vote/overseas/ways-to-vote"),
      InprogressOverseas()
    ).asInstanceOf[WaysToVoteModel]

    model.question.title should be("How do you want to vote?")
    model.question.postUrl should be("/register-to-vote/overseas/ways-to-vote")

    model.byPost.value should be("by-post")
    model.byProxy.value should be("by-proxy")
    model.inPerson.value should be("in-person")

    model.byPost.attributes should be("")
    model.byProxy.attributes should be("")
    model.inPerson.attributes should be("")
  }

  it should "produce valid model with checked in-person when application has in-person filled" in {
    val emptyApplicationForm = waysToVoteForm.fill(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)))
    )

    val model = mustache.data(
      emptyApplicationForm,
      new Call("POST","/register-to-vote/overseas/ways-to-vote"),
      InprogressOverseas()
    ).asInstanceOf[WaysToVoteModel]

    model.question.title should be("How do you want to vote?")
    model.question.postUrl should be("/register-to-vote/overseas/ways-to-vote")

    model.byPost.value should be("by-post")
    model.byProxy.value should be("by-proxy")
    model.inPerson.value should be("in-person")

    model.byPost.attributes should be("")
    model.byProxy.attributes should be("")
    model.inPerson.attributes should be("checked=\"checked\"")
  }
}
