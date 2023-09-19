package util.speters33w;

/**
 * Returns ANSI color codes for the given color.
 * Written for a CSUGlobal project, but portable across other projects.
 *
 * @author Stephan Peters (speters33w)
 * bd = bold
 * hi = high intensity
 */
public class ANSIColor {
    /**
     * Returns ANSI color codes for the given color.
     *
     * @param color The color to return ANSI color codes for.
     * @return The ANSI color codes for the named color.
     */

    public static String color(String color) {
        color = color.toLowerCase();
        switch (color) {
            case "black":
                return "\u001B[30m";
            case "blackbd":
                return "\u001B[1;30m";
            case "blackhi":
                return "\u001B[0;90m";
            case "red":
                return "\u001B[31m";
            case "redbd":
                return "\u001B[1;31m";
            case "redhi":
                return "\u001B[0;91m";
            case "green":
                return "\u001B[32m";
            case "greenbd":
                return "\u001B[1;32m";
            case "greenhi":
                return "\u001B[0;92m";
            case "yellow":
                return "\u001B[33m";
            case "yellowbd":
                return "\u001B[1;33m";
            case "yellowhi":
                return "\u001B[0;93m";
            case "blue":
                return "\u001B[34m";
            case "bluebd":
                return "\u001B[1;34m";
            case "bluehi":
                return "\u001B[0;94m";
            case "purple":
                return "\u001B[35m";
            case "purplebd":
                return "\u001B[1;35m";
            case "purplehi":
                return "\u001B[0;95m";
            case "cyan":
                return "\u001B[36m";
            case "cyanbd":
                return "\u001B[1;36m";
            case "cyanhi":
                return "\u001B[0;96m";
            case "white":
                return "\u001B[37m";
            case "whitebd":
                return "\u001B[1;37m";
            case "whitehi":
                return "\u001B[0;97m";
            default: // reset to default color
                return "\u001B[0m";
        }
    }
}
