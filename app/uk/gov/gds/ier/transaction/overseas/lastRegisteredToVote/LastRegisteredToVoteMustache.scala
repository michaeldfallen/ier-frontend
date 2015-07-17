package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.model.LastRegisteredType
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait LastRegisteredToVoteMustache extends StepTemplate[InprogressOverseas] {

  val title = "How were you last registered to vote?"

  case class LastRegisteredModel(
      question:Question,
      registeredType:Field,
      ukResident:Field,
      ukOverseas:Field,
      armedForces:Field,
      crownServant:Field,
      britishCouncil:Field,
      notRegistered:Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/lastRegisteredToVote") { (form, post) =>

    implicit val progressForm = form

    LastRegisteredModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      registeredType = Field(
        classes = if(form(keys.lastRegisteredToVote.registeredType).hasErrors) {
          "invalid"
        } else ""
      ),
      ukResident = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Ordinary.name
      ),
      ukOverseas = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Overseas.name
      ),
      armedForces = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Forces.name
      ),
      crownServant = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Crown.name
      ),
      britishCouncil = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Council.name
      ),
      notRegistered = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.NotRegistered.name
      )
    )
  }
}
