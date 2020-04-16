// Simple Hello-World program based on Akka actors.
//
// Based on the code from Akka's tutorial/example suite:
// http://doc.akka.io/docs/akka/2.3.14/java/hello-world.html
//

package se205.td6_1;

public class Main {
  // Start-up of the Akka application.
  public static void main(String[] args) {
    // Simply instantiate the HelloWorld actor and off it goes ...
    akka.Main.main(new String[] { HelloWorld.class.getName() });
  }
}
