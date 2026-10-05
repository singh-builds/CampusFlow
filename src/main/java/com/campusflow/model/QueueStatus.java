package com.campusflow.model;

import com.campusflow.util.Json;

/** Live state of one service queue. Used by REST and by the TCP server. */
public class QueueStatus {
    private final int serviceId, waiting; private final String serviceName, nowServing;

    public QueueStatus(int serviceId, String serviceName, int waiting, String nowServing) {
        this.serviceId = serviceId; this.serviceName = serviceName; this.waiting = waiting; this.nowServing = nowServing;
    }
    public int getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public int getWaiting() { return waiting; }
    public String getNowServing() { return nowServing; }

    public String toJson() {
        return Json.obj("serviceId", serviceId, "serviceName", serviceName, "waiting", waiting, "nowServing", nowServing);
    }
}
