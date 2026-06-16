package com.example.guardian.server;

import com.example.guardian.server.controller.HealthController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void healthReturnsUp() {
        HealthController controller = new HealthController();

        assertEquals("UP", controller.health().get("status"));
    }
}
