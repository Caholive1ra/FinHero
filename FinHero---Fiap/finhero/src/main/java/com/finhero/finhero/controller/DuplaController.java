package com.finhero.finhero.controller;

import com.finhero.finhero.dto.DuplaResponse;
import com.finhero.finhero.dto.LinkDuplaDTO;
import com.finhero.finhero.service.DuplaService;
import com.finhero.finhero.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dupla")
@Slf4j
public class DuplaController {
    
    @Autowired
    private DuplaService duplaService;
    
    @PostMapping("/link")
    public ResponseEntity<DuplaResponse> linkDupla(@Valid @RequestBody LinkDuplaDTO dto) {
        log.info("Recebida requisição de vínculo de dupla");
        
        Long userId = SecurityUtil.getCurrentUserId();
        
        log.info("Usuário {} tentando vincular-se com código: {}", userId, dto.getInviteCode());
        
        DuplaResponse response = duplaService.linkDupla(userId, dto.getInviteCode());
        
        log.info("Dupla criada com sucesso para usuário: {}", userId);
        return ResponseEntity.ok(response);
    }
}

