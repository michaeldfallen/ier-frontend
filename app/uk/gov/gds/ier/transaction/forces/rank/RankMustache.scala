package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.model.{Statement}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait RankMustache extends  StepTemplate[InprogressForces] {

  case class RankModel(
     question:Question,
     serviceNumber: Field,
     rank: Field
  ) extends MustacheData

  private def displayPartnerSentence (application:InprogressForces): Boolean = {
    application.statement match {
      case Some(Statement(Some(false), Some(true))) => true
      case Some(Statement(None, Some(true))) => true
      case _ => false
    }
  }

  val mustache = MustacheTemplate("forces/rank") { (form, postUrl, application) =>
    implicit val progressForm = form

    val title = if (displayPartnerSentence(application)) {
      "What is your partner's service number?"
    } else {
      "What is your service number?"
    }

    RankModel(
      question = Question(
        postUrl = postUrl.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      serviceNumber = TextField(
        key = keys.rank.serviceNumber
      ),
      rank = TextField(
        key = keys.rank.rank
      )
    )
  }
}
