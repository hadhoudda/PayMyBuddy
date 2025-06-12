package com.paymybuddy.service;


import com.paymybuddy.service.contracts.IConnectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConnectionService implements IConnectionService {
}
