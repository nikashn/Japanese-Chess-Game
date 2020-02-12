import actions.*;
import pieces.*;

import java.lang.*;
import java.util.*;

/**
 * Main file for running the Shogi Japanese Chess game. Game is played with 2 players
 * on a 5x5 board. Best of luck!
 */

public class ShogiMain {

   public static void main(String[] args) {
      if (args.length == 0 || (!args[0].equals("-i"))) {
         usage();
         return;
      }
      interactive(new Board());
   }

   // Running the the game in interactively
   private static void interactive(Board board) {
      Scanner input = new Scanner(System.in);
      while (!board.gameOver()) {

         // Print state of board
         printState(board);

         String player = board.getCurrentPlayer();
         String otherPlayer = board.getOtherPlayer();

         // Detect check/checkmate
         printCheck(board, false);

         // Prompt and get action
         System.out.print(player + ">");
         String line = input.nextLine();
         Action action = parseAction(line);

         // Move piece or end via illegal move
         boolean succeeded = board.makeAction(action);
         if (succeeded) {
            System.out.println(player + " player action: " + action);
         } else {
            System.out.println(otherPlayer + " player wins.  Illegal move.");
         }
      }
      input.close();
   }

   // Displays checkmate or prints move to escape check
   private static void printCheck(Board board, boolean print) {
      // Detect check/checkmate
      if (board.inCheck()) {
         List<Action> possibleMoves = board.escapeCheck();
         if (possibleMoves.isEmpty()) {
            if (print) {
               System.out.println(board.getOtherPlayer() + " player wins.  Checkmate.");
            }
         } else {
            System.out.println(board.getCurrentPlayer() + " player is in check!");
            System.out.println("Available moves:");
            for (Action option : possibleMoves) {
               System.out.println(option);
            }
         }
      }
   }

   // Prints the game state of the board
   private static void printState(Board board) {
      // Print board state
      System.out.println(board);
      System.out.println("Captures UPPER:" + formatList(board.capturesUpper()));
      System.out.println("Captures lower:" + formatList(board.capturesLower()));
      System.out.println();
   }

   // Re-prompt the user due to input error
   private static void usage() {
      System.out.println("Usage: myShogi -[i]");
   }

   // Returns Action object representing player input
   private static Action parseAction(String line) {
      line = line.trim();
      String[] split = line.split(" ");
      if (split[0].equals("move")) {
         // Promote piece
         if (split.length == 4 && split[3].equals("promote")) {
            return new Move(split[1], split[2], true);
         }
         return new Move(split[1], split[2]);
      } else if (split[0].equals("drop")) {
         return new Drop(split[1], split[2]);
      }

      return null;
   }

   // Returns String of reason for game end
   private static String getEndMessage(Board board) {
      if (board.isIllegalMove()) {
         return board.getOtherPlayer() + " player wins.  Illegal move.";
      } else if (board.isCheckMated()) {
         return board.getOtherPlayer() + " player wins.  Checkmate.";
      } else {
         return "Tie game.  Too many moves.";
      }
   }

   // Returns String formatting the output of Piece captures
   private static String formatList(List<Piece> pieceList) {
      if (pieceList.isEmpty()) {
         return "";
      }
      String list = pieceList.toString();
      list = list.replaceAll(",", "");
      return " " + list.substring(1, list.length()-1);
   }

}

