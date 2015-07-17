package uk.gov.gds.ier.controller

import uk.gov.gds.ier.test.ControllerTestSuite

class RegisterToVoteControllerTests extends ControllerTestSuite {

  behavior of "RegisterToVoteController.registerToVote"
  it should "display the Register to Vote start page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote"))
      contentAsString(result) should include("/register-to-vote/start")
    }
  }

  behavior of "RegisterToVoteController.registerToVoteStart"
  it should "redirect to register-to-vote/country-of-residence" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/start"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/country-of-residence"))
    }
  }

  it should "pass query string parameters to the next page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/start?_ga=1234.TEST.4321"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/country-of-residence?_ga=1234.TEST.4321"))
    }
  }

  behavior of "RegisterToVoteController.registerToVoteOverseasStart"
  it should "redirect to register-to-vote/overseas/date-of-birth" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/overseas/start"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-of-birth"))
    }
  }

  it should "pass query string parameters to the next page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/overseas/start?_ga=1234.TEST.4321"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-of-birth?_ga=1234.TEST.4321"))
    }
  }

  behavior of "RegisterToVoteController.registerToVoteCrownStart"
  it should "redirect to register-to-vote/crown/statement" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/crown/start"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/statement"))
    }
  }

  it should "pass query string parameters to the next page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/crown/start?_ga=1234.TEST.4321"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/statement?_ga=1234.TEST.4321"))
    }
  }

  behavior of "RegisterToVoteController.registerToVoteForcesStart"
  it should "redirect to register-to-vote/forces/statement" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/forces/start"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/statement"))
    }
  }

  it should "pass query string parameters to the next page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/forces/start?_ga=1234.TEST.4321"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/statement?_ga=1234.TEST.4321"))
    }
  }


}
