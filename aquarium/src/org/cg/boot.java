package org.cg;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.cg.aquarium.Aquarium;
import org.cg.aquarium.bodies.ReferenceTriangle;
import org.cg.aquarium.infrastructure.KeyboardListener;

/**
 * Bootstrap class for Aquarium.
 *
 * @author ldavid
 */
public class boot {

    static final String TITLE = "Aquarium";
    private static Aquarium aquarium;

    public static void main(String[] args) {
        Frame frame = new Frame(TITLE);

        aquarium = Aquarium.getAquarium();

        frame.add(aquarium.getCanvas());
        frame.setSize(aquarium.getCanvas().getWidth(),
                aquarium.getCanvas().getHeight());

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(() -> {
                    aquarium.getAnimator().stop();
                    System.exit(0);
                }).start();
            }
        });

        aquarium.getCanvas().addKeyListener(new KeyboardListener());

        populateEcosystem();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    protected static void populateEcosystem() {
        System.out.print("Populating ecosystem... ");

        for (int i = 0; i < 1000; i++) {
            aquarium.addToEcosystem(new ReferenceTriangle());
        }

        System.out.println("Done.");
    }
}