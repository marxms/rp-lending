package com.rp.application.transport.http;

import com.rp.application.representation.TransactionRepresentation;
import com.rp.application.representation.TransferRepresentation;
import com.rp.application.representation.WalletHistoryRepresentation;
import com.rp.application.representation.WalletRepresentation;
import com.rp.service.TransactionServiceImpl;
import com.rp.service.TransferServiceImpl;
import com.rp.service.WalletServiceImpl;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

@Controller("/rp-lending-api/wallet")
public class WalletApiController {

    private final WalletServiceImpl walletService;
    private final TransactionServiceImpl transactionService;
    private final TransferServiceImpl transferService;

    @Inject
    WalletApiController(WalletServiceImpl walletService,
                        TransactionServiceImpl transactionService,
                        TransferServiceImpl transferService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.transferService = transferService;
    }

    @Get(uri = "/{walletKey}", produces = "application/json")
    public HttpResponse<WalletRepresentation> getWalletByKey(String walletKey) {
        var result = walletService.getWalletByKey(walletKey);
        if (result.isPresent()) {
            return HttpResponse.ok(result.get());
        }
        return HttpResponse.notFound();
    }

    @Get(uri = "/{walletKey}/history", produces = "application/json")
    public HttpResponse<WalletHistoryRepresentation> getWalleHistoryByKey(@PathVariable String walletKey, @QueryValue int page, @QueryValue int size) {
        var result = walletService.getWalletHistory(walletKey, page, size);
        if (!result.getHistory().isEmpty()) {
            return HttpResponse.ok(result);
        }
        return  HttpResponse.noContent();
    }

    @Post
    public HttpResponse<WalletRepresentation> createWallet(@Body WalletRepresentation walletRepresentation) {
        return HttpResponse.created(walletService.save(walletRepresentation));
    }

    @Post(uri = "/transaction")
    public HttpResponse<TransactionRepresentation> postTransaction(@Body TransactionRepresentation transactionRepresentation) {
        return HttpResponse.created(transactionService.postTransaction(transactionRepresentation));
    }

    @Post(uri = "/transfer")
    public HttpResponse<TransferRepresentation> postTransfer(@Body TransferRepresentation transferRepresentation) {
        return HttpResponse.created(transferService.postTransfer(transferRepresentation));
    }
}