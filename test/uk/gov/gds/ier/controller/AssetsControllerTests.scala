package uk.gov.gds.ier.controller

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.config.Config
import play.api.mvc.{Filters, EssentialAction}
import uk.gov.gds.ier.filter.AssetsCacheFilter

class AssetsControllerTests extends ControllerTestSuite {
  private def createGlobalConfigWith(revisionNo: String) = {
    val mockConfig = new Config {
      override def revision = revisionNo
    }

    Some(new DynamicGlobal {
      override def bindings = { binder =>
        binder bind classOf[uk.gov.gds.ier.config.Config] toInstance mockConfig
      }

      override def doFilter(next: EssentialAction): EssentialAction = {
        Filters(super.doFilter(next), new AssetsCacheFilter(remoteAssets))
      }
    })
  }

  behavior of "Retrieving assets"
  it should "return asset without adding pragma: no-cache to the header for known sha" in {
    running(FakeApplication(withGlobal = createGlobalConfigWith("knownf1234567890knownf1234567890knownf12"))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/knownf1234567890knownf1234567890knownf12/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should not contain ("Pragma" -> "no-cache")
    }
  }

  it should "return asset without adding pragma: no-cache to the header for no sha" in {
    running(FakeApplication(withGlobal = createGlobalConfigWith("knownf1234567890knownf1234567890knownf12"))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should contain ("Pragma" -> "no-cache")
    }
  }

  it should "return asset with pragma: no-cache for unrecognised sha" in {
    running(FakeApplication(withGlobal = createGlobalConfigWith("knownf1234567890knownf1234567890knownf12"))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/atestf1234567890atestf1234567890atestf00/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should contain("Pragma" -> "no-cache")
    }
  }

  it should "return a response without pragma: no-cache for a non asset request" in {
    running(FakeApplication(withGlobal = createGlobalConfigWith("knownf1234567890knownf1234567890knownf12"))) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/country-of-residence")
        .withIerSession()
      )

      status(result) should be(OK)
      headers(result) should not contain("Pragma" -> "no-cache")
    }
  }

}
