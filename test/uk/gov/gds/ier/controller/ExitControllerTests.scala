package uk.gov.gds.ier.controller

import uk.gov.gds.ier.test.ControllerTestSuite

class ExitControllerTests extends ControllerTestSuite {

  "ExitController.scotland" should "display the scotland exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/scotland"))
    status(result) should be(OK)
    contentAsString(result) should include("Voter registration forms for Scotland")

    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  it should "clear cookies correctly" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/scotland"))
    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  "ExitController.northernIreland" should "display the northern ireland exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/northern-ireland"))
    status(result) should be(OK)
    contentAsString(result) should include("Voter canvass form for Northern Ireland")
  }

  it should "clear cookies correctly" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/northern-ireland"))
    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }
}
