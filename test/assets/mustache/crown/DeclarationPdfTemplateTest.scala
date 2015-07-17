package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.declaration.DeclarationPdfMustache
import uk.gov.gds.ier.service.{DeclarationPdfDownloadService, WithDeclarationPdfDownloadService}
import uk.gov.gds.ier.test._

/**
 * Test rendering of Mustache template from given model
 */
class DeclarationPdfTemplateTest
  extends TemplateTestSuite
  with WithDeclarationPdfDownloadService
  with DeclarationPdfMustache {

  val declarationPdfDownloadService = mock[DeclarationPdfDownloadService]

  it should "properly render all properties from the model with just election authority URL" in {
    running(FakeApplication()) {
      val data = DeclarationPdfModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          title = "Page title ABC"
        ),
        declarationPdfUrl = "http://test/pdf_download",
        pdfFileSize = "999KB"
      )

      val templateName = mustache.asInstanceOf[MustacheTemplate].mustachePath
      val html = Mustache.render(templateName, data)
      val renderedContent = html.toString
      val doc = Jsoup.parse(renderedContent)

      renderedContent should include ("your local electoral registration office")
      renderedContent should include ("999KB")

      val f = doc.select("form").first() // there should be only one form in the template
      f should not be(null)
      f.attr("action") should be ("http://some.server/post_url")

      val h = doc.select("header").first() // there should be only one header in the template
      h should not be(null)
      h.text should include ("Page title ABC")
    }
  }
}
