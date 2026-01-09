package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReplaceWarehouseUseCaseTest {

    @Inject
    CreateWarehouseUseCase createUseCase;

    @Inject
    ReplaceWarehouseUseCase replaceUseCase;

    @Test
    void shouldFailWhenWarehouseDoesNotExist() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-NOT-EXIST";
        w.capacity = 100;
        w.stock = 10;

        assertThrows(
                IllegalArgumentException.class,
                () -> replaceUseCase.replace(w)
        );
    }
    @Test
    void shouldFailWhenStockDoesNotMatch() {
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "BU-REP";
        existing.location = "AMSTERDAM-001";
        existing.capacity = 100;
        existing.stock = 20;

        createUseCase.create(existing);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU-REP";
        replacement.capacity = 100;
        replacement.stock = 10; // mismatch

        assertThrows(
                IllegalArgumentException.class,
                () -> replaceUseCase.replace(replacement)
        );
    }

    @Test
    void shouldFailWhenReplacingNonExistingWarehouse() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-NOT-FOUND";
        w.capacity = 50;
        w.stock = 10;

        IllegalArgumentException ex =
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> replaceUseCase.replace(w)
                );

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("not found")
        );
    }
    @Test
    void shouldFailWhenNewCapacityCannotAccommodateExistingStock() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-REPLACE";
        w.location = "AMSTERDAM-001";
        w.capacity = 100;
        w.stock = 80;

        createUseCase.create(w);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU-REPLACE";
        replacement.capacity = 70; // less than stock
        replacement.stock = 80;

        IllegalArgumentException ex =
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> replaceUseCase.replace(replacement)
                );

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("capacity")
        );
    }

}
