package net.mancke.microcart;

import com.codahale.metrics.health.HealthCheck;

/**
 * demo health check.
 * 
 * @author smancke
 * 
 */
public class Health extends HealthCheck {
    @Override
    protected Result check() {
        return Result.healthy();
    }
}
