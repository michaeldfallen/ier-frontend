package uk.gov.gds.ier.test

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import play.api.test._
import play.api.http._
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.ErrorMessages
import uk.gov.gds.ier.validation.FormKeys
import org.mockito.Mockito
import org.mockito.{Matchers => MockitoMatchers}
import org.mockito.AdditionalMatchers

abstract class UnitTestSuite
  extends FlatSpec
  with Matchers
  with TestHelpers

abstract class ControllerTestSuite
  extends FlatSpec
  with Matchers
  with TestHelpers
  with PlayHelpers

abstract class MockingControllerTestSuite
  extends FlatSpec
  with Matchers
  with TestHelpers
  with MockitoSugar
  with MockitoHelpers
  with PlayHelpers

abstract class MockingTestSuite
  extends FlatSpec
  with Matchers
  with TestHelpers
  with MockitoSugar
  with MockitoHelpers

abstract class FormTestSuite
  extends FlatSpec
  with Matchers
  with TestHelpers
  with FormKeys
  with ErrorMessages
  with WithSerialiser {
  val serialiser = jsonSerialiser
  val Json = play.api.libs.json.Json
  val JsNull = play.api.libs.json.JsNull
  val JsBoolean = play.api.libs.json.JsBoolean
  val JsObject = play.api.libs.json.JsObject
}

abstract class MustacheTestSuite
  extends FlatSpec
  with Matchers
  with TestHelpers
  with FormKeys
  with ErrorMessages
  with WithSerialiser
  with WithMockRemoteAssets
  with MockitoHelpers
  with WithMockConfig {
  val serialiser = jsonSerialiser
}

abstract class TemplateTestSuite
  extends FlatSpec
  with WithMockConfig
  with WithMockRemoteAssets
  with WithSerialiser
  with Matchers
  with StepMustache
  with TestHelpers
  with MockitoHelpers
  with PlayRunners {
  val Jsoup = new JsoupHelpers()
  val serialiser = jsonSerialiser
}

class JsoupHelpers {
  def parse(str:String) = org.jsoup.Jsoup.parse(str)
}

trait PlayHelpers
  extends PlayRunners
  with HeaderNames
  with Status
  with HttpProtocol
  with DefaultAwaitTimeout
  with ResultExtractors
  with Writeables
  with RouteInvokers
  with WsTestClient
  with FutureAwaits {

  val FakeRequest = play.api.test.FakeRequest
  val FakeHeaders = play.api.test.FakeHeaders
  val Call = play.api.mvc.Call
  type Controller = play.api.mvc.Controller
  val Cookie = play.api.mvc.Cookie
  val Action = play.api.mvc.Action
}

trait MockitoHelpers extends MockitoSugar {
  def when[A](methodCall:A) = Mockito.when(methodCall)
  def verify[A](mock:A) = Mockito.verify(mock)
  def verify[A,B](
      mock:A,
      times:org.mockito.verification.VerificationMode
  ) = Mockito.verify(mock, times)
  def never() = Mockito.never()
  def eq[A](a:A) = MockitoMatchers.eq(a)
  def any[A] = MockitoMatchers.any[A]
  def anyString() = MockitoMatchers.anyString()
  def isNot[T](obj:T) = AdditionalMatchers.not(eq(obj))
}
