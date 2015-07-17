package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ServiceMustacheTest
  extends MustacheTestSuite
  with ServiceForms
  with ServiceMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = serviceForm

    val serviceModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/service"),
      InprogressForces()
    ).asInstanceOf[ServiceModel]

    serviceModel.question.title should be("Which of the services are you in?")
    serviceModel.question.postUrl should be("/register-to-vote/forces/service")

    serviceModel.serviceFieldSet.classes should be("")
    serviceModel.royalNavy.attributes should be("")
    serviceModel.britishArmy.attributes should be("")
    serviceModel.royalAirForce.attributes should be("")
    serviceModel.regiment.value should be("")
    serviceModel.regimentShowFlag.value should be("")
  }

  it should "fully filled applicant statement should produce Mustache Model with statement values present" in {

    val filledApp = InprogressForces(
      service = Some(Service(
        serviceName = Some(ServiceType.BritishArmy),
        regiment = Some("my regiment")
      ))
    )

    val filledForm = serviceForm.fillAndValidate(filledApp)

    val serviceModel = mustache.data(
      filledForm,
      Call("POST", "/register-to-vote/forces/service"),
      filledApp
    ).asInstanceOf[ServiceModel]

    serviceModel.question.title should be("Which of the services are you in?")
    serviceModel.question.postUrl should be("/register-to-vote/forces/service")

    serviceModel.serviceFieldSet.classes should be("")
    serviceModel.royalNavy.attributes should be("")
    serviceModel.britishArmy.attributes should be("checked=\"checked\"")
    serviceModel.royalAirForce.attributes should be("")
    serviceModel.regiment.value should be("my regiment")
    serviceModel.regimentShowFlag.value should be("-open")
  }
}
