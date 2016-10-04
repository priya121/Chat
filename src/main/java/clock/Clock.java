package clock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Clock implements Time {

    @Override
    public String getTimeStamp() {
        return new SimpleDateFormat("hh:mm:ss a").format(new Date().getTime());
    }
}
