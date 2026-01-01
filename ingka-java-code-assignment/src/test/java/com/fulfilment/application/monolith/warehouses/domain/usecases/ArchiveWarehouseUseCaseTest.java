package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ArchiveWarehouseUseCaseTest {

    @Inject
    CreateWarehouseUseCase createUseCase;

    @Inject
    ArchiveWarehouseUseCase archiveUseCase;

    @Inject
    WarehouseRepository warehouseStore;

    @Test
    void shouldArchiveWarehouse() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-ARCH";
        w.location = "AMSTERDAM-001";
        w.capacity = 50;
        w.stock = 10;

        createUseCase.create(w);
        archiveUseCase.archive(w);

        Warehouse archived =
                warehouseStore.findByBusinessUnitCode("BU-ARCH");
        assertNotNull(archived.archivedAt);
    }
}
