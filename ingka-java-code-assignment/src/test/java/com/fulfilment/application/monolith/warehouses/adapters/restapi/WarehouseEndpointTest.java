package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class WarehouseEndpointTest {
    private static final String PATH = "/warehouse";

    /* -------------------- LIST -------------------- */

    @Test
    void shouldListAllWarehouseUnits() {
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200);
    }

    /* -------------------- CREATE -------------------- */

    @Test
    void shouldCreateWarehouseUnit() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId("WH-001");
        warehouse.setLocation("AMSTERDAM-001");
        warehouse.setCapacity(50);
        warehouse.setStock(40);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(warehouse)
                .when()
                .post(PATH)
                .then()
                .statusCode(200)
                .body("id", equalTo("WH-001"));
    }

    /* -------------------- GET BY ID -------------------- */

    @Test
    void shouldGetWarehouseById() {
        given()
                .when()
                .get(PATH + "/1")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    void shouldReturn404WhenWarehouseNotFound() {
        given()
                .when()
                .get(PATH + "/9999")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400WhenWarehouseIdIsInvalid() {
        given()
                .when()
                .get(PATH + "/abc")
                .then()
                .statusCode(400)
                .body(containsString("Invalid warehouse id"));
    }

    /* -------------------- ARCHIVE -------------------- */

    @Test
    void shouldArchiveWarehouse() {

        // First create warehouse
        Warehouse warehouse = new Warehouse();
        warehouse.setId("WH-ARCHIVE");
        warehouse.setLocation("AMSTERDAM-001");
        warehouse.setCapacity(40);
        warehouse.setStock(30);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(warehouse)
                .when()
                .post(PATH)
                .then()
                .statusCode(200);

        // Archive
        given()
                .when()
                .delete(PATH + "/1")
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404WhenArchivingNonExistingWarehouse() {
        given()
                .when()
                .delete(PATH + "/9999")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn404WhenWarehouseAlreadyArchived() {

        // Archive first time
        given()
                .when()
                .delete(PATH + "/2")
                .then()
                .statusCode(204);

        // Archive second time → 404
        given()
                .when()
                .delete(PATH + "/2")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400WhenArchivingWithInvalidId() {
        given()
                .when()
                .delete(PATH + "/xyz")
                .then()
                .statusCode(400);
    }
}
