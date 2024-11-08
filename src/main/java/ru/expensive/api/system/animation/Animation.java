package ru.expensive.api.system.animation;

import lombok.Setter;
import lombok.experimental.Accessors;
import ru.expensive.common.util.math.Counter;

import static ru.expensive.api.system.animation.Direction.FORWARDS;

@Setter
@Accessors(chain = true)
public abstract class Animation implements AnimationCalculation {
    private final Counter counter = new Counter();
    protected int ms;
    protected double value;

    protected Direction direction = FORWARDS;

    public void reset() {
        counter.resetCounter();
    }

    public boolean isDone() {
        return counter.isReached(ms);
    }

    public boolean isFinished(Direction direction) {
        return this.direction == direction && isDone();
    }

    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            adjustTimer();
        }
    }

    private void adjustTimer() {
        counter.setTime(
                System.currentTimeMillis() - ((long) ms - Math.min(ms, counter.getTime())));
    }

    public Double getOutput() {
        double time = (1 - calculation(counter.getTime())) * value;

        return direction == FORWARDS
                ? endValue()
                : isDone() ? 0.0 : time;
    }

    private double endValue() {
        return isDone()
                ? value
                : calculation(counter.getTime()) * value;
    }
}
