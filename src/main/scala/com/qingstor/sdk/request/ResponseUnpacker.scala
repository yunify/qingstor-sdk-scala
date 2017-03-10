package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.util.QSRequestUtil
import spray.json.{JsValue, JsonFormat}
import com.qingstor.sdk.service.Types.QSJsonProtocol._

import scala.concurrent.{ExecutionContextExecutor, Future}

class ResponseUnpacker(_response: HttpResponse, _operation: Operation) {
  private val response = _response
  private val operation = _operation

  def unpackResponse(): QSHttpResponse = {
    val clazz = new QSHttpResponse()
    setupStatusCode(clazz)
    setupHeaders(clazz)
    setupEntity(clazz)
    clazz
  }

  private def setupStatusCode(obj: Any) = {
    val statusCode = response.status.intValue()
    QSRequestUtil.invokeMethod(obj,
      "setStatusCode",
      Array(int2Integer(statusCode)))
  }

  private def setupHeaders(obj: Any) = {
    if (ResponseUnpacker.isRightStatusCode(response.status.intValue(), operation.statusCodes)) {
      val headersNameAndGetMethodname =
        QSRequestUtil.getResponseParams(obj, QSConstants.ParamsLocationHeader)
      for ((headerName, methodName) <- headersNameAndGetMethodname) {
        val header = response.headers.find(h => h.name().equals(headerName))
        if (header.isDefined) {
          val setMethodName = methodName.replaceFirst("get", "set")
          QSRequestUtil.invokeMethod(obj,
            setMethodName,
            Array(header.get.value()))
        }
      }
    }
  }

  private def setupEntity(obj: Any) = {
    QSRequestUtil.invokeMethod(obj, "setEntity", Array(response.entity))
  }
}

object ResponseUnpacker {
  def apply(response: HttpResponse, operation: Operation) =
    new ResponseUnpacker(response, operation)

  def isRightStatusCode(code: Int, rightCodes: Array[Int] = Array[Int](200)): Boolean = {
    rightCodes.contains(code)
  }

  def unpackToOutputOrErrorMessage[T <: Output :JsonFormat]
    (futureResponse: Future[QSHttpResponse], rightStatusCodes: Array[Int])
    (implicit system: ActorSystem, mat: ActorMaterializer,
      ec: ExecutionContextExecutor): Future[Either[ErrorMessage, T]]= {
    futureResponse.flatMap { response =>
      if (isRightStatusCode(response.getStatusCode, rightStatusCodes)) {
        unpackToOutput[T](response).map(Right(_))
      } else {
        unpackToErrorMessage(response).map(Left(_))
      }
    }
  }

  def unpackToOutput[T <: Output :JsonFormat](response: QSHttpResponse)
                                             (implicit system: ActorSystem,
                                              mat: ActorMaterializer,
                                              ec: ExecutionContextExecutor): Future[T] = {
    Unmarshal(response.getEntity).to[JsValue].map(_.convertTo[T])
  }

  def unpackToErrorMessage(response: QSHttpResponse)
                          (implicit system: ActorSystem,
                           mat: ActorMaterializer,
                           ec: ExecutionContextExecutor): Future[ErrorMessage] = {
    response.getEntity.contentType match {
      case ContentTypes.`application/json` =>
        val errorFuture = Unmarshal(response.getEntity).to[JsValue]
        errorFuture.map(_.convertTo[ErrorMessage])
      case _ =>
        Future {ErrorMessage(
          requestID = response.getRequestID,
          statusCode = Some(response.getStatusCode)
        )}
    }
  }
}
