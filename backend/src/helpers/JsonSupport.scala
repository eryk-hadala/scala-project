package helpers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import controllers.{AuthController, WorkspacesController, IssuesController}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object LocalDateTimeFormat extends JsonFormat[LocalDateTime] {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => LocalDateTime.parse(s, formatter)
      case _ => throw DeserializationException("Date time string expected")
    }

    override def write(obj: LocalDateTime): JsValue = JsString(formatter.format(obj))
  }

  implicit val signInPayloadFormat: RootJsonFormat[AuthController.SignInPayload] = jsonFormat2(AuthController.SignInPayload.apply)
  implicit val signUpPayloadFormat: RootJsonFormat[AuthController.SignUpPayload] = jsonFormat4(AuthController.SignUpPayload.apply)
  implicit val updateUserPayloadFormat: RootJsonFormat[AuthController.UpdatePayload] = jsonFormat2(AuthController.UpdatePayload.apply)
  
  implicit val createWorkspacePayloadFormat: RootJsonFormat[WorkspacesController.CreatePayload] = jsonFormat1(WorkspacesController.CreatePayload.apply)
  implicit val inviteUserPayloadFormat: RootJsonFormat[WorkspacesController.InviteUserPayload] = jsonFormat1(WorkspacesController.InviteUserPayload.apply)
  implicit val updateWorkspacePayloadFormat: RootJsonFormat[WorkspacesController.UpdatePayload] = jsonFormat1(WorkspacesController.UpdatePayload.apply)

  implicit val createIssuePayloadFormat: RootJsonFormat[IssuesController.CreatePayload] = jsonFormat2(IssuesController.CreatePayload.apply)
  implicit val updateIssuePayloadFormat: RootJsonFormat[IssuesController.UpdatePayload] = jsonFormat2(IssuesController.UpdatePayload.apply)
  implicit val assignUserPayloadFormat: RootJsonFormat[IssuesController.SetAssigneesPayload] = jsonFormat1(IssuesController.SetAssigneesPayload.apply)
}
