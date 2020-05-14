package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.programmablegolems;

import org.bukkit.entity.ArmorStand;

public class ProgrammableGolemInstance {
    private final ArmorStand golem;
    //private final Deque<Frame> frameStack = new ArrayDeque<>();

    protected ProgrammableGolemInstance(ArmorStand golem) {
        this.golem = golem;
    }

    protected void tick() {

    }

    protected void synchronizedTick() {
        if (golem.isDead())
            ProgrammableGolemHandler.makeNotProgrammable(golem);
    }
}
