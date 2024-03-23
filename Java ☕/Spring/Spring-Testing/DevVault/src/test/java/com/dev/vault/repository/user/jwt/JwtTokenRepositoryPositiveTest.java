package com.dev.vault.repository.user.jwt;

import com.dev.vault.model.user.User;
import com.dev.vault.model.user.jwt.JwtToken;
import com.dev.vault.model.user.jwt.TokenType;
import com.dev.vault.repository.user.UserRepository;
import org.assertj.core.api.AssertionsForClassTypes;
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
class JwtTokenRepositoryPositiveTest {

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
    @DisplayName("➕ Find List Of Active JWT Tokens | ( + ) Operation")
    public void givenUserID_WhenFindAllByUserUserIDAndExpiredIsFalseAndRevokedIsFalse_ThenReturnListOfActiveJWTTokens() {
        // Set up
        userRepository.save(mockUser);
        jwtTokenRepository.save(mockActiveToken);
        jwtTokenRepository.save(mockExpiredToken);

        // Act
        List<JwtToken> activeTokens = jwtTokenRepository.findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(mockUser.getUserId());

        // Assert
        assertThat(activeTokens).hasSize(1);
        assertThat(activeTokens.get(0).getToken()).isEqualTo("activeToken");
    }


    @Test
    @DisplayName("➕ Find List Of JWT Tokens By UserID | ( + ) Operation")
    public void givenUserID_WhenFindAllByUserUserID_ThenReturnListOfJWTTokens() {
        // given (precondition or setup)
        userRepository.save(mockUser);
        jwtTokenRepository.save(mockActiveToken);
        jwtTokenRepository.save(mockExpiredToken);

        // when (action occurs / action or the behaviour that we are going to test)
        List<JwtToken> jwtTokenList = jwtTokenRepository.findAllByUser_UserId(mockUser.getUserId());

        // then (verify the output)
        assertThat(jwtTokenList).hasSize(2);
        AssertionsForClassTypes.assertThat(jwtTokenList.get(0).getToken()).isEqualTo("activeToken");
        AssertionsForClassTypes.assertThat(jwtTokenList.get(1).getToken()).isEqualTo("expiredToken");
    }


    @Test
    @DisplayName("➕ Find JWT Token By Token | ( + ) Operation")
    public void givenToken_WhenFindByToken_ThenReturnJwtToken() {
        // Set up
        mockUser = userRepository.save(mockUser);
        jwtTokenRepository.save(mockActiveToken);

        // Act
        Optional<JwtToken> token = jwtTokenRepository.findByToken(mockActiveToken.getToken());

        // Assert
        assertThat(token).isPresent();
        assertThat(token.get().getToken()).isEqualTo("activeToken");
    }

}
