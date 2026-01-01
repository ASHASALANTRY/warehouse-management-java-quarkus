package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public void create(Warehouse warehouse) {
      DbWarehouse dbWarehouse=toDb(warehouse);
      dbWarehouse.createdAt = LocalDateTime.now();
      persist(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
      DbWarehouse dbWarehouse=find("businessUnitCode", warehouse.businessUnitCode).firstResult();
      if (Objects.isNull(dbWarehouse))
          throw new IllegalStateException("Warehouse not found");
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt != null
              ? warehouse.archivedAt.toLocalDateTime()
              : null;
  }

  @Override
  public void remove(Warehouse warehouse) {
   delete("businessUnitCode",warehouse.businessUnitCode);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
      DbWarehouse dbWarehouse=find("businessUnitCode", buCode).firstResult();
      return Objects.isNull(dbWarehouse)? null:toDomain(dbWarehouse);
  }

    @Override
    public long countActiveByLocation(String location) {
        return count(
                "location = ?1 and archivedAt is null",
                location
        );
  }

    @Override
    public List<Warehouse> findAllWarehouses() {
            return list("archivedAt is null").
                    stream()
                    .map(this::toDomain)
                    .toList();
        }

    private DbWarehouse toDb(Warehouse w) {
        DbWarehouse db = new DbWarehouse();
        db.businessUnitCode = w.businessUnitCode;
        db.location = w.location;
        db.capacity = w.capacity;
        db.stock = w.stock;
        return db;
    }

    private Warehouse toDomain(DbWarehouse db) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = db.businessUnitCode;
        w.location = db.location;
        w.capacity = db.capacity;
        w.stock = db.stock;
        if (db.archivedAt != null) {
            w.archivedAt = db.archivedAt.atZone(ZoneOffset.UTC);
        }
        return w;
    }
}
