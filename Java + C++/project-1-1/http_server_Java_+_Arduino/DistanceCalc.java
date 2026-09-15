/// Calculates the distance travelled and the current driving speed from a MovementRecord.
public class DistanceCalc
{
    private String command;
    private int duration;
    private double distance;
    private double speed;

    //Constants for the calculation
    private static final double PI = 3.14;
    private static final double WHEEL_DIAMETER = 0.065; //meter
    private static final double MAX_RPM = 3000;
    private static final double MAX_MOTOR_SPEED = 255;
    private static final double MOTOR_SPEED = 60;


    public DistanceCalc(MovementRecord record)
    {
            this.command = record.getCommand();
            this.duration = record.getDuration();

            /// Distance per rotation in meters
            double wheelCircumfrenceSpeed = PI * WHEEL_DIAMETER;
            /// percentage rotation speed
            double percentRPM = MOTOR_SPEED/MAX_MOTOR_SPEED;
            double rpm = percentRPM * MAX_RPM;
            /// Rotations per second
            this.speed = wheelCircumfrenceSpeed * (rpm/60);
            /// Calculation of distance
            this.distance = this.speed * (this.duration/1000.0);
    }

    public String getCommand()
    {
        return command;
    }

    public int getDuration()
    {
        return duration;
    }

    public double getDistance()
    {
        return distance;
    }

    public double getSpeed()
    {
        return speed;
    }

}