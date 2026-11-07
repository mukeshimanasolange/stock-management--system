package com.stockmanagement.service;

import com.stockmanagement.entity.Product;
import com.stockmanagement.entity.AppUser;
import com.stockmanagement.entity.Supplier;
import com.stockmanagement.exception.BadRequestException;
import com.stockmanagement.repository.ProductRepository;
import com.stockmanagement.repository.StockTransactionRepository;
import com.stockmanagement.dto.StockDtos.IncomingRequest;
import com.stockmanagement.dto.StockDtos.OutgoingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @Mock ProductRepository products;
    @Mock StockTransactionRepository transactions;
    @Mock EntityLookup lookup;
    @InjectMocks StockService service;

    @Test
    void outgoingFailureDoesNotChangeInventoryOrCreateTransaction() {
        Product product = new Product();
        product.setId(7L); product.setCode("P-7"); product.setQuantity(2); product.setActive(true);
        when(lookup.lockedProduct(7L)).thenReturn(product);

        assertThrows(BadRequestException.class,
                () -> service.outgoing(new OutgoingRequest(7L, 3, "Operations", null, null), "store@example.com"));

        assertEquals(2, product.getQuantity());
        verify(transactions, never()).save(any());
        verify(lookup, never()).userByEmail(anyString());
    }

    @Test
    void incomingIncreasesInventoryAndCreatesTransaction() {
        Product product = productWithQuantity(2);
        Supplier supplier = new Supplier();
        supplier.setId(3L); supplier.setName("Supplier"); supplier.setActive(true);
        AppUser actor = new AppUser();
        actor.setId(4L); actor.setFullName("Storekeeper");
        when(lookup.lockedProduct(7L)).thenReturn(product);
        when(lookup.supplier(3L)).thenReturn(supplier);
        when(lookup.userByEmail("store@example.com")).thenReturn(actor);
        when(transactions.save(any())).thenAnswer(call -> call.getArgument(0));

        var result = service.incoming(new IncomingRequest(7L, 3L, 5, null, null), "store@example.com");

        assertEquals(7, product.getQuantity());
        assertEquals(5, result.quantity());
        verify(transactions).save(any());
    }

    @Test
    void successfulOutgoingDecreasesInventoryAndCreatesTransaction() {
        Product product = productWithQuantity(5);
        AppUser actor = new AppUser();
        actor.setId(4L); actor.setFullName("Storekeeper");
        when(lookup.lockedProduct(7L)).thenReturn(product);
        when(lookup.userByEmail("store@example.com")).thenReturn(actor);
        when(transactions.save(any())).thenAnswer(call -> call.getArgument(0));

        var result = service.outgoing(new OutgoingRequest(7L, 3, "Operations", null, null), "store@example.com");

        assertEquals(2, product.getQuantity());
        assertEquals(3, result.quantity());
        verify(transactions).save(any());
    }

    private Product productWithQuantity(int quantity) {
        Product product = new Product();
        product.setId(7L); product.setName("Product"); product.setCode("P-7");
        product.setQuantity(quantity); product.setActive(true);
        return product;
    }
}
