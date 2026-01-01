package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.ZonedDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
    private final LocationGateway locationGateway;
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationGateway locationGateway) {
    this.warehouseStore = warehouseStore;
      this.locationGateway = locationGateway;
  }
  @Transactional
  @Override
  public void create(Warehouse warehouse) {
// 1. Business unit uniqueness
      if (warehouseStore.findByBusinessUnitCode(
              warehouse.businessUnitCode) != null) {
          throw new IllegalArgumentException(
                  "Business unit code already exists");
      }

      // 2. Location validation
      Location location =
              locationGateway.resolveByIdentifier(warehouse.location);

      // 3. Max warehouses per location
      long count =
              warehouseStore.countActiveByLocation(warehouse.location);

      if (count >= location.maxNumberOfWarehouses) {
          throw new IllegalArgumentException(
                  "Max warehouses reached for location");
      }

      // 4. Capacity validation
      if (warehouse.capacity > location.maxCapacity) {
          throw new IllegalArgumentException(
                  "Warehouse capacity exceeds location capacity");
      }

      // 5. Stock validation
      if (warehouse.stock > warehouse.capacity) {
          throw new IllegalArgumentException(
                  "Stock exceeds warehouse capacity");
      }

      warehouse.creationAt = ZonedDateTime.now();

    warehouseStore.create(warehouse);
  }
}
