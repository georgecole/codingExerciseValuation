package com.company;


import java.time.Instant;

public class LogEntry {

    public LogEntry() {

    }
    private String id;
    private String state;
    /* I was going to use java.time.Instant but needed to add further jackson modules which felt long winded for the scope of this project
    com.fasterxml.jackson.datatype:jackson-datatype-jsr310
     */
    private Long timestamp;
    private String host;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
