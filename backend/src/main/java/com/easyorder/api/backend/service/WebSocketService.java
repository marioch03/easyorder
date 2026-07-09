package com.easyorder.api.backend.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyMesaStateChange(List<MesaDTO> mesas) {
        messagingTemplate.convertAndSend("/topic/mesas", mesas);
    }

    public void notifyPedidosStateChange(List<PedidoDTO> pedidos) {
        messagingTemplate.convertAndSend("/topic/pedidos", pedidos);
    }
}