package uk.gov.gds.ier.serialiser

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.model.Success

class JsonSerialiserTests extends UnitTestSuite {

  it should "be able to serialise a simple class" in {
    val jsonString = jsonSerialiser.toJson(Success("bar",0))
    jsonString should be("""{"body":"bar","timeTakenMs":0}""")

    val mightbeFoo = jsonSerialiser.fromJson[Success](jsonString)
    mightbeFoo.body should be("bar")
  }
}
