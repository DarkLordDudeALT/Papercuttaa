package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// TODO Have names put in buildRandomDeathMessage() carry formatting (selector arguments.)

/**
 * A series of lists used to cleanly pick death messages.
 */
public final class DeathMessageLists {
    private static final Random random = new Random();

    // List of death messages for freeze effect execution and death to ice bombs.
    public final static ArrayList<String> FREEZE_DEATH_MESSAGES = new ArrayList<>(Arrays.asList(
            "{VICTIM} was straight iced", "{VICTIM} got a brain freeze", "{VICTIM} was shattered into a million pieces", "{VICTIM} forgot their blanket", "{VICTIM} couldn't handle the cold",
            "{VICTIM} forgot to close the freezer", "{VICTIM} got the cold shoulder", "{VICTIM} was frozen in time", "{VICTIM} was turned into a popsicle, funniest shit I've ever seen"
    ));

    /**
     * Constructs a death message from a list of strings.
     * Replaces all occurrences of "{VICTIM}" with victimName.
     *
     * @param messageList The list of messages to pull from.
     * @param victimName The name of the victim.
     *
     * @return The constructed deathMessage
     */
    public static String buildRandomDeathMessage(ArrayList<String> messageList, String victimName) {
        String[] splitMessage = messageList.get((int) (messageList.size() * random.nextDouble())).split("\\{VICTIM}");
        StringBuilder constructedMessage = new StringBuilder();

        for (int i = 0; i < splitMessage.length; i++) {
            constructedMessage.append(splitMessage[i]);

            if (i != splitMessage.length - 1)
                constructedMessage.append(victimName);
        }

        return constructedMessage.toString();
    }
}
