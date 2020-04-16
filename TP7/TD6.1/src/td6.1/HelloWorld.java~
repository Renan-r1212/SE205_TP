// Simple Hello-World program based on Akka actors.
//
// Based on the code from Akka's tutorial/example suite:
// http://doc.akka.io/docs/akka/2.3.14/java/hello-world.html
//

package se205.td6_1;

// Include necessary classes of Akka library
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.ActorRef;

// Main class extending an untyped actor.
public class HelloWorld extends UntypedActor {

  // Method invoked immediately after the creation of an actor.
  @Override
  public void preStart() {
    // create a Greeter actor
    final ActorRef greeter =
                   getContext().actorOf(Props.create(Greeter.class), "greeter");

    // tell the greeter to actually greet by sending a message
    greeter.tell(Greeter.Msg.GREET, getSelf());
  }

  // Method to handle incoming messages.
  @Override
  public void onReceive(Object msg) {
    if (msg == Greeter.Msg.DONE) {
      // when the greeter is done, stop this actor and with it the application
      getContext().stop(getSelf());
    } else {
      // signal that an unexpected message was received
      unhandled(msg);
    }
  }
}
