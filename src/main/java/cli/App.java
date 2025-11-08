package cli;

import java.io.PrintStream;

public class App {
  public static void main(String[] args) throws Exception {
    System.setOut(new PrintStream(System.out, true, "UTF-8"));

    new CommandProcessor().start();
  }
}
