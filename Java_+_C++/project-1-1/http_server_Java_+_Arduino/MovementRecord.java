import java.util.ArrayList;
import java.util.List;

///  Represents a single movement command sent to the Arduino board. along with the
/// time (in milliseconds) that this command was executed.
public class MovementRecord
{
    private String command;
    private int duration;

    public MovementRecord(String command, int duration)
    {
        this.command = command;
        this.duration = duration;
    }

    public String getCommand()
    {
        return command;
    }

    public int getDuration()
    {
        return duration;
    }
}