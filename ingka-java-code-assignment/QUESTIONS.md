# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**

## Database Access Layer - Would I Refactor?

### What I Noticed

The codebase uses three different approaches:

**Products Module:** Repository pattern (`PanacheRepository<Product>`)  
**Stores Module:** Active Record pattern (`Store extends PanacheEntity`)  
**Warehouses Module:** Hexagonal architecture with separate domain/persistence layers

---

### Direct Answer: Yes, I Would Refactor

**Specifically: The Stores module**

### Why Stores Needs Refactoring

The Stores module has a **mismatch between its complexity and its architecture**:

**The Problem:**
- It has sophisticated business logic (Transaction management for legacy system integration)
- But uses the simplest persistence pattern (Active Record with static methods)
- This makes it difficult to test the transaction logic properly
- Static methods like `Store.listAll()` are hard to mock in unit tests

**The Impact:**
- Testing the transaction synchronization requires full database setup
- Can't easily unit test the business logic in isolation
- Complex transaction behavior is hidden inside an entity class
- Risky to modify or extend the legacy system integration

**The Fix:**
Move to a repository pattern (like Products) or hexagonal approach (like Warehouses) to separate the complex transaction logic from the persistence layer, making it testable and maintainable.

---

### What I Would NOT Refactor

**Products Module: Leave as-is**

**Why:**
- Simple CRUD operations with no complex business logic
- Current repository pattern works fine
- No testing or maintenance problems
- Refactoring would add unnecessary complexity

---

### About Warehouses

The Warehouses approach makes sense for its complexity level (multiple business validations, location constraints, capacity checks). But applying this level of separation to something as simple as Products would be overkill.

-------
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**

## Pros and Cons of OpenAPI vs Direct REST Implementation

### Using OpenAPI Specification (e.g. Warehouse API)

**Advantages:**
- Clearly defines the API contract upfront
- Helps keep APIs consistent across implementations
- Makes API documentation easier and more reliable
- Useful when multiple teams or external clients depend on the same API

**Disadvantages:**
- Adds extra overhead to maintain the OpenAPI specification
- Code needs to be regenerated when the contract changes
- Can slow down development for small or frequently changing APIs

---

### Coding Endpoints Directly (e.g. Product and Store APIs)

**Advantages:**
- Faster to implement and simpler to maintain initially
- More flexible during early development stages
- Easier to make quick changes without updating a separate specification

**Disadvantages:**
- API contract is less explicit
- Documentation can fall out of sync with the implementation
- Harder to maintain consistency as the application grows

---

### My Choice

- Use **direct REST implementations** for:
  - Simpler APIs
  - Internal services
  - APIs that are still evolving quickly

- Use **OpenAPI-based code generation** for:
  - More complex APIs
  - Stable APIs
  - APIs exposed to external consumers or multiple teams
