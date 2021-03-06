 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package tests;

 import ai.core.AI;
 import ai.*;
 import ai.abstraction.WorkerRush;
 import ai.abstraction.pathfinding.BFSPathFinding;
 import ai.montecarlo.MonteCarlo;
 import com.jakegut.microrts.ExampleBot;
 import com.jakegut.microrts.fitness.TerminalFitnessFunction;
 import gui.PhysicalGameStatePanel;

 import javax.swing.JFrame;
 import rts.GameState;
 import rts.PhysicalGameState;
 import rts.PlayerAction;
 import rts.units.UnitTypeTable;

 import java.util.Arrays;

 /**
  *
  * @author santi
  */
 public class GameVisualSimulationTest {

     public static void runGameTest(AI ai1, UnitTypeTable utt) throws Exception {
         runGameTest(ai1, utt, new MonteCarlo(utt));
     }

     public static void runGameTest(AI ai1, UnitTypeTable utt, AI ai2) throws Exception {
         PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
//        PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();

         GameState gs = new GameState(pgs, utt);
         int PERIOD = 20;
         boolean gameover = false;

//         AI ai1 = new WorkerRush(utt, new BFSPathFinding());
//         AI ai1 = new MonteCarlo(utt);

         JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);


         long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
         do{
             if (System.currentTimeMillis()>=nextTimeToUpdate) {
                 PlayerAction pa1 = ai1.getAction(0, gs);
                 PlayerAction pa2 = ai2.getAction(1, gs);
                 gs.issueSafe(pa1);
                 gs.issueSafe(pa2);

                 // simulate:
                 gameover = gs.cycle();
                 w.repaint();
                 nextTimeToUpdate+=PERIOD;
             } else {
                 try {
                     Thread.sleep(1);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }while(!gameover);
         ai1.gameOver(gs.winner());
         ai2.gameOver(gs.winner());

         System.out.println(gs.winner());
         System.out.println(gs.getTime());

         System.out.println(Arrays.toString(new TerminalFitnessFunction().getFitness(gs).t1));

         System.out.println("Game Over");
     }
 }
