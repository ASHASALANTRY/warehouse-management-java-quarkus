package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.adapters.mapper.WarehouseMapper;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Path("/warehouse")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class WarehouseResourceImpl implements WarehouseResource {
    @Inject
    CreateWarehouseOperation createWarehouse;

    @Inject
    WarehouseRepository warehouseRepository;

    @Inject
    ArchiveWarehouseOperation archiveWarehouseOperation;
    @Inject
    WarehouseStore warehouseStore;

    @Inject
    WarehouseMapper warehouseMapper;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
      return warehouseStore.findAllWarehouses()
              .stream()
              .map(warehouseMapper::domainToApi)
              .toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
      createWarehouse.create(warehouseMapper.toDomain(data));
      return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
      Long dbId = parseId(id);
      DbWarehouse db = warehouseRepository.findById(dbId);
      if (db == null || db.archivedAt != null) {
          throw new WebApplicationException(404);
      }
      return warehouseMapper.toApi(db);
  }
    @Transactional
  @Override
  public void archiveAWarehouseUnitByID(String id) {
      Long dbId = parseId(id);
      DbWarehouse db = warehouseRepository.findById(dbId);
      if (db == null || db.archivedAt != null) {
          throw new WebApplicationException(404);
      }
      archiveWarehouseOperation.archive(warehouseMapper.toDomain(db));

  }
    private Long parseId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Invalid warehouse id: " + id, 400);
        }
    }




}
