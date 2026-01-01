package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }
    @Transactional
    @Override
    public void replace(Warehouse newWarehouse) {

        Warehouse existing =
                warehouseStore.findByBusinessUnitCode(
                        newWarehouse.businessUnitCode);

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Warehouse not found: " + newWarehouse.businessUnitCode
            );        }

        // Capacity accommodation
        if (newWarehouse.capacity < existing.stock) {
            throw new IllegalArgumentException(
                    "New warehouse capacity cannot accommodate existing stock");
        }

        // Stock matching
        if (!newWarehouse.stock.equals(existing.stock)) {
            throw new IllegalArgumentException(
                    "Stock of new warehouse must match existing warehouse");
        }

        // Location validation etc. (if required)

        warehouseStore.update(newWarehouse);
    }

}
