package car_adverts_service.components

import car_adverts_service.shared.JsonResult
import org.scalajs.dom.ext.{Ajax, AjaxException}
import org.scalajs.dom.{XMLHttpRequest, _}
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by DongHee Kim on 2019-05-09 오후 6:15.
  */
trait SimpleAjax {

  // TODO: [2019-05-11 오후 11:23]  should get a cookie of the csrfToekn set by server. -DongHee Kim-
  val csrfToken:String = "changeme"

  lazy val headers = Map("Content-Type" -> "application/json", "Csrf-Token" -> csrfToken)
  lazy val defaultHeaders = Map("Csrf-Token" -> csrfToken)

  def resultByCommon[Protocol](xhr: XMLHttpRequest)(implicit jsonFormat: OFormat[Protocol]) = {
    Json.parse(xhr.responseText).validate[Protocol] match {
      case s: JsSuccess[Protocol] => s.asOpt
      case e: JsError => {
        console.error(s"An invalid data has been received.\nerror: ${e.toString}")
        console.log(xhr.responseText)
        //window.alert(xhr.responseText)
        e.asOpt
      }
    }
  }

  def resultByJsonResult(xhr: XMLHttpRequest)(implicit jsonFormat: OFormat[JsonResult]) = {
    Json.parse(xhr.responseText).validate[JsonResult] match {
      case s: JsSuccess[JsonResult] => Right(s.get)
      case e: JsError => {
        console.error(s"An invalid data has been received.\nerror: ${e.toString}")
        console.log(xhr.responseText)
        //window.alert(xhr.responseText)
        //e.asOpt
        Left(xhr.responseText)
      }
    }
  }

  def resultWithList[Protocol](xhr: XMLHttpRequest)(implicit jsonFormat: OFormat[Protocol]) = {
    Json.parse(xhr.responseText).validate[Seq[Protocol]] match {
      case s: JsSuccess[Seq[Protocol]] => s.asOpt
      case e: JsError => {
        console.error(s"An invalid data has been received.\nerror: ${e.toString}")
        e.asOpt
      }
    }
  }

  def recoverByCommon: PartialFunction[Throwable, XMLHttpRequest] = {
    case AjaxException(xhr: XMLHttpRequest) => {
      console.error(s"status = ${xhr.statusText}")
      xhr
    }
  }

  def resultByJaValue(xhr: XMLHttpRequest) = {
    Json.parse(xhr.responseText)
  }

  def post[Protocol](url: String, data: Protocol)(implicit jsonFormat: OFormat[Protocol]) = {
    Ajax.post(
      url = url,
      data = Json.prettyPrint(Json.toJson(data)),
      headers = headers
    ).recover(recoverByCommon).map(resultByCommon(_))
  }

  def post[P1, P2](url: String , data: P1)(implicit jsonFormat: OFormat[P1], jsonFormat2: OFormat[P2]) = {
    Ajax.post(
      url = url,
      data = Json.prettyPrint(Json.toJson(data)),
      headers = headers
    ).recover(recoverByCommon).map(resultByCommon[P2](_))
  }

  def postWithJsonResult[Protocol](url: String, data: Protocol)(implicit jsonFormat: OFormat[Protocol], jsonFormat2: OFormat[JsonResult]) = {
    Ajax.post(
      url = url,
      data = Json.prettyPrint(Json.toJson(data)),
      headers = headers
    ).recover(recoverByCommon).map(resultByJsonResult(_))
  }

  def put[Protocol](url: String, data: Protocol)(implicit jsonFormat: OFormat[Protocol], jsonFormat2: OFormat[JsonResult]) = {
    Ajax.put(
      url = url,
      data = Json.prettyPrint(Json.toJson(data)),
      headers = headers
    ).recover(recoverByCommon).map(resultByJsonResult(_))
  }

  def delete[Protocol](url: String)(implicit jsonFormat: OFormat[Protocol]) = {
    Ajax.delete(
      url = url,
      headers = headers
    ).recover(recoverByCommon).map(resultByCommon(_))
  }

  def delete[Protocol](url: String, data: Protocol)(implicit jsonFormat: OFormat[Protocol], jsonFormat2: OFormat[JsonResult]) = {
    Ajax.delete(
      url = url,
      data = Json.prettyPrint(Json.toJson(data)),
      headers = headers
    ).recover(recoverByCommon).map(resultByJsonResult(_))
  }

  def getList[Protocol](url: String)(implicit jsonFormat: OFormat[Protocol]) = {
    Ajax.get(
      url = url,
      headers = defaultHeaders
    ).map(resultWithList(_))
  }

  def get[Protocol](url: String)(implicit jsonFormat: OFormat[Protocol]) = {
    Ajax.get(
      url = url,
      headers = defaultHeaders,
    ).recover(recoverByCommon).map(resultByCommon(_))
  }
}
