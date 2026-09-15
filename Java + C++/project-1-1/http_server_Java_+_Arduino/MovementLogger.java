import java.util.ArrayList;
import java.util.List;

///  It stores every movement sent (command + duration)
/// and provides access methods for reading the history.
public class MovementLogger {
    private List<MovementRecord> records = new ArrayList<>();

    /// Adds a new movement entry
    public void addRecord(String command, int duration) {
        records.add(new MovementRecord(command, duration));
    }

    /// Returns a copy of all stored records
    /// Copying prevents callers from directly modifying the internal list
    public List<MovementRecord> getAllRecords() {
        return new ArrayList<>(records); 
    }

    /// Returns the last added record (the current command)
    public MovementRecord getLastRecord() {
        if (records.isEmpty()) {
            return null;
        } else {
            return records.get(records.size() - 1);
        }
    }
}
