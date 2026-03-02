package com.finhero.finhero.service;

import com.finhero.finhero.dto.DuplaResponse;
import com.finhero.finhero.exception.InvalidInviteCodeException;
import com.finhero.finhero.exception.SelfLinkException;
import com.finhero.finhero.exception.UserAlreadyInDuplaException;
import com.finhero.finhero.exception.UserNotFoundException;
import com.finhero.finhero.model.Dupla;
import com.finhero.finhero.repository.DuplaRepository;
import com.finhero.finhero.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class DuplaService {
    
    @Autowired
    private DuplaRepository duplaRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public DuplaResponse linkDupla(Long userId, String inviteCode) {
        log.info("Iniciando vinculação de dupla para userId: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            log.error("Usuário não encontrado: {}", userId);
            throw new UserNotFoundException("Usuário não encontrado");
        }
        
        var partnerUser = userRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> {
                log.warn("Código de convite inválido: {}", inviteCode);
                throw new InvalidInviteCodeException("Código de convite inválido");
            });
        
        if (userId.equals(partnerUser.getId())) {
            log.warn("Tentativa de auto-vinculamento: {}", userId);
            throw new SelfLinkException("Você não pode vincular-se a si mesmo");
        }
        
        if (duplaRepository.existsByUserId(userId)) {
            log.warn("Usuário {} já está em uma dupla", userId);
            throw new UserAlreadyInDuplaException("Você já está em uma dupla");
        }
        
        if (duplaRepository.existsByUserId(partnerUser.getId())) {
            log.warn("Parceiro {} já está em uma dupla", partnerUser.getId());
            throw new UserAlreadyInDuplaException("O usuário convidado já está em uma dupla");
        }
        
        Long userAId = userId < partnerUser.getId() ? userId : partnerUser.getId();
        Long userBId = userId < partnerUser.getId() ? partnerUser.getId() : userId;
        
        Dupla dupla = new Dupla();
        dupla.setUserAId(userAId);
        dupla.setUserBId(userBId);
        dupla.setCreatedAt(LocalDateTime.now());
        Dupla savedDupla = duplaRepository.save(dupla);
        
        log.info("Dupla criada com sucesso: id={}, userAId={}, userBId={}", 
                 savedDupla.getId(), savedDupla.getUserAId(), savedDupla.getUserBId());
        
        return new DuplaResponse(
            savedDupla.getId(),
            savedDupla.getUserAId(),
            savedDupla.getUserBId(),
            savedDupla.getCreatedAt()
        );
    }
    
    public DuplaResponse getDuplaByUserId(Long userId) {
        Dupla dupla = duplaRepository.findByUserId(userId)
            .orElse(null);
        
        if (dupla == null) {
            return null;
        }
        
        return new DuplaResponse(
            dupla.getId(),
            dupla.getUserAId(),
            dupla.getUserBId(),
            dupla.getCreatedAt()
        );
    }
}

