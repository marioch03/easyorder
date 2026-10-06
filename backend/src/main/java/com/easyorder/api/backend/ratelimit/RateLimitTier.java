package com.easyorder.api.backend.ratelimit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitTier {

    AUTH_LOGIN(5, 60, "rl:login:"),
    CLIENTE_PEDIDO(10, 60, "rl:pedido:"),
    PUBLIC_DISCOVERY(30, 60, "rl:public:"),
    CLIENTE_GENERAL(60, 60, "rl:cliente:"),
    API_GLOBAL(120, 60, "rl:global:");

    private final long maxRequests;
    private final long windowSeconds;
    private final String keyPrefix;
}
