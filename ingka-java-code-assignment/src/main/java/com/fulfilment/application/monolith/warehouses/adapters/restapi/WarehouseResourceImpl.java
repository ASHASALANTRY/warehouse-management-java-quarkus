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
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Path("/warehouse")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class WarehouseResourceImpl implements WarehouseResource {
    private static final Logger LOG = Logger.getLogger(WarehouseResourceImpl.class);

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


    /**
     * Lists all active (non-archived) warehouse units.
     */
    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        LOG.debug("Received request to list all warehouse units");
      return warehouseStore.findAllWarehouses()
              .stream()
              .map(warehouseMapper::domainToApi)
              .toList();
  }


    /**
     * Creates a new warehouse unit.
     *
     * Delegates validation and creation logic to the domain layer.
     */
    @Override
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
        LOG.infof("Received request to create warehouse with businessUnitCode=%s",
                data.getId());
      createWarehouse.create(warehouseMapper.toDomain(data));
        LOG.infof("Warehouse created successfully with businessUnitCode=%s",
                data.getId());
      return data;
  }

    /**
     * Retrieves a warehouse unit by database ID.
     *
     * Returns 404 if warehouse does not exist or is archived.
     */
    @Override
    public Warehouse getAWarehouseUnitByID(String id) {
        LOG.debugf("Fetching warehouse by id=%s", id);
      Long dbId = parseId(id);
      DbWarehouse db = warehouseRepository.findById(dbId);
      if (db == null || db.archivedAt != null) {
          LOG.warnf("Warehouse not found or archived for id=%s", id);
          throw new WebApplicationException(404);
      }
      return warehouseMapper.toApi(db);
  }
    /**
     * Archives a warehouse unit.
     *
     * Marked transactional to ensure consistency during state change.
     */
    @Transactional
    @Override
    public void archiveAWarehouseUnitByID(String id) {
        LOG.infof("Archiving warehouse with id=%s", id);
      Long dbId = parseId(id);
      DbWarehouse db = warehouseRepository.findById(dbId);
      if (db == null || db.archivedAt != null) {
          LOG.warnf("Warehouse not found or already archived for id=%s", id);
          throw new WebApplicationException(404);
      }
      archiveWarehouseOperation.archive(warehouseMapper.toDomain(db));
        LOG.infof("Warehouse archived successfully with id=%s", id);
    }

    /**
     * Parses and validates warehouse ID from request path.
     *
     * Throws 400 Bad Request for invalid numeric values.
     */
    private Long parseId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            LOG.warnf("Invalid warehouse id received: %s", id);
            throw new WebApplicationException("Invalid warehouse id: " + id, 400);
        }
    }




}
