package com.dev.vault.repository.user.jwt;

import com.dev.vault.model.user.User;
import com.dev.vault.model.user.jwt.JwtToken;
import com.dev.vault.model.user.jwt.TokenType;
import com.dev.vault.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * <p>
 * Since we're using H2 database, the <code>User</code> is a reserved keyword, so we
 * need to somehow escape it or change it.
 * </p>
 * <p>
 * (💡 I used the escaping method: <code>@Table(name = "\"USER\"")</code>
 * </p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JwtTokenRepositoryNegativeTest {

    private @Autowired JwtTokenRepository jwtTokenRepository;
    private @Autowired UserRepository userRepository;

    private User mockUser;
    private JwtToken mockActiveToken;
    private JwtToken mockExpiredToken;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("password");
        mockUser.setEmail("test@example.com");

        mockActiveToken = new JwtToken();
        mockActiveToken.setToken("activeToken");
        mockActiveToken.setType(TokenType.BEARER);
        mockActiveToken.setExpired(false);
        mockActiveToken.setRevoked(false);

        mockExpiredToken = new JwtToken();
        mockExpiredToken.setToken("expiredToken");
        mockExpiredToken.setType(TokenType.BEARER);
        mockExpiredToken.setExpired(true);
        mockExpiredToken.setRevoked(false);

        mockActiveToken.setUser(mockUser);/* rel */
        mockExpiredToken.setUser(mockUser);/* rel */
        mockUser.setJwtTokens(List.of(mockActiveToken, mockExpiredToken));/* rel */
    }

    @Test
    @DisplayName("➖ Find List Of Active JWT Tokens | ( - ) Operation")
    public void givenInvalidUserID_WhenFindAllByUserUserIDAndExpiredIsFalseAndRevokedIsFalse_ThenReturnEmptyList() {
        // given (precondition or setup)
        userRepository.save(mockUser);
        jwtTokenRepository.save(mockExpiredToken);

        // when (action occurs / action or the behaviour that we are going to test)
        List<JwtToken> emptyActiveTokens = jwtTokenRepository.findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(mockUser.getUserId());

        // then (verify the output)
        assertThat(emptyActiveTokens).isEmpty();
    }


    @Test
    @DisplayName("➖ Find List of JWT Tokens By UserID | ( - ) Operation")
    public void givenInvalidUserID_WhenFindAllByUserUserID_ThenReturnEmptyList() {
        // given (precondition or setup)
        long invalidUserId = 1001L;
        userRepository.save(mockUser);
        jwtTokenRepository.save(mockExpiredToken);

        // when (action occurs / action or the behaviour that we are going to test)
        List<JwtToken> emptyActiveTokens = jwtTokenRepository.findAllByUser_UserId(invalidUserId);

        // then (verify the output)
        assertThat(emptyActiveTokens).isEmpty();
    }


    @Test
    @DisplayName("➖ Find JWT Token By Token | ( - ) Operation")
    public void givenToken_WhenFindByToken_ThenReturnEmpty() {
        // Set up
        String nonExistentToken = "nonExistentToken";
        mockUser = userRepository.save(mockUser);
        jwtTokenRepository.save(mockActiveToken);

        // Act
        Optional<JwtToken> token = jwtTokenRepository.findByToken(nonExistentToken);

        // Assert
        assertThat(token).isNotPresent();
    }

}
