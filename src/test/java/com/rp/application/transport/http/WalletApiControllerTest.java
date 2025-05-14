package com.rp.application.transport.http;

import com.rp.application.representation.TransactionRepresentation;
import com.rp.application.representation.TransferRepresentation;
import com.rp.application.representation.WalletHistoryRepresentation;
import com.rp.application.representation.WalletRepresentation;
import com.rp.repository.domain.TransactionType;
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
import java.math.RoundingMode;
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

    @Test
    void testSuccesfullyTransfers() {
        // Criação da primeira carteira
        WalletRepresentation wallet1 = new WalletRepresentation();
        wallet1.setOwnerDocument("11122233344");
        wallet1.setBalance(BigDecimal.valueOf(200.00));

        HttpRequest<WalletRepresentation> createRequest1 = HttpRequest.POST("/rp-lending-api/wallet", wallet1);
        WalletRepresentation createdWallet1 = client.toBlocking().retrieve(createRequest1, WalletRepresentation.class);

        assertNotNull(createdWallet1);
        assertEquals(BigDecimal.valueOf(200.00), createdWallet1.getBalance());

        // Criação da segunda carteira
        WalletRepresentation wallet2 = new WalletRepresentation();
        wallet2.setOwnerDocument("55566677788");
        wallet2.setBalance(BigDecimal.valueOf(100.00));

        HttpRequest<WalletRepresentation> createRequest2 = HttpRequest.POST("/rp-lending-api/wallet", wallet2);
        WalletRepresentation createdWallet2 = client.toBlocking().retrieve(createRequest2, WalletRepresentation.class);

        assertNotNull(createdWallet2);
        assertEquals(BigDecimal.valueOf(100.00), createdWallet2.getBalance());

        // Realização da transferência
        TransferRepresentation transfer = new TransferRepresentation();
        transfer.setSourceWalletKey(createdWallet1.getKey());
        transfer.setDestinationWalletKey(createdWallet2.getKey());
        transfer.setAmount(BigDecimal.valueOf(50.00));

        HttpRequest<TransferRepresentation> transferRequest = HttpRequest.POST("/rp-lending-api/wallet/transfer", transfer);
        TransferRepresentation transferResponse = client.toBlocking().retrieve(transferRequest, TransferRepresentation.class);

        assertNotNull(transferResponse);
        assertEquals(BigDecimal.valueOf(50.00), transferResponse.getAmount());

        // Validação do saldo da primeira carteira
        HttpRequest<?> getRequest1 = HttpRequest.GET("/rp-lending-api/wallet/" + createdWallet1.getKey());
        WalletRepresentation updatedWallet1 = client.toBlocking().retrieve(getRequest1, WalletRepresentation.class);

        assertNotNull(updatedWallet1);
        assertEquals(BigDecimal.valueOf(150.00).setScale(2, RoundingMode.HALF_UP), updatedWallet1.getBalance());

        // Validação do saldo da segunda carteira
        HttpRequest<?> getRequest2 = HttpRequest.GET("/rp-lending-api/wallet/" + createdWallet2.getKey());
        WalletRepresentation updatedWallet2 = client.toBlocking().retrieve(getRequest2, WalletRepresentation.class);

        assertNotNull(updatedWallet2);
        assertEquals(BigDecimal.valueOf(150.00).setScale(2, RoundingMode.HALF_UP), updatedWallet2.getBalance());
    }

    @Test
    void testMultipleTransactionsAndHistory() {
        // Criação de uma carteira
        WalletRepresentation wallet = new WalletRepresentation();
        wallet.setOwnerDocument("12345678900");
        wallet.setBalance(BigDecimal.valueOf(0.00));

        HttpRequest<WalletRepresentation> createRequest = HttpRequest.POST("/rp-lending-api/wallet", wallet);
        WalletRepresentation createdWallet = client.toBlocking().retrieve(createRequest, WalletRepresentation.class);

        assertNotNull(createdWallet);
        assertEquals(BigDecimal.valueOf(0.00), createdWallet.getBalance());

        // Realização de múltiplos depósitos
        for (int i = 0; i < 5; i++) {
            TransactionRepresentation deposit = new TransactionRepresentation();
            deposit.setWalletKey(createdWallet.getKey());
            deposit.setAmount(BigDecimal.valueOf(100.00));
            deposit.setTransactionType(TransactionType.DEPOSIT);

            HttpRequest<TransactionRepresentation> depositRequest = HttpRequest.POST("/rp-lending-api/wallet/transaction", deposit);
            client.toBlocking().retrieve(depositRequest, TransactionRepresentation.class);
        }

        // Realização de múltiplos saques
        for (int i = 0; i < 3; i++) {
            TransactionRepresentation withdraw = new TransactionRepresentation();
            withdraw.setWalletKey(createdWallet.getKey());
            withdraw.setAmount(BigDecimal.valueOf(50.00));
            withdraw.setTransactionType(TransactionType.WITHDRAW);

            HttpRequest<TransactionRepresentation> withdrawRequest = HttpRequest.POST("/rp-lending-api/wallet/transaction", withdraw);
            client.toBlocking().retrieve(withdrawRequest, TransactionRepresentation.class);
        }

        // Validaçao do saldo final
        HttpRequest<?> getWalletRequest = HttpRequest.GET("/rp-lending-api/wallet/" + createdWallet.getKey());
        WalletRepresentation updatedWallet = client.toBlocking().retrieve(getWalletRequest, WalletRepresentation.class);

        assertNotNull(updatedWallet);
        assertEquals(BigDecimal.valueOf(350.00).setScale(2, RoundingMode.HALF_UP), updatedWallet.getBalance());

        // Validação do histórico
        HttpRequest<?> historyRequest = HttpRequest.GET("/rp-lending-api/wallet/" + createdWallet.getKey() + "/history?page=0&size=10");
        WalletHistoryRepresentation historyResponse = client.toBlocking().retrieve(historyRequest, WalletHistoryRepresentation.class);

        assertNotNull(historyResponse);
        assertFalse(historyResponse.getHistory().isEmpty());
        assertEquals(9, historyResponse.getHistory().size());
    }

}