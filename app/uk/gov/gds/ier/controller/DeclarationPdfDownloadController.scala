package uk.gov.gds.ier.controller

import play.api.mvc.{ResponseHeader, SimpleResult, Action}
import play.api.mvc.Controller
import play.api.http.HeaderNames
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.service.DeclarationPdfDownloadService
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class DeclarationPdfDownloadController @Inject()(
  downloadService: DeclarationPdfDownloadService)
  extends Controller with HeaderNames with Logging {

  def download = Action {
    val result = SimpleResult(
      header = ResponseHeader(200,
        Map(
          CONTENT_TYPE -> "application/pdf",
          CONTENT_LENGTH -> downloadService.fileContentLength.toString,
          CONTENT_DISPOSITION -> "attachment; filename=\"crown-servant-declaration.pdf\""
        )),
      body = downloadService.fileContent
    )
    logger.info("Successfully prepared streaming out " + downloadService.pdfFileName)
    result
  }
}
