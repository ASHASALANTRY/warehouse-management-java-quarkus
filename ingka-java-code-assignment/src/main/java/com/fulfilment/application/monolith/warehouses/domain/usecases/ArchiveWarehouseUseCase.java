package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.ZonedDateTime;
import java.util.Objects;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }
  @Transactional
  @Override
  public void archive(Warehouse warehouse) {
      Warehouse existing =
              warehouseStore.findByBusinessUnitCode(
                      warehouse.businessUnitCode
              );

      if (existing == null) {
          throw new IllegalArgumentException(
                  "Warehouse not found: " + warehouse.businessUnitCode
          );
      }
      existing.archivedAt = ZonedDateTime.now();
      warehouseStore.update(existing);
  }
}
