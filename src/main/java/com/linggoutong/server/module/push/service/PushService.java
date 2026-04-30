package com.linggoutong.server.module.push.service;

import com.linggoutong.server.module.push.dto.PushRequest;

public interface PushService {

    void sendToUser(Long userId, PushRequest request);

    void sendToAll(PushRequest request);
}
