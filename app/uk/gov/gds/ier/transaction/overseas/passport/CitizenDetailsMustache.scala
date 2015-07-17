package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait CitizenDetailsMustache extends StepTemplate[InprogressOverseas] {

  val title = "When and how did you become a British citizen?"

  case class CitizenDetailsModel(
      question: Question,
      hasPassport: Field,
      bornInUk: Field,
      howBecameCitizen: Field,
      citizenDate: Field,
      citizenDateDay: Field,
      citizenDateMonth: Field,
      citizenDateYear: Field,
      birthplace: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/citizenDetails") { (form, post) =>

    implicit val progressForm = form

    CitizenDetailsModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        number = "7",
        title = title
      ),
      hasPassport =      TextField(keys.passport.hasPassport),
      bornInUk =         TextField(keys.passport.bornInsideUk),
      howBecameCitizen = TextField(keys.passport.citizenDetails.howBecameCitizen),
      citizenDateDay =   TextField(keys.passport.citizenDetails.dateBecameCitizen.day),
      citizenDateMonth = TextField(keys.passport.citizenDetails.dateBecameCitizen.month),
      citizenDateYear =  TextField(keys.passport.citizenDetails.dateBecameCitizen.year),
      citizenDate = Field(
        id = keys.passport.citizenDetails.dateBecameCitizen.asId(),
        classes = if (form(keys.passport.citizenDetails.dateBecameCitizen).hasErrors) {
          "invalid"
        } else ""
      ),
      birthplace = TextField(keys.passport.citizenDetails.birthplace)
    )
  }
}

