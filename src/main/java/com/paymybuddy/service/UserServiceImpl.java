package com.paymybuddy.service;


import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.mapper.UserLoginMapper;
import com.paymybuddy.mapper.UserRegisterMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    private final UserLoginMapper userLoginMapper;
    private final UserRegisterMapper userRegisterMapper;

    public UserServiceImpl(UserRepository userRepository, UserLoginMapper userLoginMapper, UserRegisterMapper userRegisterMapper) {
        this.userRepository = userRepository;
        this.userLoginMapper = userLoginMapper;
        this.userRegisterMapper = userRegisterMapper;
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(UserRegisterDto userDto) {
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        logger.info("Création user, userName = '{}', email = '{}'", userDto.getUserName(), userDto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        // Validation avant la conversion
        if (userDto.getUserName() == null || userDto.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom  est obligatoire");
        }
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mail est obligatoire");
        }
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        User user = userRegisterMapper.toEntity(userDto);
        logger.info("Utilisateur ajouté avec succès : {}", user.getEmail());
        user.setDateCreate(LocalDateTime.now());//enregistre date de creation
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        // Vérifie si l'utilisateur existe selon l'ID
        Optional<User> existingUserOptional = userRepository.findById(user.getUserId());
        if (!existingUserOptional.isPresent()) {
            throw new IllegalArgumentException("User with this ID does not exist");
        }

        // récupère l'utilisateur existant
        User existingUser = existingUserOptional.get();

        // empêche la modification de l'email
        user.setEmail(existingUser.getEmail());
        // Garde la date de création inchangée
        user.setDateCreate(existingUser.getDateCreate());

        logger.info("Successfully updated user (email and creation date unchanged)");
        return userRepository.save(user);
    }


    @Override
    public void deleteUser(long id) {
        //verify user is exist or no (selon id)
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) {
            throw new IllegalArgumentException("User with this ID does not exist");
        }
        logger.info("Successful deleted user");
        userRepository.deleteById(id);
    }


    @Override
    public void verseSolde(long idUser, double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + idUser));

        BigDecimal montantAjoute = BigDecimal.valueOf(montant);
        BigDecimal soldeActuel = user.getSolde() != null ? user.getSolde() : BigDecimal.ZERO;
        BigDecimal nouveauSolde = soldeActuel.add(montantAjoute);

        user.setSolde(nouveauSolde);
        userRepository.save(user);
    }

//    @Override
//    public void transfertAmount(long idSource, long idCible, double montant) {
//        if (montant <= 0) {
//            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
//        }
//
//        User source = userRepository.findById(idSource)
//                .orElseThrow(() -> new EntityNotFoundException("Expéditeur non trouvé avec l'ID : " + idSource));
//
//        User cible = userRepository.findById(idCible)
//                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID : " + idCible));
//
//        BigDecimal montantTransfert = BigDecimal.valueOf(montant);
//
//        BigDecimal soldeSource = source.getSolde() != null ? source.getSolde() : BigDecimal.ZERO;
//
//        if (soldeSource.compareTo(montantTransfert) < 0) {
//            throw new IllegalArgumentException("Solde insuffisant pour effectuer le transfert.");
//        }
//
//        // Mise à jour des soldes
//        source.setSolde(soldeSource.subtract(montantTransfert));
//
//        BigDecimal soldeCible = cible.getSolde() != null ? cible.getSolde() : BigDecimal.ZERO;
//        cible.setSolde(soldeCible.add(montantTransfert));
//
//        // Sauvegarde des deux utilisateurs
//        userRepository.save(source);
//        userRepository.save(cible);
//    }


}



