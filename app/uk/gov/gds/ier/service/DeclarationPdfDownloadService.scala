package uk.gov.gds.ier.service

import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play
import play.api.Play.current
import uk.gov.gds.ier.logging.Logging
import scala.concurrent.Await
import scala.concurrent.duration._

class DeclarationPdfDownloadService extends Logging {

  val pdfFileName = "/public/pdf/crown-servant-declaration-blank.pdf"

  private def computeFileContentLength = {
    val pdfFileStream = Play.resourceAsStream(pdfFileName) match {
      case Some(pdfFileStream) => pdfFileStream
      case None => throw new IllegalArgumentException(s"Play.resource($pdfFileName) returned None")
    }

    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(pdfFileStream)

    Await.result(
      fileContent.run(Iteratee.fold(0){(totalLength, chunk) => totalLength + chunk.length}),
      10 seconds)
  }

  lazy val fileContentLength = computeFileContentLength

  def fileContent() = {
    logger.info("About to stream out " + pdfFileName)
    val pdfFileStream = Play.resourceAsStream(pdfFileName) match {
      case Some(pdfFileStream) => pdfFileStream
      case None => throw new IllegalArgumentException(s"Play.resource($pdfFileName) returned None")
    }
    Enumerator.fromStream(pdfFileStream)
  }

  def fileSizeWithUnit = {
    (fileContentLength / 1024) + "KB"
  }
}

trait WithDeclarationPdfDownloadService {
  val declarationPdfDownloadService: DeclarationPdfDownloadService
}
