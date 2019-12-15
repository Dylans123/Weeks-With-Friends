package com.gocoddi.coddi;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;
import java.util.HashMap;

@IgnoreExtraProperties
public class Event {
    public String title;
    public String description;
    public String date;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Event(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("date", date);
        return result;
    }

}