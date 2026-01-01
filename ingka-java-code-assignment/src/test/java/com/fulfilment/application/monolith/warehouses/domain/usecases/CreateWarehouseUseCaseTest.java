package com.fulfilment.application.monolith.warehouses.domain.usecases;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CreateWarehouseUseCaseTest {

    @Inject
    CreateWarehouseUseCase useCase;

    @Test
    void shouldFailWhenStockExceedsCapacity() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-STOCK-FAIL";
        w.location = "AMSTERDAM-001";
        w.capacity = 10;
        w.stock = 50;

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(w)
        );
    }

    @Test
    void shouldFailWhenBusinessUnitAlreadyExists() {
        Warehouse first = new Warehouse();
        first.businessUnitCode = "BU-DUP";
        first.location = "AMSTERDAM-001";
        first.capacity = 100;
        first.stock = 10;

        useCase.create(first);

        Warehouse duplicate = new Warehouse();
        duplicate.businessUnitCode = "BU-DUP";
        duplicate.location = "AMSTERDAM-001";
        duplicate.capacity = 100;
        duplicate.stock = 10;

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(duplicate)
        );
    }
}
