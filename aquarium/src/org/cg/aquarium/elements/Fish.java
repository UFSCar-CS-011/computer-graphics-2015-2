package org.cg.aquarium.elements;

import com.sun.opengl.util.GLUT;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.cg.aquarium.Aquarium;
import org.cg.aquarium.infrastructure.base.Mobile;
import org.cg.aquarium.infrastructure.representations.Color;
import org.cg.aquarium.infrastructure.representations.Vector;

/**
 * Fish.
 *
 * Represents a fish of the shoal.
 *
 * @author ldavid
 */
public class Fish extends Mobile {

    public static final float MAXIMUM_SAFE_DISTANCE = 2;
    public static final float PREDATOR_DANGER_RADIUS = 1f;

    public static final float ALIGNMENT = .8f,
            COHERSION = .2f,
            SEPARATION = .4f,
            EVASION = 0,
            RANDOMNESS = .01f;

    protected Color color;
    protected Shoal shoal;

    public Fish(Shoal shoal) {
        this(shoal, Color.random());
    }

    public Fish(Shoal shoal, Color color) {
        super();

        this.shoal = shoal;
        setColor(color);
    }

    public Fish(Shoal shoal, Vector direction, float speed) {
        this(shoal, Color.random(), direction, speed);
    }

    public Fish(Shoal shoal, Color color, Vector direction, float speed) {
        super(direction, speed);

        this.shoal = shoal;
        setColor(color);
    }

    public Fish(Shoal shoal, Vector direction, float speed, Vector position) {
        this(shoal, Color.random(), direction, speed, position);
    }

    public Fish(Shoal shoal, Color color, Vector direction, float speed, Vector position) {
        super(direction, speed, position);

        this.shoal = shoal;
        setColor(color);
    }

    @Override
    public void display(GL gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();
        gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        gl.glTranslatef(position.getX(), position.getY(), position.getZ());
        glut.glutSolidSphere(1, 20, 20);
        gl.glPopMatrix();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void update() {
        Vector v = computeAlignment()
                .add(computeCohersion())
                .add(computeSeparation())
                .add(computeEvasion())
                .add(computeRandomness());

        setDirection(direction.add(v).normalize());

        move();
    }

    public boolean isPossiblyInDanger() {
        return distanceFromShoalCenter() > shoal.radius + MAXIMUM_SAFE_DISTANCE;
    }

    public float distanceFromShoalCenter() {
        return position.l2Distance(shoal.getPosition());
    }

    protected Vector computeAlignment() {
        return shoal.getDirection().scale(ALIGNMENT);
    }

    protected Vector computeCohersion() {
        return shoal.getPosition()
                .delta(position)
                .normalize()
                .scale(COHERSION * distanceFromShoalCenter() / shoal.radius);
    }

    protected Vector computeSeparation() {
        return shoal.getPosition().delta(position).reflected()
                .normalize().scale(SEPARATION);
    }

    protected Vector computeRandomness() {
        return Vector.random().normalize().scale(RANDOMNESS);
    }

    protected Vector computeEvasion() {
        Vector v = Vector.ZERO;

        Mobile predator = Aquarium.getAquarium().getPredator();

        if (predator != null) {
            float distance = predator.getPosition().squareDistance(position);

            if (distance < 200) {
                // Find a scape route and scale inversibly proportional to
                // distance between this and predator.
                v = predator.getDirection().cross(
                        predator.getDirection().mirrorOnVerticalAxis()
                ).scale(EVASION);
            }
        }

        return v;
    }
}
