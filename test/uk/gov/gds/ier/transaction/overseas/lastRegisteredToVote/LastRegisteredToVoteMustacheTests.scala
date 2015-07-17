package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class LastRegisteredToVoteMustacheTests
  extends MustacheTestSuite
  with LastRegisteredToVoteForms
  with LastRegisteredToVoteMustache {

  it should "produce empty model (empty input)" in {
    val form = lastRegisteredToVoteForm

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.question.title should be("How were you last registered to vote?")
    model.question.postUrl should be("http://postUrl")

    model.ukResident.name should be("lastRegisteredToVote.registeredType")
    model.ukResident.id should be("lastRegisteredToVote_registeredType_ordinary")
    model.ukResident.value should be("ordinary")

    model.armedForces.name should be("lastRegisteredToVote.registeredType")
    model.armedForces.id should be("lastRegisteredToVote_registeredType_forces")
    model.armedForces.value should be("forces")

    model.crownServant.name should be("lastRegisteredToVote.registeredType")
    model.crownServant.id should be("lastRegisteredToVote_registeredType_crown")
    model.crownServant.value should be("crown")

    model.britishCouncil.name should be("lastRegisteredToVote.registeredType")
    model.britishCouncil.id should be("lastRegisteredToVote_registeredType_council")
    model.britishCouncil.value should be("council")

    model.notRegistered.name should be("lastRegisteredToVote.registeredType")
    model.notRegistered.id should be("lastRegisteredToVote_registeredType_not-registered")
    model.notRegistered.value should be("not-registered")

    model.registeredType.classes should be("")
    model.ukResident.attributes should be("")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("")

  }

  it should "mark ukResident as checked (uk)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "ordinary")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.registeredType.classes should be("")
    model.ukResident.attributes should be("""checked="checked"""")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("")
  }

  it should "mark armedForces as checked (army)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "forces")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.registeredType.classes should be("")
    model.armedForces.attributes should be("""checked="checked"""")
    model.ukResident.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("")
  }

  it should "mark crownServant as checked (crown)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "crown")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.registeredType.classes should be("")
    model.ukResident.attributes should be("")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("""checked="checked"""")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("")
  }

  it should "mark britishCouncil as checked (council)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "council")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.registeredType.classes should be("")
    model.ukResident.attributes should be("")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("""checked="checked"""")
    model.notRegistered.attributes should be("")
  }

  it should "mark notRegistered as checked (not-registered)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "not-registered")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.registeredType.classes should be("")
    model.ukResident.attributes should be("")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("""checked="checked"""")
  }

  it should "mark registeredType as invalid (bad input)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "blargh")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.question.errorMessages should be(Seq("blargh is not a valid registration type"))

    model.registeredType.classes should be("invalid")
    model.ukResident.attributes should be("")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("")
  }

  it should "mark registeredType as invalid (no input)" in {
    val data = Map("lastRegisteredToVote.registeredType" -> "")
    val form = lastRegisteredToVoteForm.bind(data)

    val model = mustache.data(
      form,
      Call("GET", "http://postUrl"),
      InprogressOverseas()
    ).asInstanceOf[LastRegisteredModel]

    model.question.errorMessages should be(Seq("Please answer this question"))

    model.registeredType.classes should be("invalid")
    model.ukResident.attributes should be("")
    model.armedForces.attributes should be("")
    model.crownServant.attributes should be("")
    model.britishCouncil.attributes should be("")
    model.notRegistered.attributes should be("")
  }
}
