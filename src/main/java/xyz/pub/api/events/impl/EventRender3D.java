package xyz.pub.api.events.impl;

import xyz.pub.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class EventRender3D extends Event {



    public MatrixStack getMatrixStack() {
        return new MatrixStack();
    }

    public Game getContext() {
        return null;
    }

    @AllArgsConstructor @Getter
    public static class Game extends Event {
        private final RenderTickCounter tickCounter;
        private final MatrixStack matrixStack;

        public MatrixStack getMatrices() {
            return matrixStack;
        }

        public RenderTickCounter getTickCounter() {
            return tickCounter;
        }
    }

    @AllArgsConstructor @Getter
    public static class World extends Event {
        private final Camera camera;
        private final Matrix4f positionMatrix;
        private final RenderTickCounter tickCounter;

        public MatrixStack getMatrices() {
            MatrixStack matrices = new MatrixStack();
            matrices.peek().getPositionMatrix().mul(positionMatrix);
            return matrices;
        }

        public Camera getCamera() {
            return camera;
        }

        public RenderTickCounter getTickCounter() {
            return tickCounter;
        }

        public Matrix4f getPositionMatrix() {
            return positionMatrix;
        }
    }
}