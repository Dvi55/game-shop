package com.gameshop.ecommerce.user.service;

import com.gameshop.ecommerce.auth.dao.VerificationTokenDAO;
import com.gameshop.ecommerce.auth.model.LoginBody;
import com.gameshop.ecommerce.auth.model.PasswordResetBody;
import com.gameshop.ecommerce.auth.model.RegistrationBody;
import com.gameshop.ecommerce.auth.model.VerificationToken;
import com.gameshop.ecommerce.exception.EmailFailureException;
import com.gameshop.ecommerce.exception.EmailNotFoundException;
import com.gameshop.ecommerce.exception.UserAlreadyExistException;
import com.gameshop.ecommerce.exception.UserNotVerifiedException;
import com.gameshop.ecommerce.security.EncryptionService;
import com.gameshop.ecommerce.security.JWTService;
import com.gameshop.ecommerce.user.dao.LocalUserDAO;
import com.gameshop.ecommerce.user.model.LocalUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final LocalUserDAO localUserDAO;
    private final EncryptionService encryptionService;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final VerificationTokenDAO verificationTokenDAO;


    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistException, EmailFailureException {
        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || localUserDAO.findByPhoneNumber(registrationBody.getPhoneNumber()).isPresent()) {
            throw new UserAlreadyExistException();
        }
        LocalUser user = new LocalUser();
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setEmail(registrationBody.getEmail());
        user.setAuthProvider("Registration Form");
        user.setAuthType("Site");
        user.setPhoneNumber(registrationBody.getPhoneNumber());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = generateVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        return localUserDAO.save(user);
    }

    private VerificationToken generateVerificationToken(LocalUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(loginBody.getEmail());
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.checkPassword(loginBody.getPassword(), user.getPassword())) {
                if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
                    return jwtService.generateJWT(user);
                } else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if (resend) {
                        VerificationToken verificationToken = generateVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
        if (opToken.isPresent()) {
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if (Boolean.FALSE.equals(user.getIsEmailVerified())) {
                user.setIsEmailVerified(true);
                localUserDAO.save(user);
                verificationTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);
        } else {
            throw new EmailNotFoundException();
        }
    }

    public void resetPassword(PasswordResetBody body) {
        Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(jwtService.getResetPasswordEmail(body.getToken()));
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserDAO.save(user);
        }
    }

}