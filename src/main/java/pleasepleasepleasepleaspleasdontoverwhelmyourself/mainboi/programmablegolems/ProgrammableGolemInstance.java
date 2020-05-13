package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.programmablegolems;

import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;

// TODO Inject tick() into EntityArmorStand's tick().

public class ProgrammableGolemInstance {
    private final ArmorStand golem;
    private int ticks;
    //private final Deque<Frame> frameStack = new ArrayDeque<>();

    public ProgrammableGolemInstance(ArmorStand golem) {
        this.golem = golem;
    }

    public void tick() {
        if (golem.isDead()) {
            ProgrammableGolemHandler.makeNotProgrammable(golem);
            return;
        }

        if (ticks >= 20) {
            golem.getWorld().spawnParticle(Particle.BARRIER, golem.getLocation().add(0, 2, 0), 1);
            ticks = 0;
        }

        ticks++;
    }
}
