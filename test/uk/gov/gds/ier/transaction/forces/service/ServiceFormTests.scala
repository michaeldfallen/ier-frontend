package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.ServiceType

class ServiceFormTests
  extends FormTestSuite
  with ServiceForms {

  it should "successfully bind values to a valid form" in {
    val js = Json.toJson(
      Map(
        "service.serviceName" -> "British Army",
        "service.regiment" -> "my regiment"
      )
    )
    serviceForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.service.isDefined should be(true)
        val service = success.service.get
        service.serviceName should be(Some(ServiceType.BritishArmy))
        service.regiment should be(Some("my regiment"))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    serviceForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("service.serviceName") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "service.serviceName" -> "",
        "service.regiment" -> ""
      )
    )
    serviceForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("service.serviceName") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

}
