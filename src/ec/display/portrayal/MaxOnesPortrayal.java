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
import ec.app.knock.MaxOnes;
import ec.app.knock.PhysicsModel;
import ec.simple.SimpleStatistics;
import ec.util.Log;
import ec.util.LogRestarter;
import ec.util.Output;
import ec.util.Parameter;
import ec.vector.FloatVectorIndividual;
import ec.vector.GeneVectorIndividual;

public class MaxOnesPortrayal extends IndividualPortrayal implements KeyListener {

    EvolutionState currentState;
    FloatVectorIndividual currentIndividual;
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

    public MaxOnesPortrayal() {
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
        this.currentIndividual = (FloatVectorIndividual) individual;
        this.currentState = state;
        
        int printIndividualLog = state.output.addLog(printIndividualWriter, restarter, 0, false, false);
        individual.printIndividualForHumans(state, printIndividualLog, Output.V_NO_GENERAL);
        textPane.setText("show selected");
        textPane.setCaretPosition(0);
        state.output.removeLog(printIndividualLog);
        printIndividualWriter.reset();
        
        if(((SimpleStatistics)this.currentState.statistics).best_of_run[0] != null) {
        printIndividualLog = state.output.addLog(printIndividualWriter, restarter, 0, false, false);
        ((SimpleStatistics) this.currentState.statistics).best_of_run[0]
        .printIndividualForHumans(state, printIndividualLog, Output.V_NO_GENERAL);
        textBestPane.setText("show best");
        textBestPane.setCaretPosition(0);
        state.output.removeLog(printIndividualLog);
        printBestIndividualWriter.reset();

        }
         
    }

    public void setup(EvolutionState state, Parameter base) {
    }

    public void keyTyped(KeyEvent ke) {
        if (ke.getSource() == this.textBestPane) {
            this.currentIndividual = (FloatVectorIndividual) ((SimpleStatistics) this.currentState.statistics).best_of_run[0];
        }

        MaxOnes.paused = true;
        MaxOnes p = (MaxOnes) currentState.evaluator.p_problem.clone();
        p.model = new PhysicsModel(LWJGL.getGL(), true);
        int frame = 0;
        double sum = 0.0;
        while (frame < p.frames && !Display.isCloseRequested()) {
            p.runFrame(currentState, currentIndividual, 0, 0, frame, p.model);
            Vector3f v = new Vector3f();
            frame++;
        }
        p.model.getDynamicsWorld().destroy();
        p.model.setDynamicsWorld(null);
        Display.destroy();
        p = null;

        MaxOnes.paused = false;
    }

    public void keyPressed(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }
}
