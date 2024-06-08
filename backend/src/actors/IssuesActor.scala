package actors

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector}
import akka.util.Timeout
import controllers.IssuesController.SetAssigneesPayload
import helpers.Database
import models.*
import slick.jdbc.SQLiteProfile.api.*
import upickle.default.*

import java.time.LocalDateTime
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}


object IssuesActor {

  case class GetIssuesResponse(id: Int, title: String, modifiedAt: String, createdAt: String, assignees: Seq[User])
    derives ReadWriter

  case class GetSingleIssueResponse(id: Int, owner: User, title: String, content: String, modifiedAt: String,
                                    createdAt: String, assignees: Seq[User])derives ReadWriter

  case class CreatePayload(title: String, content: String, userId: Int, workspaceId: Int)

  case class IssueData(title: String, content: String)

  case class IssueDeleted()

  case class AssigneesSetSuccessfully()

  sealed trait Command

  final case class GetIssuesByWorkspaceId(workspaceId: Int, replyTo: ActorRef[Seq[GetIssuesResponse]]) extends Command

  final case class GetById(issueId: Int, replyTo: ActorRef[Option[GetSingleIssueResponse]]) extends Command

  final case class CreateIssue(payload: CreatePayload, replyTo: ActorRef[Issue]) extends Command

  final case class UpdateIssue(issueId: Int, payload: IssueData, replyTo: ActorRef[Option[Issue]]) extends Command

  final case class DeleteIssue(issueId: Int, replyTo: ActorRef[IssueDeleted]) extends Command

  final case class SetAssignees(issueId: Int, payload: SetAssigneesPayload, replyTo: ActorRef[AssigneesSetSuccessfully])
    extends Command

  private val issues = TableQuery[Issues]
  private val userIssues = TableQuery[UserIssues]
  private val users = TableQuery[Users]

  def apply(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]): Behavior[Command] =
    Behaviors.setup { context =>

      implicit val timeout: Timeout = 3.seconds
      val selector = DispatcherSelector.fromConfig("blocking-dispatcher")
      implicit val executionContext: ExecutionContextExecutor = system.dispatchers.lookup(selector)


      Behaviors.receiveMessage {
        case GetIssuesByWorkspaceId(workspaceId, replyTo) =>
          replyTo ! getByWorkspaceId(workspaceId).map(issue =>
            GetIssuesResponse(issue.id, issue.title, issue.modifiedAt.toString,
              issue.createdAt.toString, getIssueUsers(issue.id)))
          Behaviors.same

        case GetById(issueId, replyTo) =>
          val issue = getById(issueId)

          issue match {
            case None =>
              replyTo ! None
              Behaviors.same
            case Some(issue) =>
              val ownerFuture: Future[Option[User]] = usersActor ? (ref => UsersActor.GetById(issue.ownerId, ref))

              ownerFuture.onComplete {
                case Failure(_) => replyTo ! None
                case Success(None) => replyTo ! None
                case Success(Some(owner)) =>
                  val response =
                    GetSingleIssueResponse(issue.id, owner, issue.title, issue.content,
                      issue.modifiedAt.toString, issue.createdAt.toString, getIssueUsers(issue.id))
                  replyTo ! Some(response)
              }
              Behaviors.same
          }

        case CreateIssue(payload, replyTo) =>
          val currentDateTime = LocalDateTime.now()
          val issue = Issue(0, payload.userId, payload.workspaceId, payload.title, payload.content, currentDateTime, currentDateTime)
          val inserted = insert(issue)
          Behaviors.same

        case UpdateIssue(issueId, payload, replyTo) =>
          val issueOption = update(issueId, payload.title, payload.content)
          replyTo ! issueOption
          Behaviors.same

        case DeleteIssue(issueId, replyTo) =>
          delete(issueId)
          replyTo ! IssueDeleted()
          Behaviors.same

        case SetAssignees(issueId, payload, replyTo) =>
          setAssignees(issueId, payload.userIds)
          replyTo ! AssigneesSetSuccessfully()
          Behaviors.same
      }
    }

  def getByWorkspaceId(workspaceId: Int): Seq[Issue] = {
    val query = issues.filter(_.workspaceId === workspaceId).result
    val result = Database.exec(query)
    result
  }

  def getById(id: Int): Option[Issue] = {
    val query = issues.filter(_.id === id).result.headOption
    val result: Option[Issue] = Database.exec(query)
    result
  }

  def getIssueUsers(issueId: Int): Seq[User] = {
    val query = (for {
      (ab, a) <- userIssues join users on (_.userId === _.id)
    } yield (a, ab.issueId)).filter(_._2 === issueId).map(_._1).result
    val result: Seq[User] = Database.exec(query)
    result.map(user => User(user.id, user.avatarUrl, user.username, user.email, "", user.modifiedAt, user.createdAt))
  }

  def insert(issue: Issue): Issue = {
    val query = (issues returning issues.map(_.id)) += issue
    val insertedId: Int = Database.exec(query)
    getById(insertedId).get
  }

  def update(id: Int, title: String, content: String): Option[Issue] = {
    val query = issues.filter(_.id === id)
      .map(oldUser => (oldUser.title, oldUser.content, oldUser.modifiedAt))
      .update((title, content, LocalDateTime.now()))
    Database.exec(query)
    getById(id)
  }

  private def delete(id: Int) = {
    val query = issues.filter(_.id === id).delete
    Database.exec(query)
  }

  private def setAssignees(issueId: Int, userIds: Seq[Int]) = {
    val currentDateTime: LocalDateTime = LocalDateTime.now()

    val deleteQuery = userIssues.filter(_.issueId === issueId).delete
    val insertQuery = userIssues ++= userIds.map(userId => (userId, issueId, currentDateTime))
    val result: Unit = Database.exec(DBIO.seq(deleteQuery, insertQuery).transactionally)
  }
}
