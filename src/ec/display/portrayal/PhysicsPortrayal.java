package ec.display.portrayal;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.CharArrayWriter;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.Display;

import com.bulletphysics.demos.opengl.LWJGL;

import ec.EvolutionState;
import ec.Individual;
import ec.app.physics.PhysicsModel;
import ec.app.physics.PhysicsProblem;
import ec.app.physics.PhysicsStatistics;
import ec.util.Log;
import ec.util.LogRestarter;
import ec.util.Output;
import ec.util.Parameter;

public class PhysicsPortrayal extends IndividualPortrayal implements KeyListener {

    EvolutionState currentState;
    Individual currentIndividual;
    private static final LogRestarter restarter = new LogRestarter() {

        public Log reopen(Log l)
                throws IOException {
            return null;
        }

        public Log restart(Log l)
                throws IOException {
            return null;
        }
    };
    final JTextPane textPane;
    final JTextPane textBestPane;
    final JPanel panel;
    private CharArrayWriter printIndividualWriter;
    private CharArrayWriter printBestIndividualWriter;

    public PhysicsPortrayal() {
        super(new BorderLayout());
        textPane = new JTextPane();
        textPane.setEditable(false);
        textBestPane = new JTextPane();
        textBestPane.setEditable(false);
        panel = new JPanel();
        this.add(panel);
        panel.add(textPane, BorderLayout.CENTER);
        panel.add(textBestPane, BorderLayout.CENTER);
        printIndividualWriter = new CharArrayWriter();
        printBestIndividualWriter = new CharArrayWriter();
        textPane.addKeyListener(this);
        textBestPane.addKeyListener(this);
    }

    public void portrayIndividual(EvolutionState state, Individual individual) {
        this.currentIndividual = individual;
        this.currentState = state;
        
        int printIndividualLog = state.output.addLog(printIndividualWriter, restarter, 0, false, false);
        individual.printIndividualForHumans(state, printIndividualLog, Output.V_NO_GENERAL);
        textPane.setText(printIndividualWriter.toString());
        textPane.setCaretPosition(0);
        state.output.removeLog(printIndividualLog);
        printIndividualWriter.reset();
        if(((PhysicsStatistics) this.currentState.statistics).bestSoFar[0] != null) {
        printIndividualLog = state.output.addLog(printIndividualWriter, restarter, 0, false, false);
        ((PhysicsStatistics) this.currentState.statistics).bestSoFar[0]
        .printIndividualForHumans(state, printIndividualLog, Output.V_NO_GENERAL);
        textBestPane.setText(printIndividualWriter.toString());
        textBestPane.setCaretPosition(0);
        state.output.removeLog(printIndividualLog);
        printBestIndividualWriter.reset();

        }
         
    }

    public void setup(EvolutionState state, Parameter base) {
    }

    public void keyTyped(KeyEvent ke) {
        if (ke.getSource() == this.textBestPane) {
            this.currentIndividual = ((PhysicsStatistics) this.currentState.statistics).bestSoFar[0];
        }

        PhysicsProblem.paused = true;
        PhysicsProblem p = (PhysicsProblem) currentState.evaluator.p_problem.clone();
        p.model = new PhysicsModel(LWJGL.getGL(), true);
        int frame = 0;
        double sum = 0.0;
        while (frame < 10000 && !Display.isCloseRequested()) {
            p.runFrame(currentState, currentIndividual, 0, 0, frame, p.model);
            Vector3f v = new Vector3f();
            p.model.ragdolls.get(0).bodies[2].getCenterOfMassPosition(v);
            sum += v.y / 2000f;

            frame++;
            if(frame==2000) {
                v = new Vector3f();
                p.model.ragdolls.get(0).bodies[2].getCenterOfMassPosition(v);
                System.out.println("Fitness: "+Math.abs(1100 - (Math.abs(v.x) + Math.abs(v.y)))+" evaludated:"+this.currentIndividual.evaluated);
            }
        }
        p.model.getDynamicsWorld().destroy();
        p.model.setDynamicsWorld(null);
        Display.destroy();
        p = null;

        PhysicsProblem.paused = false;
    }

    public void keyPressed(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }
}
