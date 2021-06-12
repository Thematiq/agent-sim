package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.Vector2D


object Supervisor {
    def apply(X: Int, Y: Int, initialPop: Int): Behavior[SupervisorCommand] =
        Behaviors.setup { context =>
            val city: Map[Vector2D, ActorRef[Command]] = Map()
            for (x <- 0 to X; y <- 0 to Y)
                city + (Vector2D(x, y) -> context.spawn(Cell(initialPop, Vector2D(x, y)), "Cell" + Vector2D.toString))

            Behaviors.receiveMessage {
                case Poison =>
                    city foreach (x => x._2 ! Poison)
                    Behaviors.stopped
                case GetRandomStateAt(pos, replyTo) =>
                    city(pos) ! GetRandomState(replyTo)
                    Behaviors.same
            }
        }
}