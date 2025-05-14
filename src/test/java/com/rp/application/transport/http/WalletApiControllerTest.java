package com.rp.application.transport.http;

import com.rp.application.representation.WalletRepresentation;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(propertySources="classpath:application-test.yml")
@Testcontainers(disabledWithoutDocker = true)
class WalletApiControllerTest {

    @Rule
    @Container
    public PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:16-3.4-alpine").asCompatibleSubstituteFor("postgres"))
            .withUsername("postgres")
            .withPassword("postgres")
            .withDatabaseName("rp-lending");

    @Inject
    @Client("/")
    HttpClient client;


    @Test
    void testCreateAndGetWallet() {
        Pattern uuidRegex =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        WalletRepresentation wallet = new WalletRepresentation();
        wallet.setOwnerDocument("11122233344");
        wallet.setBalance(BigDecimal.valueOf(150.75));

        // POST /rp-lending-api/wallet
        HttpRequest<WalletRepresentation> createRequest = HttpRequest.POST("/rp-lending-api/wallet", wallet);
        WalletRepresentation createdWallet = client.toBlocking().retrieve(createRequest, WalletRepresentation.class);

        assertNotNull(createdWallet);
        assertTrue(uuidRegex.matcher(createdWallet.getKey()).matches());

        HttpRequest<?> getRequest = HttpRequest.GET("/rp-lending-api/wallet/" + createdWallet.getKey());
        WalletRepresentation retrievedWallet = client.toBlocking().retrieve(getRequest, WalletRepresentation.class);

        assertNotNull(retrievedWallet);
        assertEquals(createdWallet.getKey(), retrievedWallet.getKey());
        assertEquals("11122233344", retrievedWallet.getOwnerDocument());
        assertEquals(BigDecimal.valueOf(150.75), retrievedWallet.getBalance());
    }

}