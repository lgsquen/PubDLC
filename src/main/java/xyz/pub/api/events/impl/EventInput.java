package xyz.pub.api.events.impl;

import xyz.pub.api.events.Event;

/**
 * Event для обработки пользовательского ввода движения
 * Используется для коррекции движения при активных ротациях
 */
public class EventInput extends Event {

    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;
    private boolean sprint;

    public EventInput(float forward, float strafe, boolean jump, boolean sneak, boolean sprint) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sprint = sprint;
    }

    public EventInput(float forward, float strafe) {
        this(forward, strafe, false, false, false);
    }

    // Getters
    public float getForward() {
        return forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public boolean isJump() {
        return jump;
    }

    public boolean isSneak() {
        return sneak;
    }

    public boolean isSprint() {
        return sprint;
    }

    // Setters
    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }

    // Utility methods
    public boolean hasMovement() {
        return forward != 0.0f || strafe != 0.0f;
    }

    public void resetMovement() {
        this.forward = 0.0f;
        this.strafe = 0.0f;
    }

    public void normalizeMovement() {
        // Нормализация для диагонального движения
        if (hasMovement()) {
            float length = (float) Math.sqrt(forward * forward + strafe * strafe);
            if (length > 1.0f) {
                this.forward /= length;
                this.strafe /= length;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("EventInput{forward=%.2f, strafe=%.2f, jump=%s, sneak=%s, sprint=%s}",
                forward, strafe, jump, sneak, sprint);
    }
}