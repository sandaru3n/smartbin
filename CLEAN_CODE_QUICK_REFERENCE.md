# Clean Code Quick Reference Card

## 🚀 Quick Fixes Cheat Sheet

### Session Management
```java
// ❌ OLD WAY
User user = (User) session.getAttribute("user");
if (user == null || user.getRole() != User.UserRole.RESIDENT) {
    return "redirect:/login";
}

// ✅ NEW WAY
User user = sessionManager.validateUserRole(session, User.UserRole.RESIDENT)
        .orElse(null);
if (user == null) return "redirect:/resident/login";
```

---

### Location Parameters
```java
// ❌ OLD WAY (Data Clumps)
public String search(@RequestParam Double lat,
                    @RequestParam Double lon,
                    @RequestParam Double radius) { }

// ✅ NEW WAY
public String search(@ModelAttribute LocationDTO location) { }
```

---

### Bin Operations
```java
// ❌ OLD WAY (Feature Envy)
if (bin.getStatus() == BinStatus.FULL || 
    bin.getStatus() == BinStatus.OVERDUE ||
    bin.getAlertFlag()) {
    // needs collection
}

// ✅ NEW WAY
if (bin.needsCollection()) {
    // Clean and readable
}

// ❌ OLD WAY (Shotgun Surgery)
bin.setStatus(BinStatus.EMPTY);
bin.setFillLevel(0);
bin.setLastEmptied(LocalDateTime.now());
bin.setAlertFlag(false);

// ✅ NEW WAY
bin.markAsEmptied();
```

---

### Route Operations
```java
// ❌ OLD WAY (Law of Demeter Violation)
if (!route.getCollector().getId().equals(user.getId())) { }
String name = route.getCollector().getName();

// ✅ NEW WAY
if (!route.isAssignedTo(user)) { }
String name = route.getCollectorName();

// ❌ OLD WAY (Shotgun Surgery)
route.setStatus(RouteStatus.IN_PROGRESS);
route.setStartedDate(LocalDateTime.now());

// ✅ NEW WAY
route.start();
```

---

### Filtering Collections
```java
// ❌ OLD WAY (Imperative Loop)
List<Bin> fullBins = new ArrayList<>();
for (Bin bin : allBins) {
    if (bin.getStatus() == BinStatus.FULL) {
        fullBins.add(bin);
    }
}

// ✅ NEW WAY (Functional)
List<Bin> fullBins = allBins.stream()
        .filter(Bin::needsCollection)
        .toList();
```

---

## 🎯 Code Smell Detection

### How to Spot Code Smells:

1. **Long Method**: > 20 lines → Extract methods
2. **Large Class**: > 500 lines → Split into smaller classes
3. **Long Parameter List**: > 3 params → Use DTOs
4. **Duplicate Code**: Same code 2+ places → Extract to method/class
5. **Law of Demeter**: More than 1 dot → Add delegation method
6. **Feature Envy**: Using another class's data → Move method there
7. **Primitive Obsession**: Many primitives → Create value object
8. **Data Clumps**: Same params together → Group into object

---

## ✅ SOLID Principles Checklist

### S - Single Responsibility
- [ ] Class changes for only ONE reason?
- [ ] Method does ONE thing?

### O - Open/Closed
- [ ] Can extend without modifying?
- [ ] Using interfaces/abstractions?

### L - Liskov Substitution
- [ ] Subclass works where parent expected?
- [ ] No broken contracts?

### I - Interface Segregation
- [ ] Clients don't depend on unused methods?
- [ ] Interfaces are focused?

### D - Dependency Inversion
- [ ] Depending on abstractions, not concretions?
- [ ] High-level independent of low-level?

---

## 🛠️ Quick Refactoring Patterns

### Extract Method
```java
// Before: Long method
public void doEverything() {
    // 100 lines of code
}

// After: Extracted methods
public void doEverything() {
    step1();
    step2();
    step3();
}

private void step1() { /* focused logic */ }
private void step2() { /* focused logic */ }
private void step3() { /* focused logic */ }
```

### Replace Conditional with Polymorphism
```java
// Before: Switch statement
public double calculate(String type) {
    switch(type) {
        case "A": return value * 1.1;
        case "B": return value * 1.2;
        default: return value;
    }
}

// After: Strategy pattern
interface CalculationStrategy {
    double calculate(double value);
}

Map<String, CalculationStrategy> strategies = Map.of(
    "A", value -> value * 1.1,
    "B", value -> value * 1.2
);
```

### Introduce Parameter Object
```java
// Before: Long parameter list
public void createOrder(String name, String address, 
                       String city, String zip,
                       String phone, String email) { }

// After: Parameter object
public void createOrder(CustomerInfo customer) { }

record CustomerInfo(String name, String address, 
                   String city, String zip,
                   String phone, String email) { }
```

---

## 📖 Resources Created

- `CODE_SMELL_FIXES_DOCUMENTATION.md` - Detailed guide
- `REFACTORING_EXAMPLES.md` - Practical examples
- `SOLID_AND_CODE_SMELLS_SUMMARY.md` - Complete overview
- `UserSessionManager.java` - Session helper
- `LocationDTO.java` - Location value object
- Enhanced `Bin.java` - Rich domain model
- Enhanced `Route.java` - Rich domain model

---

## ✨ Remember:

1. **Keep It Simple** - Simple code is better than clever code
2. **DRY (Don't Repeat Yourself)** - Extract common code
3. **YAGNI (You Aren't Gonna Need It)** - Don't over-engineer
4. **KISS (Keep It Simple, Stupid)** - Prefer simple solutions
5. **Tell, Don't Ask** - Objects should do, not expose
6. **Fail Fast** - Validate early, throw exceptions
7. **One Level of Abstraction** - Per method
8. **Meaningful Names** - Name tells you everything

---

**Happy Coding! 🎉**

