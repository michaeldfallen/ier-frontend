package uk.gov.gds.ier.validation

import play.api.data.{Mapping, Field, FormError, Form}
import play.api.libs.json.JsValue
import play.api.i18n.Lang

case class ErrorTransformForm[T](private val form:Form[T]) {
  lazy val transformedForm = transformErrors(form)

  lazy val mapping : Mapping[T] = form.mapping
  lazy val data : Map[String, String] = form.data
  lazy val errors : Seq[FormError] = transformedForm.errors

  private[validation] lazy val value : Option[T] = form.value

  def apply(key : Key) = {
    transformedForm(key.key)
  }

  def bind(data : scala.Predef.Map[scala.Predef.String, scala.Predef.String]) : ErrorTransformForm[T] = {
    this.copy(form.bind(data))
  }
  def bind(data : play.api.libs.json.JsValue) : ErrorTransformForm[T] = {
    this.copy(form.bind(data))
  }
  def bindFromRequest()(implicit request : play.api.mvc.Request[_]) : ErrorTransformForm[T] = {
    val filledForm = form.bindFromRequest()
    val dataFromValue = this.fill(filledForm.value).data

    this.copy(form = filledForm.copy(data = dataFromValue ++ filledForm.data))
  }
  def bindFromRequest(data : Map[String, Seq[String]]) : ErrorTransformForm[T] = {
    this.copy(form.bindFromRequest(data))
  }
  def fill(value : T) : ErrorTransformForm[T] = {
    this.copy(form.fill(value))
  }
  def fill(value : Option[T]): ErrorTransformForm[T] = {
    value match {
      case Some(v) => this.fill(v)
      case None => this
    }
  }
  def fillAndValidate(value : T) : ErrorTransformForm[T] = {
    val filledForm = form.fillAndValidate(value)
    this.copy(form.bind(filledForm.data))
  }
  def fold[R](hasErrors : ErrorTransformForm[T] => R, success : T => R) : R = form.value match {
    case Some(v) if transformedForm.errors.isEmpty => success(v)
    case _ => hasErrors(this)
  }
  def globalError : Option[FormError] = {
    transformedForm.globalError
  }
  def globalErrors : Seq[FormError] = {
    transformedForm.globalErrors
  }
  def forField[R](key : Key)(handler : Field => R) : R = {
    handler(this(key))
  }
  def hasErrors : scala.Boolean = {
    transformedForm.hasErrors
  }
  def error(key : String) : Option[FormError] = {
    transformedForm.error(key)
  }
  def errors(key : String) : Seq[FormError] = {
    transformedForm.errors(key)
  }
  def hasGlobalErrors : Boolean = {
    transformedForm.hasGlobalErrors
  }
  def get : T = {
    form.get
  }
  def errorsAsJson(implicit lang : Lang) : JsValue = {
    transformedForm.errorsAsJson
  }
  def withError(error : FormError) : ErrorTransformForm[T] = {
    this.copy(form.withError(error))
  }
  def withError(key : String, message : String, args : Any*) : ErrorTransformForm[T] = {
    this.copy(form.withError(key, message, args:_*))
  }
  def withGlobalError(message : String, args : Any*) : ErrorTransformForm[T] = {
    this.copy(form.withGlobalError(message, args:_*))
  }
  def discardingErrors : ErrorTransformForm[T] = {
    this.copy(form.discardingErrors)
  }

  def transformErrors[T](errorForm: Form[T]):Form[T] = {
    val transformedErrors = errorForm.errors.flatMap(
      error => {
        val parseKeysForError = error.args.foldLeft(Seq.empty[Key]) (
          (sequence, arg) => if (arg.isInstanceOf[Key]) {
            sequence :+ arg.asInstanceOf[Key]
          } else {
            sequence
          }
        )

        val seqOfErrors = parseKeysForError.map( k =>
          error.copy(key = k.key)
        )

        error.copy(key = "") +: {if (seqOfErrors.isEmpty) Seq(error) else seqOfErrors}
      }
    )
    errorForm.copy(errors = transformedErrors)
  }

  def errorMessages(key:String) = this.errors(key).map(_.message)

  def globalErrorMessages = this.globalErrors.map(_.message)

  def prettyPrint = this.errors.map(error => s"${error.key} -> ${error.message}")
}

object ErrorTransformForm {
  def apply[T](mapping : Mapping[T]) : ErrorTransformForm[T] = {
    new ErrorTransformForm[T](Form(mapping))
  }
  def apply[T](mapping: (String, Mapping[T])): ErrorTransformForm[T] = {
    new ErrorTransformForm[T](Form(mapping))
  }
}
