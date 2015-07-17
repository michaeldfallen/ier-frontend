package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.model.{Statement}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait ServiceMustache extends StepTemplate[InprogressForces] {

  case class ServiceModel (
      question:Question,
      serviceFieldSet: FieldSet,
      royalNavy: Field,
      britishArmy: Field,
      royalAirForce: Field,
      regiment: Field,
      regimentShowFlag: Text
  ) extends MustacheData

  private def displayPartnerSentence (application:InprogressForces): Boolean = {
    application.statement match {
      case Some(Statement(Some(false), Some(true))) => true
      case Some(Statement(None, Some(true))) => true
      case _ => false
    }
  }

  val mustache = MustacheTemplate("forces/service") { (form, postUrl, application) =>
    implicit val progressForm = form

    def makeRadio(serviceName:String) = {
      Field(
        id = keys.service.serviceName.asId(serviceName),
        name = keys.service.serviceName.key,
        attributes = if (progressForm(keys.service.serviceName).value == Some(serviceName))
          "checked=\"checked\"" else ""
      )
    }

    val title = if (displayPartnerSentence(application)) {
      "Which of the services is your partner in?"
    } else {
      "Which of the services are you in?"
    }

    ServiceModel(
      question = Question(
        postUrl = postUrl.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      serviceFieldSet = FieldSet(
        classes = if (progressForm(keys.service).hasErrors) "invalid" else ""
      ),
      royalNavy = makeRadio("Royal Navy"),
      britishArmy = makeRadio("British Army"),
      royalAirForce = makeRadio("Royal Air Force"),
      regiment = TextField(
        key = keys.service.regiment
      ),
      regimentShowFlag = Text (
        value = progressForm(keys.service.regiment).value.fold("")(_ => "-open")
      )
    )
  }

}
